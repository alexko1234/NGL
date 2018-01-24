package controllers.runs.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import controllers.APICommonController;
//import controllers.CommonController;
import controllers.authorisation.Permission;
import controllers.history.UserHistory;
import fr.cea.ig.play.NGLContext;
import models.laboratory.run.description.RunCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;
import views.components.datatable.DatatableResponse;

public class RunCategories extends APICommonController<RunCategoriesSearchForm> { //CommonController {

	private final /*static*/ Form<RunCategoriesSearchForm> runCategoriesForm;// = form(RunCategoriesSearchForm.class);
	
	@Inject
	public RunCategories(NGLContext ctx) {
		super(ctx, RunCategoriesSearchForm.class);
		runCategoriesForm = ctx.form(RunCategoriesSearchForm.class);
	}
	
	@Permission(value={"reading"})
	public /*static*/ Result list(){
		Form<RunCategoriesSearchForm> runCategoryFilledForm = filledFormQueryString(runCategoriesForm,RunCategoriesSearchForm.class);
		RunCategoriesSearchForm runCategoriesSearch = runCategoryFilledForm.get();
		
		List<RunCategory> runCategories;
		
		try{		
			runCategories = RunCategory.find.findAll();
			
			if(runCategoriesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<RunCategory>(runCategories, runCategories.size()))); 
			}else if(runCategoriesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(RunCategory et:runCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(runCategories));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
