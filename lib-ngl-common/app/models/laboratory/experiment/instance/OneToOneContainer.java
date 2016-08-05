package models.laboratory.experiment.instance;

import play.Logger;
import models.laboratory.container.description.ContainerSupportCategory;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;

public class OneToOneContainer extends AtomicTransfertMethod{

	public OneToOneContainer(){
		super();
	}

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
				if (null == ocu.locationOnContainerSupport.code && null != supportCode) {
					ocu.locationOnContainerSupport.code = supportCode;
					ocu.code = supportCode+"_"+ocu.locationOnContainerSupport.line;
				}else if(null != ocu.locationOnContainerSupport.code && null != ocu.locationOnContainerSupport.line
						&& (null == ocu.code || !ocu.code.equals(ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.line))){
					ocu.code = ocu.locationOnContainerSupport.code+"_"+ocu.locationOnContainerSupport.line;
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
		long t0 = System.currentTimeMillis();
		super.validate(contextValidation);
		long t1 = System.currentTimeMillis();
		AtomicTransfertMethodValidationHelper.validateOneInputContainer(inputContainerUseds, contextValidation);
		long t2 = System.currentTimeMillis();
		AtomicTransfertMethodValidationHelper.validateOneOutputContainer(outputContainerUseds, contextValidation);
		long t3 = System.currentTimeMillis();
		AtomicTransfertMethodValidationHelper.validateOutputContainers(contextValidation, outputContainerUseds);
		long t4 = System.currentTimeMillis();
		/*
		Logger.debug("ATMOneTOOne validate \n "
				+"1 = "+(t1-t0)+" ms\n"
			//	+"2 = "+(t2-t1)+" ms\n"
			//	+"3 = "+(t3-t2)+" ms\n"
				+"4 = "+(t4-t3)+" ms\n"
		//		+"5 = "+(t2-t0)+" ms\n"
				);
		*/
	}

}
