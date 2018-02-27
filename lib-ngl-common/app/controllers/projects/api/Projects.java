package controllers.projects.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;
import fr.cea.ig.mongo.MongoStreamer;
import fr.cea.ig.play.NGLContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import com.mongodb.BasicDBObject;

//import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.DocumentController;
//import fr.cea.ig.MongoDBDatatableResponseChunks;
import fr.cea.ig.MongoDBResult;
/**
 * Controller around Project object
 *
 */
// @Controller
public class Projects extends DocumentController<Project> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Projects.class);
	
	private final /*static*/ Form<ProjectsSearchForm> searchForm;// = form(ProjectsSearchForm.class); 
//	private final /*static*/ Form<Project> projectForm;// = form(Project.class);
	
	@Inject
	public Projects(NGLContext ctx) {
		super(ctx,InstanceConstants.PROJECT_COLL_NAME, Project.class);		
		searchForm = ctx.form(ProjectsSearchForm.class); 
//		projectForm = ctx.form(Project.class);
	}


	public Result list() {
		Form<ProjectsSearchForm> filledForm = filledFormQueryString(searchForm, ProjectsSearchForm.class);
		ProjectsSearchForm form = filledForm.get();
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		if (form.datatable) {			
			MongoDBResult<Project> results = mongoDBFinder(form, q, keys);			
			// return ok(new MongoDBDatatableResponseChunks<Project>(results)).as("application/json");
			//return ok(MongoStreamer.stream(results)).as("application/json");
			// return ok(MongoStreamer.streamUDT(results)).as("application/json");
			return MongoStreamer.okStreamUDT(results);
		} else if(form.list) {
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			if (form.orderBy    == null) form.orderBy    = "code";
			if (form.orderSense == null) form.orderSense = 0;
			MongoDBResult<Project> results = mongoDBFinder(form, q, keys);			
			List<Project> projects = results.toList();			
			return ok(Json.toJson(toListObjects(projects)));
		} else {
			if (form.orderBy    == null) form.orderBy    = "code";
			if (form.orderSense == null) form.orderSense = 0;
			MongoDBResult<Project> results = mongoDBFinder(form, q, keys);	
			List<Project> projects = results.toList();
			return ok(Json.toJson(projects));
		}
	}
	
	public Result get(String code) {
		return ok(Json.toJson(super.getObject(code)));
	}

	

	private List<ListObject> toListObjects(List<Project> projects){
		List<ListObject> lo = new ArrayList<ListObject>();
		for(Project p : projects){
			lo.add(new ListObject(p.code, p.name));
		}
		return lo;
	}
	
	private Query getQuery(ProjectsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if(CollectionUtils.isNotEmpty(form.projectCodes)){
			queries.add(DBQuery.in("code", form.projectCodes));
		}else if(StringUtils.isNotBlank(form.projectCode)){
			queries.add(DBQuery.is("code", form.projectCode));
		}
		
		if(CollectionUtils.isNotEmpty(form.fgGroups)){
			queries.add(DBQuery.in("bioinformaticParameters.fgGroup", form.fgGroups));
		}
		
		if (null != form.isFgGroup) {
			if(form.isFgGroup){
				queries.add(DBQuery.exists("bioinformaticParameters.fgGroup"));
			}else{
				queries.add(DBQuery.notExists("bioinformaticParameters.fgGroup"));
			}
		}
		
		if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.is("state.code", form.stateCode));
		}else if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", form.stateCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.typeCodes)) { //all
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.existingFields)) { //all
			for(String field : form.existingFields){
				queries.add(DBQuery.exists(field));
			}		
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}


	public Result save() {
		Form<Project> filledForm = getMainFilledForm();
		Project projectInput = filledForm.get();
		if (projectInput._id == null) { 
			projectInput.traceInformation = new TraceInformation();
			projectInput.traceInformation.setTraceInformation(getCurrentUser());
			
			if (projectInput.state == null) {
				projectInput.state = new State();
			}
			projectInput.state.code = "N";
			projectInput.state.user = getCurrentUser();
			projectInput.state.date = new Date();		
		} else {
			return badRequest("use PUT method to update the project");
		}
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
		ctxVal.setCreationMode();
		projectInput.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			saveObject(projectInput);
			return ok(Json.toJson(projectInput));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

	public Result update(String code) {
		Project project = getObject(code);
		if (project == null) {
			return badRequest("Project with code "+code+" not exist");
		}
		Form<Project> filledForm = getMainFilledForm();
		Project projectInput = filledForm.get();
		
		if (code.equals(projectInput.code)) {
			if (projectInput.traceInformation != null) {
				projectInput.traceInformation.setTraceInformation(getCurrentUser());
			} else {
				logger.error("traceInformation is null !!");
			}
//			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	
			ctxVal.setUpdateMode();
			projectInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				updateObject(projectInput);
				return ok(Json.toJson(projectInput));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("Project codes are not the same");
		}
	}

}
