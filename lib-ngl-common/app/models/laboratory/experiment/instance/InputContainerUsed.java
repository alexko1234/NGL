package models.laboratory.experiment.instance;

import java.util.Set;

import play.Logger;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import validation.experiment.instance.ContainerUsedValidationHelper;

public class InputContainerUsed extends AbstractContainerUsed {
	
	
	public Double percentage; //percentage of input in the final output
	
	public Set<String> projectCodes; 
	public Set<String> sampleCodes; 
	public Set<String> fromTransformationTypeCodes;
	public Set<String> fromTransformationCodes;
	public Set<String> processTypeCodes;
	public Set<String> processCodes;
	
	public Valuation valuation; //only on input because qc are to-void experiment !
	
	//keep for some html page pool or flowcell
	public State state;
	
	public InputContainerUsed() {
		super();
		
	}
	
	public InputContainerUsed(String code) {
		super(code);
		
	}

	
	@Override
	public void validate(ContextValidation contextValidation) {
		long t0 = System.currentTimeMillis();
		Container container = ContainerUsedValidationHelper.validateExistInstanceCode(contextValidation, code, Container.class, InstanceConstants.CONTAINER_COLL_NAME, true);
		
		long t1 = System.currentTimeMillis();
		ContainerUsedValidationHelper.compareInputContainerWithContainer(this, container, contextValidation);
		
		long t2 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateInputContainerCategoryCode(categoryCode, contextValidation);
		
		long t3 = System.currentTimeMillis();
		ContainerValidationHelper.validateVolume(volume, contextValidation);
		
		long t4 = System.currentTimeMillis();
		ContainerValidationHelper.validateConcentration(concentration, contextValidation);
		
		long t5 = System.currentTimeMillis();
		ContainerValidationHelper.validateQuantity(quantity, contextValidation);
		
		long t6 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validatePercentage(percentage, contextValidation);
		
		ContainerValidationHelper.validateSize(size, contextValidation);
		
		long t7 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateExperimentProperties(experimentProperties, Level.CODE.ContainerIn, contextValidation);
		
		long t8 = System.currentTimeMillis();
		ContainerUsedValidationHelper.validateInstrumentProperties(instrumentProperties, Level.CODE.ContainerIn, contextValidation);
		
		long t9 = System.currentTimeMillis();
		/*
		Logger.debug("InputContainerUsed validate \n "
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
