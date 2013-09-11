package validation;

import validation.utils.ValidationConstants;
import  validation.utils.ValidationHelper;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import models.laboratory.common.instance.Comment;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentInstance;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
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

		if(ValidationHelper.required(contextValidation, contents, "container.contents")){
			for(Content content :contents){
				content.validate(contextValidation);
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
		contextValidation.addKeyToRootKeyName("container.support");
		if(ValidationHelper.required(contextValidation, support, "container.support")) {
			support.validate(contextValidation);
		}
		
	}
	
	public static void validationReagentInstanceCode(String reagentInstanceCode, ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("reagent");
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
		contextValidation.removeKeyFromRootKeyName("reagent");
	}
	
	public static void validationLanes(List<Lane> lanes, ContextValidation contextValidation) {		
		//TODO number of lanes (depends of the type run and the mode incremental insert or full insert !!!)
		//TODO validate lane number
		if(null != lanes ) {
			int index = 0;			
			Set<Integer> laneNumbers = new TreeSet<Integer>();
			for (Lane lane : lanes) {
				contextValidation.addKeyToRootKeyName("lanes"+"["+index+"]");
				lane.validate(contextValidation);
				if(laneNumbers.contains(lane.number)){
					contextValidation.addErrors("number", ValidationConstants.ERROR_NOTUNIQUE,lane.number);
				}				
				laneNumbers.add(lane.number);			
				contextValidation.removeKeyFromRootKeyName("lanes"+"["+index+"]");
				index++;
			}
		}

	}	
	
	public static void validationReadSets(List<ReadSet> readsets, ContextValidation contextValidation) {
		if(null != readsets) {
			int index = 0;
			for (ReadSet readSet : readsets) {
				contextValidation.addKeyToRootKeyName("readsets["+index+"]"); 
				readSet.validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName("readsets["+index+"]");
				index++;
			}
		}
		
	}
	
	public static void validationFiles(List<File> files, ContextValidation contextValidation) {
		if(null != files) {	
			int index = 0;
			for (File file : files) {
				contextValidation.addKeyToRootKeyName("files["+index+"]"); 
				file.validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName("files["+index+"]");
				index++;
			}
		}

	}

}