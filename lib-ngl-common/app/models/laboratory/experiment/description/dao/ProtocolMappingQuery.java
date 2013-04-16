package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.reagent.description.ReagentType;
import models.laboratory.reagent.description.dao.ReagentTypeDAO;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class ProtocolMappingQuery extends MappingSqlQuery<Protocol>{

	public ProtocolMappingQuery()
	{
		super();
	}
	public ProtocolMappingQuery(DataSource ds, String sql,SqlParameter sqlParameter)
	{
		super(ds,sql);
		if(sqlParameter!=null)
			super.declareParameter(sqlParameter);
		compile();
		
	}
	@Override
	protected Protocol mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Protocol protocol = new Protocol();
		protocol.id = rs.getLong("id");
		protocol.code = rs.getString("code");
		protocol.name = rs.getString("name");
		protocol.filePath = rs.getString("file_path");
		protocol.version = rs.getString("version");
		long idProtocolCategory = rs.getLong("fk_protocol_category");
		//Add reagent type
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		List<ReagentType> reagentTypes = reagentTypeDAO.findByProtocol(protocol.id);
		protocol.reagentTypes = reagentTypes;
		//Get protocol category
		ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
		ProtocolCategory protocolCategory=null;
		try {
			protocolCategory = (ProtocolCategory) protocolCategoryDAO.findById(idProtocolCategory);
		} catch (DAOException e) {
			throw new SQLException(e);
		}
		protocol.protocolCategory = protocolCategory;
		return protocol;
	}

}
