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

// import org.mongojack.MongoCollection;

import validation.ContextValidation;
import validation.ICRUDValidation;
import validation.IValidation;
// import validation.experiment.instance.ContainerUsedValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.ICommentable;
import fr.cea.ig.DBObject;

// link to this : {@link models.laboratory.container.instance.Container}

/**
 * 
 * Container is referenced from Experiment, Purifying, TransferMethod, 
 * Extraction, QC in embedded class ListInputOutputContainer.
 * The Relationship between containers aren't stored in the container 
 * but in class/collection RelationshipContainer. 
 * In Container, the link with experiment is the attribute 'fromExperimentTypes' 
 * who helps to manage Container in workflow. 
 *  
 * Container collection name is defined as {@link models.utils.InstanceConstants#CONTAINER_COLL_NAME}.
 * 
 * @author mhaquell
 * @author vrd
 *
 */
// @MongoCollection(name="Container")
public class Container extends DBObject implements IValidation, ITracingAccess, ICommentable, ICRUDValidation<Container> {

	//duplication for input in exp : code, categoryCode, contents, mesured*, //contents just for tag and tagCategory 
	//duplication for output in exp :code, categoryCode, contents, mesured*, //contents just for tag and tagCategory
	
	
	public String importTypeCode;
	
	/**
	 * Category code ({@link models.laboratory.container.description.ContainerCategory}). 
	 */
	public String categoryCode;

	/**
	 * State information, see {@link models.laboratory.common.instance.State} for definition.
	 */
	public State state;
	
	/**
	 * Valuation information.
	 */
	public Valuation valuation;

	/**
	 * Access information.
	 */
	public TraceInformation traceInformation;
	
	/**
	 * Properties.
	 */
	public Map<String, PropertyValue> properties;
	
	/**
	 * Comments.
	 */
	public List<Comment> comments = new ArrayList<Comment>(0);

	/**
	 * Relation with container support.
	 */
	public LocationOnContainerSupport support; 

	/**
	 * Content description as a list of content that describe the
	 * parts contained in this container (percentages of samples).
	 */
	public List<Content> contents;
	
	/**
	 * QC Experiment results (projection). 
	 */
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
	
	/**
	 * Container history tree.
	 */
	public TreeOfLifeNode treeOfLife;
	
	
	public Container() {
		//properties=new HashMap<String, PropertyValue>();
		contents          = new ArrayList<Content>();
		traceInformation  = new TraceInformation();
		projectCodes      = new HashSet<String>();
		sampleCodes       = new HashSet<String>();
		//comments = new ArrayList<>();
		//qualityControlResults = new HashSet<>();
		fromTransformationTypeCodes = new HashSet<>();
		valuation         = new Valuation();	
	}
		
	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation){
		
		if (contextValidation.getObject(FIELD_STATE_CODE) == null) {
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

	// IAccessTracking
	
	@Override
	public TraceInformation getTraceInformation() {
		if (traceInformation == null)
			traceInformation = new TraceInformation();
		return traceInformation;
	}

	// ICommentable
	
	@Override
	public List<Comment> getComments() {
		return comments;
	}

	@Override
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

}
