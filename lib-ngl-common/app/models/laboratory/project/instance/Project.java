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
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.InstanceValidationHelper;
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

	public String typeCode;
	public String categoryCode;
	public String name;
	public State state;
	public TraceInformation traceInformation;
	public Map<String, PropertyValue> properties;
	public List<Comment> comments;
	
	
	public Project(){
		traceInformation=new TraceInformation();
	}
	
	public Project(String code, String name){
		this.code=code;
		this.name=name;
		traceInformation=new TraceInformation();
	}
	
	@JsonIgnore
	public ProjectType getProjectType(){
			return new HelperObjects<ProjectType>().getObject(ProjectType.class, typeCode);
	}
	
	@JsonIgnore
	public ProjectCategory getProjectCategory(){
			return new HelperObjects<ProjectCategory>().getObject(ProjectCategory.class, categoryCode);
	}


	@Override
	@JsonIgnore
	public void validate(ContextValidation contextValidation) {
				
		ProjectValidationHelper.validateId(this, contextValidation);
		ProjectValidationHelper.validateCode(this, InstanceConstants.PROJECT_COLL_NAME, contextValidation);
		ProjectValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		ProjectValidationHelper.validateProjectCategoryCode(categoryCode,contextValidation);
		//ProjectValidationHelper.validateStateCode(typeCode,stateCode, contextValidation);
		ProjectValidationHelper.validateProjectType(typeCode,properties, contextValidation);

	}

}
