package models.description;

import models.description.common.CommonInfoType;



public interface IDynamicType {
	
	CommonInfoType getInformations();
	long getIdType();
	public IDynamicType findById(long id);
}
