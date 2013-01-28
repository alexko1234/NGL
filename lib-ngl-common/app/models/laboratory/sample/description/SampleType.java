package models.laboratory.sample.description;

import models.laboratory.IDynamicType;
import models.laboratory.common.description.CommonInfoType;
import models.laboratory.sample.description.dao.SampleTypeDAO;
import play.modules.spring.Spring;

public class SampleType extends CommonInfoType implements IDynamicType{

	public Long id;
	
	public SampleCategory sampleCategory;

	@Override
	public CommonInfoType getInformations() {		
		return this;
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

		
	
}
