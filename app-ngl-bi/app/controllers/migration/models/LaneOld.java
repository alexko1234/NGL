package controllers.migration.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;

import models.laboratory.common.instance.TBoolean;
import validation.IValidation;


public class LaneOld implements IValidation {
	
	public Integer number;
	public TBoolean abort = TBoolean.UNSET;
	public Date abortDate;
	
	public List<ReadSetOld> readsets;
	public Map<String, PropertyValueOld> properties= new HashMap<String, PropertyValueOld>();
	
	
	@Override
	public void validate(ContextValidation contextValidation) {

	}
}
