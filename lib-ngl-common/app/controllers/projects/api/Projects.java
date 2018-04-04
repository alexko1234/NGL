package controllers.projects.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.mongodb.BasicDBObject;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import controllers.NGLAPIController;
import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.ngl.NGLApplication;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.projects.ProjectsAPI;
import fr.cea.ig.ngl.dao.projects.ProjectsDAO;
import fr.cea.ig.util.Streamer;
import models.laboratory.project.instance.Project;
import play.data.Form;
import play.mvc.Result;
/**
 * Controller around Project object
 *
 */
@Historized
public class Projects extends NGLAPIController<ProjectsAPI, ProjectsDAO, Project> { // implements NGLForms, DBObjectConvertor {

	private final Form<ProjectsSearchForm> searchForm;
	private final Form<Project> projectForm;
	
	@Inject
	public Projects(NGLApplication app, ProjectsAPI api) {
		super(app, api);
		this.searchForm  = app.formFactory().form(ProjectsSearchForm.class);
		this.projectForm = app.formFactory().form(Project.class);
	}
	
	@Override
	@Authenticated
	@Authorized.Read
	public Result list() {
		try {
			ProjectsSearchForm form = filledFormQueryString(searchForm, ProjectsSearchForm.class).get();
			Query q = getQuery(form);
			BasicDBObject keys = getKeys(form);
			List<Project> results = null;
			Source<ByteString, ?> resultsAsStream = null; 
			if (form.datatable) {
				if(form.isServerPagination()){
					resultsAsStream = api().streamUDT(q, form.orderBy, Sort.valueOf(form.orderSense), keys, form.pageNumber, form.numberRecordsPerPage);
				} else {
					resultsAsStream = api().streamUDT(q, form.orderBy, Sort.valueOf(form.orderSense), keys);
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
					results = api().list(q, form.orderBy, Sort.valueOf(form.orderSense), keys, form.limit);	
					return okAsJson(convertToListObject(results, x -> x.getCode(), x -> x.name));
				} else {
					results = api().list(q, form.orderBy, Sort.valueOf(form.orderSense), keys);
					return okAsJson(results);
				}
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}

	@Override
	@Authenticated
	@Authorized.Read
	public Result get(String code) {
		try {
			Project p = api().get(code);
			if(p != null) {
				return okAsJson(p);
			} else {
				return notFound();
			}
		} catch (Exception e) {
			getLogger().error(e.getMessage());
			return nglGlobalBadRequest();
		}
	}

//	@Override
//	@Authenticated
//	@Authorized.Write
//	public Result save() {
//		Form<Project> filledForm = getFilledForm(projectForm, Project.class);
//		Project projectInput = filledForm.get();
//		if(! filledForm.hasErrors()) {
//			try {
//				Project p = api().create(projectInput, getCurrentUser());
//				return ok(Json.toJson(p));
//			} catch (APIValidationException e) {
//				getLogger().error(e.getMessage());
//				if(e.getErrors() != null) {
//					return badRequest(errorsAsJson(e.getErrors()));
//				} else {
//					return badRequest(e.getMessage());
//				}
//			} catch (APIException e) {
//				getLogger().error(e.getMessage());
//				return badRequest("use PUT method to update the project");
//			}
//		} else {
//			return badRequest(errorsAsJson(mapErrors(filledForm.allErrors())));
//		}
//	}
	
	@Override
	public Project saveImpl() throws APIValidationException, APIException {
		Project projectInput = getFilledForm(projectForm, Project.class).get();
		Project p = api().create(projectInput, getCurrentUser());
		return p;
	}

//	@Override
//	@Authenticated
//	@Authorized.Write
//	public Result update(String code) {
//		Project project = api().get(code);
//		if (project == null) {
//			return badRequest("Project with code "+ code + " not exist");
//		}
//		Form<Project> filledForm = getFilledForm(projectForm, Project.class);
//		Project projectInput = filledForm.get();
//		if(! filledForm.hasErrors()) {
//			try {
//				api().update(projectInput, getCurrentUser());
//			} catch (APIValidationException e) {
//				getLogger().error(e.getMessage());
//				if(e.getErrors() != null) {
//					return badRequest(errorsAsJson(e.getErrors()));
//				} else {
//					return badRequest(e.getMessage());
//				}
//			} catch (APIException e) {
//				getLogger().error(e.getMessage());
//				return badRequest(e.getMessage());
//			}
//
//			return ok(Json.toJson(projectInput));
//		} else {
//			return badRequest(errorsAsJson(mapErrors(filledForm.allErrors())));
//		}
//	}

	@Override
	public Project updateImpl(String code) throws Exception, APIException, APIValidationException {
		Form<Project> filledForm = getFilledForm(projectForm, Project.class);
		Project projectInput = filledForm.get();
		if(code.equals(projectInput.code)) {
		Project project = api().update(projectInput, getCurrentUser());
		return project;
		} else {
			throw new Exception("Project codes are not the same");
		}
	}

	
	// TODO factoriser avec class fr.cea.ig.mongo.QueryBuilder
	private Query getQuery(ProjectsSearchForm form) {
		List<Query> queries = new ArrayList<>();
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
