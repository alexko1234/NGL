package models.laboratory.project.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import net.vz.mongodb.jackson.MongoCollection;
import fr.cea.ig.DBObject;



/**
 * Instance Project is stocked in Collection mongodb Project
 * Project Name are referencing in class Experience and Container
 * 
 * @author mhaquell
 *
 */
@MongoCollection(name="Project")
public class Project extends DBObject {

	//public String categoryCode;
	public String name;
	public TraceInformation traceInformation;
	public Map<String, PropertyValue> properties;
	public List<String> comments;
	

}
