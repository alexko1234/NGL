package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.utils.dao.AbstractDAOMapping;
import models.utils.dao.DAOException;

import org.springframework.asm.Type;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import play.api.modules.spring.Spring;

@Repository
public class CommonInfoTypeDAO extends AbstractDAOMapping<CommonInfoType>{

	public CommonInfoTypeDAO() {
		super("common_info_type", CommonInfoType.class, CommonInfoTypeMappingQuery.class, 
				"SELECT t.id as cId, t.name, t.code as codeSearch, t.collection_name, o.id as oId, o.code as codeObject, o.generic "+
				"FROM common_info_type as t "+
				"JOIN object_type as o ON o.id=t.fk_object_type ",true);
	}

	public List<CommonInfoType> findByName(String typeName)
	{
		String sql = sqlCommon+
				"WHERE name like \'%"+typeName+"%\' "+
				"ORDER by name asc";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, null);
		return commonInfoTypeMappingQuery.execute();

	}

	public List<CommonInfoType> findByTypeNameAndType(String typeName, long idobjectType)
	{
		String sql = sqlCommon+
				"WHERE name like \'%"+typeName+"%\' "+
				"AND fk_object_type=? "+
				"ORDER by name asc";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, new SqlParameter("fk_object_type",Type.LONG));
		return commonInfoTypeMappingQuery.execute(idobjectType);

	}
	
	/**
	 * Particular sql with two code must be implemented
	 */
	public CommonInfoType findByCode(String code) throws DAOException
	{
		String sql = sqlCommon+
				"WHERE codeSearch = ? ";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, new SqlParameter("code",Types.VARCHAR));
		return commonInfoTypeMappingQuery.findObject(code);
	}


	public long save(CommonInfoType cit) throws DAOException
	{
		//Check if objectType exist
		
		if(cit.objectType!=null){
			ObjectType objectTypeDB = ObjectType.find.findByCode(cit.objectType.code);
			if(objectTypeDB==null){
			ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
			cit.objectType.id = objectTypeDAO.save(cit.objectType);
			}else
				cit.objectType=objectTypeDB;
		}
		//Create new cit
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", cit.name);
		parameters.put("code", cit.code);
		parameters.put("collection_name", cit.collectionName);
		parameters.put("fk_object_type", cit.objectType.id);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		cit.id = newId;

		//Add states list
		List<State> states = cit.variableStates;
		if(states!=null && states.size()>0){
			StateDAO stateDao = Spring.getBeanOfType(StateDAO.class);
			String sql = "INSERT INTO common_info_type_state (fk_common_info_type,fk_state) VALUES(?,?)";
			for(State state : states){
				State stateDB = State.find.findByCode(state.code);
				if(stateDB ==null)
					state.id = stateDao.save(state);
				else
					state=stateDB;
				jdbcTemplate.update(sql, newId,state.id);
			}
		}
		//Add resolutions list
		List<Resolution> resolutions = cit.resolutions;
		if(resolutions!=null && resolutions.size()>0){
			ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
			String sql = "INSERT INTO common_info_type_resolution (fk_common_info_type, fk_resolution) VALUES(?,?)";
			for(Resolution resolution:resolutions){
				Resolution resolutionDB = Resolution.find.findByCode(resolution.code);
				if(resolutionDB ==null)
					resolution.id=resolutionDAO.save(resolution);
				else
					resolution=resolutionDB;
				jdbcTemplate.update(sql, newId,resolution.id);
			}
		}

		//Add PropertyDefinition
		List<PropertyDefinition> propertyDefinitions = cit.propertiesDefinitions;
		if(propertyDefinitions!=null && propertyDefinitions.size()>0){
			PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
			for(PropertyDefinition propertyDefinition : propertyDefinitions){
				PropertyDefinition propertyDefinitionDB = PropertyDefinition.find.findByCode(propertyDefinition.code);
				if(propertyDefinitionDB ==null)
					propertyDefinitionDAO.save(propertyDefinition, cit.id);
				
			}
		}
		return cit.id;
	}

	public void update(CommonInfoType cit) throws DAOException
	{
		CommonInfoType citDB = findById(cit.id);

		String sql = "UPDATE common_info_type SET name=?, code=?, collection_name=?, fk_object_type=? WHERE id=?";
		jdbcTemplate.update(sql, cit.name, cit.code, cit.collectionName, cit.objectType.id, cit.id);
		//Update states list
		List<State> states = cit.variableStates;
		if(states!=null && states.size()>0){
			StateDAO stateDao = Spring.getBeanOfType(StateDAO.class);
			String sqlState = "INSERT INTO common_info_type_state (fk_common_info_type,fk_state) VALUES(?,?)";
			for(State state : states){
				if(citDB.variableStates==null || (citDB.variableStates!=null && !citDB.variableStates.contains(state))){
					State stateDB = State.find.findByCode(state.code);
					if(stateDB==null)
						state.id = stateDao.save(state);
					else
						state=stateDB;
					jdbcTemplate.update(sqlState, citDB.id,state.id);
					
				}
			}
		}
		//Update resolutions list
		List<Resolution> resolutions = cit.resolutions;
		if(resolutions!=null && resolutions.size()>0){
			ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
			String sqlReso = "INSERT INTO common_info_type_resolution (fk_common_info_type, fk_resolution) VALUES(?,?)";
			for(Resolution resolution:resolutions){
				if(citDB.resolutions==null || (citDB.resolutions!=null && !citDB.resolutions.contains(resolution))){
					Resolution resolutionDB = Resolution.find.findByCode(resolution.code);
					if(resolutionDB==null)
						resolution.id=resolutionDAO.save(resolution);
					else
						resolution=resolutionDB;
					jdbcTemplate.update(sqlReso, citDB.id,resolution.id);
				}
			}
		}
		//Update propertiesDefinition list (add new)
		List<PropertyDefinition> propertyDefinitions = cit.propertiesDefinitions;
		if(propertyDefinitions!=null && propertyDefinitions.size()>0){
			PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
			for(PropertyDefinition propDef : propertyDefinitions){
				if(citDB.propertiesDefinitions==null || (citDB.propertiesDefinitions!=null && !citDB.propertiesDefinitions.contains(propDef))){
					propertyDefinitionDAO.save(propDef, cit.id);
				}
			}
		}
	}

	@Override
	public void remove(CommonInfoType commonInfoType) throws DAOException{
		//Delete state common_info_type_state
		String sqlState = "DELETE FROM common_info_type_state WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlState, commonInfoType.id);
		//Delete resolution common_info_type_resolution
		String sqlResol = "DELETE FROM common_info_type_resolution WHERE fk_common_info_type=?";
		jdbcTemplate.update(sqlResol, commonInfoType.id);
		//Delete property_definition
		PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
		for(PropertyDefinition propertyDefinition : commonInfoType.propertiesDefinitions){
			propertyDefinitionDAO.remove(propertyDefinition);
		}
		//Delete common_info_type
		super.remove(commonInfoType);
	}



	
	

}
