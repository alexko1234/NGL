package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.IValidation;
import validation.InstanceValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.RunPropertyDefinitionHelper;
import validation.utils.ValidationHelper;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.InstrumentUsed;
import models.utils.InstanceConstants;
import fr.cea.ig.DBObject;


public class Run extends DBObject implements IValidation {
        
    public TraceInformation traceInformation;
    public String typeCode;
    
    public Date transfertStartDate;
    public Date transfertEndDate;
    public Boolean dispatch = Boolean.FALSE;
    
    public String containerSupportCode; //id flowcell
    
    public TBoolean abort = TBoolean.UNSET;
    public Date abortDate;
        
    public Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();
    public InstrumentUsed instrumentUsed;
    public List<Lane> lanes;
    
    

    @Override
    public void validate(ContextValidation contextValidation) {
            
        	contextValidation.putObject("_id",this._id);
        	
        	contextValidation.setRootKeyName("");

        	if(ValidationHelper.required(contextValidation, this.code, "code")){        
                BusinessValidationHelper.validateUniqueInstanceCode(contextValidation, this.code, Run.class, InstanceConstants.RUN_ILLUMINA_COLL_NAME);		
            }

            traceInformation.validate(contextValidation);
            
            DescriptionValidationHelper.validationRunTypeCode(this.typeCode, contextValidation); 
            DescriptionValidationHelper.validationContainerSupportCode(this.containerSupportCode, contextValidation); 
            
            this.instrumentUsed.validate(contextValidation); 
                
        	contextValidation.putObject("run", this);
            InstanceValidationHelper.validationLanes(this.lanes, contextValidation);

            contextValidation.setRootKeyName("properties");
            ValidationHelper.validateProperties(contextValidation, this.properties, RunPropertyDefinitionHelper.getRunPropertyDefinitions(), "");
            contextValidation.setRootKeyName("");
		
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

