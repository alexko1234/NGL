package models.laboratory.container.instance;

import static validation.common.instance.CommonValidationHelper.FIELD_IMPORT_TYPE_CODE;
import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import static validation.common.instance.CommonValidationHelper.validateCode;
import static validation.common.instance.CommonValidationHelper.validateExperimentTypeCodes;
import static validation.common.instance.CommonValidationHelper.validateId;
import static validation.common.instance.CommonValidationHelper.validateProjectCodes;
import static validation.common.instance.CommonValidationHelper.validateSampleCodes;
import static validation.common.instance.CommonValidationHelper.validateTraceInformation;
import static validation.container.instance.ContainerValidationHelper.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.ITracingAccess;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.tree.TreeOfLifeNode;
import models.utils.InstanceConstants;

import org.mongojack.MongoCollection;

import validation.ContextValidation;
import validation.IValidation;
import validation.experiment.instance.ContainerUsedValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Container extends DBObject implements IValidation, ITracingAccess {

	//duplication for input in exp : code, categoryCode, contents, mesured*, //contents just for tag and tagCategory 
	//duplication for output in exp :code, categoryCode, contents, mesured*, //contents just for tag and tagCategory
	
	
	public String importTypeCode;
	//ContainerCategory Ref
	public String categoryCode;

	public State state;
	public Valuation valuation;

	// Container informations
	public TraceInformation traceInformation;
	public Map<String, PropertyValue> properties;
	public List<Comment> comments = new ArrayList<Comment>(0);

	//Relation with container support
	public LocationOnContainerSupport support; 

	//Embedded content with values;
	//public List<Content> contents;
	public List<Content> contents;
	// Embedded QC result, this data are copying from collection QC
	public List<QualityControlResult> qualityControlResults; 

	public PropertySingleValue volume;        
	public PropertySingleValue concentration; 
	public PropertySingleValue quantity; 	
	public PropertySingleValue size;
	
	// For search optimisation
	public Set<String> projectCodes; // getProjets
	public Set<String> sampleCodes; // getSamples
	// ExperimentType must be an internal or external experiment ( origine )
	// List for pool experimentType
	public Set<String> fromTransformationTypeCodes;
	public Set<String> fromTransformationCodes; 
	public Set<String> processTypeCodes;
	public Set<String> processCodes;

	public String fromPurificationTypeCode;
	public String fromPurificationCode; 
	public String fromTransfertCode;
	public String fromTransfertTypeCode;
	
	//tree of life
	public TreeOfLifeNode treeOfLife;
	
	
	public Container(){
		//properties=new HashMap<String, PropertyValue>();
		contents=new ArrayList<Content>();
		traceInformation=new TraceInformation();
		projectCodes = new HashSet<String>();
		sampleCodes = new HashSet<String>();
		//comments = new ArrayList<>();
		//qualityControlResults = new HashSet<>();
		fromTransformationTypeCodes = new HashSet<>();
		valuation = new Valuation();	
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
		//TODO GA processTypeCodes
		validateProjectCodes(projectCodes, contextValidation);
		validateSampleCodes(sampleCodes, contextValidation);
		validateExperimentTypeCodes(fromTransformationTypeCodes, contextValidation);
		validateImportType(importTypeCode, properties ,contextValidation);
		
		if(importTypeCode != null){
			contextValidation.putObject(FIELD_IMPORT_TYPE_CODE , importTypeCode);			
		}
		validateContents(contents,contextValidation);
		validateContainerSupport(support,contextValidation);//bug here Yann
		validateInputProcessCodes(processCodes,contextValidation);
		
		validateQualityControlResults(qualityControlResults, contextValidation);
		
		validateConcentration(concentration, contextValidation);
		validateQuantity(quantity, contextValidation);
		validateVolume(volume, contextValidation);
		validateSize(size, contextValidation);
		validateRules(this, contextValidation);
	}

	@Override
	public TraceInformation getTraceInformation() {
		if (traceInformation == null)
			traceInformation = new TraceInformation();
		return traceInformation;
	}

	
}
