package models.laboratory.container.instance;

import java.util.List;

import fr.cea.ig.DBObject;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.instance.ContainerUsed;
import net.vz.mongodb.jackson.MongoCollection;

@MongoCollection(name="Basket")
public class Basket extends DBObject{
	//unique code, choose by the creator
	public String code;
	
	//informations
	public TraceInformation traceInformation;
	
	// ExperimentType for the tubes in the basket
	public String experimentTypeCode;
	
	//Tubes in the basket
	public List<String> inputContainers;
}
