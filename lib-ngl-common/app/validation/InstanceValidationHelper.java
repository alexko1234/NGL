package validation;

import static validation.utils.ConstraintsHelper.required;

import java.util.List;


import models.laboratory.common.instance.Comment;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.laboratory.stock.instance.Stock;
import models.utils.InstanceConstants;

import validation.utils.BusinessValidationHelper;
import validation.utils.ContextValidation;

public class InstanceValidationHelper {


	public static void validationProjectCodes(List<String> projectCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation.errors, projectCodes, "projectCodes",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}

	public static void validationSampleCodes(List<String> sampleCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation.errors, sampleCodes,"sampleCodes",Sample.class,InstanceConstants.SAMPLE_COLL_NAME,false);
	}

	
	public static void validationExperimentCodes(List<String> experimentCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCodes(contextValidation.errors, experimentCodes, "fromExperimentTypeCodes", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}


	public static void validationExperimentCode(String experimentCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation.errors, experimentCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);		
	}

	

	public static void validationSampleCode(String sampleCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation.errors, sampleCode, "sampleCode", Sample.class, InstanceConstants.SAMPLE_COLL_NAME, false);
		
	}

	public static void validationStockCode(String stockCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCode(contextValidation.errors, stockCode, "stockCode",Stock.class,InstanceConstants.STOCK_COLL_NAME ,false);

	}

	public static void validationContainerCode(String containerCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation.errors, containerCode, "containerCode", Container.class,InstanceConstants.CONTAINER_COLL_NAME);
	}

	public static void validationProjectCode(String projectCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation.errors, projectCode,"projectCode",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}

	public static void validationContents(List<Content> contents,
			ContextValidation contextValidation) {

		if(required(contextValidation.errors, contents, "container.contents")){
			for(Content content :contents){
				content.validate(contextValidation);
			}
		}
	}

	public static void validationComments(List<Comment> comments,
			ContextValidation contextValidation) {
		for(Comment comment:comments){
			comment.validate(contextValidation);
		}
	}

	public static void validationContainerSupport(ContainerSupport support,
			ContextValidation contextValidation) {
		if(required(contextValidation.errors,support,"container.support")) {
			support.validate(contextValidation);
		}
		
	}
	
}
