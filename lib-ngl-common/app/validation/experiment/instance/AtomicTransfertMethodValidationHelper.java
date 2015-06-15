package validation.experiment.instance;

import java.util.List;

import models.laboratory.experiment.instance.ContainerUsed;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;

public class AtomicTransfertMethodValidationHelper extends CommonValidationHelper {

	
	public static void validateOneOutputContainer(List<ContainerUsed> outputContainers,ContextValidation contextValidation){					
			if(outputContainers.size()>1){						
				contextValidation.addErrors("outputContainerUseds", ValidationConstants.ERROR_BADSIZEARRAY, outputContainers.size(), "1");
			}
	}
	
	public static void validateOneInputContainer(List<ContainerUsed> inputContainers,ContextValidation contextValidation){		
			if(inputContainers.size()>1){							
				contextValidation.addErrors("atomictransfertmethod", ValidationConstants.ERROR_BADSIZEARRAY, inputContainers.size(), "1");
			}			
	}
}
