package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
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
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		experimentType.commonInfoType = commonInfoType;
		//Get nextExperimentType
		ExperimentTypeDAO expTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		List<ExperimentType> nextExpTypes = expTypeDAO.findNextExperiments(experimentType.id);
		experimentType.nextExperimentTypes = nextExpTypes;
		//Get protocol
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		List<Protocol> protocols = protocolDAO.findByExperimentType(experimentType.id);
		experimentType.protocols = protocols;
		//Get instrumentUsedType
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		List<InstrumentUsedType> instrumentUsedTypes = instrumentUsedTypeDAO.findByExperimentType(experimentType.id);
		experimentType.instrumentTypes = instrumentUsedTypes;
		return experimentType;
	}

}
