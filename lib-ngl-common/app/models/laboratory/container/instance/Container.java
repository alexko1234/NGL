package models.laboratory.container.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.instance.Process;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.ContainerValidationHelper;
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
	public final static String HEADER="Container.code;Container.categoryCode;Container.comments;LocationOnContainerSupport.categorycode;LocationOnContainerSupport.x;LocationOnContainerSupport.y;LocationOnContainerSupport.barecode";

	//ContainerCategory Ref
	public String categoryCode;

	public State state;
	public Valuation valuation;

	// Container informations
	public TraceInformation traceInformation;
	public Map<String, PropertyValue> properties;
	public List<Comment> comments;

	//Relation with support
	public LocationOnContainerSupport support; 

	//Embedded content with values;
	//public List<Content> contents;
	public List<Content> contents;
	// Embedded QC result, this data are copying from collection QC
	public List<QualityControlResult> qualityControlResults;

	//Stock management 
	public PropertyValue mesuredVolume;
	public PropertyValue mesuredConcentration;
	public PropertyValue mesuredQuantity;

	public List<PropertyValue> calculedVolume;

	// For search optimisation
	public List<String> projectCodes; // getProjets //TODO SET instead of LIST
	public List<String> sampleCodes; // getSamples //TODO SET instead of LIST
	// ExperimentType must be an internal or external experiment ( origine )
	// List for pool experimentType
	public List<String> fromExperimentTypeCodes; // getExperimentType

	// Propager au container de purif ??
	//public String fromExperimentCode; ??
	public String fromPurifingCode;
	//public String fromExtractionTypeCode;
	//process
	public String processTypeCode; //TODO GA : est ce bien utile comme info ?

	public List<String> inputProcessCodes;

	public Container(){
		properties=new HashMap<String, PropertyValue>();
		contents=new ArrayList<Content>();
		traceInformation=new TraceInformation();
	}

	@JsonIgnore
	public Container(Content sampleUsed){

		this.contents.add(sampleUsed);
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
	public List<Process> getCurrentProcesses() {
		if(inputProcessCodes!=null){
		return MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code",inputProcessCodes)).toList();
		}
		return null;
	}

		
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation){
		
    	ContainerValidationHelper.validateId(this, contextValidation);
		ContainerValidationHelper.validateCode(this, InstanceConstants.CONTAINER_COLL_NAME, contextValidation);
		//TODO 
		ContainerValidationHelper.validateStateCode(this.state.code, contextValidation);
		ContainerValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
		ContainerValidationHelper.validateContainerCategoryCode(categoryCode, contextValidation);
		ContainerValidationHelper.validateProcessTypeCode(processTypeCode, contextValidation);
		ContainerValidationHelper.validateProjectCodes(projectCodes, contextValidation);
		ContainerValidationHelper.validateSampleCodes(sampleCodes, contextValidation);
		ContainerValidationHelper.validateExperimentTypeCodes(fromExperimentTypeCodes, contextValidation);
		ContainerValidationHelper.validateExperimentCode(fromPurifingCode, contextValidation);//bug here Yann
		ContainerValidationHelper.validateContents(contents,contextValidation);
		ContainerValidationHelper.validateContainerSupport(support,contextValidation);//bug here Yann
		
		//InstanceValidationHelper.validationComments(comments,contextValidation);
		
		//TODO validate properties
		
		
		
	}

	
}
