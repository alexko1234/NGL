package validation.project.instance;

import java.util.Map;

import play.Logger;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.WriteResult;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.Project;
import models.laboratory.project.instance.ProjectUmbrella;
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
	
	public static void synchronizeProjectLists(Project project, ContextValidation contextValidation) {
		for (String code : project.projectUmbrellaCodes) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, ProjectUmbrella.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.in("projectCodes", project.code)))) {
				//add the value in the other list 
				WriteResult<ProjectUmbrella, String> w = MongoDBDAO.update(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, ProjectUmbrella.class, DBQuery.is("code", code), 
						DBUpdate.push("projectCodes", project.code));
				if(null != w.getError())
					contextValidation.addErrors("projectCode",ValidationConstants.ERROR_CODE_NOTUPDATED_MSG + ":" + w.getError(),  project.code, "ProjectUmbrella");
			}
		}
	}


	public static void synchronizeProjectLists(ProjectUmbrella projectUmbrella, ContextValidation contextValidation) {
		for (String code : projectUmbrella.projectCodes) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.in("projectUmbrellaCodes", projectUmbrella.code)))) {
				//add the value in the other list : the child project needs to be linked to his father! 
				WriteResult<Project, String> w = MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code", code), 
						DBUpdate.push("projectUmbrellaCodes", projectUmbrella.code));
				if(null != w.getError())
					contextValidation.addErrors("projectUmbrellaCode",ValidationConstants.ERROR_CODE_NOTUPDATED_MSG + ":" + w.getError(),  projectUmbrella.code, "Project");
			}
		}
	}

}
