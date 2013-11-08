package validation;

import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.reagent.instance.ReagentInstance;
import models.utils.InstanceConstants;
import validation.utils.BusinessValidationHelper;


public class InstanceValidationHelper {
	@Deprecated
	public static void validationExperimentCodes(List<String> experimentCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCodes(contextValidation, experimentCodes, "fromExperimentTypeCodes", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	@Deprecated
	public static void validationComments(List<Comment> comments,
			ContextValidation contextValidation) {
		if(comments != null){
			for(Comment comment:comments){
				comment.validate(contextValidation);
			}
		}
	}

	@Deprecated
	public static void validationReagentInstanceCode(String reagentInstanceCode, ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("reagent");
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
		contextValidation.removeKeyFromRootKeyName("reagent");
	}

	
	
	

}