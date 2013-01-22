package models.laboratory;

import models.laboratory.common.description.CommonInfoType;



public interface IDynamicType {
	
	CommonInfoType getInformations();
	long getIdType();
	public IDynamicType findById(long id);
}
