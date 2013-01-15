package models.description.experiment;

import models.description.common.CommonInfoType;

public class ReagentType{

	public Long id;
	
	public CommonInfoType commonInfoType;
	
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


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReagentType other = (ReagentType) obj;
		if (commonInfoType.getCode() == null) {
			if (other.getCommonInfoType().getCode() != null)
				return false;
		} else if (!commonInfoType.getCode().equals(other.getCommonInfoType().getCode()))
			return false;
		return true;
	}
}
