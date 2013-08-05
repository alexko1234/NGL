package models.laboratory.project.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.project.description.dao.ProjectTypeDAO;

public class ProjectType extends CommonInfoType{

	public ProjectCategory category;

	public static Finder<ProjectType> find = new Finder<ProjectType>(ProjectTypeDAO.class.getName()); 
	
	public ProjectType() {
		super(ProjectTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertydefinitionByInstance("Project");
	}
	
}
