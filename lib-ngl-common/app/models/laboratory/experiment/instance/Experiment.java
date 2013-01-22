package models.laboratory.experiment.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.instance.InstrumentUsed;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.ObjectSGBDReference;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cea.ig.DBObject;


/**
 * 
 * Experiment instance are stored one collection for the moment
 * 
 * For find the collection, see the value of attribut commoninfotype.collectionName in class experimentType
 * 
 * @author mhaquell
 *
 */

@MongoCollection(name="Experiment")
public class Experiment extends DBObject {

	// ExperimentType
	public String experimentTypeCode;
	
	// Informations
	public TraceInformation traceInformation;
	public Map<String,PropertyValue> experimentProperties;
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
	
	/* Relation with input and output containers
	*/
	public List<InputOutputContainer> listInputOutputContainers;
	
	// Protocol ref
	public String protocolCode;
//	public List<ReagentUsed> reagentsUsed;
	//public DBRef<Processus, String> fromProcessus;

	
	public Experiment(){
		traceInformation=new TraceInformation();
	}
	
	public Experiment(String code){
		this.experimentTypeCode=code;
		traceInformation=new TraceInformation();
	}
	
	@JsonIgnore
	public ExperimentType getExperimentType(){
		try {
			return new ObjectSGBDReference<ExperimentType>(ExperimentType.class,experimentTypeCode).getObject();
		} catch (Exception e) {
			//TODO
		
		}
		return null;
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
		
		try {
			return new ObjectSGBDReference<Protocol>(Protocol.class, protocolCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;

	}
	
	@JsonIgnore
	public State getState(){

		try {
			return new ObjectSGBDReference<State>(State.class, stateCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	
	@JsonIgnore
	public Resolution getResolution(){

		try {
			return new ObjectSGBDReference<Resolution>(Resolution.class, resolutionCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	
	
	
}
