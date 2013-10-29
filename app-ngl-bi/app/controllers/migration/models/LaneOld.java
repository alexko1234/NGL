package controllers.migration.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Validation;
import models.laboratory.run.instance.Lane;
import validation.IValidation;


public class LaneOld extends Lane {
	public String stateCode;
	public TBoolean valid = TBoolean.UNSET;
	public Date validDate;
    
}
