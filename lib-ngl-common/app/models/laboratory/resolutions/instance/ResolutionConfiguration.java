package models.laboratory.resolutions.instance;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.utils.InstanceConstants;
import play.Logger;
import validation.ContextValidation;
import validation.IValidation;
import validation.common.instance.CommonValidationHelper;
import validation.resolution.instance.ResolutionValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;

public class ResolutionConfiguration extends DBObject implements IValidation {
	
    public String objectTypeCode;
	public List<String> typeCodes;
    public List<Resolution> resolutions;
    public TraceInformation traceInformation;
    
	@Override
	public void validate(ContextValidation contextValidation) {
		
    	contextValidation.putObject("resolutionConfigurations", this);
    	CommonValidationHelper.validateCode(this, InstanceConstants.RESOLUTION_COLL_NAME, contextValidation);
    	//TODO : validate objectTypeCode & typeCodes
    	
    	ValidationHelper.required(contextValidation, this.objectTypeCode, "type");
    	
    	contextValidation.removeObject("resolutionConfigurations");
    	
    	ResolutionValidationHelper.validationResolutions(this.resolutions, contextValidation);
    	
		//manage traceInformation
		TraceInformation t = new TraceInformation();
		t.setTraceInformation("ngsrg");
		this.traceInformation = t;
		
		CommonValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
	}

}