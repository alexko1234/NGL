package controllers.migration.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import validation.ContextValidation;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.InstrumentUsed;
import validation.IValidation;
import fr.cea.ig.DBObject;


public class RunOld extends DBObject implements IValidation {
        
    public TraceInformation traceInformation;
    public String typeCode;
    
    public Date transfertStartDate;
    public Date transfertEndDate;
    public Boolean dispatch = Boolean.FALSE;
    
    public String containerSupportCode; //id flowcell
    
    public TBoolean abort = TBoolean.UNSET;
    public Date abortDate;
    

    public Map<String, PropertyValueOld> properties = new HashMap<String, PropertyValueOld>();

    public InstrumentUsed instrumentUsed;
    public List<LaneOld> lanes;
    
    

    @Override
    public void validate(ContextValidation contextValidation) {
            
		
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

