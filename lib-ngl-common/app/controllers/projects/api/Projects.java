package controllers.projects.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.mongo.DBObjectConvertor;
import fr.cea.ig.mongo.DBObjectRestrictor;
import fr.cea.ig.ngl.APINGLController;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.NGLController;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.support.NGLForms;
import fr.cea.ig.ngl.support.api.ProjectAPIHolder;
import fr.cea.ig.util.Streamer;
import models.laboratory.project.instance.Project;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
/**
 * Controller around Project object
 *
 */
// @Controller
public class Projects extends NGLController implements APINGLController, NGLForms, ProjectAPIHolder, DBObjectRestrictor, DBObjectConvertor {

	private final Form<ProjectsSearchForm> searchForm;
	private final Form<Project> projectForm;
	
	@Inject
	public Projects(NGLApplication app) {
		super(app);
		this.searchForm  = app.formFactory().form(ProjectsSearchForm.class);
		this.projectForm = app.formFactory().form(Project.class);
	}
	
	@Override
	public Result head(String code) {
		if(! getProjectAPI().isObjectExist(code)){			
			return notFound();					
		}
		return ok();
	}
	
	@Override
	public Result list() {
		ProjectsSearchForm form = filledFormQueryString(searchForm, ProjectsSearchForm.class).get();
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		List<Project> results = null;
		Source<ByteString, ?> resultsAsStream = null; 
		if (form.datatable) {
			if(form.isServerPagination()){
				resultsAsStream = getProjectAPI().stream(q, form.orderBy, Sort.valueOf(form.orderSense), keys, form.pageNumber, form.numberRecordsPerPage);
			} else {
				resultsAsStream = getProjectAPI().stream(q, form.orderBy, Sort.valueOf(form.orderSense), keys);
			}
			return Streamer.okStream(resultsAsStream);
		} else {
			if(form.orderBy == null) {
				form.orderBy = "code";
			}
			if(form.orderSense == null) {
				form.orderSense = 0;
			}

			if(form.list) {
				keys = new BasicDBObject();
				keys.put("_id", 0);//Don't need the _id field
				keys.put("name", 1);
				keys.put("code", 1);
				results = getProjectAPI().list(q, form.orderBy, Sort.valueOf(form.orderSense), keys, form.limit);	
				return ok(Json.toJson(convertToListObject(results, x -> x.getCode(), x -> x.name)));
			} else {
				results = getProjectAPI().list(q, form.orderBy, Sort.valueOf(form.orderSense), keys);
				return ok(Json.toJson(results));
			}
		}
	}

	@Override
	public Result get(String code) {
		return ok(Json.toJson(getProjectAPI().get(code)));
	}
	
	@Override
	public Result save() {
		Form<Project> filledForm = getFilledForm(projectForm, Project.class);
		Project projectInput = filledForm.get();
		try {
			Project p = getProjectAPI().create(projectInput, getCurrentUser(), filledForm.errors());
			return ok(Json.toJson(p));
		} catch (APIValidationException e) {
			getLogger().error(e.getMessage());
			if(e.getErrors() != null) {
				return badRequest(errorsAsJson(e.getErrors()));
			} else {
				return badRequest(e.getMessage());
			}
		} catch (APIException e) {
			getLogger().error(e.getMessage());
			return badRequest("use PUT method to update the project");
		}
		
	}
	
	@Override
	public Result update(String code) {
		Project project = getProjectAPI().get(code);
		if (project == null) {
			return badRequest("Project with code "+ code + " not exist");
		}
		Form<Project> filledForm = getFilledForm(projectForm, Project.class);
		Project projectInput = filledForm.get();
		
		try {
			getProjectAPI().update(code, projectInput, getCurrentUser(), filledForm.errors());
		} catch (APIValidationException e) {
			getLogger().error(e.getMessage());
			if(e.getErrors() != null) {
				return badRequest(errorsAsJson(e.getErrors()));
			} else {
				return badRequest(e.getMessage());
			}
		} catch (APIException e) {
			getLogger().error(e.getMessage());
			return badRequest(e.getMessage());
		}
		
		return ok(Json.toJson(projectInput));
	}
	
	
	// TODO voir class fr.cea.ig.mongo.QueryBuilder
	private Query getQuery(ProjectsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if(CollectionUtils.isNotEmpty(form.projectCodes)){
			queries.add(DBQuery.in("code", form.projectCodes));
		} else if(StringUtils.isNotBlank(form.projectCode)){
			queries.add(DBQuery.is("code", form.projectCode));
		}
		
		if(CollectionUtils.isNotEmpty(form.fgGroups)){
			queries.add(DBQuery.in("bioinformaticParameters.fgGroup", form.fgGroups));
		}
		
		if (form.isFgGroup != null) {
			if(form.isFgGroup){
				queries.add(DBQuery.exists("bioinformaticParameters.fgGroup"));
			} else{
				queries.add(DBQuery.notExists("bioinformaticParameters.fgGroup"));
			}
		}
		
		if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.is("state.code", form.stateCode));
		} else if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
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
}
/*	
	private final static Form<ProjectsSearchForm> searchForm;// = form(ProjectsSearchForm.class); 
	private final static Form<Project> projectForm;// = form(Project.class);
	
	@Inject
	public Projects(NGLContext ctx) {
		super(ctx,InstanceConstants.PROJECT_COLL_NAME, Project.class);		
		searchForm = ctx.form(ProjectsSearchForm.class); 
		projectForm = ctx.form(Project.class);
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
		
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
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
			if(null != projectInput.traceInformation){
				projectInput.traceInformation.setTraceInformation(getCurrentUser());
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
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

}*/
