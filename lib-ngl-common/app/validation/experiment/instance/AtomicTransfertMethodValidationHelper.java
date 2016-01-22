package validation.experiment.instance;

import java.util.List;


import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class AtomicTransfertMethodValidationHelper extends CommonValidationHelper {

	
	public static void validateOneOutputContainer(List<OutputContainerUsed> outputContainerUseds, ContextValidation contextValidation){					
		if(null != outputContainerUseds && outputContainerUseds.size()>1){						
			contextValidation.addErrors("outputContainerUseds", ValidationConstants.ERROR_BADSIZEARRAY, outputContainerUseds.size(), "1");
		}
	}
	
	public static void validateOneInputContainer(List<InputContainerUsed> inputContainers,ContextValidation contextValidation){		
		if(inputContainers.size() != 1){							
			contextValidation.addErrors("inputContainerUseds", ValidationConstants.ERROR_BADSIZEARRAY, inputContainers.size(), "1");
		}			
	}

	public static void validateVoidOutputContainer(List<OutputContainerUsed> outputContainerUseds,	ContextValidation contextValidation) {
		if(null != outputContainerUseds && outputContainerUseds.size() != 0){							
			contextValidation.addErrors("outputContainerUseds", ValidationConstants.ERROR_BADSIZEARRAY, outputContainerUseds.size(), "0");
		}	
	}
	
	public static void validateInputContainers(ContextValidation contextValidation,	List<InputContainerUsed> inputContainerUseds) {
		if(ValidationHelper.required(contextValidation, inputContainerUseds, "inputContainerUseds")){
			int i = 0;
			double percentage = 0.0;
			for(InputContainerUsed icu: inputContainerUseds){
				contextValidation.addKeyToRootKeyName("inputContainerUseds["+i+"]");
				percentage += icu.percentage.doubleValue();
				icu.validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName("inputContainerUseds["+i+++"]");
			}
			if(!(percentage >=99.9 && percentage <=100)){
				contextValidation.addErrors("inputContainerUseds", "error.validationexp.percentperoutputcontainerdefault", percentage);
			}
		}
		
	}

	public static void validateOutputContainers(ContextValidation contextValidation, List<OutputContainerUsed> outputContainerUseds) {
		String stateCode = getObjectFromContext(FIELD_STATE_CODE, String.class, contextValidation);
		if("N".equals(stateCode) && null != outputContainerUseds){
			int i = 0;
			for(OutputContainerUsed icu: outputContainerUseds){
				contextValidation.addKeyToRootKeyName("outputContainerUseds["+i+"]");
				icu.validate(contextValidation);
				contextValidation.removeKeyFromRootKeyName("outputContainerUseds["+i+++"]");
			}
		}else if(!"N".equals(stateCode)){
			if(ValidationHelper.required(contextValidation, outputContainerUseds, "outputContainerUseds")){
				int i = 0;
				for(OutputContainerUsed icu: outputContainerUseds){
					contextValidation.addKeyToRootKeyName("outputContainerUseds["+i+"]");
					icu.validate(contextValidation);
					contextValidation.removeKeyFromRootKeyName("outputContainerUseds["+i+++"]");
				}
			}
		}
	}

	
}
