package models.description.project;

import models.description.common.CommonInfoType;

public class ProjectType {

	public Long id;
	
	public CommonInfoType commonInfoType;
	
	public ProjectCategory projectCategory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CommonInfoType getCommonInfoType() {
		return commonInfoType;
	}

	public void setCommonInfoType(CommonInfoType commonInfoType) {
		this.commonInfoType = commonInfoType;
	}

	public ProjectCategory getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(ProjectCategory projectCategory) {
		this.projectCategory = projectCategory;
	}
	
	
	
}
