package models.laboratory.common.description;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import models.laboratory.common.description.dao.PropertyDefinitionDAO;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.utils.Model;
import play.data.validation.Constraints.Required;

/**
 * Type property definition 
 * @author ejacoby
 *
 */

public class PropertyDefinition extends Model<PropertyDefinition>{

	@Required	
	public String name;

	public String description;

	public Boolean required = Boolean.FALSE;

	public Boolean active = Boolean.TRUE;
	public Boolean choiceInList = Boolean.FALSE;

	public String propertyType = PropertySingleValue.class.getName(); //Single, Map, List, File, Object, List<Object>
	@Required	
	public String valueType;  //String, Integer, Boolean, TBoolean, etc.
	public String displayFormat;
	public Integer displayOrder;

	public List<Level> levels;
	
	public List<Value> possibleValues;

	public String defaultValue;

	public MeasureCategory measureCategory;

	//Unité de stockage
	public MeasureUnit saveMeasureValue;
	//Unité d'affichage
	public MeasureUnit displayMeasureValue;
	
	@JsonIgnore
	public static Finder<PropertyDefinition> find = new Finder<PropertyDefinition>(PropertyDefinitionDAO.class.getName()); 

	@JsonIgnore
	public PropertyDefinition() {
		super(PropertyDefinitionDAO.class.getName());
	}

}
