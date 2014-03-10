package models.laboratory.reagent.instance;

import java.util.Date;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import net.vz.mongodb.jackson.MongoCollection;
import fr.cea.ig.DBObject;

@MongoCollection(name="Reagent")
public class Reagent extends DBObject {

	public String providerCode;
	
	//public String typeCode;
	public String categoryCode;
	
	public Date dateReception;
	
	public TraceInformation traceInformation;
	
	public Map<String, PropertyValue> properties;
	
}
