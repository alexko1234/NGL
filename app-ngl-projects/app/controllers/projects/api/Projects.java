package controllers.projects.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

import com.mongodb.BasicDBObject;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.Props;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import rules.services.RulesActor;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
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
		
		if(CollectionUtils.isNotEmpty(form.codes)){
			queries.add(DBQuery.in("code", form.codes));
		}else if(StringUtils.isNotBlank(form.code)){
			queries.add(DBQuery.is("code", form.code));
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
		
		if(queryFieldsForm.fields == null){
			if (code.equals(projectInput.code)) {
				if(null != projectInput.traceInformation){
					projectInput.traceInformation.setTraceInformation(getCurrentUser());
				}else{
					Logger.error("traceInformation is null !!");
				}
				
				//if(!project.state.code.equals(projectInput.state.code)){
				//	return badRequest("You cannot change the state code. Please used the state url ! ");
				//}
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
				return badRequest("Project code are not the same");
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


}
