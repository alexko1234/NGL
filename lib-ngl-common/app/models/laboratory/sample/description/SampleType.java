package models.laboratory.sample.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.dao.SampleTypeDAO;

public class SampleType extends CommonInfoType{

	public SampleCategory category;

	public static Finder<SampleType> find = new Finder<SampleType>(SampleTypeDAO.class.getName());
	
	public SampleType() {
		super(SampleTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Sample);
	}

}
