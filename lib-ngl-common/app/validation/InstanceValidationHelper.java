package validation;

import validation.utils.ValidationConstants;
import  validation.utils.ValidationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.cea.ig.DBObject;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentInstance;
import models.laboratory.run.description.RunType;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.laboratory.sample.instance.Sample;
import models.laboratory.stock.instance.Stock;
import models.utils.InstanceConstants;

import validation.utils.BusinessValidationHelper;


public class InstanceValidationHelper {
	
	public static void validationProjectCodes(List<String> projectCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, projectCodes, "projectCodes",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}

	public static void validationSampleCodes(List<String> sampleCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateRequiredInstanceCodes(contextValidation, sampleCodes,"sampleCodes",Sample.class,InstanceConstants.SAMPLE_COLL_NAME,false);
	}

	public static void validationExperimentCodes(List<String> experimentCodes,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCodes(contextValidation, experimentCodes, "fromExperimentTypeCodes", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	public static void validationExperimentCode(String experimentCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, experimentCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}

	public static void validationSampleCode(String sampleCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, sampleCode, "sampleCode", Sample.class, InstanceConstants.SAMPLE_COLL_NAME, false);

	}

	public static void validationStockCode(String stockCode,ContextValidation contextValidation){
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, stockCode, "stockCode",Stock.class,InstanceConstants.STOCK_COLL_NAME ,false);

	}

	public static void validationContainerCode(String containerCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerCode, "containerCode", Container.class,InstanceConstants.CONTAINER_COLL_NAME);
	}
	
	

	public static void validationProjectCode(String projectCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, projectCode,"projectCode",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);
	}

	public static void validationContents(List<Content> contents,
			ContextValidation contextValidation) {
		String rootKeyName=null;
		if(ValidationHelper.required(contextValidation, contents, "contents")){
			
			for(int i=0; i<contents.size();i++){
				rootKeyName="content"+"["+i+"]";
				contextValidation.addKeyToRootKeyName(rootKeyName);
				contents.get(i).validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName(rootKeyName);
			}
		}
		
	}

	public static void validationComments(List<Comment> comments,
			ContextValidation contextValidation) {
		if(comments != null){
			for(Comment comment:comments){
				comment.validate(contextValidation);
			}
		}
	}

	public static void validationContainerSupport(ContainerSupport support,
			ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("containersupport");
		if(ValidationHelper.required(contextValidation, support, "containersupport")) {
			support.validate(contextValidation);
		}
		contextValidation.removeKeyFromRootKeyName("containersupport");
	}

	public static void validationReagentInstanceCode(String reagentInstanceCode, ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("reagent");
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
		contextValidation.removeKeyFromRootKeyName("reagent");
	}

	
	
	

}