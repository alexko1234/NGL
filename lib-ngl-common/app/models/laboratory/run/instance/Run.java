package models.laboratory.run.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.Logger;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.experiment.instance.ExperimentValidationHelper;
import validation.run.instance.LaneValidationHelper;
import validation.run.instance.RunValidationHelper;
import validation.run.instance.TreatmentValidationHelper;
import fr.cea.ig.DBObject;


public class Run extends DBObject implements IValidation {
        
	public String typeCode;
	public Date sequencingStartDate;
	
	public String categoryCode;
	
	
	
	public State state;
	
	public String containerSupportCode; //id flowcell
    public Boolean dispatch = Boolean.FALSE;
    
    public Valuation valuation = new Valuation();
    
    public Set<String> projectCodes = new TreeSet<String>();
    public Set<String> sampleCodes = new TreeSet<String>();
    
    public Boolean keep = Boolean.FALSE;
    public Boolean deleted = Boolean.FALSE;
    
    
    public TraceInformation traceInformation;
    public InstrumentUsed instrumentUsed; //Instrument used to obtain the run
    public Map<String,Treatment> treatments = new HashMap<String,Treatment>();
    public Map<String, PropertyValue<?>> properties = new HashMap<>(); // <String, PropertyValue>();
    public List<Lane> lanes;
    
    @JsonIgnore
    public Lane getLane(Integer laneNumber){
    	if(lanes != null){
    		Iterator<Lane> iti = lanes.iterator();
	    	while(iti.hasNext()){
	    		Lane next = iti.next();
	    		if(next.number.equals(laneNumber)){
	    			return next;
	    		}
	    	}
    	}
    	return null;
    	//return lanes.stream().filter((Lane l) -> l.number.equals(laneNumber)).findFirst().get();
    }
    
    @Override
    public void validate(ContextValidation contextValidation) {
    	contextValidation.putObject("run", this);
    	RunValidationHelper.validateId(this, contextValidation);
    	RunValidationHelper.validateCode(this, InstanceConstants.RUN_ILLUMINA_COLL_NAME, contextValidation);
    	RunValidationHelper.validateRunType(this.typeCode, this.properties, contextValidation);
    	RunValidationHelper.validationRunCategoryCode(categoryCode, contextValidation);
    	//TODO ValidationHelper.required(contextValidation, sequencingStartDate, "sequencingStartDate");
    	
    	RunValidationHelper.validateState(this.typeCode, this.state, contextValidation);
    	RunValidationHelper.validateValuation(this.typeCode, this.valuation, contextValidation);
    	RunValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
    	if(!(contextValidation.getContextObjects().containsKey("external")) || 
    			(contextValidation.getContextObjects().containsKey("external") && !(Boolean)contextValidation.getContextObjects().get("external")))
    		RunValidationHelper.validateContainerSupportCode(this.containerSupportCode, contextValidation, "containerSupportCode"); 
    	RunValidationHelper.validateRunInstrumentUsed(this.instrumentUsed, contextValidation);		
		contextValidation.putObject("level", Level.CODE.Run);
		
		RunValidationHelper.validateRunProjectCodes(this.code, this.projectCodes, contextValidation);
		
		RunValidationHelper.validateRunSampleCodes(this.code, this.sampleCodes, contextValidation);
		
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
	    id du type de sequen
    */
    
}

