package validation.project.instance;

import java.util.Map;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;

import play.Logger;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.Project;
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
		if (ValidationHelper.required(contextValidation, umbrellaProjectCode, "umbrellaProjectCode")) {
			if (! MongoDBDAO.checkObjectExist(InstanceConstants.PROJECT_COLL_NAME, Project.class,  DBQuery.is("code", umbrellaProjectCode))) {
				contextValidation.addErrors("umbrellaProjectCode", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, umbrellaProjectCode);
			}
		}		 
	}
	

}
