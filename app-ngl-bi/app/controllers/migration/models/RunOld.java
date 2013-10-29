package controllers.migration.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import validation.IValidation;
import fr.cea.ig.DBObject;


public class RunOld extends Run {
        
	public String stateCode;
	public TBoolean valid;
    public Date validDate;
    public List<LaneOld> lanes;
    
}

