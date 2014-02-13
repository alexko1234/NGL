package models.laboratory.parameter;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class Index extends DBObject implements IValidation{

	public final String categoryCode="indexIllumina";
	public String sequence;
	public String illuminaName;
	public List<String> groups;
	public TraceInformation traceInformation;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		
	}
}



