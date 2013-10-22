package validation;

import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentInstance;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import validation.utils.BusinessValidationHelper;


public class InstanceValidationHelper {
	@Deprecated
	public static void validationExperimentCodes(List<String> experimentCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCodes(contextValidation, experimentCodes, "fromExperimentTypeCodes", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	@Deprecated
	public static void validationSampleCode(String sampleCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, sampleCode, "sampleCode", Sample.class, InstanceConstants.SAMPLE_COLL_NAME, false);

	}

	public static void validationContainerCode(String containerCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerCode, "containerCode", Container.class,InstanceConstants.CONTAINER_COLL_NAME);
	}
	
	

	public static void validationProjectCode(String projectCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, projectCode,"projectCode",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}

	public static void validationComments(List<Comment> comments,
			ContextValidation contextValidation) {
		if(comments != null){
			for(Comment comment:comments){
				comment.validate(contextValidation);
			}
		}
	}

	public static void validationReagentInstanceCode(String reagentInstanceCode, ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("reagent");
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
		contextValidation.removeKeyFromRootKeyName("reagent");
	}

	
	
	

}