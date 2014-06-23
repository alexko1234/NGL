package controllers.migration.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Lane;


public class LaneOld extends Lane {
	public String stateCode;
	public TBoolean valid = TBoolean.UNSET;
	public Date validDate;
	
	public State state;
    
}
