package models.laboratory.experiment.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.experiment.description.ReagentType;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

public class ProtocolMappingQuery extends MappingSqlQuery<Protocol>{

	public ProtocolMappingQuery(DataSource ds, String sql)
	{
		super(ds,sql);
		super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}
	@Override
	protected Protocol mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Protocol protocol = new Protocol();
		protocol.setId(rs.getLong("id"));
		protocol.setName(rs.getString("name"));
		protocol.setFilePath(rs.getString("file_path"));
		protocol.setVersion(rs.getString("version"));
		long idProtocolCategory = rs.getLong("fk_protocol_category");
		//Add reagent type
		ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
		List<ReagentType> reagentTypes = reagentTypeDAO.findByProtocol(protocol.getId());
		protocol.setReagentTypes(reagentTypes);
		//Get protocol category
		ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
		ProtocolCategory protocolCategory = protocolCategoryDAO.findById(idProtocolCategory);
		protocol.setProtocolCategory(protocolCategory);
		return protocol;
	}

}
