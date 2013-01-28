package models.laboratory.common.description.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import play.modules.spring.Spring;

@Repository
public class PropertyDefinitionDAO {

	private DataSource dataSource;
	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	private String sqlCommon="SELECT p.id as pId,p.name as pName,p.code as pCode, description, required,active,choice_in_list, type,display_format,display_order,p.default_value as pDefaultValue, level, in_out,propagation, mc.id as mcId,mc.name as mcName,mc.code as mcCode,mv.id as mvId,value,mv.default_value as mvDefaultValue "+
			"FROM property_definition as p "+
			"LEFT OUTER JOIN measure_category as mc ON  measure_category_id=mc.id "+
			"LEFT OUTER JOIN measure_value as mv ON measure_value_id=mv.id ";

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("property_definition").usingGeneratedKeyColumns("id");

	}

	public List<PropertyDefinition> findByCommonInfoType(long idCommonInfoType)
	{
		String sql = sqlCommon+" WHERE common_info_type_id = ? ";
		PropertyDefinitionMappingQuery propertyDefinitionMappingQuery=new PropertyDefinitionMappingQuery(dataSource, sql);
		return propertyDefinitionMappingQuery.execute(idCommonInfoType);
	}

	public PropertyDefinition findById(long id)
	{
		String sql = sqlCommon+" WHERE p.id=?";
		PropertyDefinitionMappingQuery propertyDefinitionMappingQuery = new PropertyDefinitionMappingQuery(dataSource, sql);
		return propertyDefinitionMappingQuery.findObject(id);
	}

	public PropertyDefinition add(PropertyDefinition propertyDefinition, long idCommonInfoType)
	{
		//Create propertyDefinition
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("code", propertyDefinition.code);
		parameters.put("name", propertyDefinition.name);
		parameters.put("description", propertyDefinition.description);
		parameters.put("required", propertyDefinition.required);
		parameters.put("active", propertyDefinition.active);
		parameters.put("choice_in_list", propertyDefinition.choiceInList);
		parameters.put("type", propertyDefinition.type);
		parameters.put("display_format", propertyDefinition.displayFormat);
		parameters.put("display_order", propertyDefinition.displayOrder);
		parameters.put("default_value", propertyDefinition.defaultValue);
		parameters.put("level", propertyDefinition.level);
		parameters.put("in_out", propertyDefinition.inOut);
		parameters.put("propagation", propertyDefinition.propagation);
		parameters.put("common_info_type_id", idCommonInfoType);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		propertyDefinition.id = newId;

		//Add values list
		List<Value> values = propertyDefinition.possibleValues;
		if(values!=null && values.size()>0){
			ValueDAO valueDao = Spring.getBeanOfType(ValueDAO.class);
			for(Value value : values){
				valueDao.add(value, propertyDefinition.id);
			}
		}

		//Add measureCategory
		if(propertyDefinition.measureCategory!=null){
			if(propertyDefinition.measureCategory.id==null){

				MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
				propertyDefinition.measureCategory = measureCategoryDAO.add(propertyDefinition.measureCategory);
			}
			//Update propertyDefinition
			String sqlCategory = "UPDATE property_definition SET measure_category_id=? WHERE id=?";
			jdbcTemplate.update(sqlCategory, propertyDefinition.measureCategory.id, propertyDefinition.id);
		}

		//Add measureValue
		if(propertyDefinition.measureValue!=null){
			if(propertyDefinition.measureValue.id==null){
				MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
				propertyDefinition.measureValue.measureCateroryId = propertyDefinition.measureCategory.id;
				propertyDefinition.measureValue = measureValueDAO.add(propertyDefinition.measureValue);
			}
			//Update propertyDefinition
			String sqlValue = "UPDATE property_definition SET measure_value_id=? WHERE id=?";
			jdbcTemplate.update(sqlValue, propertyDefinition.measureValue.id, propertyDefinition.id);
		}
		return propertyDefinition;
	}

	public void update(PropertyDefinition propertyDefinition)
	{
		PropertyDefinition propertyDefinitionDB = findById(propertyDefinition.id);
		String sql = "UPDATE property_definition SET name=?, description=?, required=?, active=?,choice_in_list=?, type=?, display_format=?, display_order=?, default_value=?, level=?, in_out=?, propagation=? WHERE id=?";
		jdbcTemplate.update(sql, propertyDefinition.name, propertyDefinition.description, propertyDefinition.required, propertyDefinition.active, propertyDefinition.choiceInList, propertyDefinition.type, propertyDefinition.displayFormat, propertyDefinition.displayOrder, propertyDefinition.defaultValue, propertyDefinition.level, propertyDefinition.inOut, propertyDefinition.propagation, propertyDefinition.id);

		//Update values list (add new)
		List<Value> values = propertyDefinition.possibleValues;
		if(values!=null && values.size()>0){
			ValueDAO valueDao = Spring.getBeanOfType(ValueDAO.class);
			for(Value value : values){
				if(propertyDefinitionDB.possibleValues==null || (propertyDefinition.possibleValues!=null && !propertyDefinitionDB.possibleValues.contains(value))){
					valueDao.add(value, propertyDefinition.id);
				}
			}
		}

		//Update measure category
		if(propertyDefinition.measureCategory!=null){
			if(propertyDefinition.measureCategory.id==null){

				MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
				propertyDefinition.measureCategory = measureCategoryDAO.add(propertyDefinition.measureCategory);
			}
			//Update propertyDefinition
			String sqlCategory = "UPDATE property_definition SET measure_category_id=? WHERE id=?";
			jdbcTemplate.update(sqlCategory, propertyDefinition.measureCategory.id, propertyDefinition.id);
		}

		//Update measureValue
		if(propertyDefinition.measureValue!=null){
			if(propertyDefinition.measureValue.id==null){
				MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
				propertyDefinition.measureValue.measureCateroryId = propertyDefinition.measureCategory.id;
				propertyDefinition.measureValue = measureValueDAO.add(propertyDefinition.measureValue);
			}
			//Update propertyDefinition
			String sqlValue = "UPDATE property_definition SET measure_value_id=? WHERE id=?";
			jdbcTemplate.update(sqlValue, propertyDefinition.measureValue.id, propertyDefinition.id);
		}
	}
}
