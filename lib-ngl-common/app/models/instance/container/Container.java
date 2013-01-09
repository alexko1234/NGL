package models.instance.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.description.common.Resolution;
import models.description.common.State;
import models.description.content.ContainerCategory;
import models.description.experiment.ExperimentType;
import models.instance.common.Comment;
import models.instance.common.PropertyValue;
import models.instance.common.TraceInformation;
import models.instance.common.Valid;
import models.instance.project.Project;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import utils.refobject.HelperObjects;
import utils.refobject.ObjectMongoDBReference;
import utils.refobject.ObjectSGBDReference;
import fr.cea.ig.DBObject;



/**
 * 
 * Instances Container are stored in MongoDB collection named Container 
 * Container is referenced in collection Experiment, Purifying, TransferMethod, Extraction, QC in embedded class ListInputOutputContainer
 * The Relationship between containers aren't storing in the container but in class/collection RelationshipContainer 
 * In Container, the link with experiment are the attribut 'fromExperimentTypes' who help to manage Container in workflow 
 *  
 * @author mhaquell
 *
 */
@MongoCollection(name="Container")
public class Container extends DBObject {

	//ContainerCategory Ref
	public String categoryCode;
	
	// State Ref
	public String stateCode;
	public Valid valid;
	// Resolution Ref
	public String resolutionCode; //used to classify the final state (ex : ) 
	
	// Container informations
	public TraceInformation traceInformation;
	public Map<String, PropertyValue> properties;
	public List<Comment> comments;

	
	//Relation with support
	public ContainerSupport support; 
	
	//Embedded content with values;
	public List<Content> contents;
	
	//Stock management 
	public List<Volume> mesuredVolume;
	public List<Volume> calculedVolume;
	
	// For search optimisation
	public List<String> projetCodes; // getProjets
	public List<String> sampleCodes; // getSamples
	// ExperimentType must be an internal or external experiment ( origine )
	// List for pool experimentType
	public List<String> fromExperimentTypeCodes; // getExperimentType
	
	// Propager au container de purif ??
	//public String fromExperimentCode; ??
	//public String fromPurifingCode;
	//public String fromExtractionTypeCode;
	//public List<String> fromQCCodes;
	

	
	@JsonIgnore
	public ContainerCategory getContainerCategory(){
		try {
			return new ObjectSGBDReference<ContainerCategory>(ContainerCategory.class,code).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	
	@JsonIgnore
	public List<Project> getProjects() {
		
		List<Project> projets =new ArrayList<Project>();

		for (int i = 0; i < projetCodes.size(); i++) {
			try {
				projets.add(new ObjectMongoDBReference<Project>(Project.class,projetCodes.get(i)).getObject());
			} catch (Exception e) {
				// TODO
			}	
		}
		
		return projets;
		
	}
	
	@JsonIgnore
	public List<Sample> getSamples() {
		
		return new HelperObjects<Sample>().getObjects(Sample.class, sampleCodes);
		
	}

	@JsonIgnore
	public List<ExperimentType> getExperimentTypes() {
		
		return new HelperObjects<ExperimentType>().getObjects(ExperimentType.class, fromExperimentTypeCodes);
		
	}
	
	@JsonIgnore
	public State getState(){
		try {
			return new ObjectSGBDReference<State>(State.class,stateCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	
	@JsonIgnore
	public Resolution getResolution(){
		try {
			return new ObjectSGBDReference<Resolution>(Resolution.class,resolutionCode).getObject();
		} catch (Exception e) {
			// TODO
		}
		return null;
	}
	

}
