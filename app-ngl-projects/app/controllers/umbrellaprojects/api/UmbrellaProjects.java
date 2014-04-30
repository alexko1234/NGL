package controllers.umbrellaprojects.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.project.instance.UmbrellaProject;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.QueryFieldsForm;
import controllers.projects.api.ProjectsController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
/**
 * Controller around Project object
 *
 */
public class UmbrellaProjects extends ProjectsController {

	
	final static Form<UmbrellaProjectsSearchForm> searchForm = form(UmbrellaProjectsSearchForm.class); 
	final static Form<UmbrellaProject> projectForm = form(UmbrellaProject.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("keep");


	public static Result list(){
		Form<UmbrellaProjectsSearchForm> filledForm = filledFormQueryString(searchForm, UmbrellaProjectsSearchForm.class);
		UmbrellaProjectsSearchForm form = filledForm.get();
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<UmbrellaProject> results = mongoDBFinder(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, form, UmbrellaProject.class, getQuery(form), keys);			
			List<UmbrellaProject> umbrellaProjects = results.toList();
			return ok(Json.toJson(new DatatableResponse<UmbrellaProject>(umbrellaProjects, results.count())));
		} else if (form.list) {
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			if (null == form.orderBy) form.orderBy = "code";
			if (null == form.orderSense) form.orderSense = 0;
			MongoDBResult<UmbrellaProject> results = mongoDBFinder(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, form, UmbrellaProject.class, getQuery(form), keys);			
			List<UmbrellaProject> umbrellaProjects = results.toList();			
			return ok(Json.toJson(toListObjects(umbrellaProjects)));
		} else {
			if (null == form.orderBy) form.orderBy = "code";
			if (null == form.orderSense) form.orderSense = 0;
			MongoDBResult<UmbrellaProject> results = mongoDBFinder(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, form, UmbrellaProject.class, getQuery(form), keys);	
			List<UmbrellaProject> projects = results.toList();
			return ok(Json.toJson(projects));
		}
	}

	

	private static List<ListObject> toListObjects(List<UmbrellaProject> proj) {
		List<ListObject> lo = new ArrayList<ListObject>();
		for (UmbrellaProject p : proj) {
			lo.add(new ListObject(p.code, p.name));
		}
		return lo;
	}
	
	private static Query getQuery(UmbrellaProjectsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if (CollectionUtils.isNotEmpty(form.projectCodes)) {
			queries.add(DBQuery.in("code", form.projectCodes));
		} else if(StringUtils.isNotBlank(form.projectCode)) {
			queries.add(DBQuery.is("code", form.projectCode));
		}
		
		
		if (queries.size() > 0) {
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}
	
	public static Result get(String code) {
		UmbrellaProject projectValue = getUmbrellaProject(code);
		if (projectValue != null) {		
			return ok(Json.toJson(projectValue));					
		} else {
			return notFound();
		}
	}
	
	public static Result head(String code) {
		if (MongoDBDAO.checkObjectExistByCode(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProject.class, code)) { 			
			return ok();					
		} else {
			return notFound();
		}
	}


	public static Result save() {
		Form<UmbrellaProject> filledForm = getFilledForm(projectForm, UmbrellaProject.class);
		UmbrellaProject projectInput = filledForm.get();

		if (null == projectInput._id) { 
			projectInput.traceInformation = new TraceInformation();
			projectInput.traceInformation.setTraceInformation(getCurrentUser());			
		} else {
			return badRequest("use PUT method to update the project");
		}
		
		synchronizeUmbrellaProjectCodes(projectInput);

		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		ctxVal.setCreationMode();
		projectInput.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			projectInput = MongoDBDAO.save(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, projectInput);
			return ok(Json.toJson(projectInput));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}


	public static Result update(String code) {
		UmbrellaProject project = getUmbrellaProject(code);
		if (project == null) {
			return badRequest("ProjectUmbrella with code "+code+" not exist");
		}

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Form<UmbrellaProject> filledForm = getFilledForm(projectForm, UmbrellaProject.class);
		UmbrellaProject projectInput = filledForm.get();
		
		synchronizeUmbrellaProjectCodes(projectInput);		
		
		if(queryFieldsForm.fields == null){
			if (code.equals(projectInput.code)) {
				if(null != projectInput.traceInformation){
					projectInput.traceInformation.setTraceInformation(getCurrentUser());
				}else{
					Logger.error("traceInformation is null !!");
				}
				
				ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 	
				ctxVal.setUpdateMode();
				projectInput.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, projectInput);
					return ok(Json.toJson(projectInput));
				}else {
					return badRequest(filledForm.errorsAsJson());
				}
				
			}else{
				return badRequest("Project codes are not the same");
			}	
		}else{
			//warning no validation !!!
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			if(!filledForm.hasErrors()){
				TraceInformation ti = project.traceInformation;
				ti.setTraceInformation(getCurrentUser());
				MongoDBDAO.update(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProject.class, 
						DBQuery.and(DBQuery.is("code", code)), getBuilder(projectInput, queryFieldsForm.fields, UmbrellaProject.class).set("traceInformation", ti));
				return ok(Json.toJson(getUmbrellaProject(code)));
			}else{
				return badRequest(filledForm.errorsAsJson());
			}			
		}
	}


	public static Result delete(String code) {
		UmbrellaProject project = getUmbrellaProject(code);
		if (project == null) {
			return badRequest();
		}		
		MongoDBDAO.delete(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, project);	
		return ok();
	}
	
	public static void synchronizeUmbrellaProjectCodes(UmbrellaProject umbrellaProject) {
		for (String code : umbrellaProject.projectCodes) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.in("umbrellaProjectCodes", umbrellaProject.code)))) {
				//add the value in the other list : the child project needs to be linked to his father! 
				MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code", code), 
						DBUpdate.push("umbrellaProjectCodes", umbrellaProject.code));
			}
		}
	}

}

