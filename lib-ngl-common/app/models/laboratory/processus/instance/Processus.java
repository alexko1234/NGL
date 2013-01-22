package models.laboratory.processus.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;




@MongoCollection(name="Processus")
public class Processus extends DBObject {
	
	//public ProcessusTypeRef processusType;

	public String name;
	public String status;
	public TBoolean valid;
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
