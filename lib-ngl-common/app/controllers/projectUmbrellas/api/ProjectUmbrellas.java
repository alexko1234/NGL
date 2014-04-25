package controllers.projectUmbrellas.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.project.instance.ProjectUmbrella;
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
public class ProjectUmbrellas extends ProjectsController {

	
	final static Form<ProjectUmbrellasSearchForm> searchForm = form(ProjectUmbrellasSearchForm.class); 
	final static Form<ProjectUmbrella> projectForm = form(ProjectUmbrella.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("keep");


	public static Result list(){
		Form<ProjectUmbrellasSearchForm> filledForm = filledFormQueryString(searchForm, ProjectUmbrellasSearchForm.class);
		ProjectUmbrellasSearchForm form = filledForm.get();
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<ProjectUmbrella> results = mongoDBFinder(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, form, ProjectUmbrella.class, getQuery(form), keys);			
			List<ProjectUmbrella> projectUmbrellas = results.toList();
			return ok(Json.toJson(new DatatableResponse<ProjectUmbrella>(projectUmbrellas, results.count())));
		} else if (form.list) {
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			if (null == form.orderBy) form.orderBy = "code";
			if (null == form.orderSense) form.orderSense = 0;
			MongoDBResult<ProjectUmbrella> results = mongoDBFinder(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, form, ProjectUmbrella.class, getQuery(form), keys);			
			List<ProjectUmbrella> projectUmbrellas = results.toList();			
			return ok(Json.toJson(toListObjects(projectUmbrellas)));
		} else {
			if (null == form.orderBy) form.orderBy = "code";
			if (null == form.orderSense) form.orderSense = 0;
			MongoDBResult<ProjectUmbrella> results = mongoDBFinder(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, form, ProjectUmbrella.class, getQuery(form), keys);	
			List<ProjectUmbrella> projects = results.toList();
			return ok(Json.toJson(projects));
		}
	}

	

	private static List<ListObject> toListObjects(List<ProjectUmbrella> proj) {
		List<ListObject> lo = new ArrayList<ListObject>();
		for (ProjectUmbrella p : proj) {
			lo.add(new ListObject(p.code, p.name));
		}
		return lo;
	}
	
	private static Query getQuery(ProjectUmbrellasSearchForm form) {
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
		ProjectUmbrella projectValue = getProjectUmbrella(code);
		if (projectValue != null) {		
			return ok(Json.toJson(projectValue));					
		} else {
			return notFound();
		}
	}
	
	public static Result head(String code) {
		if (MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, ProjectUmbrella.class, code)) { 			
			return ok();					
		} else {
			return notFound();
		}
	}


	public static Result save() {
		Form<ProjectUmbrella> filledForm = getFilledForm(projectForm, ProjectUmbrella.class);
		ProjectUmbrella projectInput = filledForm.get();

		if (null == projectInput._id) { 
			projectInput.traceInformation = new TraceInformation();
			projectInput.traceInformation.setTraceInformation(getCurrentUser());			
		} else {
			return badRequest("use PUT method to update the project");
		}
		
		synchronizeProjectUmbrellaCodes(projectInput);

		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		ctxVal.setCreationMode();
		projectInput.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			projectInput = MongoDBDAO.save(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, projectInput);
			return ok(Json.toJson(projectInput));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}


	public static Result update(String code) {
		ProjectUmbrella project = getProjectUmbrella(code);
		if (project == null) {
			return badRequest("ProjectUmbrella with code "+code+" not exist");
		}

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Form<ProjectUmbrella> filledForm = getFilledForm(projectForm, ProjectUmbrella.class);
		ProjectUmbrella projectInput = filledForm.get();
		
		if(queryFieldsForm.fields == null){
			if (code.equals(projectInput.code)) {
				if(null != projectInput.traceInformation){
					projectInput.traceInformation.setTraceInformation(getCurrentUser());
				}else{
					Logger.error("traceInformation is null !!");
				}
				
				synchronizeProjectUmbrellaCodes(projectInput);
				
				ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 	
				ctxVal.setUpdateMode();
				projectInput.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, projectInput);
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
				MongoDBDAO.update(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, ProjectUmbrella.class, 
						DBQuery.and(DBQuery.is("code", code)), getBuilder(projectInput, queryFieldsForm.fields, ProjectUmbrella.class).set("traceInformation", ti));
				return ok(Json.toJson(getProjectUmbrella(code)));
			}else{
				return badRequest(filledForm.errorsAsJson());
			}			
		}
	}


	public static Result delete(String code) {
		ProjectUmbrella project = getProjectUmbrella(code);
		if (project == null) {
			return badRequest();
		}		
		MongoDBDAO.delete(InstanceConstants.PROJECT_UMBRELLA_COLL_NAME, project);	
		return ok();
	}
	
	public static void synchronizeProjectUmbrellaCodes(ProjectUmbrella projectUmbrella) {
		for (String code : projectUmbrella.projectCodes) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.in("projectUmbrellaCodes", projectUmbrella.code)))) {
				//add the value in the other list : the child project needs to be linked to his father! 
				MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code", code), 
						DBUpdate.push("projectUmbrellaCodes", projectUmbrella.code));
			}
		}
	}

}

