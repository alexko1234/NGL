package controllers.migration.models;

import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Run;


public class RunOld extends Run {
        
	public String stateCode;
	public TBoolean valid;
    public Date validDate;
    public List<LaneOld> lanes;
    
    public InstrumentUsedOld instrumentUsed;
    
}

