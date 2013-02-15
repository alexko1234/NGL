package models.laboratory.project.description;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.project.description.dao.ProjectTypeDAO;

public class ProjectType extends CommonInfoType{

	public ProjectCategory projectCategory;

	public static Finder<ProjectType> find = new Finder<ProjectType>(ProjectTypeDAO.class.getName()); 
	
	public ProjectType() {
		super(ProjectTypeDAO.class.getName());
	}
	
	
	
}
