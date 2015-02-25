package models.laboratory.container.instance;


import java.util.HashMap;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.ContentValidationHelper;
import validation.sample.instance.SampleValidationHelper;

public class Content implements IValidation{

	// Reference Sample code
	public String sampleCode;
	// Reference SampleType code
	public String sampleTypeCode;
	// Reference SampleCategory code
	public String sampleCategoryCode;
	
	// Reference Project code
	public String projectCode;
	
	public Double percentage;
	
	public Map<String,PropertyValue> properties;
	public Content(){
		properties=new HashMap<String, PropertyValue>();
		
	}

	@JsonIgnore
	public Content(String sampleCode,String typeCode,String categoryCode){
		this.sampleCode=sampleCode;
		this.sampleTypeCode=typeCode;
		this.sampleCategoryCode=categoryCode;
		this.properties=new HashMap<String, PropertyValue>();
		
	}


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

		ContentValidationHelper.validateSampleCode(sampleCode, contextValidation);
		SampleValidationHelper.validateSampleCategoryCode(sampleCategoryCode,contextValidation);
		ContentValidationHelper.validateSampleTypeCode(sampleTypeCode,contextValidation);
		ContentValidationHelper.validatePercentageContent(percentage, contextValidation);
		ContentValidationHelper.validateProjectCode(projectCode, contextValidation);
		//TODO Validate all properties with used the level Content
	}

}
