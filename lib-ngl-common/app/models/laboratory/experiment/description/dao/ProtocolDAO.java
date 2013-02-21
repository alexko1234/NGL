package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.experiment.description.ReagentType;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class ProtocolDAO extends AbstractDAOMapping<Protocol>{

	protected ProtocolDAO() {
		super("protocol", Protocol.class, ProtocolMappingQuery.class,
				"SELECT t.id, code, name, file_path, version, fk_protocol_category "+
				"FROM protocol as t ",true);
	}

	public List<Protocol> findByCommonExperiment(long idCommonInfoType)
	{
		String sql = sqlCommon+
				" JOIN common_info_type_protocol as cit ON fk_protocol=id "+
				"WHERE fk_common_info_type = ? ";
		ProtocolMappingQuery protocolMappingQuery=new ProtocolMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return protocolMappingQuery.execute(idCommonInfoType);
	}

	public Protocol findByName(String name)
	{
		String sql = sqlCommon+" WHERE name=?";
		ProtocolMappingQuery protocolMappingQuery = new ProtocolMappingQuery(dataSource, sql,new SqlParameter("name", Types.VARCHAR));
		return protocolMappingQuery.findObject(name);
	}
	
	public void save(List<Protocol> protocols, long idCommonInfoType) throws DAOException
	{
		//Add protocols list
		if(protocols!=null && protocols.size()>0){
			for(Protocol protocol : protocols){
				save(protocol,idCommonInfoType);
			}
		}
	}

	public void save(Protocol protocol, long idCommonInfoType) throws DAOException
	{
		String sql = "INSERT INTO common_info_type_protocol(fk_common_info_type, fk_protocol) VALUES(?,?)";
		if(protocol.id==null)
			protocol.id = save(protocol);
		jdbcTemplate.update(sql, idCommonInfoType, protocol.id);
	}

	public long save(Protocol protocol) throws DAOException
	{
		//Check if category exist
		if(protocol.protocolCategory!=null && protocol.protocolCategory.code!=null && ProtocolCategory.find.findByCode(protocol.protocolCategory.code)==null)
		{
			ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
			protocol.protocolCategory.id = protocolCategoryDAO.save(protocol.protocolCategory);
		}

		//Create new Protocol
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", protocol.code);
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
				if(reagentType.code!=null && ReagentType.find.findByCode(reagentType.code)==null)
					reagentType.id = reagentTypeDAO.save(reagentType);
				jdbcTemplate.update(sql, newId,reagentType.id);
			}
		}
		return protocol.id;
	}

	public void update(Protocol protocol) throws DAOException
	{
		Protocol protoDB = findById(protocol.id);
		String sql = "UPDATE protocol SET code=?, name=?, file_path=?, version=? WHERE id=?";
		jdbcTemplate.update(sql, protocol.code, protocol.name, protocol.filePath, protocol.version, protocol.id);

		//Update reagentTypes list
		List<ReagentType> reagentTypes = protocol.reagentTypes;
		if(reagentTypes!=null && reagentTypes.size()>0){
			ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
			String sqlReagent = "INSERT INTO protocol_reagent_type (fk_protocol,fk_reagent_type) VALUES(?,?)";
			for(ReagentType reagentType : reagentTypes){
				if(protoDB.reagentTypes==null || (protoDB.reagentTypes!=null && !protoDB.reagentTypes.contains(reagentType))){
					reagentType.id = reagentTypeDAO.save(reagentType);
					jdbcTemplate.update(sqlReagent, protoDB.id, reagentType.id);
				}
			}

		}
	}



	@Override
	public void remove(Protocol protocol) {
		//Remove list reagentType protocol_reagent_type
		String sqlState = "DELETE FROM protocol_reagent_type WHERE fk_protocol=?";
		jdbcTemplate.update(sqlState, protocol.id);
		//remove protocol
		super.remove(protocol);
	}
}
