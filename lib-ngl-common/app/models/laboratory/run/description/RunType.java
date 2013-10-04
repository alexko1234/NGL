package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.RunTypeDAO;


public class RunType extends CommonInfoType{
	public RunCategory category;
	public Integer nbLanes;

	public static Finder<RunType> find = new Finder<RunType>(RunTypeDAO.class.getName()); 
	
	public RunType() {
		super(RunTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Run);
	}
}


