package models.laboratory.common.description;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.laboratory.common.description.dao.PropertyDefinitionDAO;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentQueryParams;
import models.laboratory.instrument.description.dao.InstrumentDAO;
import models.utils.ListObject;
import models.utils.Model;
import models.utils.Model.Finder;
import models.utils.dao.AbstractDAOCommonInfoType;
import models.utils.dao.DAOException;
import play.data.validation.Constraints.Required;

/**
 * Type property definition 
 * @author ejacoby
 *
 */

public class PropertyDefinition extends Model<PropertyDefinition>{

	public String name;

	public String description;

	public Boolean required = Boolean.FALSE;
	public Boolean editable=Boolean.TRUE;
	public Boolean active = Boolean.TRUE;
	public Boolean choiceInList = Boolean.FALSE;

	public String propertyValueType ;
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
	public static PropertyDefinitionFinder find = new PropertyDefinitionFinder(); 

	@JsonIgnore
	public PropertyDefinition() {
		super(PropertyDefinitionDAO.class.getName());
	}

	
	public static class PropertyDefinitionFinder extends Finder<PropertyDefinition>{

		public PropertyDefinitionFinder() {
			super(PropertyDefinitionDAO.class.getName());			
		}
		
		public PropertyDefinition findUnique(String code, Level.CODE levelCode) throws DAOException{
			return ((PropertyDefinitionDAO)getInstance()).findUnique(code, levelCode);
		}
	}
}
