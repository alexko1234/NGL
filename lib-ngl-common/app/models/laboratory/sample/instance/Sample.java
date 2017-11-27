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

import org.mongojack.MongoCollection;

import validation.ContextValidation;
import validation.IValidation;
import validation.sample.instance.SampleValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import controllers.ICommentable;
import fr.cea.ig.DBObject;

/**
 * 
 * Instances Sample are stored in MongoDB collection named Sample
 *  
 * Sample is referenced in Content
 *  
 * @author mhaquell
 *
 */
@MongoCollection(name="Sample")
public class Sample extends DBObject implements IValidation, ITracingAccess, ICommentable {


	@JsonIgnore
	public final static String HEADER="Sample.code;Sample.projectCodes;Sample.name;Sample.referenceCollab;Sample.taxonCode;Sample.comments";

	// SampleType Ref
	public String typeCode;

	public String importTypeCode;
	//Sample Category Ref
	public String categoryCode;

	public Set<String> projectCodes;

	// ?? Wath is difference with code / referenceCollbab => code s'est interne au genoscope
	public String name;
	public String referenceCollab; 
	public Map<String,PropertyValue> properties;
	// Valid taxon
	public Valuation valuation;
	//public List<CollaboratorInvolve> collaborators;
	public String taxonCode;
	public String ncbiScientificName;
	public String ncbiLineage;
	public List<Comment> comments = new ArrayList<Comment>(0);
	public TraceInformation traceInformation;

	public SampleLife life;
	
	public List<SampleProcess> processes;
	public SampleProcessesStatistics processesStatistics;
	public Date processesUpdatedDate;
	
	public Sample(){
		this.traceInformation=new TraceInformation();
	}


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

    	SampleValidationHelper.validateId(this, contextValidation);
		SampleValidationHelper.validateCode(this, InstanceConstants.SAMPLE_COLL_NAME, contextValidation);

		SampleValidationHelper.validateSampleCategoryCode(categoryCode,contextValidation);
		SampleValidationHelper.validateProjectCodes(this.projectCodes, contextValidation);

		SampleValidationHelper.validateSampleType(typeCode,importTypeCode,properties,contextValidation);
		SampleValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		SampleValidationHelper.validateRules(this, contextValidation);
		//TODO validation taxon
		
	}


	// Interfaces implementations
	
	@Override
	public TraceInformation getTraceInformation() {
		if (traceInformation == null)
			traceInformation = new TraceInformation();
		return traceInformation;
	}

	@Override
	public List<Comment> getComments() {
		return comments;
	}


	@Override
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
}
