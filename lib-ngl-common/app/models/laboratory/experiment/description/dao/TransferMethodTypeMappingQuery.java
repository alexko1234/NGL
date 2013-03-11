package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.TransferMethodType;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class TransferMethodTypeMappingQuery extends MappingSqlQuery<TransferMethodType>{

	public TransferMethodTypeMappingQuery()
	{
		super();
	}
	public TransferMethodTypeMappingQuery(DataSource ds, String sql, SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
	}
	@Override
	protected TransferMethodType mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		TransferMethodType transferMethodType = new TransferMethodType();
		transferMethodType.id = rs.getLong("id");
		long idCommonInfoType = rs.getLong("fk_common_info_type");
		//Get commonInfoType
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		CommonInfoType commonInfoType;
		try {
			commonInfoType = commonInfoTypeDAO.findById(idCommonInfoType);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		transferMethodType.setCommonInfoType(commonInfoType);
		
		//Get List protocols
		ProtocolDAO protocolDAO = Spring.getBeanOfType(ProtocolDAO.class);
		List<Protocol> protocols = protocolDAO.findByCommonExperiment(idCommonInfoType);
		transferMethodType.protocols=protocols;
		//Get list instruments
		InstrumentUsedTypeDAO instrumentUsedTypeDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		List<InstrumentUsedType> instrumentUsedTypes = instrumentUsedTypeDAO.findByCommonExperiment(idCommonInfoType);
		transferMethodType.instrumentUsedTypes=instrumentUsedTypes;
		return transferMethodType;
	}

}
