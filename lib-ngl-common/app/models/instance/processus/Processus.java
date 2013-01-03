package models.instance.processus;

import java.util.List;
import java.util.Map;

import models.instance.common.Comment;
import models.instance.common.PropertyValue;
import models.instance.common.TraceInformation;
import models.instance.common.Valid;
import models.instance.container.Sample;
import models.instance.experiment.Experiment;
import models.instance.project.Project;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import utils.refobject.HelperObjects;

import fr.cea.ig.DBObject;




@MongoCollection(name="Processus")
public class Processus extends DBObject {
	
	//public ProcessusTypeRef processusType;

	public String name;
	public String status;
	public Valid valid;
	public TraceInformation traceinformation;
	public List<Comment> comments;

	public Map<String,PropertyValue> properties;
	
	// User or Equipe
	//public List<String> equipeCode;
	
	// Experiments ref
	public List<String> experimentCodes;
	// Projects ref
	public List<String> projectCodes;
	// Samples ref
	public List<String> sampleCodes;
	
	@JsonIgnore
	public List<Sample> getSamples(){
		return new HelperObjects<Sample>().getObjects(Sample.class, sampleCodes);
	}
	
	@JsonIgnore
	public List<Project> getProjects(){
		return new HelperObjects<Project>().getObjects(Project.class, projectCodes);
	}
	
	
	@JsonIgnore
	public List<Experiment> getExperiments(){
		return new HelperObjects<Experiment>().getObjects(Experiment.class, experimentCodes);
	}
	
}
