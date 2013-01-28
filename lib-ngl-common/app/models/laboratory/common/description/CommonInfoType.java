package models.laboratory.common.description;


import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import models.laboratory.common.description.dao.CommonInfoTypeDAO;
import models.utils.Model;
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
	
	/*public CommonInfoType(Class<CommonInfoTypeDAO> dao) {
		super(dao);
	}*/
	
	public static CommonInfoType findById(long id)
	{
		CommonInfoTypeDAO commonInfoTypeDAO = Spring.getBeanOfType(CommonInfoTypeDAO.class);
		return commonInfoTypeDAO.findById(id);
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
	
}
