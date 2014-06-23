package models.laboratory.container.instance;

import java.util.List;

import fr.cea.ig.DBObject;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.instance.ContainerUsed;
import org.mongojack.MongoCollection;

@MongoCollection(name="Basket")
public class Basket extends DBObject{
	//unique code, choose by the creator
	public String code;
	
	//Informations
	public TraceInformation traceInformation;
	
	//ExperimentType for the tubes in the basket
	public String experimentTypeCode;
	
	//Code of tubes in the basket
	public List<String> inputContainers;
}
