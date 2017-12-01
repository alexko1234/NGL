package controllers.experiments.api;

// import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.description.ExperimentCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.authorisation.Permission;

public class ExperimentCategories extends CommonController{
	
	final static Form<ExperimentCategoriesSearchForm> experimentCategoryForm = form(ExperimentCategoriesSearchForm.class);
	
	@Permission(value={"reading"})
	public static Result list() throws DAOException{
		Form<ExperimentCategoriesSearchForm>  experimentCategoryFilledForm = filledFormQueryString(experimentCategoryForm,ExperimentCategoriesSearchForm.class);
		ExperimentCategoriesSearchForm experimentCategoriesSearch = experimentCategoryFilledForm.get();
		try{
			List<ExperimentCategory> experimentCategories;
			
			if(StringUtils.isNotBlank(experimentCategoriesSearch.processTypeCode)){
				experimentCategories = ExperimentCategory.find.findByProcessTypeCode(experimentCategoriesSearch.processTypeCode);
			}else{
				experimentCategories = ExperimentCategory.find.findAll();
			}
			if(experimentCategoriesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ExperimentCategory>(experimentCategories, experimentCategories.size()))); 
			}else if(experimentCategoriesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ExperimentCategory et:experimentCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(experimentCategories));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
