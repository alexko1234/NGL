package models.laboratory.container.instance;

import static validation.utils.ConstraintsHelper.required;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import play.data.validation.ValidationError;
import validation.utils.BusinessValidationHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;



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
public class Container extends DBObject implements IValidation {


	@JsonIgnore
	public final static String HEADER="Container.code;Container.categoryCode;Container.comments;ContainerSupport.categorycode;ContainerSupport.x;ContainerSupport.y;ContainerSupport.barecode";

	//ContainerCategory Ref
	public String categoryCode;

	public String stateCode;
	public TBoolean valid;
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
	public PropertyValue mesuredVolume;
	public PropertyValue mesuredConcentration;
	public PropertyValue mesuredQuantity;

	public List<PropertyValue> calculedVolume;

	// For search optimisation
	public List<String> projectCodes; // getProjets
	public List<String> sampleCodes; // getSamples
	// ExperimentType must be an internal or external experiment ( origine )
	// List for pool experimentType
	public List<String> fromExperimentTypeCodes; // getExperimentType

	// Propager au container de purif ??
	//public String fromExperimentCode; ??
	public String fromPurifingCode;
	//public String fromExtractionTypeCode;
	//process
	public String processTypeCode;


	public Container(){
		properties=new HashMap<String, PropertyValue>();
		traceInformation=new TraceInformation();
	}

	@JsonIgnore
	public Container(SampleUsed sampleUsed){

		this.contents=new ArrayList<Content>();
		this.contents.add(new Content(sampleUsed));
		this.traceInformation=new TraceInformation();
		properties=new HashMap<String, PropertyValue>();
	}


	@JsonIgnore
	public ContainerCategory getContainerCategory(){
		return new HelperObjects<ContainerCategory>().getObject(ContainerCategory.class, categoryCode);

	}

	@JsonIgnore
	public List<Project> getProjects() {
		return new HelperObjects<Project>().getObjects(Project.class, projectCodes);

	}

	@JsonIgnore
	public List<Sample> getSamples() {
		return new HelperObjects<Sample>().getObjects(Sample.class, sampleCodes);
	}

	@JsonIgnore
	public List<ExperimentType> getFromExperimentTypes() {
		return new HelperObjects<ExperimentType>().getObjects(ExperimentType.class, fromExperimentTypeCodes);
	}


	@JsonIgnore
	public State getState(){
		return new HelperObjects<State>().getObject(State.class, stateCode);
	}

	@JsonIgnore
	public Resolution getResolution(){
		return new HelperObjects<Resolution>().getObject(Resolution.class, resolutionCode);
	}

		
	@JsonIgnore
	@Override
	public void validate(Map<String,List<ValidationError>> errors){

		BusinessValidationHelper.validateUniqueInstanceCode(errors, this.code, Container.class,InstanceConstants.CONTAINER_COLL_NAME);
		
		BusinessValidationHelper.validateRequiredDescriptionCode(errors, this.categoryCode, "categoryCode", ContainerCategory.find);
				
		BusinessValidationHelper.validateRequiredInstanceCodes(errors, this.projectCodes, "projectCodes",Project.class,InstanceConstants.PROJECT_COLL_NAME,false);

		BusinessValidationHelper.validateRequiredInstanceCodes(errors, this.sampleCodes,"sampleCodes",Sample.class,InstanceConstants.SAMPLE_COLL_NAME,false);

		BusinessValidationHelper.validateExistInstanceCodes(errors, fromExperimentTypeCodes, "fromExperimentTypeCodes", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);	

		BusinessValidationHelper.validateExistInstanceCode(errors, fromPurifingCode, "fromPurifingCode", Experiment.class, InstanceConstants.EXPERIMENT_COLL_NAME, false);
		
		BusinessValidationHelper.validateExistDescriptionCode(errors, this.resolutionCode,"resolutionCode", Resolution.find);

		if(required(errors, this.contents, "container.contents")){
			for(Content content :this.contents){
				content.validate(errors);
			}
		}

	}

}
