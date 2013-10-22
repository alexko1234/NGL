package models.laboratory.sample.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleCategory;
import models.laboratory.sample.description.SampleType;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;


import play.Logger;
import validation.ContextValidation;
import validation.DescriptionValidationHelper;
import validation.IValidation;
import validation.InstanceValidationHelper;
import validation.common.instance.CommonValidationHelper;
import validation.container.instance.SampleUsedValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ValidationHelper;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

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

	public List<String> projectCodes;

	// ?? Wath is difference with code / referenceCollbab => code s'est interne au genoscope
	public String name;
	public String referenceCollab; 
	public Map<String,PropertyValue> properties;
	// Valid taxon
	public TBoolean valid;
	//public List<CollaboratorInvolve> collaborators;
	public String taxonCode;

	public List<Comment> comments;
	public TraceInformation traceInformation;

	public Sample(){
		this.traceInformation=new TraceInformation();
	}


	@JsonIgnore
	public SampleType getSampleType(){
		return new HelperObjects<SampleType>().getObject(SampleType.class, typeCode);
	}

	@JsonIgnore
	public SampleCategory getSampleCategory(){
		return new HelperObjects<SampleCategory>().getObject(SampleCategory.class, categoryCode);
	}

	@JsonIgnore
	public List<Project> getProjects(){
		return new HelperObjects<Project>().getObjects(Project.class, projectCodes);
	}


	@JsonIgnore
	@Override
	public void validate(ContextValidation contextValidation) {

		contextValidation.putObject("_id",this._id);
		
		BusinessValidationHelper.validateUniqueInstanceCode(contextValidation, this.code, Sample.class, InstanceConstants.SAMPLE_COLL_NAME);

		SampleUsedValidationHelper.validateSampleCategoryCode(categoryCode,contextValidation);
		CommonValidationHelper.validateProjectCodes(this.projectCodes, contextValidation);

		DescriptionValidationHelper.validationSampleType(typeCode,importTypeCode,properties,contextValidation);
		
		traceInformation.validate(contextValidation);
	
		InstanceValidationHelper.validationComments(comments, contextValidation);

		//TODO validation taxon
	}



}
