package models.laboratory.common.description;


import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.modules.spring.Spring;

public class CommonInfoType{
	
	public Long id;
	@Required
	@MaxLength(10)
	public String name; //used as label
	
	@Required
	public String code; //used for research in mongodb

	//document-oriented NoSQL database system (actually MongoDB) collection name 
	@Required
	public String collectionName;

	public List<State> variableStates = new ArrayList<State>();

	public List<Resolution> resolutions = new ArrayList<Resolution>();

	@Valid
	public List<PropertyDefinition> propertiesDefinition=new ArrayList<PropertyDefinition>();

	public ObjectType objectType;

	
	public CommonInfoType() {
		super();
	}

	public CommonInfoType(String name, String code,
			String collectionName, List<State> variableStates,
			List<Resolution> resolutions,
			List<PropertyDefinition> propertiesDefinition, ObjectType objectType) {
		super();
		this.name = name;
		this.code = code;
		this.collectionName = collectionName;
		this.variableStates = variableStates;
		this.resolutions = resolutions;
		this.propertiesDefinition = propertiesDefinition;
		this.objectType = objectType;
	}

	/*public static CommonInfoType findById(long id)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		return commonInfoTypeDAO.findById(id);
	}*/
	
	public static List<CommonInfoType> findAll()
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		return commonInfoTypeDAO.findAll();
	}
	
	public static CommonInfoType findByCode(String code)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		return commonInfoTypeDAO.findByCode(code);
	}
	
	public static List<CommonInfoType> findByNameAndType(String name, Long idObjectType)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		if(idObjectType==null)
			return commonInfoTypeDAO.findByName(name);
		else 
			return commonInfoTypeDAO.findByTypeNameAndType(name, idObjectType);
	}
	
	public CommonInfoType add()
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		return commonInfoTypeDAO.add(this);
	}
	
	public void update()
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		commonInfoTypeDAO.update(this);
		
	}
		
	public List<PropertyDefinition> getPropertiesDefinition() {
		return propertiesDefinition;
	}


	public void setPropertiesDefinition(
			List<PropertyDefinition> propertiesDefinition) {
		this.propertiesDefinition = propertiesDefinition;
	}
	
	public void setCommonInfoType(CommonInfoType commonInfoType)
	{
		this.id=commonInfoType.id;
		this.name=commonInfoType.name;
		this.code=commonInfoType.code;
		this.collectionName=commonInfoType.collectionName;
		this.variableStates=commonInfoType.variableStates;
		this.resolutions=commonInfoType.resolutions;
		this.propertiesDefinition=commonInfoType.propertiesDefinition;
		this.objectType=commonInfoType.objectType;
	}
	
	public long getIdCommonInfoType()
	{
		return this.id;
	}
	
}
