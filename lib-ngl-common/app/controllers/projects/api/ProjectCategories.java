package controllers.projects.api;


// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import controllers.APICommonController;
import fr.cea.ig.play.NGLContext;
import models.laboratory.project.description.ProjectCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
//import controllers.CommonController;

public class ProjectCategories extends APICommonController<ProjectCategoriesSearchForm> { //CommonController{

	private static final play.Logger.ALogger logger = play.Logger.of(ProjectCategories.class);
	
	private final /*static*/ Form<ProjectCategoriesSearchForm> projectCategoryForm;// = form(ProjectCategoriesSearchForm.class);
	
	@Inject
	public ProjectCategories(NGLContext ctx) {
		super(ctx, ProjectCategoriesSearchForm.class);
		projectCategoryForm = ctx.form(ProjectCategoriesSearchForm.class);
	}
	
	public /*static*/ Result list() throws DAOException{
		Form<ProjectCategoriesSearchForm> projectCategoryFilledForm = filledFormQueryString(projectCategoryForm,ProjectCategoriesSearchForm.class);
		ProjectCategoriesSearchForm projectCategoriesSearch = projectCategoryFilledForm.get();
		
		List<ProjectCategory> projectCategories;
		
		try{		
			projectCategories = ProjectCategory.find.findAll();
			
			if(projectCategoriesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<>(projectCategories, projectCategories.size()))); 
			}else if(projectCategoriesSearch.list){
				List<ListObject> lop = new ArrayList<>();
				for(ProjectCategory et:projectCategories){
					logger.debug(Json.toJson(et).toString());
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(projectCategories));
			}
		} catch (DAOException e) {
			logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
	
}
