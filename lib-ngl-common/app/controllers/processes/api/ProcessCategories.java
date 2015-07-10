package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.processes.description.ProcessCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ProcessCategories extends CommonController{
	
	final static Form<ProcessCategoriesSearchForm> processCategoryForm = form(ProcessCategoriesSearchForm.class);
	
	public static Result list() throws DAOException{
		Form<ProcessCategoriesSearchForm> processCategoryFilledForm = filledFormQueryString(processCategoryForm,ProcessCategoriesSearchForm.class);
		ProcessCategoriesSearchForm processCategoriesSearch = processCategoryFilledForm.get();
		
		List<ProcessCategory> processCategories;
		
		try{		
			processCategories = ProcessCategory.find.findAll();
			
			if(processCategoriesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ProcessCategory>(processCategories, processCategories.size()))); 
			}else if(processCategoriesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ProcessCategory et:processCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(processCategories));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
