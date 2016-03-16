package models.laboratory.experiment.instance;

import play.Logger;
import models.laboratory.common.description.Level;
import validation.ContextValidation;
import validation.experiment.instance.ContainerUsedValidationHelper;

public class OutputContainerUsed extends AbstractContainerUsed{
		
	public OutputContainerUsed() {
		super();
		
	}
	
	public OutputContainerUsed(String code) {
		super(code);
		
	}
	
	@Override
	public void validate(ContextValidation contextValidation) {
		long t0 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateOutputContainerCode(code, contextValidation);
		
		long t1 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateLocationOnSupportOnContainer(locationOnContainerSupport, contextValidation);
		
		long t2 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateOutputContainerCategoryCode(categoryCode, contextValidation);
		
		long t3 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateOutputContents(contents, contextValidation);
		
		long t4 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateVolume(volume, contextValidation);
		
		long t5 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateConcentration(concentration, contextValidation);
		
		long t6 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateQuantity(quantity, contextValidation);
		
		long t7 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateExperimentProperties(experimentProperties, Level.CODE.ContainerOut, contextValidation);
		
		long t8 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateInstrumentProperties(instrumentProperties, Level.CODE.ContainerOut, contextValidation);
		
		long t9 = System.currentTimeMillis();
		/*
		Logger.debug("OutputContainerUsed validate \n "
				+"1 = "+(t1-t0)+" ms\n"
				+"2 = "+(t2-t1)+" ms\n"
				+"3 = "+(t3-t2)+" ms\n"
				+"4 = "+(t4-t3)+" ms\n"
				+"5 = "+(t5-t4)+" ms\n"
				+"6 = "+(t6-t5)+" ms\n"
				+"7 = "+(t7-t6)+" ms\n"
				+"8 = "+(t8-t7)+" ms\n"
				+"9 = "+(t9-t8)+" ms\n"				
				+"10 = "+(t9-t0)+" ms\n"				
				);
		*/		
	}

	
}
