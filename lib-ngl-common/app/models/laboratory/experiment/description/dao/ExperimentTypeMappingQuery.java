package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.PurificationMethodType;
import models.laboratory.experiment.description.QualityControlType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

public class ExperimentTypeMappingQuery extends MappingSqlQuery<ExperimentType>{

	public ExperimentTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	@Override
	protected ExperimentType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		ExperimentType experimentType = new ExperimentType();
		play.Logger.debug("Experiment type "+experimentType);
		experimentType.id = rs.getLong("id");
		experimentType.doPurification=rs.getBoolean("doPurification");
		experimentType.mandatoryPurification=rs.getBoolean("mandatoryPurification");
		experimentType.doQualityControl=rs.getBoolean("doQualityControl");
		experimentType.mandatoryQualityControl=rs.getBoolean("mandatoryQualityControl");
		long idExperimentCategory = rs.getLong("fk_experiment_category");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
		experimentType.setCommonInfoType(commonInfoType);
		
		//Get List protocols by common info type
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		List<Protocol> protocols = protocolDAO.findByCommonExperiment(idCommonInfoType);
		experimentType.protocols=protocols;
		//Get list instruments by common info type
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		List<InstrumentUsedType> instrumentUsedTypes = instrumentUsedTypeDAO.findByCommonExperiment(idCommonInfoType);
		experimentType.instrumentUsedTypes=instrumentUsedTypes;
		//Get Experiment category
		ExperimentCategoryDAO experimentCategoryDAO = Spring.getBeanOfType(ExperimentCategoryDAO.class);
		ExperimentCategory experimentCategory = (ExperimentCategory) experimentCategoryDAO.findById(idExperimentCategory);
		experimentType.experimentCategory = experimentCategory;
		
		//Get nextExperimentType
		ExperimentTypeDAO expTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		List<ExperimentType> nextExpTypes = expTypeDAO.findNextExperiments(experimentType.id);
		experimentType.previousExperimentTypes = nextExpTypes;
		
		//Get purification method
		PurificationMethodTypeDAO purificationMethodTypeDAO = Spring.getBeanOfType(PurificationMethodTypeDAO.class);
		List<PurificationMethodType> purificationMethodTypes = purificationMethodTypeDAO.findByExperimentType(experimentType.id);
		experimentType.possiblePurificationMethodTypes=purificationMethodTypes;
		//Get qualityControl
		QualityControlTypeDAO qualityControlTypeDAO = Spring.getBeanOfType(QualityControlTypeDAO.class);
		List<QualityControlType> qualityControlTypes = qualityControlTypeDAO.findByExperimentType(experimentType.id);
		experimentType.possibleQualityControlTypes=qualityControlTypes;
		
		return experimentType;
	}

}
