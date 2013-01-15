package models.description.experiment;

import models.description.common.CommonInfoType;

public class ProcessType{

	public Long id;
	
	public CommonInfoType commonInfoType;
	
	public ProcessCategory processCategory;
	
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

	public ProcessCategory getProcessCategory() {
		return processCategory;
	}

	public void setProcessCategory(ProcessCategory processCategory) {
		this.processCategory = processCategory;
	}

	
}
