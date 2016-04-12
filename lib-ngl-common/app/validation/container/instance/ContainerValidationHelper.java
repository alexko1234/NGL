package validation.container.instance;

import java.util.Arrays;
import java.util.Collections;
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
			
			String context = (String) contextValidation.getObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT);
			
			switch (context) {
			case "workflow":
				
				if("IW-P".equals(currentStateCode) && !nextStateCode.startsWith("A")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if(currentStateCode.startsWith("A") && !"IW-E".equals(nextStateCode)){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IW-E".equals(currentStateCode) && !"IU".equals(nextStateCode) && !"IW-D".equals(nextStateCode)){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IU".equals(currentStateCode) && !"IW-D".equals(nextStateCode)){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}
				
				break;
			case "controllers":
				
				if("IW-P".equals(currentStateCode) && 
						!nextStateCode.equals("UA") && !nextStateCode.equals("IS")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if(currentStateCode.startsWith("A") && 
						!nextStateCode.startsWith("A")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IW-D".equals(currentStateCode) && 
						!nextStateCode.equals("UA") && !nextStateCode.equals("IS") && !nextStateCode.startsWith("A")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IS".equals(currentStateCode) && 
						!nextStateCode.equals("UA") && !nextStateCode.equals("IW-P")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("UA".equals(currentStateCode) && 
						!nextStateCode.equals("IW-P") && !nextStateCode.equals("IS")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("N".equals(currentStateCode) && 
						!nextStateCode.equals("UA") && !nextStateCode.equals("IW-P") && !nextStateCode.startsWith("A")){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}else if("IW-E".equals(currentStateCode) || "IU".equals(currentStateCode)){
					contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
				}
				
				break;

			default:
				throw new RuntimeException("FIELD_STATE_CONTAINER_CONTEXT : "+context+" not manage !!!");
				
			}
			
			/* old validation lol
			if(("IS".equals(currentStateCode) || "UA".equals(currentStateCode)) && 
					(!nextStateCode.equals("IW-P") && !nextStateCode.equals("UA") && !nextStateCode.equals("IS")) ){
				contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );
			}
			*/
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

	public static void validateInputProcessCodes(Set<String> processCodes, ContextValidation contextValidation) {
		if(processCodes!=null && processCodes.size() > 0){
			for(String processCode: processCodes){
				BusinessValidationHelper.validateExistInstanceCode(contextValidation, processCode, "processCodes", Process.class, InstanceConstants.PROCESS_COLL_NAME); 
			}
		}
		
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if(stateCode.startsWith("A") || stateCode.startsWith("IW-E")){
			ValidationHelper.required(contextValidation, processCodes, "processCodes");
		}else if("IW-P".equals(stateCode) && CollectionUtils.isNotEmpty(processCodes)){
			contextValidation.addErrors("processCodes", "error.validation.container.inputProcesses.notnull");
		}		
	}
	@Deprecated
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
		if( CollectionUtils.isEmpty(container.processCodes) && container.state.code.startsWith("A") && !workflow ){
			contextValidation.addErrors("state.code",ValidationConstants.ERROR_BADSTATE_MSG,container.code );
		}
		if(CollectionUtils.isNotEmpty(container.processCodes) && container.state.code.equals("IW-P") && !workflow){
			contextValidation.addErrors("state.code",ValidationConstants.ERROR_BADSTATE_MSG,container.code );
		}
		contextValidation.addKeyToRootKeyName("state");
		CommonValidationHelper.validateStateCode(ObjectType.CODE.Container, container.state.code, contextValidation);
		contextValidation.removeKeyFromRootKeyName("state");
	}
}
