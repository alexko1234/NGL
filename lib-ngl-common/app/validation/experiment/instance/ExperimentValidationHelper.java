package validation.experiment.instance;

import static validation.utils.ValidationHelper.required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.reagent.instance.ReagentUsed;
import models.utils.InstanceConstants;
import models.laboratory.container.instance.ContainerSupport;
import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;

import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.container.instance.ContainerSupportValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ExperimentValidationHelper  extends CommonValidationHelper {

	public static void validationProtocol(String typeCode,String protocolCode,
			ContextValidation contextValidation)  {
		String stateCode = getObjectFromContext(STATE_CODE, String.class, contextValidation);
		if(!stateCode.equals("N")){
			if(required(contextValidation, protocolCode, "protocol")){				
					if(!MongoDBDAO.checkObjectExist(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, DBQuery.and(DBQuery.is("code",protocolCode), DBQuery.in("experimentTypeCodes", typeCode)))){
						contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, protocolCode);
					}				
			}
		}
	}

	public static void validateResolutionCodes(String typeCode,List<String> resoCodes,ContextValidation contextValidation){
		String stateCode= getObjectFromContext(STATE_CODE, String.class, contextValidation);
		if(stateCode.equals("F")){
			if(required(contextValidation, resoCodes, "resolution")){
				CommonValidationHelper.validateResolutionCodes(typeCode,resoCodes,contextValidation);
			}
		}else {
			CommonValidationHelper.validateResolutionCodes(typeCode,resoCodes,contextValidation);
		}
	}

	public static void validateState(String typeCode, State state, ContextValidation contextValidation){
		if(contextValidation.getObject(STATE_CODE)!=null){
			CommonValidationHelper.validateState(typeCode, state, contextValidation);
		}
	}

	public static void validationExperimentType(
			String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
				contextValidation.addKeyToRootKeyName("experimentProperties");
				ValidationHelper.validateProperties(contextValidation, properties, exType.getPropertiesDefinitionDefaultLevel(), true);
				contextValidation.removeKeyFromRootKeyName("experimentProperties");
		}
		
		
	}

	public static void validationExperimentCategoryCode(String categoryCode,
			ContextValidation contextValidation) {
		BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, categoryCode, "categoryCode", ExperimentCategory.find,false);
	}

	public static void validateNewState(Experiment experiment,
			ContextValidation contextValidation){
		ExperimentValidationHelper.validateResolutionCodes(experiment.typeCode,experiment.state.resolutionCodes,contextValidation);
		ExperimentValidationHelper.validationProtocol(experiment.typeCode,experiment.protocolCode,contextValidation);
		//Validation InstrumentUsedType
		ExperimentValidationHelper.validateInstrumentUsed(experiment.instrument,experiment.instrumentProperties,contextValidation);
		//TODO Validate Properties
	}

	public static void validateReagents(List<ReagentUsed> reagentsUsed,ContextValidation contextValidation) {
		if(reagentsUsed != null){
			for(ReagentUsed reagentUsed:reagentsUsed){
				reagentUsed.validate(contextValidation);
			}
		}
	}

	public static void validateAtomicTransfertMethodes(
			Map<Integer, AtomicTransfertMethod> atomicTransfertMethods,
			ContextValidation contextValidation) {
		String rootKeyName;
		for(int i=0;i<atomicTransfertMethods.size();i++){
			rootKeyName="atomictransfertmethod"+"["+i+"]";
			contextValidation.addKeyToRootKeyName(rootKeyName);
			atomicTransfertMethods.get(i).validate(contextValidation);
			contextValidation.removeKeyFromRootKeyName(rootKeyName);
		}
	}

	public static void validateInstrumentUsed(InstrumentUsed instrumentUsed,Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		if(ValidationHelper.required(contextValidation, instrumentUsed, "instrumentUsed")){
			InstrumentUsedType instrumentUsedType =BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, instrumentUsed.typeCode, "typeCode", InstrumentUsedType.find,true);

			String stateCode= getObjectFromContext(STATE_CODE, String.class, contextValidation);

			if(instrumentUsedType!=null){
					List<PropertyDefinition> listPropertyDefinitions=instrumentUsedType.getPropertiesDefinitionDefaultLevel();
					contextValidation.addKeyToRootKeyName("instrumentProperties");
					ValidationHelper.validateProperties(contextValidation, properties, listPropertyDefinitions, false);
					contextValidation.removeKeyFromRootKeyName("instrumentProperties");
					
					for(PropertyDefinition propertyDefinition:listPropertyDefinitions){			
						if(propertyDefinition.code.equals("containerSupportCode")){
							if(!stateCode.equals("F")){
								ContainerSupportValidationHelper.validateUniqueInstanceCode(contextValidation,properties.get("containerSupportCode").value.toString() , ContainerSupport.class, InstanceConstants.CONTAINER_SUPPORT_COLL_NAME);
							}
						}
					}
			}

			contextValidation.addKeyToRootKeyName("instrumentUsed");
			instrumentUsed.validate(contextValidation); 
			contextValidation.removeKeyFromRootKeyName("instrumentUsed");
			
		}
	}

	public static void validateRules(Experiment exp,ContextValidation contextValidation){
		ArrayList<Object> validationfacts = new ArrayList<Object>();
		validationfacts.add(exp);
		for(int i=0;i<exp.atomicTransfertMethods.size();i++){
			if(ManytoOneContainer.class.isInstance(exp.atomicTransfertMethods.get(i))){
				ManytoOneContainer atomic = (ManytoOneContainer) exp.atomicTransfertMethods.get(i);
				validationfacts.add(atomic);
			}
		}
		ExperimentValidationHelper.validateRules(validationfacts, contextValidation);
	}

	public static void validateInputOutputContainerSupport(Experiment experiment,
			ContextValidation contextValidation) {
		String stateCode = getObjectFromContext(STATE_CODE, String.class, contextValidation);

		if(CollectionUtils.isNotEmpty(experiment.outputContainerSupportCodes)){
			contextValidation.addKeyToRootKeyName("outputContainerSupportCodes");
			for(String supportCode:experiment.outputContainerSupportCodes){
				CommonValidationHelper.validateContainerSupportCode(supportCode, contextValidation);
			}
			contextValidation.removeKeyFromRootKeyName("outputContainerSupportCodes");
		}

		if(!stateCode.equals("N")){
			if(required(contextValidation, experiment.inputContainerSupportCodes, "inputContainerSupportCodes")){
				contextValidation.addKeyToRootKeyName("inputContainerSupportCodes");
				for(String supportCode:experiment.inputContainerSupportCodes){
					CommonValidationHelper.validateContainerSupportCode(supportCode, contextValidation);
				}
				contextValidation.removeKeyFromRootKeyName("inputContainerSupportCodes");
			}
		}

	}


}
