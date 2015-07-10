package models.laboratory.container.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongojack.DBQuery;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.container.instance.ContainerValidationHelper;



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

	//Relation with container support
	public LocationOnContainerSupport support; 

	//Embedded content with values;
	//public List<Content> contents;
	public Set<Content> contents;
	// Embedded QC result, this data are copying from collection QC
	public Set<QualityControlResult> qualityControlResults;

	//Stock management 
	public PropertySingleValue mesuredVolume;
	public PropertySingleValue mesuredConcentration;
	public PropertySingleValue mesuredQuantity;

	public List<PropertyValue> calculedVolume;

	// For search optimisation
	public Set<String> projectCodes; // getProjets //TODO SET instead of LIST
	public Set<String> sampleCodes; // getSamples //TODO SET instead of LIST
	// ExperimentType must be an internal or external experiment ( origine )
	// List for pool experimentType
	public Set<String> fromExperimentTypeCodes; // getExperimentType

	// Propager au container de purif ??
	//public String fromExperimentCode; ??
	public String fromPurifingCode;
	//public String fromExtractionTypeCode;
	//process
	public String processTypeCode; //TODO GA : est ce bien utile comme info ?

	public Set<String> inputProcessCodes;

	public Container(){
		properties=new HashMap<String, PropertyValue>();
		contents=new HashSet<Content>();
		traceInformation=new TraceInformation();
		projectCodes = new HashSet<String>();
		sampleCodes = new HashSet<String>();
		comments = new ArrayList<>();
		qualityControlResults = new HashSet<>();
		calculedVolume = new ArrayList<>();
		fromExperimentTypeCodes = new HashSet<>();
	
	}

	@JsonIgnore
	public Container(Content sampleUsed){

		this.contents.add(sampleUsed);
		this.traceInformation=new TraceInformation();
		properties=new HashMap<String, PropertyValue>();
	}

	
	@JsonIgnore
	public List<Process> getCurrentProcesses() {
		List<Process> processes=new ArrayList<Process>();
		if(inputProcessCodes!=null){
			processes= MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code",inputProcessCodes)).toList();
		}
		return processes;
		
	}

		
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation){
		
    	ContainerValidationHelper.validateId(this, contextValidation);
		ContainerValidationHelper.validateCode(this, InstanceConstants.CONTAINER_COLL_NAME, contextValidation);
		ContainerValidationHelper.validateStateCode(this, contextValidation);
		ContainerValidationHelper.validateTraceInformation(this.traceInformation, contextValidation);
		ContainerValidationHelper.validateContainerCategoryCode(categoryCode, contextValidation);
		ContainerValidationHelper.validateProcessTypeCode(processTypeCode, contextValidation);
		ContainerValidationHelper.validateProjectCodes(projectCodes, contextValidation);
		ContainerValidationHelper.validateSampleCodes(sampleCodes, contextValidation);
		ContainerValidationHelper.validateExperimentTypeCodes(fromExperimentTypeCodes, contextValidation);
		ContainerValidationHelper.validateExperimentCode(fromPurifingCode, contextValidation);//bug here Yann
		ContainerValidationHelper.validateContents(contents,contextValidation);
		ContainerValidationHelper.validateContainerSupport(support,contextValidation);//bug here Yann
		ContainerValidationHelper.validateProcessCodes(inputProcessCodes,contextValidation);
		
	}

	
}
