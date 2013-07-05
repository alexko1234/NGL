package models.laboratory.container.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Logger;
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
		return new HelperObjects<Sample>().getObject(Sample.class, sampleCode);
		
	}
	
	@JsonIgnore
	public SampleType getSampleType(){
		return new HelperObjects<SampleType>().getObject(SampleType.class, typeCode);

	}
	
	@JsonIgnore
	public SampleCategory getSampleCategory(){
		return new HelperObjects<SampleCategory>().getObject(SampleCategory.class, categoryCode);

		
	}

	@JsonIgnore
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {

		BusinessValidationHelper.validateExistInstanceCode(errors, sampleCode, "sampleCode", Sample.class, InstanceConstants.SAMPLE_COLL_NAME, false);
		Logger.debug("Type code"+typeCode);
		//BusinessValidationHelper.validateExistDescriptionCode(errors, typeCode, "typeCode", SampleType.find,false);
		BusinessValidationHelper.validateExistDescriptionCode(errors, categoryCode, "categoryCode",SampleCategory.find,false);

	}

}
