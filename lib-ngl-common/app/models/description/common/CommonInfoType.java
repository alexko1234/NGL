package models.description.common;


import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import models.description.common.dao.CommonInfoTypeDAO;
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

	public static CommonInfoType findById(long id)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		return commonInfoTypeDAO.find(id);
	}
	
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


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getCollectionName() {
		return collectionName;
	}


	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}


	public List<State> getVariableStates() {
		return variableStates;
	}


	public void setVariableStates(List<State> variableStates) {
		this.variableStates = variableStates;
	}


	public List<Resolution> getResolutions() {
		return resolutions;
	}


	public void setResolutions(List<Resolution> resolutions) {
		this.resolutions = resolutions;
	}


	public ObjectType getObjectType() {
		return objectType;
	}


	public void setObjectType(ObjectType objectType) {
		this.objectType = objectType;
	}

	
	
	
}
