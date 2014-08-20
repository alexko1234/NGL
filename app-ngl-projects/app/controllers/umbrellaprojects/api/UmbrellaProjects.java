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
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.DBQuery.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.DocumentController;
import controllers.QueryFieldsForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
/**
 * Controller around Project object
 *
 */
@Controller
public class UmbrellaProjects extends DocumentController<UmbrellaProject> {

	
	final static Form<UmbrellaProjectsSearchForm> searchForm = form(UmbrellaProjectsSearchForm.class); 
	final static Form<UmbrellaProject> projectForm = form(UmbrellaProject.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("keep");
	
	public UmbrellaProjects() {
		super(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProject.class);		
	}


	public Result list(){
		Form<UmbrellaProjectsSearchForm> filledForm = filledFormQueryString(searchForm, UmbrellaProjectsSearchForm.class);
		UmbrellaProjectsSearchForm form = filledForm.get();
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<UmbrellaProject> results = mongoDBFinder(form, q, keys);			
			List<UmbrellaProject> umbrellaProjects = results.toList();
			return ok(Json.toJson(new DatatableResponse<UmbrellaProject>(umbrellaProjects, results.count())));
		} else if (form.list) {
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			if (null == form.orderBy) form.orderBy = "code";
			if (null == form.orderSense) form.orderSense = 0;
			MongoDBResult<UmbrellaProject> results = mongoDBFinder(form, q, keys);			
			List<UmbrellaProject> umbrellaProjects = results.toList();			
			return ok(Json.toJson(toListObjects(umbrellaProjects)));
		} else {
			if (null == form.orderBy) form.orderBy = "code";
			if (null == form.orderSense) form.orderSense = 0;
			MongoDBResult<UmbrellaProject> results = mongoDBFinder(form, q, keys);	
			List<UmbrellaProject> projects = results.toList();
			return ok(Json.toJson(projects));
		}
	}

	

	private List<ListObject> toListObjects(List<UmbrellaProject> proj) {
		List<ListObject> lo = new ArrayList<ListObject>();
		for (UmbrellaProject p : proj) {
			lo.add(new ListObject(p.code, p.name));
		}
		return lo;
	}
	
	private Query getQuery(UmbrellaProjectsSearchForm form) {
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


	public Result save() {
		Form<UmbrellaProject> filledForm = getMainFilledForm();
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
			projectInput = saveObject(projectInput);
			return ok(Json.toJson(projectInput));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}


	public Result update(String code) {
		UmbrellaProject objectInDB = getObject(code);
		if (objectInDB == null) {
			return badRequest("ProjectUmbrella with code "+code+" not exist");
		}

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Form<UmbrellaProject> filledForm = getMainFilledForm();
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
					updateObject(projectInput);
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
				updateObject(DBQuery.and(DBQuery.is("code", code)), 
						getBuilder(projectInput, queryFieldsForm.fields).set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
				return ok(Json.toJson(getObject(code)));
			}else{
				return badRequest(filledForm.errorsAsJson());
			}			
		}
	}

	
	private void synchronizeUmbrellaProjectCodes(UmbrellaProject umbrellaProject) {
		if (umbrellaProject.projectCodes != null) {
			for (String code : umbrellaProject.projectCodes) {
				if (!MongoDBDAO.checkObjectExist(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.and(DBQuery.is("code", code), DBQuery.is("umbrellaProjectCode", umbrellaProject.code)))) {
					MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code", code), DBUpdate.set("umbrellaProjectCode", umbrellaProject.code));
				}
			}
		}
	}

}

