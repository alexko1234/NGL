package fr.cea.ig.ngl.dao.projects;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import controllers.projects.api.ProjectsSearchForm;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.ValidationException;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import play.data.Form;
import validation.ContextValidation;

@Singleton
public class ProjectAPI {

	private final ProjectDAO dao; 
	
	@Inject
	public ProjectAPI(ProjectDAO dao) {
		this.dao = dao;
	}
	
	public Iterable<Project> all() throws APIException {
		return dao.all();
	}

	public Project get(String code) throws APIException {
		return dao.findByCode(code);
	}
	
	public List<Project> list(ProjectsSearchForm form, Query query, BasicDBObject keys) throws APIException {
		if (form.datatable) {
			return datatableFormList(form, query, keys);
		} else if(form.list) {
			return listFormList(form, query);
		} else {
			return null;
		}
	}

	private List<Project> listFormList(ProjectsSearchForm form, Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Project> datatableFormList(ProjectsSearchForm form, Query query, BasicDBObject keys) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Project create(Form<Project> form, String currentUser) throws ValidationException {
		Project projectInput = form.get();
		ContextValidation ctxVal = new ContextValidation(currentUser, form.errors()); 
		if (projectInput._id == null) { 
			projectInput.traceInformation = new TraceInformation();
			projectInput.traceInformation.creationStamp(ctxVal, currentUser);
			
			if(null == projectInput.state){
				projectInput.state = new State();
			}
			projectInput.state.code = "N";
			projectInput.state.user = currentUser;
			projectInput.state.date = new Date();		
			
		} else {
			throw new ValidationException("use PUT method to update the project");
		}
		
		ctxVal.setCreationMode();
		projectInput.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			return dao.saveObject(projectInput);
		} else {
			throw new ValidationException("invalid input", ctxVal.getErrors());
		}
	}
}
