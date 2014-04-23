package models.laboratory.project.instance;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import validation.ContextValidation;
import validation.IValidation;
import validation.project.instance.ProjectValidationHelper;

import models.laboratory.common.instance.TraceInformation;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.MongoCollection;
import fr.cea.ig.DBObject;

/**
 * Instance ProjectUmbrella is stocked in Collection mongodb ProjectUmbrella
 * A projectUmbrella contains from one to n projects
 * Use by "ngl-projects" application
 * 
 * @author dnoisett
 *
 */

@MongoCollection(name="ProjectUmbrella")
public class ProjectUmbrella extends DBObject implements IValidation {

	public String name;
	public TraceInformation traceInformation;
	public List<String> projectCodes;
	
	
	public ProjectUmbrella(){
		traceInformation=new TraceInformation();
	}
	
	public ProjectUmbrella(String code, String name){
		this.code=code;
		this.name=name;
		traceInformation=new TraceInformation();
	}
	
	@Override
	@JsonIgnore
	public void validate(ContextValidation contextValidation) {
				
		ProjectValidationHelper.validateId(this, contextValidation);
		ProjectValidationHelper.validateCode(this, InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, contextValidation);
		ProjectValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		
		ProjectValidationHelper.synchronizeProjectLists(this, contextValidation);

	}
	
}
