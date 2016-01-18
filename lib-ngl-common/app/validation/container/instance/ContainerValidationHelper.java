package validation.container.instance;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class ContainerValidationHelper extends CommonValidationHelper{

	public static void validateContainerCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ContainerCategory.find,false);

	}

	
	public static void validateExperimentCode(String experimentCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistInstanceCode(contextValidation, experimentCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
	}
	
	public static void validateContents(List<Content> contents, ContextValidation contextValidation) {
		
		if(ValidationHelper.required(contextValidation, contents, "contents")){
			Iterator<Content> iterator = contents.iterator();
			int i = 0;
			while (iterator.hasNext()){
				contextValidation.addKeyToRootKeyName("contents["+i+"]");
				iterator.next().validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName("contents["+i+"]");
				i++;
			}

			validateContentPercentageSum(contents, contextValidation);
		}
	}
	
	
	public static void validateState(State state, ContextValidation contextValidation) {
		if (ValidationHelper.required(contextValidation, state, "state")) {
			contextValidation.putObject(FIELD_OBJECT_TYPE_CODE, ObjectType.CODE.Container);
			contextValidation.addKeyToRootKeyName("state");
			state.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("state");
			contextValidation.removeObject(FIELD_OBJECT_TYPE_CODE);
		}		
	}
	
	public static void validateNextState(Container container, State nextState, ContextValidation contextValidation) {
		CommonValidationHelper.validateState(ObjectType.CODE.Container, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(container.state.code)){
			String nextStateCode = nextState.code;
			String currentStateCode = container.state.code;
			if(("IS".equals(currentStateCode) || "UA".equals(currentStateCode)) && 
					(!nextStateCode.equals("IW-P") && !nextStateCode.equals("UA") && !nextStateCode.equals("IS")) ){
				contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
			}
		}
				
	}
	
	
	@Deprecated
	public static void validateStateCode(String stateCode,ContextValidation contextValidation){
		contextValidation.addKeyToRootKeyName("state");
		CommonValidationHelper.validateStateCode(ObjectType.CODE.Container, stateCode, contextValidation);
		contextValidation.removeKeyFromRootKeyName("state");
	}
	
	//Check the sum of percentage of contents is 100
	public static void validateContentPercentageSum(List<Content> contents, ContextValidation contextValidation){
		Double percentageSum = 0.00;
		for(Content t:contents){			
			if(t.percentage!=null){
				percentageSum = percentageSum + t.percentage;
			}							
		}
		// NOTE do not test exactilty 100 because of floating values...
		if(!(Math.abs(100.00-percentageSum)<=0.40)){
			contextValidation.addKeyToRootKeyName("contents");
			contextValidation.addErrors("percentageSum", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, percentageSum);
			contextValidation.removeKeyFromRootKeyName("contents");			
		}
	}

	public static void validateContainerSupport(LocationOnContainerSupport support,
			ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, support, "support")) {
			contextValidation.addKeyToRootKeyName("support");
			support.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("support");
		}		
	}

	public static void validateInputProcessCodes(Set<String> inputProcessCodes, ContextValidation contextValidation) {
		if(inputProcessCodes!=null && inputProcessCodes.size() > 0){
			for(String processCode: inputProcessCodes){
				BusinessValidationHelper.validateExistInstanceCode(contextValidation, processCode, "inputProcessCodes", Process.class, InstanceConstants.PROCESS_COLL_NAME); 
			}
		}
		
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if(stateCode.startsWith("A") || stateCode.startsWith("IW-E")){
			ValidationHelper.required(contextValidation, inputProcessCodes, "inputProcessCodes");
		}else if("IW-P".equals(stateCode) && CollectionUtils.isNotEmpty(inputProcessCodes)){
			contextValidation.addErrors("inputProcessCodes", "error.validation.container.inputProcesses.notnull");
		}		
	}

	public static void validateProcessTypeCode(String processTypeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, processTypeCode, "processTypeCode", ProcessType.find);
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if(stateCode.startsWith("A") || stateCode.startsWith("IW-E")){
			ValidationHelper.required(contextValidation, processTypeCode, "processTypeCode");
		}else if("IW-P".equals(stateCode) && null != processTypeCode){
			contextValidation.addErrors("processTypeCode", "error.validation.container.inputProcesses.notnull");
		}	
	}
	
	@Deprecated
	public static void validateStateCode(Container container,ContextValidation contextValidation) {
		
		boolean workflow=false;
		if(contextValidation.getObject("workflow")!=null){
			workflow=true;
		}
//		if(( CollectionUtils.isEmpty(container.inputProcessCodes) || !container.state.historical.get(container.state.historical.size() - 2).code.equals("UA")) && container.state.code.equals("A") ){
		if( CollectionUtils.isEmpty(container.inputProcessCodes) && container.state.code.startsWith("A") && !workflow ){
			contextValidation.addErrors("state.code",ValidationConstants.ERROR_BADSTATE_MSG,container.code );
		}
		if(CollectionUtils.isNotEmpty(container.inputProcessCodes) && container.state.code.equals("IW-P") && !workflow){
			contextValidation.addErrors("state.code",ValidationConstants.ERROR_BADSTATE_MSG,container.code );
		}
		contextValidation.addKeyToRootKeyName("state");
		CommonValidationHelper.validateStateCode(ObjectType.CODE.Container, container.state.code, contextValidation);
		contextValidation.removeKeyFromRootKeyName("state");
	}
}
