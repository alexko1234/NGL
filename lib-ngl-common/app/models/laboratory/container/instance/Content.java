package models.laboratory.container.instance;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.ContentValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	public String referenceCollab;
	public String taxonCode;
	public String ncbiScientificName;

	public Map<String,PropertyValue> properties;

	/* Put process properties to analyse container*/
	//public String processTypeCode;
	//public String processCode;
	public Map<String,PropertyValue> processProperties;
	public List<Comment> processComments;

	
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
		ContentValidationHelper.validateProjectCode(projectCode, contextValidation);
		ContentValidationHelper.validateSampleCodeWithProjectCode(projectCode, sampleCode, contextValidation);
		ContentValidationHelper.validateSampleCategoryCode(sampleCategoryCode,contextValidation);
		ContentValidationHelper.validateSampleTypeCode(sampleTypeCode,contextValidation);
		ContentValidationHelper.validatePercentageContent(percentage, contextValidation);
		ContentValidationHelper.validateProperties(sampleTypeCode, properties, contextValidation);
	}

	@Override
	public Content clone() {
		Content finalContent = new Content();
		
		finalContent.projectCode = this.projectCode;
		finalContent.sampleCode = this.sampleCode;
		finalContent.sampleCategoryCode = this.sampleCategoryCode;
		finalContent.sampleTypeCode = this.sampleTypeCode;
		finalContent.referenceCollab = this.referenceCollab;
		finalContent.percentage = this.percentage;
		if(null != this.properties)
			finalContent.properties = new HashMap<String,PropertyValue>(this.properties);
		finalContent.taxonCode=this.taxonCode;
		finalContent.ncbiScientificName=this.ncbiScientificName;
		if(null != this.processProperties)
			finalContent.processProperties = new HashMap<String,PropertyValue>(this.processProperties);
		finalContent.processComments = this.processComments;
		return finalContent;
	}
}
