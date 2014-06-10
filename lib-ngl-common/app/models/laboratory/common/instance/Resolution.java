package models.laboratory.common.instance;

import java.util.List;

import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;

public class Resolution extends DBObject implements IValidation {
	
	public String  code;
    public String type;
    public List<StateResolution> stateResolutions;
    
    public TraceInformation traceInformation;
    
	@Override
	public void validate(ContextValidation contextValidation) {
		
		//TODO : validate attributes
		TraceInformation t = new TraceInformation();
		t.setTraceInformation("ngsrg");
		this.traceInformation = t;
		
		//add trace information
		CommonValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
	}

}