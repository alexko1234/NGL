package controllers.containers.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import controllers.APICommonController;
//import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.play.NGLContext;
import models.laboratory.container.description.ContainerCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;

// Could provide implementation through CRUD base class.
public class ContainerCategories extends APICommonController<ContainerCategoriesSearchForm> { //CommonController{
	
	private /*static*/ final Form<ContainerCategoriesSearchForm> containerCategoriesTypeForm; // = form(ContainerCategoriesSearchForm.class);
	
	@Inject
	public ContainerCategories(NGLContext ctx) {
		super(ctx, ContainerCategoriesSearchForm.class);
		this.containerCategoriesTypeForm = ctx.form(ContainerCategoriesSearchForm.class);
	}
		
	@Permission(value={"reading"})
	public Result list() throws DAOException {
		Form<ContainerCategoriesSearchForm>  containerCategoryFilledForm = filledFormQueryString(containerCategoriesTypeForm,ContainerCategoriesSearchForm.class);
		ContainerCategoriesSearchForm containerCategoriesSearch = containerCategoryFilledForm.get();
		try {
			List<ContainerCategory> containerCategories = ContainerCategory.find.findAll();
			
			if (containerCategoriesSearch.datatable) {
				return ok(Json.toJson(new DatatableResponse<>(containerCategories, containerCategories.size()))); 
			} else if(containerCategoriesSearch.list) {
				List<ListObject> lop = new ArrayList<>();
				for(ContainerCategory et:containerCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			} else {
				return Results.ok(Json.toJson(containerCategories));
			}
		} catch (DAOException e) {
			Logger.error("DAO error: " + e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
	
}
