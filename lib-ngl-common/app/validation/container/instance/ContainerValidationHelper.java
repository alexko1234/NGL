package validation.container.instance;

import java.util.List;

import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

public class ContainerValidationHelper extends CommonValidationHelper{

	public static void validateContainerCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerCategory.find,false);

	}

	public static void validateProcessTypeCode(String typeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, typeCode,"processTypeCode", ProcessType.find);

	}

	public static void validateExperimentTypeCodes(
			List<String> experimentTypeCodes, ContextValidation contextValidation) {
		if(experimentTypeCodes!=null){
			for(String s: experimentTypeCodes){
				BusinessValidationHelper.validateExistDescriptionCode(contextValidation, s, "experimentTypeCode", ExperimentType.find);
			}
		}
	}

	public static void validateExperimentCode(String experimentCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, experimentCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	
	public static void validateContents(List<Content> contents, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, contents, "contents")){
			contextValidation.addKeyToRootKeyName("contents");
			for(Content t:contents){
					t.validate(contextValidation);					
			}
			contextValidation.removeKeyFromRootKeyName("contents");
		}
	}

	public static void validateContainerSupport(LocationOnContainerSupport support,
			ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("containersupport");
		if(ValidationHelper.required(contextValidation, support, "containersupport")) {
			support.validate(contextValidation);
		}
		contextValidation.removeKeyFromRootKeyName("containersupport");
	}
	


}
