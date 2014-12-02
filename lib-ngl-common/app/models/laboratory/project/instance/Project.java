package models.laboratory.project.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.utils.HelperObjects;
import models.utils.InstanceConstants;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.project.instance.ProjectValidationHelper;
import fr.cea.ig.DBObject;



/**
 * Instance Project is stocked in Collection mongodb Project
 * Project Name are referencing in class Experience and Container
 * 
 * @author mhaquell
 *
 */
@MongoCollection(name="Project")
public class Project extends DBObject implements IValidation{

	public String name;
	public String typeCode;
	public String categoryCode;
	public State state;
    public String description;
    public List<Comment> comments;
	public TraceInformation traceInformation;
	public Map<String, PropertyValue> properties;
	public String umbrellaProjectCode;
	public BioinformaticParameters bioinformaticParameters;
	

	@Override
	@JsonIgnore
	public void validate(ContextValidation contextValidation) {				
		ProjectValidationHelper.validateId(this, contextValidation);
		ProjectValidationHelper.validateCode(this, InstanceConstants.PROJECT_COLL_NAME, contextValidation);
		ProjectValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		ProjectValidationHelper.validateProjectType(typeCode,properties, contextValidation);
		ProjectValidationHelper.validateProjectCategoryCode(categoryCode,contextValidation);
		ProjectValidationHelper.validateState(typeCode,state, contextValidation);
		ProjectValidationHelper.validateUmbrellaProjectCode(umbrellaProjectCode, contextValidation);
		ProjectValidationHelper.validateBioformaticParameters(bioinformaticParameters,contextValidation);
	}

}
