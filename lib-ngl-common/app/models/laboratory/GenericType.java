package models.laboratory;

import models.laboratory.common.description.CommonInfoType;
/**
 * GenericType is used when the ObjectType does not have a specific table.
 * In this case the DAO wrap the CommonInfoType inside a GenericType.
 * 
 * After each type are treat with the same way
 *  
 * @author galbini
 *
 */
public class GenericType implements IDynamicType{
	
	//not getter to be compatible with ebean play system where play generate getter and setter
	public CommonInfoType commonInfoType;
	

	public GenericType(CommonInfoType commonInfoType) {
		super();
		this.commonInfoType = commonInfoType;
	}

	@Override
	public CommonInfoType getInformations() {		
		return commonInfoType;
	}

	@Override
	public long getIdType() {
		return commonInfoType.id;
	}

	@Override
	public IDynamicType findById(long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
