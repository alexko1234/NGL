package models.laboratory.experiment.description;

import models.laboratory.common.description.CommonInfoType;

public class ReagentType{

	public Long id;
	
	public CommonInfoType commonInfoType;
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReagentType other = (ReagentType) obj;
		if (commonInfoType.code == null) {
			if (other.commonInfoType.code != null)
				return false;
		} else if (!commonInfoType.code.equals(other.commonInfoType.code))
			return false;
		return true;
	}
}
