package models.laboratory.container.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valid;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.ObjectMongoDBReference;
import models.utils.ObjectSGBDReference;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

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

	// Embedded QC result, this data are copying from collection QC
	public List<QualityControlResult> qualityControlResults;

	
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
	public String fromPurifingCode;
	//public String fromExtractionTypeCode;
	

	
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
