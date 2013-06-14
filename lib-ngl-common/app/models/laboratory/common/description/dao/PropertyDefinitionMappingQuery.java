package models.laboratory.common.description.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.laboratory.common.description.MeasureCategory;
import models.laboratory.common.description.MeasureValue;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.api.modules.spring.Spring;

public class PropertyDefinitionMappingQuery extends MappingSqlQuery<PropertyDefinition>{

	public PropertyDefinitionMappingQuery()
	{
		super();
	}

	public PropertyDefinitionMappingQuery(DataSource ds, String sql, SqlParameter ... sqlParameters)
	{
		super(ds,sql);
		for(SqlParameter sqlParameter : sqlParameters){
			if(sqlParameter!=null)
				super.declareParameter(sqlParameter);
		}
		compile();
	}

	@Override
	protected PropertyDefinition mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		PropertyDefinition propertyDefinition = new PropertyDefinition();
		propertyDefinition.id = rs.getLong("pId");
		propertyDefinition.name = rs.getString("pName");
		propertyDefinition.code = rs.getString("codeSearch");
		propertyDefinition.description = rs.getString("description");
		propertyDefinition.required = rs.getBoolean("required");
		propertyDefinition.active = rs.getBoolean("active");
		propertyDefinition.choiceInList = rs.getBoolean("choice_in_list");
		propertyDefinition.type = rs.getString("type");
		propertyDefinition.displayFormat = rs.getString("display_format");
		propertyDefinition.displayOrder = rs.getInt("display_order");
		propertyDefinition.defaultValue = rs.getString("pDefaultValue");
		propertyDefinition.level = rs.getString("level");
		propertyDefinition.inOut = rs.getString("in_out");
		propertyDefinition.propagation = rs.getBoolean("propagation");
		//Add measure category
		if(rs.getLong("mcId")!=0){
			MeasureCategory measureCategory = new MeasureCategory();
			measureCategory.id = rs.getLong("mcId");
			measureCategory.name = rs.getString("mcName");
			measureCategory.code = rs.getString("mcCode");
			propertyDefinition.measureCategory = measureCategory;
		}
		//Add measure value
		if(rs.getLong("mvId")!=0){
			MeasureValue measureValue = new MeasureValue();
			measureValue.id = rs.getLong("mvId");
			measureValue.code=rs.getString("mvCode");
			measureValue.defaultValue = rs.getBoolean("mvDefaultValue");
			measureValue.value = rs.getString("mvValue");
			propertyDefinition.measureValue = measureValue;
		}

		//Add display measure value
		if(rs.getLong("mvDispId")!=0){
			MeasureValue measureValue = new MeasureValue();
			measureValue.id = rs.getLong("mvDispId");
			measureValue.code = rs.getString("mvDispCode");
			measureValue.defaultValue = rs.getBoolean("mvDispDefaultValue");
			measureValue.value = rs.getString("mvDispValue");
			propertyDefinition.displayMeasureValue = measureValue;
		}
		//Add possible values
		ValueDAO valueDAO = Spring.getBeanOfType(ValueDAO.class);
		List<Value> values = valueDAO.findByPropertyDefinition(propertyDefinition.id);
		propertyDefinition.possibleValues = values;

		return propertyDefinition;
	}

}
