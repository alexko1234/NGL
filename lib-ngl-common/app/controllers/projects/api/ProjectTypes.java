package controllers.projects.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.project.description.ProjectType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ProjectTypes extends CommonController{
	
	final static Form<ProjectTypesSearchForm> projectTypeForm = form(ProjectTypesSearchForm.class);
	
	public static Result list() throws DAOException{
		Form<ProjectTypesSearchForm> projectTypeFilledForm = filledFormQueryString(projectTypeForm,ProjectTypesSearchForm.class);
		ProjectTypesSearchForm projectTypesSearch = projectTypeFilledForm.get();
		
		List<ProjectType> projectTypes;
		
		try{	
			projectTypes = ProjectType.find.findAll();

			if(projectTypesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ProjectType>(projectTypes, projectTypes.size()))); 
			}else if(projectTypesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ProjectType et:projectTypes){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(projectTypes));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
