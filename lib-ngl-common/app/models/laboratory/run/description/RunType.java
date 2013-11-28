package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.RunTypeDAO;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;


public class RunType extends CommonInfoType {
	public RunCategory category;
	public Integer nbLanes;

	public static CommonInfoTypeFinder<RunTypeDAO,RunType> find = new CommonInfoTypeFinder<RunTypeDAO,RunType>(RunTypeDAO.class); 
	
	public RunType() {
		super(RunTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Run);
	}
}


