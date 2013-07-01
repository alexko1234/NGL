package models.laboratory.common.description;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import models.laboratory.common.description.dao.PropertyDefinitionDAO;
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

	@Required	
	public String type;
	public String displayFormat;
	public Integer displayOrder;

	public Level level;
	//Propagation des propriétés
	public Boolean propagation;

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
