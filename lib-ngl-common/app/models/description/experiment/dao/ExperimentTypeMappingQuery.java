package models.description.experiment.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.dao.CommonInfoTypeDAO;
import models.description.experiment.ExperimentType;
import models.description.experiment.InstrumentUsedType;
import models.description.experiment.Protocol;

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
		System.out.println("Experiment type "+experimentType);
		experimentType.setId(rs.getLong("id"));
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType = commonInfoTypeDAO.find(idCommonInfoType);
		experimentType.setCommonInfoType(commonInfoType);
		//Get nextExperimentType
		ExperimentTypeDAO expTypeDAO = Spring.getBeanOfType(ExperimentTypeDAO.class);
		List<ExperimentType> nextExpTypes = expTypeDAO.findNextExperiments(experimentType.getId());
		experimentType.setNextExperimentTypes(nextExpTypes);
		//Get protocol
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		List<Protocol> protocols = protocolDAO.findByExperimentType(experimentType.getId());
		experimentType.setProtocols(protocols);
		//Get instrumentUsedType
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		List<InstrumentUsedType> instrumentUsedTypes = instrumentUsedTypeDAO.findByExperimentType(experimentType.getId());
		experimentType.setInstrumentTypes(instrumentUsedTypes);
		return experimentType;
	}

}
