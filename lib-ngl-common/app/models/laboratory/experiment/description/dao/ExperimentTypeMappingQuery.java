package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class ExperimentTypeMappingQuery extends MappingSqlQuery<ExperimentType>{

	public ExperimentTypeMappingQuery()
	{
		super();
	}
	
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
		//play.Logger.debug("Experiment type "+experimentType);
		experimentType.id = rs.getLong("id");
		experimentType.atomicTransfertMethod=rs.getString("atomic_transfert_method");
		experimentType.shortCode = rs.getString("short_code");
		long idExperimentCategory = rs.getLong("fk_experiment_category");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType=null;
		try {
			commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
		} catch (DAOException e1) {
			throw new SQLException(e1);
		}
		experimentType.setCommonInfoType(commonInfoType);
		
		//Get list instruments by common info type
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		List<InstrumentUsedType> instrumentUsedTypes = instrumentUsedTypeDAO.findByExperimentId(idCommonInfoType);
		experimentType.instrumentUsedTypes=instrumentUsedTypes;
		//Get Experiment category
		ExperimentCategoryDAO experimentCategoryDAO = Spring.getBeanOfType(ExperimentCategoryDAO.class);
		ExperimentCategory experimentCategory=null;
		try {
			experimentCategory = (ExperimentCategory) experimentCategoryDAO.findById(idExperimentCategory);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		experimentType.category = experimentCategory;
		
		return experimentType;
	}

}
