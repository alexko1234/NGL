package fr.cea.ig.ngl.dao.projects;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.GenericAPI;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import validation.ContextValidation;

@Singleton
public class ProjectsAPI extends GenericAPI<ProjectsDAO, Project> {

	private static final play.Logger.ALogger logger = play.Logger.of(ProjectsAPI.class);
	
	@Inject
	public ProjectsAPI(ProjectsDAO dao) {
		super(dao);
	}
	
	@Override
	protected List<String> authorizedUpdateFields() {
		//TODO implement
		return null;
	}


	@Override
	protected List<String> defaultKeys() {
		//TODO implement
		return null;
	}
	
	public Iterable<Project> all() throws APIException {
		return dao.all();
	}
	
	@Override
	public Project create(Project project, String currentUser) throws APIException, APIValidationException {
		ContextValidation ctxVal = new ContextValidation(currentUser);
		if (project._id != null)
			throw new APIException("create method does not update existing objects"); 
		project.traceInformation = new TraceInformation();
		project.traceInformation.creationStamp(ctxVal, currentUser);
		if (project.state == null)
			project.state = new State();
		project.state.code = "N";
		project.state.user = currentUser;
		project.state.date = new Date();
		
		ctxVal.setCreationMode();
		project.validate(ctxVal);
		if (ctxVal.hasErrors())
			throw new APIValidationException("invalid input", ctxVal.getErrors());
		return dao.saveObject(project);
	}
	
	// TODO may be need to change implementation
	@Override
	public Project update(Project input, String currentUser, List<String> fields) throws APIException, APIValidationException {
		return update(input, currentUser);
		
	}
	
	@Override
	public Project update(Project input, String currentUser) throws APIException, APIValidationException {
		Project project = this.get(input.code);
		if (project == null)
			throw new APIException("Project with code " + input.code + " not exist");
		ContextValidation ctxVal = new ContextValidation(currentUser); 
		if (input.traceInformation != null) {
			input.traceInformation.modificationStamp(ctxVal, currentUser);
		} else {
			logger.error("traceInformation is null !!");
		}
		ctxVal.setUpdateMode();
		input.validate(ctxVal);
		if (ctxVal.hasErrors()) 
			throw new APIValidationException("Invalid Project object", ctxVal.getErrors());		
		dao.updateObject(input);
		return input;
	}
	
}
