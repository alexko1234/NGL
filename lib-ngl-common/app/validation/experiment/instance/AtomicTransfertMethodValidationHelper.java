package validation.experiment.instance;

import java.util.List;

import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class AtomicTransfertMethodValidationHelper extends CommonValidationHelper {

	
	
	public static void validateInputContainers(ContextValidation contextValidation,	List<ContainerUsed> inputContainers) {
		if(ValidationHelper.required(contextValidation, inputContainers, "inputContainer")){
			inputContainers.forEach((ContainerUsed c)-> c.validate(contextValidation));
		}
	}
	
	public static void validateOutputContainers(ContextValidation contextValidation, List<ContainerUsed> outputContainers) {
		String stateCode = getObjectFromContext(STATE_CODE, String.class, contextValidation);
		if("N".equals(stateCode) && null != outputContainers){
			outputContainers.forEach((ContainerUsed c)-> c.validate(contextValidation));
		}else{
			if(ValidationHelper.required(contextValidation, outputContainers, "outputContainers")){
				outputContainers.forEach((ContainerUsed c)-> c.validate(contextValidation));
			}
		}
		
	}
	
	
	public static void validateOneOutputContainer(List<OutputContainerUsed> outputContainers,ContextValidation contextValidation){					
		if(outputContainers!=null && outputContainers.size()>1){						
			contextValidation.addErrors("outputContainerUseds", ValidationConstants.ERROR_BADSIZEARRAY, outputContainers.size(), "1");
		}
	}
	
	public static void validateOneInputContainer(List<InputContainerUsed> inputContainers,ContextValidation contextValidation){		
			if(inputContainers.size()>1){							
				contextValidation.addErrors("atomictransfertmethod", ValidationConstants.ERROR_BADSIZEARRAY, inputContainers.size(), "1");
			}			
	}

}
