package models.laboratory.sample.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.dao.SampleTypeDAO;
import models.utils.dao.AbstractDAOCommonInfoType.CommonInfoTypeFinder;

public class SampleType extends CommonInfoType{

	public SampleCategory category;

	public static CommonInfoTypeFinder<SampleTypeDAO,SampleType> find = new CommonInfoTypeFinder<SampleTypeDAO,SampleType>(SampleTypeDAO.class);
	
	public SampleType() {
		super(SampleTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Sample);
	}

}
