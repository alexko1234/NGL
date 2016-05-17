package models.laboratory.experiment.instance;

import java.util.ArrayList;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.LocationOnContainerSupport;
import models.utils.CodeHelper;
import models.utils.dao.DAOException;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import validation.ContextValidation;
import validation.experiment.instance.AtomicTransfertMethodValidationHelper;

public class OneToManyContainer extends AtomicTransfertMethod {

	public int outputNumber;
	
	public OneToManyContainer(){
		super();
	}
	
	@Override
	public void updateOutputCodeIfNeeded(ContainerSupportCategory outputCsc, String supportCode) {
		//case tube :one support for each output
		if(outputCsc.nbLine.compareTo(Integer.valueOf(1)) == 0 && outputCsc.nbColumn.compareTo(Integer.valueOf(1)) == 0){
			outputContainerUseds.forEach((OutputContainerUsed ocu) -> {
					if(null == ocu.locationOnContainerSupport.code){
						String newSupportCode = CodeHelper.getInstance().generateContainerSupportCode();
						ocu.locationOnContainerSupport.code = newSupportCode;
						ocu.code = newSupportCode;
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
					ocu.code = supportCode+"_"+ocu.locationOnContainerSupport.line+ocu.locationOnContainerSupport.column;
				}
			}
		);
		}
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		super.validate(contextValidation);
		AtomicTransfertMethodValidationHelper.validateOneInputContainer(inputContainerUseds, contextValidation);
		AtomicTransfertMethodValidationHelper.validateOutputContainers(contextValidation, outputContainerUseds);
	}
	

	
}
