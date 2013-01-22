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
		parameters.put("code", propertyDefinition.getCode());
		parameters.put("name", propertyDefinition.getName());
		parameters.put("description", propertyDefinition.getDescription());
		parameters.put("required", propertyDefinition.getRequired());
		parameters.put("active", propertyDefinition.getActive());
		parameters.put("choice_in_list", propertyDefinition.getChoiceInList());
		parameters.put("type", propertyDefinition.getType());
		parameters.put("display_format", propertyDefinition.getDisplayFormat());
		parameters.put("display_order", propertyDefinition.getDisplayOrder());
		parameters.put("default_value", propertyDefinition.getDefaultValue());
		parameters.put("level", propertyDefinition.getLevel());
		parameters.put("in_out", propertyDefinition.getInOut());
		parameters.put("propagation", propertyDefinition.getPropagation());
		parameters.put("common_info_type_id", idCommonInfoType);
		Long newId = (Long) jdbcInsert.executeAndReturnKey(parameters);
		propertyDefinition.setId(newId);

		//Add values list
		List<Value> values = propertyDefinition.getPossibleValues();
		if(values!=null && values.size()>0){
			ValueDAO valueDao = Spring.getBeanOfType(ValueDAO.class);
			for(Value value : values){
				valueDao.add(value, propertyDefinition.getId());
			}
		}

		//Add measureCategory
		if(propertyDefinition.getMeasureCategory()!=null){
			if(propertyDefinition.getMeasureCategory().getId()==null){

				MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
				propertyDefinition.setMeasureCategory(measureCategoryDAO.add(propertyDefinition.getMeasureCategory()));
			}
			//Update propertyDefinition
			String sqlCategory = "UPDATE property_definition SET measure_category_id=? WHERE id=?";
			jdbcTemplate.update(sqlCategory, propertyDefinition.getMeasureCategory().getId(), propertyDefinition.getId());
		}

		//Add measureValue
		if(propertyDefinition.getMeasureValue()!=null){
			if(propertyDefinition.getMeasureValue().getId()==null){
				MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
				propertyDefinition.setMeasureValue(measureValueDAO.add(propertyDefinition.getMeasureValue(),propertyDefinition.getMeasureCategory().getId()));
			}
			//Update propertyDefinition
			String sqlValue = "UPDATE property_definition SET measure_value_id=? WHERE id=?";
			jdbcTemplate.update(sqlValue, propertyDefinition.getMeasureValue().getId(), propertyDefinition.getId());
		}
		return propertyDefinition;
	}

	public void update(PropertyDefinition propertyDefinition)
	{
		PropertyDefinition propertyDefinitionDB = findById(propertyDefinition.getId());
		String sql = "UPDATE property_definition SET name=?, description=?, required=?, active=?,choice_in_list=?, type=?, display_format=?, display_order=?, default_value=?, level=?, in_out=?, propagation=? WHERE id=?";
		jdbcTemplate.update(sql, propertyDefinition.getName(), propertyDefinition.getDescription(), propertyDefinition.getRequired(), propertyDefinition.getActive(), propertyDefinition.getChoiceInList(), propertyDefinition.getType(), propertyDefinition.getDisplayFormat(), propertyDefinition.getDisplayOrder(), propertyDefinition.getDefaultValue(), propertyDefinition.getLevel(), propertyDefinition.getInOut(), propertyDefinition.getPropagation(), propertyDefinition.getId());

		//Update values list (add new)
		List<Value> values = propertyDefinition.getPossibleValues();
		if(values!=null && values.size()>0){
			ValueDAO valueDao = Spring.getBeanOfType(ValueDAO.class);
			for(Value value : values){
				if(propertyDefinitionDB.getPossibleValues()==null || (propertyDefinition.getPossibleValues()!=null && !propertyDefinitionDB.getPossibleValues().contains(value))){
					valueDao.add(value, propertyDefinition.getId());
				}
			}
		}

		//Update measure category
		if(propertyDefinition.getMeasureCategory()!=null){
			if(propertyDefinition.getMeasureCategory().getId()==null){

				MeasureCategoryDAO measureCategoryDAO = Spring.getBeanOfType(MeasureCategoryDAO.class);
				propertyDefinition.setMeasureCategory(measureCategoryDAO.add(propertyDefinition.getMeasureCategory()));
			}
			//Update propertyDefinition
			String sqlCategory = "UPDATE property_definition SET measure_category_id=? WHERE id=?";
			jdbcTemplate.update(sqlCategory, propertyDefinition.getMeasureCategory().getId(), propertyDefinition.getId());
		}

		//Update measureValue
		if(propertyDefinition.getMeasureValue()!=null){
			if(propertyDefinition.getMeasureValue().getId()==null){
				MeasureValueDAO measureValueDAO = Spring.getBeanOfType(MeasureValueDAO.class);
				propertyDefinition.setMeasureValue(measureValueDAO.add(propertyDefinition.getMeasureValue(),propertyDefinition.getMeasureCategory().getId()));
			}
			//Update propertyDefinition
			String sqlValue = "UPDATE property_definition SET measure_value_id=? WHERE id=?";
			jdbcTemplate.update(sqlValue, propertyDefinition.getMeasureValue().getId(), propertyDefinition.getId());
		}
	}
}
