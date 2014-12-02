package validation.project.instance;

import static validation.utils.ValidationHelper.required;

import java.util.Map;

import org.mongojack.DBQuery;
import org.springframework.beans.factory.annotation.Required;

import fr.cea.ig.MongoDBDAO;
import play.Logger;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.BioinformaticParameters;
import models.laboratory.project.instance.UmbrellaProject;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
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
	

	public static void validateUmbrellaProjectCode (String umbrellaProjectCode, ContextValidation contextValidation) {		
		//TODO : temporary unset if
		//if (ValidationHelper.required(contextValidation, umbrellaProjectCode, "umbrellaProjectCode")) {
			if ((umbrellaProjectCode != null) && !MongoDBDAO.checkObjectExist(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProject.class,  DBQuery.is("code", umbrellaProjectCode))) {
				contextValidation.addErrors("umbrellaProjectCode", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, umbrellaProjectCode);
			}
		//}		 
	}

	public static void validateBioformaticParameters(BioinformaticParameters bioinformaticParameters,ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, bioinformaticParameters, "bioinformaticParameters")){
			bioinformaticParameters.validate(contextValidation);
		}
	}
	

}
