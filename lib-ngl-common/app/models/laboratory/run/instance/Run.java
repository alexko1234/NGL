package models.laboratory.run.instance;



import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.IValidation;
import validation.InstanceValidationHelper;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.InstrumentUsed;
import models.utils.InstanceConstants;
import fr.cea.ig.DBObject;


public class Run extends DBObject implements IValidation {
        
    public String typeCode;
    public String stateCode;
	public List<String> resolutionCode;
	public String containerSupportCode; //id flowcell
    public Boolean dispatch = Boolean.FALSE;
    public TBoolean valid = TBoolean.UNSET;
    public Date validDate;
    
    public TraceInformation traceInformation;
    public InstrumentUsed instrumentUsed;
    public Map<String,Treatment> treatments = new HashMap<String,Treatment>();
    public Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();
    public List<Lane> lanes;
    

    @Override
    public void validate(ContextValidation contextValidation) {
    	
    	InstanceValidationHelper.validateId(this, contextValidation);
    	InstanceValidationHelper.validateCode(this, InstanceConstants.RUN_ILLUMINA_COLL_NAME, contextValidation);
		
		if(ValidationHelper.required(contextValidation, this.stateCode, "stateCode")){
			if(!RunPropertyDefinitionHelper.getRunStateCodes().contains(this.stateCode)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, this.stateCode);
			}
		}
		
		InstanceValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
		
		
		
		DescriptionValidationHelper.validationRunTypeCode(this.typeCode, contextValidation); 
		DescriptionValidationHelper.validationContainerSupportCode(this.containerSupportCode, contextValidation); 
		
		if(ValidationHelper.required(contextValidation, this.instrumentUsed, "instrumentUsed")){
			contextValidation.addKeyToRootKeyName("instrumentUsed");
			this.instrumentUsed.validate(contextValidation); 
			contextValidation.removeKeyFromRootKeyName("instrumentUsed");
		}
		
		contextValidation.putObject("run", this);
		contextValidation.putObject("level", Level.CODE.Run);
		//WARN DON'T CHANGE THE ORDER OF VALIDATION
		InstanceValidationHelper.validationTreatments(this.treatments, contextValidation);
		InstanceValidationHelper.validationLanes(this.lanes, contextValidation);
		
		
		contextValidation.addKeyToRootKeyName("properties");
		ValidationHelper.validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getRunPropertyDefinitions());
		contextValidation.removeKeyFromRootKeyName("properties");            
		
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

