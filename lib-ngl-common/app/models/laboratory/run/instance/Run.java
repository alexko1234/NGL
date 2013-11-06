package models.laboratory.run.instance;



import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.Logger;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Validation;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.run.instance.LaneValidationHelper;
import validation.run.instance.RunValidationHelper;
import validation.run.instance.TreatmentValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;


public class Run extends DBObject implements IValidation {
        
	public String typeCode;
	
	public State state;
	
	public String containerSupportCode; //id flowcell
    public Boolean dispatch = Boolean.FALSE;
    
    public Validation validation;
   
    
    
    public TraceInformation traceInformation;
    public InstrumentUsed instrumentUsed;
    public Map<String,Treatment> treatments = new HashMap<String,Treatment>();
    public Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();
    public List<Lane> lanes;
    
    //TODO Samples, Projects

    @Override
    public void validate(ContextValidation contextValidation) {
    	contextValidation.putObject("run", this);
    	RunValidationHelper.validateId(this, contextValidation);
    	RunValidationHelper.validateCode(this, InstanceConstants.RUN_ILLUMINA_COLL_NAME, contextValidation);
    	RunValidationHelper.validateRunType(this.typeCode, this.properties, contextValidation);
    	
    	RunValidationHelper.validateState(this.typeCode, this.state, contextValidation);
    	RunValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
    	RunValidationHelper.validationContainerSupportCode(this.containerSupportCode, contextValidation); 
    	RunValidationHelper.validateRunInstrumentUsed(this.instrumentUsed, contextValidation);		
		contextValidation.putObject("level", Level.CODE.Run);
		//WARN DON'T CHANGE THE ORDER OF VALIDATION
		TreatmentValidationHelper.validationTreatments(this.treatments, contextValidation);
		LaneValidationHelper.validationLanes(this.lanes, contextValidation);
		
    }


	


    /*
        nbClusterIlluminaFilter
        nbCycle
        nbClusterTotal
        nbBase
        flowcellPosition
        rtaVersion
        flowcellVersion
        controlLane
        mismatch

	    id du depot flowcell ???
	    id du type de sequençage ???
    */
    
}

