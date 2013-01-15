package models.description.common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import models.description.common.MeasureCategory;
import models.description.common.MeasureValue;
import models.description.common.PropertyDefinition;
import models.description.common.Value;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import play.modules.spring.Spring;

import com.avaje.ebean.enhance.asm.Type;

public class PropertyDefinitionMappingQuery extends MappingSqlQuery<PropertyDefinition>{

	public PropertyDefinitionMappingQuery(DataSource ds, String sql)
	{
		super(ds,sql);
		super.declareParameter(new SqlParameter("id", Type.LONG));
		compile();
	}

	@Override
	protected PropertyDefinition mapRow(ResultSet rs, int rowNumber)
			throws SQLException {
		PropertyDefinition propertyDefinition = new PropertyDefinition();
		propertyDefinition.setId(rs.getLong("pId"));
		propertyDefinition.setName(rs.getString("pName"));
		propertyDefinition.setCode(rs.getString("pCode"));
		propertyDefinition.setDescription(rs.getString("description"));
		propertyDefinition.setRequired(rs.getBoolean("required"));
		propertyDefinition.setActive(rs.getBoolean("active"));
		propertyDefinition.setChoiceInList(rs.getBoolean("choice_in_list"));
		propertyDefinition.setType(rs.getString("type"));
		propertyDefinition.setDisplayFormat(rs.getString("display_format"));
		propertyDefinition.setDisplayOrder(rs.getInt("display_order"));
		propertyDefinition.setDefaultValue(rs.getString("pDefaultValue"));
		propertyDefinition.setLevel(rs.getString("level"));
		propertyDefinition.setInOut(rs.getString("in_out"));
		propertyDefinition.setPropagation(rs.getBoolean("propagation"));
		//Add measure category
		if(rs.getLong("mcId")!=0){
			MeasureCategory measureCategory = new MeasureCategory();
			measureCategory.setId(rs.getLong("mcId"));
			measureCategory.setName(rs.getString("mcName"));
			measureCategory.setCode(rs.getString("mcCode"));
			propertyDefinition.setMeasureCategory(measureCategory);
		}
		//Add measure value
		if(rs.getLong("mvId")!=0){
			MeasureValue measureValue = new MeasureValue();
			measureValue.setId(rs.getLong("mvId"));
			measureValue.setDefaultValue(rs.getBoolean("mvDefaultValue"));
			measureValue.setValue(rs.getString("value"));
			propertyDefinition.setMeasureValue(measureValue);
		}
		//Add possible values
		ValueDAO valueDAO = Spring.getBeanOfType(ValueDAO.class);
		List<Value> values = valueDAO.findByPropertyDefinition(propertyDefinition.getId());
		propertyDefinition.setPossibleValues(values);
		
		return propertyDefinition;
	}

}
