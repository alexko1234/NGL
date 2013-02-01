package models.laboratory.common.description.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class CommonInfoTypeDAO{


	private DataSource dataSource;
	private String sqlCommon = "SELECT c.id as cId, c.name, c.code , c.collection_name, o.id as oId, o.type, o.generic "+
			"FROM common_info_type as c "+
			"JOIN object_type as o ON o.id=c.fk_object_type ";
	private SimpleJdbcInsert jdbcInsert;
	private SimpleJdbcTemplate jdbcTemplate;



	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcInsert=new SimpleJdbcInsert(dataSource).withTableName("common_info_type").usingGeneratedKeyColumns("id");
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);       
	}

	public CommonInfoType findById(Long id)
	{
		String sql = sqlCommon+
				"WHERE c.id = ? ";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql,new SqlParameter("id", Type.LONG));
		return commonInfoTypeMappingQuery.findObject(id);
	}

	public List<CommonInfoType> findAll()
	{
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sqlCommon,null);
		return commonInfoTypeMappingQuery.execute();
	}

	public CommonInfoType findByCode(String code)
	{
		String sql = sqlCommon+
				"WHERE c.code = ? ";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, new SqlParameter("code",Types.VARCHAR));
		return commonInfoTypeMappingQuery.findObject(code);
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


	public CommonInfoType add(CommonInfoType cit)
	{
		//Check if objectType exist
		if(cit.objectType!=null && cit.objectType.id==null)
		{
			ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
			ObjectType ot = objectTypeDAO.add(cit.objectType);
			cit.objectType = ot;
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
				if(state.id==null)
					state = stateDao.add(state);
				jdbcTemplate.update(sql, newId,state.id);
			}
		}
		//Add resolutions list
		List<Resolution> resolutions = cit.resolutions;
		if(resolutions!=null && resolutions.size()>0){
			ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
			String sql = "INSERT INTO common_info_type_resolution (fk_common_info_type, fk_resolution) VALUES(?,?)";
			for(Resolution resolution:resolutions){
				if(resolution.id==null)
					resolution=resolutionDAO.add(resolution);
				jdbcTemplate.update(sql, newId,resolution.id);
			}
		}

		//Add PropertyDefinition
		List<PropertyDefinition> propertyDefinitions = cit.propertiesDefinitions;
		if(propertyDefinitions!=null && propertyDefinitions.size()>0){
			PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
			for(PropertyDefinition propertyDefinition : propertyDefinitions){
				if(propertyDefinition.id==null)
					propertyDefinitionDAO.add(propertyDefinition, cit.id);
			}
		}
		return cit;
	}

	public CommonInfoType update(CommonInfoType cit)
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
					if(state.id==null)
						state = stateDao.add(state);
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
					if(resolution.id==null)
						resolution=resolutionDAO.add(resolution);
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
					propertyDefinitionDAO.add(propDef, cit.id);
				}
			}
		}
		return citDB;
	}

	

}
