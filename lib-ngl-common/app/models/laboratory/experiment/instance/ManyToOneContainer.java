package models.laboratory.experiment.instance;

import models.laboratory.container.description.ContainerSupportCategory;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;

public class ManyToOneContainer extends AtomicTransfertMethod{


	public ManyToOneContainer(){
		super();
	}

	@Override
	public void updateOutputCodeIfNeeded(ContainerSupportCategory outputCsc, String supportCode) {
		//case tube :one support for each atm
		if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) == 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) == 0){
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
					if(null == ocu.locationOnContainerSupport.code){
						ocu.locationOnContainerSupport.code = supportCode;
						ocu.code = supportCode;
					}
				}
			);
		}else if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) > 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) == 0){
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
				if(null == ocu.locationOnContainerSupport.code){
					ocu.locationOnContainerSupport.code = supportCode;
					ocu.code = supportCode+"_"+ocu.locationOnContainerSupport.line;
				}
			}
		);
		}else if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) > 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) > 0){
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
				if(null == ocu.locationOnContainerSupport.code){
					ocu.locationOnContainerSupport.code = supportCode;
					ocu.code = supportCode+"_"+ocu.locationOnContainerSupport.line+"_"+ocu.locationOnContainerSupport.column;
				}
			}
		);
		}
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		AtomicTransfertMethodValidationHelper.validateOneOutputContainer(outputContainerUseds, contextValidation);
		AtomicTransfertMethodValidationHelper.validateOutputContainers(contextValidation, outputContainerUseds);
	}




}
