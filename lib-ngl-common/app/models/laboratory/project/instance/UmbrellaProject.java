package models.laboratory.project.instance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.project.instance.ProjectValidationHelper;

import models.laboratory.common.instance.TraceInformation;
import models.utils.InstanceConstants;
import org.mongojack.MongoCollection;
import fr.cea.ig.DBObject;

/**
 * Instance UmbrellaProject is stocked in a MongoDB Collection named "ngl_project.UmbrellaProject"
 * An UmbrellaProject references one to n projects
 * Use by "ngl-projects" application
 * 
 * @author dnoisett
 *
 */

@MongoCollection(name="UmbrellaProject")
public class UmbrellaProject extends DBObject implements IValidation {

	public String name;
	public TraceInformation traceInformation;
	public List<String> projectCodes;
	
	
	public UmbrellaProject(){
		traceInformation=new TraceInformation();
	}
	
	public UmbrellaProject(String code, String name){
		this.code=code;
		this.name=name;
		traceInformation=new TraceInformation();
	}
	
	@Override
	@JsonIgnore
	public void validate(ContextValidation contextValidation) {
				
		ProjectValidationHelper.validateId(this, contextValidation);
		ProjectValidationHelper.validateCode(this, InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, contextValidation);
		ProjectValidationHelper.validateTraceInformation(traceInformation, contextValidation);

	}
	
}
