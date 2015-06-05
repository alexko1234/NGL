package controllers.containers.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.container.description.ContainerCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ContainerCategories extends CommonController{
	
	final static Form<ContainerCategoriesSearchForm> containerCategoriesTypeForm = form(ContainerCategoriesSearchForm.class);
	
	public static Result list() throws DAOException{
		Form<ContainerCategoriesSearchForm>  containerCategoryFilledForm = filledFormQueryString(containerCategoriesTypeForm,ContainerCategoriesSearchForm.class);
		ContainerCategoriesSearchForm containerCategoriesSearch = containerCategoryFilledForm.get();
		try{
			List<ContainerCategory> containerCategories = ContainerCategory.find.findAll();
			
			if(containerCategoriesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ContainerCategory>(containerCategories, containerCategories.size()))); 
			}else if(containerCategoriesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ContainerCategory et:containerCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(containerCategories));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
