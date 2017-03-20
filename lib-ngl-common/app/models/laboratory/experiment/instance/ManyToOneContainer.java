package models.laboratory.experiment.instance;

import models.laboratory.container.description.ContainerSupportCategory;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;

public class ManyToOneContainer extends AtomicTransfertMethod{


	public ManyToOneContainer(){
		super();
	}

	//GA 22/06/2016 gestion des cas ou le locationOnContainerSupport.code n'est pas null
	@Override
	public void updateOutputCodeIfNeeded(ContainerSupportCategory outputCsc, String supportCode) {
		//case tube :one support for each atm
		if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) == 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) == 0){
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
					if(null == ocu.locationOnContainerSupport.code && null != supportCode){
						ocu.locationOnContainerSupport.code = supportCode;
						ocu.code = supportCode;
					}else if(null != ocu.locationOnContainerSupport.code 
							&& (null == ocu.code || !ocu.code.equals(ocu.locationOnContainerSupport.code))){
						ocu.code = ocu.locationOnContainerSupport.code;
					}
			});
		}else if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) > 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) == 0){
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
				if(null == ocu.locationOnContainerSupport.code && null != supportCode){
					ocu.locationOnContainerSupport.code = supportCode;
					ocu.code = supportCode+"_"+ocu.locationOnContainerSupport.line;
				}else if(null != ocu.locationOnContainerSupport.code && null != ocu.locationOnContainerSupport.line
						&& (null == ocu.code || !ocu.code.equals(ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.line))){
					ocu.code = ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.line;
				}
			});
		}else if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) == 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) > 0){
			// case strip-8
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
				if(null == ocu.locationOnContainerSupport.code && null != supportCode){
					ocu.locationOnContainerSupport.code = supportCode;
					ocu.code = supportCode+"_"+ocu.locationOnContainerSupport.column;
				}else if(null != ocu.locationOnContainerSupport.code && null != ocu.locationOnContainerSupport.column
						&& (null == ocu.code || !ocu.code.equals(ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.column))){
					ocu.code = ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.column;
				}
			});
		}else if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) > 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) > 0){
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
				if(null == ocu.locationOnContainerSupport.code && null != supportCode){
					ocu.locationOnContainerSupport.code = supportCode;
					ocu.code = supportCode+"_"+ocu.locationOnContainerSupport.line+ocu.locationOnContainerSupport.column;
				}else if(null != ocu.locationOnContainerSupport.code && null != ocu.locationOnContainerSupport.line && null != ocu.locationOnContainerSupport.column
						&& (null == ocu.code || !ocu.code.equals(ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.line+ocu.locationOnContainerSupport.column))){
					ocu.code = ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.line+ocu.locationOnContainerSupport.column;
				}
			});
		}
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		AtomicTransfertMethodValidationHelper.validateOneOutputContainer(outputContainerUseds, contextValidation);
		AtomicTransfertMethodValidationHelper.validateOutputContainers(contextValidation, outputContainerUseds);
	}




}
