package models.laboratory.container.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.BusinessValidationHelper;

public class SampleUsed implements IValidation{

	// Reference Sample code
	public String sampleCode;
	// Reference SampleType code
	public String typeCode;
	// Reference SampleCategory code
	public String categoryCode;
	
	public SampleUsed(){
	
	}

	@JsonIgnore
	public SampleUsed(String sampleCode,String typeCode,String categoryCode){
		this.sampleCode=sampleCode;
		this.typeCode=typeCode;
		this.categoryCode=categoryCode;
		
	}

	@JsonIgnore
	public Sample getSample(){
		return new HelperObjects<Sample>().getObject(Sample.class, sampleCode, null);
		
	}
	
	@JsonIgnore
	public SampleType getSampleType(){
		return new HelperObjects<SampleType>().getObject(SampleType.class, typeCode, null);

	}
	
	@JsonIgnore
	public SampleCategory getSampleCategory(){
		return new HelperObjects<SampleCategory>().getObject(SampleCategory.class, categoryCode, null);

		
	}

	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {

		BusinessValidationHelper.validationType(errors, sampleCode, Sample.class);
		BusinessValidationHelper.validationType(errors, typeCode, SampleType.class);
		BusinessValidationHelper.validationType(errors, categoryCode, SampleCategory.class);

	}

}
