package validation.processes.instance;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessCategory;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.SampleOnInputContainer;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult.Sort;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class ProcessValidationHelper extends CommonValidationHelper {

	public static void validateProcessType(String typeCode,
			Map<String, PropertyValue> properties,
			ContextValidation contextValidation) {
		ProcessType processType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ProcessType.find,true);
		if(processType!=null){
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, processType.getPropertiesDefinitionDefaultLevel());
			contextValidation.removeKeyFromRootKeyName("properties");
		}
		
	}

	public static void validateProcessCategory(String categoryCode, ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ProcessCategory.find,false);
		
	}

	public static void validateCurrentExperimentTypeCode(String currentExperimentTypeCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateExistDescriptionCode(contextValidation, currentExperimentTypeCode, "currentExperimentTypeCode", ExperimentType.find,false);
	}

	public static void validateExperimentCodes(List<String> experimentCodes, ContextValidation contextValidation) {

		if(CollectionUtils.isNotEmpty(experimentCodes)){
			for(String expCode:experimentCodes){
				CommonValidationHelper.validateExperimenCode(expCode, contextValidation);
			}
		}
	}
	
	public static void validateStateCode(String stateCode,ContextValidation contextValidation){
		contextValidation.addKeyToRootKeyName("state");
		CommonValidationHelper.validateStateCode(ObjectType.CODE.Process, stateCode, contextValidation);
		contextValidation.removeKeyFromRootKeyName("state");
	}
	
	public static void validateSampleOnInputContainer(SampleOnInputContainer soic, ContextValidation contextValidation ){				
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		
		
		if(!"IW-C".equals(stateCode) && ValidationHelper.required(contextValidation, soic, "sampleOnInputContainer")){		
			contextValidation.addKeyToRootKeyName("sampleOnInputContainer");
			soic.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("sampleOnInputContainer");
		}
	}
	public static void validateContainerSupportCode (String containerSupportCode, ContextValidation contextValidation, String propertyName) {
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if(!"IW-C".equals(stateCode)){
			BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerSupportCode, propertyName, ContainerSupport.class,InstanceConstants.CONTAINER_SUPPORT_COLL_NAME);
		}
	}
	public static void validateContainerCode(String containerCode, ContextValidation contextValidation, String propertyName) {
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		
		if("N".equals(stateCode) && contextValidation.isCreationMode()){
			Container c = BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerCode, propertyName, Container.class,InstanceConstants.CONTAINER_COLL_NAME, true);
			if(null != c && !"IW-P".equals(c.state.code)){
				contextValidation.addErrors("inputContainerCode", ValidationConstants.ERROR_BADSTATE_MSG, c.state.code);
			}
		}else if("IW-C".equals(stateCode) && contextValidation.isUpdateMode() && containerCode != null){
			Container c = BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerCode, propertyName, Container.class,InstanceConstants.CONTAINER_COLL_NAME, true);
			if(null != c && !"IW-P".equals(c.state.code)){
				contextValidation.addErrors("inputContainerCode", ValidationConstants.ERROR_BADSTATE_MSG, c.state.code);
			}
		} else if(!"IW-C".equals(stateCode)){
			Container c = BusinessValidationHelper.validateRequiredInstanceCode(contextValidation, containerCode, propertyName, Container.class,InstanceConstants.CONTAINER_COLL_NAME, true);			
		}
	}
	
	public static void validateNextState(Process process, State nextState, ContextValidation contextValidation) {
		CommonValidationHelper.validateState(ObjectType.CODE.Process, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(process.state.code)){
			String nextStateCode = nextState.code;
			String currentStateCode = process.state.code;
			
			if("IP".equals(currentStateCode) && 
					!nextStateCode.equals("F")){
				contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );			
			}else if("F".equals(currentStateCode) && 
					!nextStateCode.equals("F")){
				contextValidation.addErrors("code",ValidationConstants.ERROR_BADSTATE_MSG, nextStateCode );			
			}else if("F".equals(nextStateCode)
					&& process.outputContainerCodes != null && process.outputContainerCodes.size() > 0){
				Container container = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", process.outputContainerCodes))
					.sort("traceInformation.creationDate", Sort.DESC).limit(1).toList().get(0);
				if(!Arrays.asList("UA","IS","IW-P").contains(container.state.code)){
					contextValidation.addErrors("outputContainerCodes."+container.code, ValidationConstants.ERROR_BADSTATE_MSG, container.state.code);
				}
			}else if("F".equals(nextStateCode)
					&& (process.outputContainerCodes == null || process.outputContainerCodes.size() == 0)){
				Container container = MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code", process.inputContainerCode));
				if(!Arrays.asList("UA","IS","IW-P").contains(container.state.code)){
					contextValidation.addErrors("outputContainerCodes."+container.code, ValidationConstants.ERROR_BADSTATE_MSG, container.state.code);
				}
			}
		}
				
	}
}
