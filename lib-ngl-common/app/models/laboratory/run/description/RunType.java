package models.laboratory.run.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.CommonInfoType.AbstractCommonInfoTypeFinder;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.run.description.dao.RunTypeDAO;

public class RunType extends CommonInfoType {
	
	public static final CommonInfoType.AbstractCommonInfoTypeFinder<RunType,RunTypeDAO> find = 
			new CommonInfoType.AbstractCommonInfoTypeFinder<>(RunTypeDAO.class); 

	public RunCategory category;
	public Integer     nbLanes;

	public RunType() {
		super(RunTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Run);
	}
	
}


