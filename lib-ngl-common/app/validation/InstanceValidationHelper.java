package validation;

import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.vz.mongodb.jackson.DBQuery;
import fr.cea.ig.MongoDBDAO;
import play.data.validation.ValidationError;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.reagent.instance.ReagentInstance;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.laboratory.stock.instance.Stock;
import models.utils.InstanceConstants;

import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;
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
	
	public static void validationReagentInstanceCode(
			String reagentInstanceCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredInstanceCode(contextValidation.errors, reagentInstanceCode, "reagentInstanceCode", ReagentInstance.class,InstanceConstants.REAGENT_INSTANCE_COLL_NAME);
		}
	}
public static void validationLanes(List<Lane> lanes, ContextValidation contextValidation) {		
		//TODO number of lanes (depends of the type run and the mode incremental insert or full insert !!!)
		//TODO validate lane number
				
		if(null != lanes ) {
			String rootKeyName = getKey(contextValidation.rootKeyName, "lanes");
			int index = 0;			
			Set<Integer> laneNumbers = new TreeSet<Integer>();
			for (Lane lane : lanes) {
				contextValidation.rootKeyName = rootKeyName+"["+index+++"]";
				lane.validate(contextValidation);
				if(laneNumbers.contains(lane.number)){
					ConstraintsHelper.addErrors(contextValidation.errors, getKey(contextValidation.rootKeyName,"number"), InstanceConstants.ERROR_NOTUNIQUE,lane.number);
				}				
				laneNumbers.add(lane.number);				
			}
		}

	}
	
	
	public static void validationReadSets(List<ReadSet> readsets, ContextValidation contextValidation) {
		
		if(null != readsets) {
			String rootKeyName = getKey(contextValidation.rootKeyName,"readsets");
			int index = 0;
			for (ReadSet readSet : readsets) {
				contextValidation.rootKeyName = rootKeyName+"["+index+++"]";
				readSet.validate(contextValidation);
			}
		}
		
	}
	
	
	
	
	public static void validationFiles(List<File> files, ContextValidation contextValidation) {

		if(null != files) {	
			String rootKeyName = getKey(contextValidation.rootKeyName,"files");
			int index = 0;
			for (File file : files) {
				contextValidation.rootKeyName = rootKeyName+"["+index+++"]";
				file.validate(contextValidation);
			}
		}

	}

	
}