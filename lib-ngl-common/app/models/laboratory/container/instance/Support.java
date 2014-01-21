package models.laboratory.container.instance;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.Valuation;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class Support extends DBObject implements IValidation{
	public String categoryCode;
	public State state;
	public String stockCode;
	public Valuation valuation;
	
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}
}
