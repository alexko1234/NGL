package controllers.projects.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
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
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
/**
 * Controller around Project object
 *
 */
public class Projects extends ProjectsController {

	
	final static Form<ProjectsSearchForm> searchForm = form(ProjectsSearchForm.class); 
	final static Form<Project> projectForm = form(Project.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("keep");


	public static Result list(){
		Form<ProjectsSearchForm> filledForm = filledFormQueryString(searchForm, ProjectsSearchForm.class);
		ProjectsSearchForm form = filledForm.get();
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<Project> results = mongoDBFinder(InstanceConstants.PROJECT_COLL_NAME, form, Project.class, getQuery(form), keys);			
			List<Project> projects = results.toList();
			return ok(Json.toJson(new DatatableResponse<Project>(projects, results.count())));
		}else if(form.list){
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			if(null == form.orderBy)form.orderBy = "code";
			if(null == form.orderSense)form.orderSense = 0;
			MongoDBResult<Project> results = mongoDBFinder(InstanceConstants.PROJECT_COLL_NAME, form, Project.class, getQuery(form), keys);			
			List<Project> projects = results.toList();			
			return ok(Json.toJson(toListObjects(projects)));
		}else{
			if(null == form.orderBy)form.orderBy = "code";
			if(null == form.orderSense)form.orderSense = 0;
			MongoDBResult<Project> results = mongoDBFinder(InstanceConstants.PROJECT_COLL_NAME, form, Project.class, getQuery(form), keys);	
			List<Project> projects = results.toList();
			return ok(Json.toJson(projects));
		}
	}

	

	private static List<ListObject> toListObjects(List<Project> projects){
		List<ListObject> lo = new ArrayList<ListObject>();
		for(Project p : projects){
			lo.add(new ListObject(p.code, p.name));
		}
		return lo;
	}
	
	private static Query getQuery(ProjectsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if(CollectionUtils.isNotEmpty(form.projectCodes)){
			queries.add(DBQuery.in("code", form.projectCodes));
		}else if(StringUtils.isNotBlank(form.projectCode)){
			queries.add(DBQuery.is("code", form.projectCode));
		}
		
		if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.is("state.code", form.stateCode));
		}else if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", form.stateCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.typeCodes)) { //all
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}
	
	public static Result get(String code) {
		Project projectValue = getProject(code);
		if (projectValue != null) {		
			return ok(Json.toJson(projectValue));					
		} else {
			return notFound();
		}
	}
	
	public static Result head(String code){
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, code)){			
			return ok();					
		}else{
			return notFound();
		}
	}


	public static Result save() {
		Form<Project> filledForm = getFilledForm(projectForm, Project.class);
		Project projectInput = filledForm.get();

		if (null == projectInput._id) { 
			projectInput.traceInformation = new TraceInformation();
			projectInput.traceInformation.setTraceInformation(getCurrentUser());
			
			if(null == projectInput.state){
				projectInput.state = new State();
			}
			projectInput.state.code = "N";
			projectInput.state.user = getCurrentUser();
			projectInput.state.date = new Date();		
			
		} else {
			return badRequest("use PUT method to update the project");
		}
		
		//synchronization of the 2 lists of projects (projectCodes & projectUmbrellaCodes)
		synchronizeProjectCodes(projectInput);		

		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		ctxVal.setCreationMode();
		projectInput.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			projectInput = MongoDBDAO.save(InstanceConstants.PROJECT_COLL_NAME, projectInput);
			return ok(Json.toJson(projectInput));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}


	public static Result update(String code) {
		Project project = getProject(code);
		if (project == null) {
			return badRequest("Project with code "+code+" not exist");
		}

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Form<Project> filledForm = getFilledForm(projectForm, Project.class);
		Project projectInput = filledForm.get();
		
		//synchronization of the 2 lists of projects (projectCodes & projectUmbrellaCodes)
		synchronizeProjectCodes(projectInput);		
		
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
					MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, projectInput);
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
				MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
						DBQuery.and(DBQuery.is("code", code)), getBuilder(projectInput, queryFieldsForm.fields, Project.class).set("traceInformation", ti));
				return ok(Json.toJson(getProject(code)));
			}else{
				return badRequest(filledForm.errorsAsJson());
			}			
		}
	}


	public static Result delete(String code) {
		Project project = getProject(code);
		if (project == null) {
			return badRequest();
		}		
		MongoDBDAO.delete(InstanceConstants.PROJECT_COLL_NAME, project);	
		return ok();
	}
	
	public static void synchronizeProjectCodes(Project project) {
		for (String code : project.umbrellaProjectCodes) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProject.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.in("projectCodes", project.code)))) {
				//add the value in the other list 
				MongoDBDAO.update(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProject.class, DBQuery.is("code", code), 
						DBUpdate.push("projectCodes", project.code));
			}
		}
	}


}
