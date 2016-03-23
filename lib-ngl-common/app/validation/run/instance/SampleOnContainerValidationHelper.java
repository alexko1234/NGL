package validation.run.instance;

import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;



public class SampleOnContainerValidationHelper extends CommonValidationHelper {
	
	public static void validateSampleCode(String sampleCode, ContextValidation contextValidation)
	{
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, sampleCode, "sampleCode", Sample.class,InstanceConstants.SAMPLE_COLL_NAME);
	}
	
	public static SampleType validateSampleTypeCode(String sampleTypeCode, ContextValidation contextValidation)
	{
		return validateRequiredDescriptionCode(contextValidation, sampleTypeCode, "typeCode", SampleType.find,true);
	}
	
	public static void validateSampleCategoryCode(String sampleCategoryCode, SampleType sampleType, ContextValidation contextValidation)
	{
		if(ValidationHelper.required(contextValidation, sampleCategoryCode, "sampleCategoryCode")){
			SampleCategory sc = validateExistDescriptionCode(contextValidation, sampleCategoryCode, "sampleCategoryCode", SampleCategory.find, true);
			if(!sampleType.category.equals(sc)){
				contextValidation.addErrors("categorySampleCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, sampleCategoryCode);
			}
		}
	}
}
