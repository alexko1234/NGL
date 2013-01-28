package models.laboratory.project.description;

import models.laboratory.common.description.CommonInfoType;

public class ProjectType extends CommonInfoType{

	public Long id;
	
	public ProjectCategory projectCategory;

	
	public ProjectType() {
		super();
	}


	public ProjectType(ProjectCategory projectCategory) {
		super();
		this.projectCategory = projectCategory;
	}
	
	
}
