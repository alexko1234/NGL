package models.description.common.dao;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.description.common.CommonInfoType;
import models.description.common.ObjectType;
import models.description.common.PropertyDefinition;
import models.description.common.Resolution;
import models.description.common.State;

import org.springframework.asm.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class CommonInfoTypeDAO {

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

	public CommonInfoType find(long id)
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
	
	public List<CommonInfoType> findByTypeNameAndType(String typeName, long idObjectType)
	{
		String sql = sqlCommon+
					"WHERE name like \'%"+typeName+"%\' "+
					"AND fk_object_type=? "+
					"ORDER by name asc";
		CommonInfoTypeMappingQuery commonInfoTypeMappingQuery = new CommonInfoTypeMappingQuery(dataSource, sql, new SqlParameter("fk_object_type",Type.LONG));
		return commonInfoTypeMappingQuery.execute(idObjectType);
					
	}
	public CommonInfoType add(CommonInfoType cit)
	{
		//Check if objectType exist
		if(cit.getObjectType()!=null && cit.getObjectType().getId()==null)
		{
			ObjectTypeDAO objectTypeDAO = Spring.getBeanOfType(ObjectTypeDAO.class);
			ObjectType ot = objectTypeDAO.add(cit.getObjectType());
			cit.setObjectType(ot);
		}
		//Create new cit
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", cit.getName());
		parameters.put("code", cit.getCode());
		parameters.put("collection_name", cit.getCollectionName());
		parameters.put("fk_object_type", cit.getObjectType().getId());
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		cit.setId(newId);

		//Add states list
		List<State> states = cit.getVariableStates();
		if(states!=null && states.size()>0){
			StateDAO stateDao = Spring.getBeanOfType(StateDAO.class);
			String sql = "INSERT INTO common_info_type_state (fk_common_info_type,fk_state) VALUES(?,?)";
			for(State state : states){
				if(state.getId()==null)
					state = stateDao.add(state);
				jdbcTemplate.update(sql, newId,state.getId());
			}
		}
		//Add resolutions list
		List<Resolution> resolutions = cit.getResolutions();
		if(resolutions!=null && resolutions.size()>0){
			ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
			String sql = "INSERT INTO common_info_type_resolution (fk_common_info_type, fk_resolution) VALUES(?,?)";
			for(Resolution resolution:resolutions){
				if(resolution.getId()==null)
					resolution=resolutionDAO.add(resolution);
				jdbcTemplate.update(sql, newId,resolution.getId());
			}
		}
		
		//Add PropertyDefinition
		List<PropertyDefinition> propertyDefinitions = cit.getPropertiesDefinition();
		if(propertyDefinitions!=null && propertyDefinitions.size()>0){
			PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
			for(PropertyDefinition propertyDefinition : propertyDefinitions){
				if(propertyDefinition.getId()==null)
					propertyDefinitionDAO.add(propertyDefinition, cit.getId());
			}
		}
		return cit;
	}

	public void update(CommonInfoType cit)
	{
		CommonInfoType citDB = find(cit.getId());

		String sql = "UPDATE common_info_type SET name=?, code=?, collection_name=?, fk_object_type=? WHERE id=?";
		jdbcTemplate.update(sql, cit.getName(), cit.getCode(), cit.getCollectionName(), cit.getObjectType().getId(), cit.getId());
		//Update states list
		List<State> states = cit.getVariableStates();
		if(states!=null && states.size()>0){
			StateDAO stateDao = Spring.getBeanOfType(StateDAO.class);
			String sqlState = "INSERT INTO common_info_type_state (fk_common_info_type,fk_state) VALUES(?,?)";
			for(State state : states){
				if(citDB.getVariableStates()==null || (citDB.getVariableStates()!=null && !citDB.getVariableStates().contains(state))){
					if(state.getId()==null)
						state = stateDao.add(state);
					jdbcTemplate.update(sqlState, citDB.getId(),state.getId());
				}
			}
		}
		//Update resolutions list
		List<Resolution> resolutions = cit.getResolutions();
		if(resolutions!=null && resolutions.size()>0){
			ResolutionDAO resolutionDAO = Spring.getBeanOfType(ResolutionDAO.class);
			String sqlReso = "INSERT INTO common_info_type_resolution (fk_common_info_type, fk_resolution) VALUES(?,?)";
			for(Resolution resolution:resolutions){
				if(citDB.getResolutions()==null || (citDB.getResolutions()!=null && !citDB.getResolutions().contains(resolution))){
					if(resolution.getId()==null)
						resolution=resolutionDAO.add(resolution);
					jdbcTemplate.update(sqlReso, citDB.getId(),resolution.getId());
				}
			}
		}
		//Update propertiesDefinition list (add new)
		List<PropertyDefinition> propertyDefinitions = cit.getPropertiesDefinition();
		if(propertyDefinitions!=null && propertyDefinitions.size()>0){
			PropertyDefinitionDAO propertyDefinitionDAO = Spring.getBeanOfType(PropertyDefinitionDAO.class);
			for(PropertyDefinition propDef : propertyDefinitions){
				if(citDB.getPropertiesDefinition()==null || (citDB.getPropertiesDefinition()!=null && !citDB.getPropertiesDefinition().contains(propDef))){
					propertyDefinitionDAO.add(propDef, cit.getId());
				}
			}
		}
	}

}
