package models.laboratory.sample.description;

import models.laboratory.IDynamicType;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.sample.description.dao.SampleTypeDAO;
import play.modules.spring.Spring;

public class SampleType implements IDynamicType{

	public Long id;
	
	public CommonInfoType commonInfoType;
	
	public SampleCategory sampleCategory;

	@Override
	public CommonInfoType getInformations() {		
		return commonInfoType;
	}
	
	@Override
	public long getIdType() {
		return id;
	}
	
	public IDynamicType findById(long id)
	{
		SampleTypeDAO sampleTypeDAO = Spring.getBeanOfType(SampleTypeDAO.class);
		return sampleTypeDAO.findById(id);
	}

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

	public SampleCategory getSampleCategory() {
		return sampleCategory;
	}

	public void setSampleCategory(SampleCategory sampleCategory) {
		this.sampleCategory = sampleCategory;
	}
	
	
}
