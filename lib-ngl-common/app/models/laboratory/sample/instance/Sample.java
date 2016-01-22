package models.laboratory.sample.instance;

import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.utils.InstanceConstants;

import org.mongojack.MongoCollection;

import validation.ContextValidation;
import validation.IValidation;
import validation.sample.instance.SampleValidationHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Sample extends DBObject implements IValidation{


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

	public List<Comment> comments;
	public TraceInformation traceInformation;

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
		//TODO validation taxon
	}



}
