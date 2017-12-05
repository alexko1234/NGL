package validation.sample.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
// import models.laboratory.common.instance.State;
// import models.laboratory.experiment.instance.AtomicTransfertMethod;
// import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;


public class SampleValidationHelper extends CommonValidationHelper {

	public static void validateSampleCategoryCode(String categoryCode,
			                                      ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", SampleCategory.find,false);
	}

	public static void validateSampleType(String typeCode,
										  String importTypeCode, 
										  Map<String, PropertyValue> properties,
										  ContextValidation contextValidation) {

		List<PropertyDefinition> proDefinitions = new ArrayList<PropertyDefinition>();

		SampleType sampleType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", SampleType.find,true);
		if(sampleType!=null ){
			proDefinitions.addAll(sampleType.getPropertiesDefinitionDefaultLevel());				
		}

		ImportType importType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, importTypeCode,"importTypeCode", ImportType.find,true);
		if(importType!=null){
			proDefinitions.addAll(importType.getPropertiesDefinitionSampleLevel());			
		}

		if(proDefinitions.size() > 0){
			ValidationHelper.validateProperties(contextValidation,properties, proDefinitions,false); //need false because we generate sample from another sample
		}
	}

	public static void validateRules(Sample sample,ContextValidation contextValidation){
		ArrayList<Object> validationfacts = new ArrayList<Object>();
		validationfacts.add(sample);
		validateRules(validationfacts, contextValidation);
	}
	
}
