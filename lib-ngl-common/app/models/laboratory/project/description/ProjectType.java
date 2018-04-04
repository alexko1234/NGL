package models.laboratory.project.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.project.description.dao.ProjectTypeDAO;

public class ProjectType extends CommonInfoType {

	@SuppressWarnings("hiding")
	public static final CommonInfoType.AbstractCommonInfoTypeFinder<ProjectType,ProjectTypeDAO> find = 
			new CommonInfoType.AbstractCommonInfoTypeFinder<>(ProjectTypeDAO.class); 

	public ProjectCategory category;

	public ProjectType() {
		super(ProjectTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Project);
	}
	
}
