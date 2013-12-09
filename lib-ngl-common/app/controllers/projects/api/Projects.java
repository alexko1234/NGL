package controllers.projects.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import net.vz.mongodb.jackson.DBQuery;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBResult;

public class Projects extends CommonController{
	final static Form<ProjectsSearchForm> projectForm = form(ProjectsSearchForm.class);
	
	public static Result list(){
		Form<ProjectsSearchForm> projectFilledForm = filledFormQueryString(projectForm,ProjectsSearchForm.class);
		ProjectsSearchForm projectsSearch = projectFilledForm.get();
		
		DBQuery.Query query = getQuery(projectsSearch);
		if(projectsSearch.datatable){
			MongoDBResult<Project> results = mongoDBFinder(InstanceConstants.PROJECT_COLL_NAME, projectsSearch, Project.class, query);
			List<Project> projects = results.toList();

			return ok(Json.toJson(new DatatableResponse<Project>(projects, results.count())));
		}else if(projectsSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			MongoDBResult<Project> results = mongoDBFinder(InstanceConstants.PROJECT_COLL_NAME, projectsSearch, Project.class, query, keys);
			List<Project> projects = results.toList();
			List<ListObject> lop = new ArrayList<ListObject>();
			for(Project p: projects){
				lop.add(new ListObject(p.code, p.name));
			}
			
			return ok(Json.toJson(lop));
		}else{
			MongoDBResult<Project> results = mongoDBFinder(InstanceConstants.PROJECT_COLL_NAME, projectsSearch, Project.class, query);
			List<Project> projects = results.toList();
			return ok(Json.toJson(projects));
		}
	}
	

	/**
	 * Construct the project query
	 * @param samplesSearch
	 * @return
	 */
	private static DBQuery.Query getQuery(ProjectsSearchForm projectsSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}
