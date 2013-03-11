package models.laboratory.common.description;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.utils.Model;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.api.modules.spring.Spring;

/**
 * Class attributes common types
 * Represented by a table in the database with its own id
 * The subclasses are represented by tables in the database with the same id as the parent class
 * Relations with the protocols and instruments are accessible by the common_info_type table for the experiment subclasses (experimentType, qualityCcontrolType ...)
 * @author ejacoby
 *
 */
public class CommonInfoType extends Model<CommonInfoType>{
	
	@Required
	@MaxLength(10)
	public String name; //used as label
	
	//document-oriented NoSQL database system (actually MongoDB) collection name 
	@Required
	public String collectionName;

	public List<State> variableStates = new ArrayList<State>();

	public List<Resolution> resolutions = new ArrayList<Resolution>();

	@Valid
	public List<PropertyDefinition> propertiesDefinitions=new ArrayList<PropertyDefinition>();

	public ObjectType objectType;

	public static Finder<CommonInfoType> find = new Finder<CommonInfoType>(CommonInfoTypeDAO.class.getName()); 
	
	public CommonInfoType() {
		super(CommonInfoTypeDAO.class.getName());
	}
	
	protected CommonInfoType(String classNameDAO){
		super(classNameDAO);
	}
	
	
	public static List<CommonInfoType> findByNameAndType(String name, Long idObjectType)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		if(idObjectType==null)
			return commonInfoTypeDAO.findByName(name);
		else 
			return commonInfoTypeDAO.findByTypeNameAndType(name, idObjectType);
	}
	
	
	
	public Map<String, PropertyDefinition> getMapPropertyDefinition()
	{
		Map<String, PropertyDefinition> mapProperties = new HashMap<String, PropertyDefinition>();
		for(PropertyDefinition propertyDefinition : propertiesDefinitions){
			mapProperties.put(propertyDefinition.code, propertyDefinition);
		}
		return mapProperties;
	}
	
	public void setCommonInfoType(CommonInfoType commonInfoType)
	{
		this.id=commonInfoType.id;
		this.name=commonInfoType.name;
		this.code=commonInfoType.code;
		this.collectionName=commonInfoType.collectionName;
		this.variableStates=commonInfoType.variableStates;
		this.resolutions=commonInfoType.resolutions;
		this.propertiesDefinitions=commonInfoType.propertiesDefinitions;
		this.objectType=commonInfoType.objectType;
	}
	
}
