package models.laboratory.project.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.project.description.dao.ProjectTypeDAO;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;

public class ProjectType extends CommonInfoType{

	public ProjectCategory category;

	public static CommonInfoTypeFinder<ProjectTypeDAO,ProjectType> find = new CommonInfoTypeFinder<ProjectTypeDAO,ProjectType>(ProjectTypeDAO.class); 
	
	public ProjectType() {
		super(ProjectTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Project);
	}
	
}
