package controllers.projects.api;

//import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import controllers.APICommonController;
//import controllers.CommonController;
import fr.cea.ig.play.NGLContext;
import models.laboratory.project.description.ProjectType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;

public class ProjectTypes extends APICommonController<ProjectTypesSearchForm> { //CommonController{

	private static final play.Logger.ALogger logger = play.Logger.of(ProjectTypes.class);
	
	private final /*static*/ Form<ProjectTypesSearchForm> projectTypeForm ;//= form(ProjectTypesSearchForm.class);
	
	@Inject
	public ProjectTypes(NGLContext ctx) {
		super(ctx, ProjectTypesSearchForm.class);
		projectTypeForm = ctx.form(ProjectTypesSearchForm.class);
	}
	
	public /*static*/ Result list() throws DAOException{
		Form<ProjectTypesSearchForm> projectTypeFilledForm = filledFormQueryString(projectTypeForm,ProjectTypesSearchForm.class);
		ProjectTypesSearchForm projectTypesSearch = projectTypeFilledForm.get();
		
		List<ProjectType> projectTypes;
		
		try{	
			projectTypes = ProjectType.find.findAll();

			if(projectTypesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<>(projectTypes, projectTypes.size()))); 
			}else if(projectTypesSearch.list){
				List<ListObject> lop = new ArrayList<>();
				for(ProjectType et:projectTypes){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(projectTypes));
			}
		} catch (DAOException e) {
			logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
