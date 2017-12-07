package models.laboratory.sample.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.ITracingAccess;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.sample.instance.tree.SampleLife;
import models.utils.InstanceConstants;
import models.laboratory.sample.instance.reporting.SampleProcess;
import models.laboratory.sample.instance.reporting.SampleProcessesStatistics;

// import org.mongojack.MongoCollection;

import validation.ContextValidation;
import validation.IValidation;
import validation.sample.instance.SampleValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.ICommentable;
import fr.cea.ig.DBObject;

//Link to this : {@link models.laboratory.sample.instance.Sample}

// TODO: cleanup, comment
// TODO: create a base class DBOject,ITracingAccess,ICommentable,IValidation

/**
 * Sample information as required by the laboratory (the L in LIMS).
 * 
 * Sample collection name is defined as {@link models.utils.InstanceConstants#SAMPLE_COLL_NAME}.
 * 
 * @author mhaquell
 * @author vrd
 * 
 */
// @MongoCollection(name="Sample")
public class Sample extends DBObject implements IValidation, ICommentable, ITracingAccess {

	// @JsonIgnore
	// TODO: explain
	public final static String HEADER = 
			"Sample.code;Sample.projectCodes;Sample.name;Sample.referenceCollab;Sample.taxonCode;Sample.comments";

	// ngl-data/services.description.sample.SampleServiceCNS
	
	/*
	 * Type code, defined in ngl-data project {@link services.description.sample.SampleServiceCNS}.
	 */
	public String typeCode;
	
	/**
	 * Import source type (import file type or so).
	 */
	public String importTypeCode;

	/*
	 * Sample type category code, implied by the type definition and defined 
	 * in ngl-data project {@link services.description.sample.SampleServiceCNS}.
	 */
	public String categoryCode;

	/**
	 * Set of projects code this sample is used in.
	 */
	public Set<String> projectCodes;

	// ?? Wath is difference with code / referenceCollbab => code s'est interne au genoscope
	/**
	 * Name = localized code (default:null)
	 */
	public String name;
	
	/**
	 * Name of the sample in the collab referential
	 */
	public String referenceCollab;
	
	// TODO: use Map<String,PropertyValue<?>>
	/**
	 * Mandatory : meta : false (meta:metagneomnic,metatrucs)
	 */
	public Map<String,PropertyValue> properties;
	
	/**
	 * Unused (not yet ?).
	 */
	public Valuation valuation;
	//public List<CollaboratorInvolve> collaborators;
	
	// Expanded taxonomy information retrieved from the NCBI.
	// See https://www.ncbi.nlm.nih.gov/taxonomy.
	
	// TODO: describe if/how the taxonCode can be changed for a sample.
	/**
	 * Taxonomy code (@see <a href="https://www.ncbi.nlm.nih.gov/taxonomy">taxonomy</a>).
	 */
	public String taxonCode;
	
	/**
	 * Scientific name (@see <a href="https://www.ncbi.nlm.nih.gov/taxonomy">taxonomy</a>).
	 * Auto filled (at least on prod).
	 */
	public String ncbiScientificName;
	
	/**
	 * Lineage (@see <a href="https://www.ncbi.nlm.nih.gov/taxonomy">taxonomy</a>).
	 * Auto filled (at least on prod).
	 */
	public String ncbiLineage;
	
	/**
	 * Comments.
	 */
	public List<Comment> comments;
	
	/**
	 * System maintained access information.
	 */
	public TraceInformation traceInformation;

	/**
	 * Sample parent if any.
	 * 
	 * life: {
from: {
projectCode: "BUP",
sampleCode: "BUP_AAAA",
sampleTypeCode: "DNA",
experimentCode: "TAG-PCR-20160819_130125AHH",
experimentTypeCode: "tag-pcr",
containerCode: "18ID3I6DL",
supportCode: "18ID3I6DL",
fromTransformationTypeCodes: null,
fromTransformationCodes: null,
processTypeCodes: [
"tag-pcr-and-dna-library"
],
processCodes: [
"BUP_AAAA_TAG-PCR-AND-DNA-LIBRARY_18IF26TQZ"
],
},
path: ",CO-0000140,BUP_AAAA",
},
	 */
	public SampleLife life;
	
	/**
	 * List of projections of process that use this sample.
	 * Populated when experiments/processes are done.
	 */
	public List<SampleProcess> processes;
	
	/**
	 * Summary (count redeasets & stuff).
	 */
	public SampleProcessesStatistics processesStatistics;
	
	/**
	 * Time of the last automated update.
	 */
	public Date processesUpdatedDate;
	
	/**
	 * Constructs a new Sample.
	 */
	public Sample() {
		// TODO: remove trace information initialization as it is not needed
		traceInformation = new TraceInformation();
		comments         = new ArrayList<Comment>(0);
	}


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

    	SampleValidationHelper.validateId(this, contextValidation);
		SampleValidationHelper.validateCode(this, InstanceConstants.SAMPLE_COLL_NAME, contextValidation);

		SampleValidationHelper.validateSampleCategoryCode(categoryCode,contextValidation);
		SampleValidationHelper.validateProjectCodes(projectCodes, contextValidation);

		SampleValidationHelper.validateSampleType(typeCode,importTypeCode,properties,contextValidation);
		SampleValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		SampleValidationHelper.validateRules(this, contextValidation);
		// TODO: validation taxon
		
	}


	// Interfaces implementations
	
	// We cannot @JsonIgnore setters or getters otherwise the corresponding
	// field serialization is disabled.
	
	// IAccessTracking
	
	//@JsonIgnore
	@Override
	public TraceInformation getTraceInformation() {
		if (traceInformation == null)
			traceInformation = new TraceInformation();
		return traceInformation;
	}

	// ICommentable
	
	//@JsonIgnore
	@Override
	public List<Comment> getComments() {
		return comments;
	}

	//@JsonIgnore
	@Override
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
}
