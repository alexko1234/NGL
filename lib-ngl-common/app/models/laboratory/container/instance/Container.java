package models.laboratory.container.instance;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;

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
import static validation.container.instance.ContainerValidationHelper.*;



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

	//duplication for input in exp : code, categoryCode, contents, mesured*, //contents just for tag and tagCategory 
	//duplication for output in exp :code, categoryCode, contents, mesured*, //contents just for tag and tagCategory
	
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
	public List<Content> contents;
	// Embedded QC result, this data are copying from collection QC
	public Set<QualityControlResult> qualityControlResults; //TODO GA remove

	//Stock management 
	public PropertySingleValue mesuredVolume;        //TODO GA rename to volume
	public PropertySingleValue mesuredConcentration; //TODO GA rename to concentration
	public PropertySingleValue mesuredQuantity; 	 //TODO GA rename to quantity

	public List<PropertyValue> calculedVolume; //TODO GA remove

	// For search optimisation
	public Set<String> projectCodes; // getProjets
	public Set<String> sampleCodes; // getSamples
	// ExperimentType must be an internal or external experiment ( origine )
	// List for pool experimentType
	public Set<String> fromExperimentTypeCodes; // getExperimentType

	// Propager au container de purif ??
	//public String fromExperimentCode; ??
	public String fromPurifingCode; //TODO GA remove
	//public String fromExtractionTypeCode;
	//process
	
	//TODO GA merge in same objet processTypeCode and processCode and add a list of this object
	//TODO GA may be with fromExperimentTypeCodes ???
	public String processTypeCode; //TODO GA remove and replace by processTypeCodes warning find container for create experiment ?
	public Set<String> processTypeCodes;
	public Set<String> inputProcessCodes; //TODO GA rename to processCodes

	public Container(){
		//properties=new HashMap<String, PropertyValue>();
		contents=new ArrayList<Content>();
		traceInformation=new TraceInformation();
		projectCodes = new HashSet<String>();
		sampleCodes = new HashSet<String>();
		//comments = new ArrayList<>();
		//qualityControlResults = new HashSet<>();
		//calculedVolume = new ArrayList<>();
		fromExperimentTypeCodes = new HashSet<>();
	
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
		
		if(contextValidation.getObject(FIELD_STATE_CODE) == null){
			contextValidation.putObject(FIELD_STATE_CODE , state.code);			
		}
		
    	validateId(this, contextValidation);
		validateCode(this, InstanceConstants.CONTAINER_COLL_NAME, contextValidation);
		validateState(this.state, contextValidation);
		validateTraceInformation(this.traceInformation, contextValidation);
		validateContainerCategoryCode(categoryCode, contextValidation);
		validateProcessTypeCode(processTypeCode, contextValidation);
		//TODO GA processTypeCodes
		validateProjectCodes(projectCodes, contextValidation);
		validateSampleCodes(sampleCodes, contextValidation);
		validateExperimentTypeCodes(fromExperimentTypeCodes, contextValidation);
		validateExperimentCode(fromPurifingCode, contextValidation);//bug here Yann
		validateContents(contents,contextValidation);
		validateContainerSupport(support,contextValidation);//bug here Yann
		validateInputProcessCodes(inputProcessCodes,contextValidation);
		
	}

	
}
