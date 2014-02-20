package models.laboratory.project.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.CommonInfoType.AbstractCommonInfoTypeFinder;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.project.description.dao.ProjectTypeDAO;

public class ProjectType extends CommonInfoType{

	public ProjectCategory category;

	public static CommonInfoType.AbstractCommonInfoTypeFinder<ProjectType> find = new CommonInfoType.AbstractCommonInfoTypeFinder<ProjectType>(ProjectTypeDAO.class); 
	
	public ProjectType() {
		super(ProjectTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Project);
	}
	
}
