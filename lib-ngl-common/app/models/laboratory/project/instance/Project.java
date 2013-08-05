package models.laboratory.project.instance;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.State;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.description.ProjectCategory;
import models.laboratory.project.description.ProjectType;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;

import org.codehaus.jackson.annotate.JsonIgnore;


import validation.DescriptionValidationHelper;
import validation.InstanceValidationHelper;
import validation.utils.BusinessValidationHelper;
import validation.utils.ConstraintsHelper;
import validation.utils.ContextValidation;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;



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
	public String stateCode;
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

	@JsonIgnore
	public State getState(){
			return new HelperObjects<State>().getObject(State.class, stateCode);
	}

	@Override
	@JsonIgnore
	public void validate(ContextValidation contextValidation) {
		
		contextValidation.contextObjects.put("_id",this._id);
		BusinessValidationHelper.validateUniqueInstanceCode(contextValidation, code, Project.class, InstanceConstants.PROJECT_COLL_NAME);

		DescriptionValidationHelper.validationProjectCategoryCode(categoryCode,contextValidation);
		InstanceValidationHelper.validationStateCode(stateCode, contextValidation);
		
		DescriptionValidationHelper.validationProject(typeCode,properties, contextValidation);
		
		traceInformation.validate(contextValidation);
		for(Comment comment:comments){
			comment.validate(contextValidation);
		}
	}

}
