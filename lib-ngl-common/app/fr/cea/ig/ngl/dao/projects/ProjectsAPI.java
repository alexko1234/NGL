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
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import play.data.validation.ValidationError;
import validation.ContextValidation;

@Singleton
public class ProjectsAPI {

	private final ProjectsDAO dao; 
	private static final play.Logger.ALogger logger = play.Logger.of(ProjectsAPI.class);
	
	@Inject
	public ProjectsAPI(ProjectsDAO dao) {
		this.dao = dao;
	}
	
	public Iterable<Project> all() throws APIException {
		return dao.all();
	}

	public Project get(String code) {
		return dao.findByCode(code);
	}
	
	public boolean isObjectExist(String code) {
		return dao.isObjectExist(code);
	}
	
	public List<Project> list(Query query, String orderBy, Sort orderSense) {
		return dao.mongoDBFinder(query, orderBy, orderSense).toList();
	}
	
	public List<Project> list(Query query, String orderBy, Sort orderSense, 
			Integer pageNumber, Integer numberRecordsPerPage) {
		return dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage).toList();
	}
	
	public List<Project> list(Query query, String orderBy, Sort orderSense, BasicDBObject keys) {
		return dao.mongoDBFinder(query, orderBy, orderSense, keys).toList();
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys) {
		return MongoStreamer.streamUDT(dao.mongoDBFinder(query, orderBy, orderSense, keys));
	}
	
	public List<Project> list(Query query, String orderBy, Sort orderSense, BasicDBObject keys, Integer limit) {
		return dao.mongoDBFinder(query, orderBy, orderSense, limit, keys).toList();
	}
	
	public List<Project> list(Query query, String orderBy, Sort orderSense, BasicDBObject keys, 
			Integer pageNumber, Integer numberRecordsPerPage) {
		return dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, keys).toList();
	}
	
	public Source<ByteString, ?> stream(Query query, String orderBy, Sort orderSense, BasicDBObject keys, 
			Integer pageNumber, Integer numberRecordsPerPage) {
		return MongoStreamer.streamUDT(dao.mongoDBFinderWithPagination(query, orderBy, orderSense, pageNumber, numberRecordsPerPage, keys));
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
			return dao.saveObject(project);
		} else {
			throw new APIValidationException("invalid input", ctxVal.getErrors());
		}
	}
	
	public void delete(String code) {
		this.dao.deleteObject(code);
	}
	
	public Project update(String code, Project input, String currentUser) throws APIException, APIValidationException {
		Project project = this.get(code);
		if (project == null) {
			throw new APIException("Project with code " + code + " not exist");
		}
		ContextValidation ctxVal = new ContextValidation(currentUser); 
		if (code.equals(input.code)) {
			if(input.traceInformation != null){
				input.traceInformation.modificationStamp(ctxVal, currentUser);
			}else{
				logger.error("traceInformation is null !!");
			}
			ctxVal.setUpdateMode();
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				dao.updateObject(input);
				return input;
			} else {
				throw new APIValidationException("Invalid Project object", ctxVal.getErrors());
			}
			
		} else {
			throw new APIException("Project codes are not the same");
		}
		
	}
}
