package models.laboratory.run.instance;

import static validation.utils.ConstraintsHelper.getKey;
import static validation.utils.ConstraintsHelper.required;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.InstrumentUsed;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import validation.DescriptionValidationHelper;
import validation.InstanceValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;
import validation.utils.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
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
	
	public Map<String, PropertyValue> properties = InstanceHelpers.getLazyMapPropertyValue();

	public InstrumentUsed instrumentUsed;
	public List<Lane> lanes;
	
	

	@Override
	public void validate(ContextValidation contextValidation) {
			
			contextValidation.contextObjects.put("_id",this._id);
			
			if(required(contextValidation.errors, this.code, getKey(contextValidation.rootKeyName,"code"))){
				BusinessValidationHelper.validateUniqueInstanceCode(contextValidation, this.code, Run.class, InstanceConstants.RUN_ILLUMINA_COLL_NAME);		
			}

			traceInformation.validate(contextValidation);
			
			DescriptionValidationHelper.validationRunTypeCode(this.typeCode, contextValidation); 
			DescriptionValidationHelper.validationContainerSupportCode(this.containerSupportCode, contextValidation); 
			
			this.instrumentUsed.validate(contextValidation); 
				
			contextValidation.contextObjects.put("run", this);
			contextValidation.rootKeyName = "lanes";
			InstanceValidationHelper.validationLanes(this.lanes, contextValidation);
			

			String rootKeyNameProp = getKey(contextValidation.rootKeyName,"properties");
			ConstraintsHelper.validateProperties(contextValidation.errors, this.properties, RunPropertyDefinitionHelper.getRunPropertyDefinitions(), rootKeyNameProp);
				
		}		
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
	 */
	
