package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.experiment.description.ReagentType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.avaje.ebean.enhance.asm.Type;

import play.modules.spring.Spring;

@Repository
public class ProtocolDAO {

	private SimpleJdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	private SimpleJdbcInsert jdbcInsert;
	private String sqlCommon = "SELECT id, name, file_path, version, fk_protocol_category "+
			"FROM protocol ";

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcInsert=new SimpleJdbcInsert(dataSource).withTableName("protocol").usingGeneratedKeyColumns("id");
	}



	public List<Protocol> findByCommonExperiment(long idCommonInfoType)
	{
		String sql = sqlCommon+
				" JOIN common_info_type_protocol as cit ON fk_protocol=id "+
				"WHERE fk_common_info_type = ? ";
		ProtocolMappingQuery protocolMappingQuery=new ProtocolMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return protocolMappingQuery.execute(idCommonInfoType);
	}

	public Protocol findById(long id)
	{
		String sql = sqlCommon+" WHERE id=?";
		ProtocolMappingQuery protocolMappingQuery = new ProtocolMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return protocolMappingQuery.findObject(id);
	}

	public Protocol findByName(String name)
	{
		String sql = sqlCommon+" WHERE name=?";
		ProtocolMappingQuery protocolMappingQuery = new ProtocolMappingQuery(dataSource, sql,new SqlParameter("name", Types.VARCHAR));
		return protocolMappingQuery.findObject(name);
	}

	public void add(List<Protocol> protocols, long idCommonInfoType)
	{
		//Add protocols list
		if(protocols!=null && protocols.size()>0){
			for(Protocol protocol : protocols){
				add(protocol,idCommonInfoType);
			}
		}
	}

	public void add(Protocol protocol, long idCommonInfoType)
	{
		String sql = "INSERT INTO common_info_type_protocol(fk_common_info_type, fk_protocol) VALUES(?,?)";
		if(protocol.id==null)
			protocol = add(protocol);
		jdbcTemplate.update(sql, idCommonInfoType, protocol.id);
	}

	public Protocol add(Protocol protocol)
	{
		//Check if category exist
		if(protocol.protocolCategory!=null && protocol.protocolCategory.id==null)
		{
			ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
			ProtocolCategory pc = (ProtocolCategory) protocolCategoryDAO.add(protocol.protocolCategory);
			protocol.protocolCategory = pc;
		}


		//Create new Protocol
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", protocol.name);
		parameters.put("file_path", protocol.filePath);
		parameters.put("version", protocol.version);
		parameters.put("fk_protocol_category", protocol.protocolCategory.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		protocol.id = newId;

		//Add reagent type list
		List<ReagentType> reagentTypes = protocol.reagentTypes;
		if(reagentTypes!=null && reagentTypes.size()>0){
			ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
			String sql = "INSERT INTO protocol_reagent_type (fk_protocol,fk_reagent_type) VALUES(?,?)";
			for(ReagentType reagentType : reagentTypes){
				if(reagentType.id==null)
					reagentType = reagentTypeDAO.add(reagentType);
				jdbcTemplate.update(sql, newId,reagentType.id);
			}
		}
		return protocol;
	}

	public void update(Protocol protocol)
	{
		Protocol protoDB = findById(protocol.id);
		String sql = "UPDATE protocol SET name=?, file_path=?, version=? WHERE id=?";
		jdbcTemplate.update(sql, protocol.name, protocol.filePath, protocol.version, protocol.id);

		//Update reagentTypes list
		List<ReagentType> reagentTypes = protocol.reagentTypes;
		if(reagentTypes!=null && reagentTypes.size()>0){
			ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
			String sqlReagent = "INSERT INTO protocol_reagent_type (fk_protocol,fk_reagent_type) VALUES(?,?)";
			for(ReagentType reagentType : reagentTypes){
				if(protoDB.reagentTypes==null || (protoDB.reagentTypes!=null && !protoDB.reagentTypes.contains(reagentType))){
					reagentType = reagentTypeDAO.add(reagentType);
					jdbcTemplate.update(sqlReagent, protoDB.id, reagentType.id);
				}
			}

		}
	}
}
