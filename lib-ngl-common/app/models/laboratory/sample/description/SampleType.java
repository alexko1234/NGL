package models.laboratory.sample.description;

import java.util.List;

import models.laboratory.common.description.CommonInfoType;
// import models.laboratory.common.description.CommonInfoType.AbstractCommonInfoTypeFinder;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.sample.description.dao.SampleTypeDAO;

public class SampleType extends CommonInfoType {

	@SuppressWarnings("hiding")
	public static final CommonInfoType.AbstractCommonInfoTypeFinder<SampleType,SampleTypeDAO> find = 
			new CommonInfoType.AbstractCommonInfoTypeFinder<>(SampleTypeDAO.class);
	
	public SampleCategory category;

	public SampleType() {
		super(SampleTypeDAO.class.getName());
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionDefaultLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Sample);
	}
	
	public List<PropertyDefinition> getPropertiesDefinitionContentLevel(){
		return getPropertyDefinitionByLevel(Level.CODE.Content);
	}

}
