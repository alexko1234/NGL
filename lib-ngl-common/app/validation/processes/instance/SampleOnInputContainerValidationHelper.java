package validation.processes.instance;

import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.processes.instance.SampleOnInputContainer;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class SampleOnInputContainerValidationHelper extends CommonValidationHelper {
	
	public static void validateExistSampleCode(SampleOnInputContainer soic,ContextValidation contextValidation){		
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, soic.sampleCode, Sample.class, InstanceConstants.SAMPLE_COLL_NAME);		
	}
	
	public static void validateSampleCategoryCode(SampleOnInputContainer soic,ContextValidation contextValidation){		
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, soic.sampleCategoryCode, "sampleOnInputContainer.sampleCategoryCode", SampleCategory.find,false);		
	}
	
	public static void validateContainerSupportCode(SampleOnInputContainer soic,ContextValidation contextValidation){
		if (ValidationHelper.required(contextValidation, soic.containerSupportCode, "sampleOnInputContainer")) {
			if (! MongoDBDAO.checkObjectExist(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,  DBQuery.is("code", soic.containerSupportCode))) {
				contextValidation.addErrors("sampleOnInputContainer.containerSupportCode", ValidationConstants.ERROR_CODE_NOTEXISTS_MSG, soic.containerSupportCode);
			}
		}		 
	}
	
	public static void validateExistContainerCode(SampleOnInputContainer soic,ContextValidation contextValidation){		
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, soic.containerCode, Container.class, InstanceConstants.CONTAINER_COLL_NAME);
	}

}
