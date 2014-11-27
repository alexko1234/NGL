package validation.sample.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import play.Logger;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class SampleValidationHelper extends CommonValidationHelper {

	public static void validateSampleCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		Logger.debug("CategoryCode "+categoryCode);
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", SampleCategory.find,false);

	}

	public static void validateSampleType(String typeCode,
			String importTypeCode, Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
			
			List<PropertyDefinition> proDefinitions=new ArrayList<PropertyDefinition>();

			SampleType sampleType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", SampleType.find,true);
			ImportType importType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, importTypeCode,"importTypeCode", ImportType.find,true);
			
			if(sampleType!=null && importType!=null){
				proDefinitions.addAll(sampleType.getPropertiesDefinitionDefaultLevel());
				proDefinitions.addAll(importType.getPropertiesDefinitionSampleLevel());

				ValidationHelper.validateProperties(contextValidation,properties, proDefinitions);
			}
	}

}
