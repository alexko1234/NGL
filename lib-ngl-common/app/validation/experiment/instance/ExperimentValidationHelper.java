package validation.experiment.instance;

import static validation.utils.ValidationHelper.required;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.reagent.instance.ReagentUsed;
import models.utils.dao.DAOException;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class ExperimentValidationHelper  extends CommonValidationHelper {

	public static void validationProtocol(String typeCode,String protocolCode,
			 ContextValidation contextValidation)  {
		if(contextValidation.getObject("stateCode")!="N" && contextValidation.getObject("stateCode")!=null){
					if(required(contextValidation, protocolCode, "protocol")){
						try {
							if(!Protocol.find.isCodeExistForTypeCode(protocolCode, typeCode) ){
								contextValidation.addErrors("code", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, protocolCode);
							}
						} catch (DAOException e) {
							throw new RuntimeException(e);
						}
					}
		}
	}
	
	public static void validateResolutionCodes(String typeCode,List<String> resoCodes,ContextValidation contextValidation){
		if(contextValidation.getObject("stateCode")!=null && contextValidation.getObject("stateCode").equals("F")){
			if(required(contextValidation, resoCodes, "resolution")){
				CommonValidationHelper.validateResolutionCodes(typeCode,resoCodes,contextValidation);
			}
		}else {
			CommonValidationHelper.validateResolutionCodes(typeCode,resoCodes,contextValidation);
		}
	}
	
	
	public static void validationExperimentType(
			String typeCode, Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		ExperimentType exType=BusinessValidationHelper.validateRequiredDescriptionCode(contextValidation, typeCode, "typeCode", ExperimentType.find,true);
		if(exType!=null){
			contextValidation.addKeyToRootKeyName("properties");
			ValidationHelper.validateProperties(contextValidation, properties, exType.getPropertiesDefinitionDefaultLevel(), true);
			contextValidation.removeKeyFromRootKeyName("properties");
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
		ExperimentValidationHelper.validateInstrumentUsed(experiment.typeCode,experiment.instrument,experiment.instrumentProperties,contextValidation);
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

	public static void validateInstrumentUsed(String typeCode,
			InstrumentUsed instrumentUsed,Map<String,PropertyValue> properties, ContextValidation contextValidation) {
		if(contextValidation.getObject("stateCode")!="N" && contextValidation.getObject("stateCode")!=null){
			if(ValidationHelper.required(contextValidation, instrumentUsed, "instrumentUsed")){
				contextValidation.addKeyToRootKeyName("instrumentUsed");
				instrumentUsed.validate(contextValidation); 
				contextValidation.removeKeyFromRootKeyName("instrumentUsed");
			}}
		
	}

	
}
