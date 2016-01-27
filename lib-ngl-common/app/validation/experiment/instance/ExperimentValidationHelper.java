package validation.experiment.instance;

import static validation.utils.ValidationHelper.required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.reagent.instance.ReagentUsed;
import models.utils.InstanceConstants;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;

import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ExperimentValidationHelper  extends CommonValidationHelper {

	public static void validationProtocoleCode(String typeCode, String protocolCode,
			ContextValidation contextValidation)  {
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if(!stateCode.equals("N")){
			if(required(contextValidation, protocolCode, "protocolCode")){				
				if(!MongoDBDAO.checkObjectExist(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, DBQuery.and(DBQuery.is("code",protocolCode), DBQuery.in("experimentTypeCodes", typeCode)))){
					contextValidation.addErrors("protocolCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, protocolCode);
				}				
			}
		}else if(StringUtils.isNotBlank(protocolCode)){
			if(!MongoDBDAO.checkObjectExist(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, DBQuery.and(DBQuery.is("code",protocolCode), DBQuery.in("experimentTypeCodes", typeCode)))){
				contextValidation.addErrors("protocolCode", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, protocolCode);
			}
		}
	}
	
	public static void validationExperimentType(String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			contextValidation.addKeyToRootKeyName("experimentProperties");
			ValidationHelper.validateProperties(contextValidation, properties, exType.getPropertiesDefinitionDefaultLevel(), true);
			contextValidation.removeKeyFromRootKeyName("experimentProperties");
		}
		
		
	}

	public static void validateState(String typeCode, State state, ContextValidation contextValidation) {
		if (ValidationHelper.required(contextValidation, state, "state")) {
			contextValidation.putObject(FIELD_TYPE_CODE, typeCode);
			contextValidation.addKeyToRootKeyName("state");
			state.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("state");
			contextValidation.removeObject(FIELD_TYPE_CODE);
		}		
	}
	
	public static void validateStatus(String typeCode, Valuation status, ContextValidation contextValidation) {
		if (ValidationHelper.required(contextValidation, status, "status")) {
			contextValidation.putObject(FIELD_TYPE_CODE, typeCode);
			contextValidation.addKeyToRootKeyName("status");
			status.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("status");
			contextValidation.removeObject(FIELD_TYPE_CODE);
			
			String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);			
			if("F".equals(stateCode) && TBoolean.UNSET.equals(status.valid)){
				contextValidation.addErrors("status", "error.validationexp.status.empty");
			}
			
		}	
		
	}

	
	public static void validationExperimentCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ExperimentCategory.find,false);
	}

	
	public static void validateReagents(List<ReagentUsed> reagentsUsed,ContextValidation contextValidation) {
		if(reagentsUsed != null){
			for(ReagentUsed reagentUsed:reagentsUsed){
				reagentUsed.validate(contextValidation);
			}
		}
	}

	public static void validateAtomicTransfertMethods(String expTypeCode, InstrumentUsed instrument, List <AtomicTransfertMethod> atomicTransfertMethods, ContextValidation contextValidation) {
		String rootKeyName;
		contextValidation.putObject(FIELD_TYPE_CODE , expTypeCode);
		contextValidation.putObject(FIELD_INST_USED , instrument);
		for(int i=0;i<atomicTransfertMethods.size();i++){
			rootKeyName="atomictransfertmethod"+"["+i+"]";
			if(atomicTransfertMethods.get(i)!=null){
				contextValidation.addKeyToRootKeyName(rootKeyName);
				atomicTransfertMethods.get(i).validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName(rootKeyName);
			}else{
				contextValidation.addErrors(rootKeyName, "error.validationexp.atomicTransfertMethod.null");
			}
			
		}
		
		//TODO GA validate number of ATM against SupportContainerCategory nbLine and nbColumn
		contextValidation.removeObject(FIELD_TYPE_CODE);
		contextValidation.removeObject(FIELD_INST_USED);
	}

	public static void validateInstrumentUsed(InstrumentUsed instrumentUsed, Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, instrumentUsed, "instrumentUsed")){
			contextValidation.addKeyToRootKeyName("instrumentUsed");
			instrumentUsed.validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName("instrumentUsed");
			
			InstrumentUsedType instrumentUsedType = BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, instrumentUsed.typeCode, "typeCode", InstrumentUsedType.find,true);
			if(instrumentUsedType!=null){
				contextValidation.addKeyToRootKeyName("instrumentProperties");
				ValidationHelper.validateProperties(contextValidation, properties, instrumentUsedType.getPropertiesDefinitionDefaultLevel(), false);
				contextValidation.removeKeyFromRootKeyName("instrumentProperties");
			}
		}
	}
	
	
	public static void validateRules(Experiment exp,ContextValidation contextValidation){
		ArrayList<Object> validationfacts = new ArrayList<Object>();
		validationfacts.add(exp);
		exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> validationfacts.add(atm));
		
		validateRules(validationfacts, contextValidation);
	}

	public static void validateInputContainerSupport(Set<String> inputContainerSupportCodes,
			List<InputContainerUsed> allInputContainers,
			ContextValidation contextValidation) {
		
		if(required(contextValidation, inputContainerSupportCodes, "inputContainerSupportCodes")){
			Set<String> allInputCode = allInputContainers.stream().map((InputContainerUsed i) -> i.locationOnContainerSupport.code).collect(Collectors.toSet());
			if(!allInputCode.equals(inputContainerSupportCodes)){
				contextValidation.addErrors("inputContainerSupportCodes", "error.inputContainerSupportCodes.notequals.allinputContainerSupportCode");
			}
		}
	}

	
	
	public static void validateOutputContainerSupport(Set<String> outputContainerSupportCodes,
			List<OutputContainerUsed> allOutputContainers,
			ContextValidation contextValidation) {
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		
		if(!"N".equals(stateCode)){
			if(required(contextValidation, outputContainerSupportCodes, "outputContainerSupportCodes")){
				Set<String> allInputCode = allOutputContainers.stream().map((OutputContainerUsed i) -> i.locationOnContainerSupport.code).collect(Collectors.toSet());
				if(!allInputCode.equals(outputContainerSupportCodes)){
					contextValidation.addErrors("outputContainerSupportCodes", "error.validationexp.outputContainerSupportCodes");
				}
			}
		}
		
	}

	public static void validateComments(List<Comment> comments, ContextValidation contextValidation){
		if(null != comments && comments.size() > 0){
			for(int i=0;i<comments.size();i++){
				String rootKeyName="comments"+"["+i+"]";
				if(comments.get(i)!=null){
					contextValidation.addKeyToRootKeyName(rootKeyName);
					comments.get(i).validate(contextValidation);
					contextValidation.removeKeyFromRootKeyName(rootKeyName);
				}else{
					contextValidation.addErrors(rootKeyName, "error.validationexp.comments.null");
				}
				
			}
		}
	}
	
}
