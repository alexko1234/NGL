package fr.cea.ig.ngl.dao.projects;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.mongo.MongoStreamer;
import fr.cea.ig.ngl.dao.GenericMongoDAO;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import play.data.validation.ValidationError;
import validation.ContextValidation;

@Singleton
public class ProjectsAPI extends GenericAPI<ProjectsDAO, Project>{

	private static final play.Logger.ALogger logger = play.Logger.of(ProjectsAPI.class);
	
	@Inject
	public ProjectsAPI(ProjectsDAO dao) {
		super(dao);
	}
	
	public Iterable<Project> all() throws APIException {
		return dao().all();
	}
	
	public Project create(Project project, String currentUser) throws APIException, APIValidationException {
		ContextValidation ctxVal = new ContextValidation(currentUser); 
		if (project._id == null) { 
			project.traceInformation = new TraceInformation();
			project.traceInformation.creationStamp(ctxVal, currentUser);
			
			if(null == project.state){
				project.state = new State();
			}
			project.state.code = "N";
			project.state.user = currentUser;
			project.state.date = new Date();		
			
		} else {
			throw new APIException("create method does not update existing objects"); 
		}
		
		ctxVal.setCreationMode();
		project.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			return dao().saveObject(project);
		} else {
			throw new APIValidationException("invalid input", ctxVal.getErrors());
		}
	}
	
	// TODO may be need to change implementation
	public Project update(Project input, String currentUser, List<String> fields) throws APIException, APIValidationException {
		return this.update(input, currentUser);
		
	}
	
	public Project update(Project input, String currentUser) throws APIException, APIValidationException {
		Project project = this.get(input.code);
		if (project == null) {
			throw new APIException("Project with code " + input.code + " not exist");
		} else {
			ContextValidation ctxVal = new ContextValidation(currentUser); 
			if(input.traceInformation != null) {
				input.traceInformation.modificationStamp(ctxVal, currentUser);
			}else {
				logger.error("traceInformation is null !!");
			}
			ctxVal.setUpdateMode();
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				dao().updateObject(input);
				return input;
			} else {
				throw new APIValidationException("Invalid Project object", ctxVal.getErrors());
			}
		}
	}
}
