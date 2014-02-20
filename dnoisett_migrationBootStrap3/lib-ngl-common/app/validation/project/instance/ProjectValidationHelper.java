package validation.project.instance;

import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class ProjectValidationHelper extends CommonValidationHelper {

	public static void validateProjectType(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		ProjectType projectType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ProjectType.find,true);
		if(projectType!=null){
			ValidationHelper.validateProperties(contextValidation, properties, projectType.getPropertiesDefinitionDefaultLevel());
		}
		
	}

	public static void validateProjectCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ProjectCategory.find);
	
	}

	
}
