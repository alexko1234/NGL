package models.laboratory.experiment.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.ProtocolCategory;
import models.laboratory.reagent.description.ReagentType;
import models.laboratory.reagent.description.dao.ReagentTypeDAO;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;
import models.utils.dao.DAOHelpers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

@Repository
public class ProtocolDAO extends AbstractDAOMapping<Protocol>{

	protected ProtocolDAO() {
		super("protocol", Protocol.class, ProtocolMappingQuery.class,
				"SELECT t.id, t.code, t.name, file_path, version, fk_protocol_category "+
						"FROM protocol as t ",true);
	}

	public List<Protocol> findByExperimentTypeId(long id)
	{
		String sql = sqlCommon+
				" JOIN experiment_type_protocol as cit ON fk_protocol=id "+
				"WHERE fk_experiment_type = ? ";
		ProtocolMappingQuery protocolMappingQuery=new ProtocolMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return protocolMappingQuery.execute(id);
	}
	
	public List<Protocol> findByExperimentTypeCode(String code)
	{
		String sql = sqlCommon+
				" JOIN experiment_type_protocol as cit ON fk_protocol=id,  experiment_type as et, common_info_type citype "+
				"WHERE cit.fk_experiment_type=et.id AND cit.fk_protocol=t.id AND et.fk_common_info_type=citype.id AND citype.code=? ";
		ProtocolMappingQuery protocolMappingQuery=new ProtocolMappingQuery(dataSource, sql,new SqlParameter("id", Types.VARCHAR));
		return protocolMappingQuery.execute(code);
	}

	public Protocol findByName(String name)
	{
		String sql = sqlCommon+" WHERE name=?";
		ProtocolMappingQuery protocolMappingQuery = new ProtocolMappingQuery(dataSource, sql,new SqlParameter("name", Types.VARCHAR));
		return protocolMappingQuery.findObject(name);
	}
/*
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
*/
	@Override
	public long save(Protocol protocol) throws DAOException
	{
		//Check if category exist
		if(protocol.category!=null){
			ProtocolCategory protocolCategoryDB = ProtocolCategory.find.findByCode(protocol.category.code);
			if(protocolCategoryDB ==null){
				ProtocolCategoryDAO protocolCategoryDAO = Spring.getBeanOfType(ProtocolCategoryDAO.class);
				protocol.category.id = protocolCategoryDAO.save(protocol.category);
			}else
				protocol.category=protocolCategoryDB;
		}

		//Create new Protocol
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", protocol.code);
		parameters.put("name", protocol.name);
		parameters.put("file_path", protocol.filePath);
		parameters.put("version", protocol.version);
		parameters.put("fk_protocol_category", protocol.category.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		protocol.id = newId;

		//Add reagent type list
		/*
		List<ReagentType> reagentTypes = protocol.reagentTypes;
		if(reagentTypes!=null && reagentTypes.size()>0){
			ReagentTypeDAO reagentTypeDAO = Spring.getBeanOfType(ReagentTypeDAO.class);
			String sql = "INSERT INTO protocol_reagent_type (fk_protocol,fk_reagent_type) VALUES(?,?)";
			for(ReagentType reagentType : reagentTypes){
				ReagentType reagentTypeDB = ReagentType.find.findByCode(reagentType.code);
				if(reagentTypeDB ==null)
					reagentType.id = reagentTypeDAO.save(reagentType);
				else
					reagentType=reagentTypeDB;
				jdbcTemplate.update(sql, newId,reagentType.id);
			}
		}
		*/
		return protocol.id;
	}

	@Override
	public void update(Protocol protocol) throws DAOException
	{
		Protocol protoDB = findById(protocol.id);
		String sql = "UPDATE protocol SET code=?, name=?, file_path=?, version=? WHERE id=?";
		jdbcTemplate.update(sql, protocol.code, protocol.name, protocol.filePath, protocol.version, protocol.id);

		//Update reagentTypes list
		/*
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
		*/
	}

	@Override
	public void remove(Protocol protocol) throws DAOException {
		//Remove list reagentType protocol_reagent_type
		//String sqlState = "DELETE FROM protocol_reagent_type WHERE fk_protocol=?";
		//jdbcTemplate.update(sqlState, protocol.id);
		//remove protocol
		super.remove(protocol);
	}

	public boolean isCodeExistForTypeCode(String codeProtocol, String typeCode) throws DataAccessException, DAOException {
		String sql = sqlCommon +
				"JOIN experiment_type_protocol etp ON etp.fk_protocol=t.id "+
				"JOIN experiment_type e ON e.id=etp.fk_experiment_type "+
				"JOIN common_info_type c on c.id =e.fk_common_info_type "+
				  DAOHelpers.getCommonInfoTypeSQLForInstitute("c")+
				" where t.code=? and c.code=?";
		return( initializeMapping(sql, new SqlParameter("t.code", Types.VARCHAR),
				 new SqlParameter("c.code", Types.VARCHAR)).findObject(codeProtocol, typeCode) != null )? true : false;	
	}
}
