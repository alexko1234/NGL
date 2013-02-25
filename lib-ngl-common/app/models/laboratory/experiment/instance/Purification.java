package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.PurificationMethodType;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;


/**
 * 
 * Purification instance are stored in collection Purification
 * Purification have one input container and one output container
 * 
 * TODO : manque la classe purificationType
 */

@MongoCollection(name="Purification")
public class Purification extends DBObject {

	// PurificationType
	public String purificationTypeCode;
	
	// Informations
	public TraceInformation traceInformation;
	public Map<String,PropertyValue> PurificationProperties;
	public List<Comment> comment;
	public Map<String, PropertyValue> instrumentProperties;
	
	public InstrumentUsed instrument;
	
	// States
	public String stateCode;
	public String resolutionCode;
	
	// For search optimisation
	//Projects ref
	public List<String> projectCodes;
	//Samples ref
	public List<String> sampleCodes;
	
	/* 1 container input / 1 container output
	*/
	public ContainerUsed inputContainer;
	public ContainerUsed outputContainer;
	
	// Protocol ref
	public String protocolCode;
//	public List<ReagentUsed> reagentsUsed;
	//public DBRef<Processus, String> fromProcessus;

	
	public Purification(){
		traceInformation=new TraceInformation();
	}
	
	public Purification(String code){
	//	this.PurificationTypeCode=code;
	//	traceInformation=new TraceInformation();
	}
	
	
	@JsonIgnore
	public PurificationMethodType getPurificationType(){
		return new HelperObjects<PurificationMethodType>().getObject(PurificationMethodType.class, protocolCode, null);
	}
	
	@JsonIgnore
	public List<Sample> getSamples(){
		return new HelperObjects<Sample>().getObjects(Sample.class, sampleCodes);
	}
	
	@JsonIgnore
	public List<Project> getProjects(){
		return new HelperObjects<Project>().getObjects(Project.class, projectCodes);
	}
	
	@JsonIgnore
	public Protocol getProtocol(){
		return new HelperObjects<Protocol>().getObject(Protocol.class, protocolCode, null);
	}
	
	@JsonIgnore
	public State getState(){
		return new HelperObjects<State>().getObject(State.class, stateCode, null);
	}
	
	@JsonIgnore
	public Resolution getResolution(){
		return new HelperObjects<Resolution>().getObject(Resolution.class, resolutionCode, null);
	}
	
}
