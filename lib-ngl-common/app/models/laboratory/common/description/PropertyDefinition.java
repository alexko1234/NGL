package models.laboratory.common.description;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.laboratory.common.description.dao.PropertyDefinitionDAO;
import models.utils.Model;
//TODO: fix doc generation that produces an error with the unqualified name
import models.utils.Model.Finder;
import models.utils.dao.DAOException;

/**
 * Type property definition
 *  
 * @author ejacoby
 *
 */

public class PropertyDefinition extends Model<PropertyDefinition> {

	public String name;

	public String description;

	public Boolean required = Boolean.FALSE;
	public String requiredState = null;
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

	public static class PropertyDefinitionFinder extends Finder<PropertyDefinition,PropertyDefinitionDAO> {

//		public PropertyDefinitionFinder() {
//			super(PropertyDefinitionDAO.class.getName());			
//		}
		public PropertyDefinitionFinder() {	super(PropertyDefinitionDAO.class); }
		
		public PropertyDefinition findUnique(String code, Level.CODE levelCode) throws DAOException{
//			return ((PropertyDefinitionDAO)getInstance()).findUnique(code, levelCode);
			return getInstance().findUnique(code, levelCode);
		}
		
		public List<PropertyDefinition> findUnique(Level.CODE levelCode) throws DAOException{
//			return ((PropertyDefinitionDAO)getInstance()).findUnique(levelCode);
			return getInstance().findUnique(levelCode);
		}
		
		public List<PropertyDefinition> findUnique() throws DAOException{
//			return ((PropertyDefinitionDAO)getInstance()).findUnique();
			return getInstance().findUnique();
		}
		
	}
	
}
