package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.description.ExperimentType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ExperimentTypes extends CommonController{
	
	final static Form<ExperimentTypesSearchForm> experimentTypeForm = form(ExperimentTypesSearchForm.class);
	
	public static Result list() throws DAOException{
		Form<ExperimentTypesSearchForm> experimentTypeFilledForm = filledFormQueryString(experimentTypeForm,ExperimentTypesSearchForm.class);
		ExperimentTypesSearchForm experimentTypesSearch = experimentTypeFilledForm.get();
		
		List<ExperimentType> experimentTypes;
		
		try{		
			if(experimentTypesSearch.categoryCode != null){
				experimentTypes = ExperimentType.find.findByCategoryCode(experimentTypesSearch.categoryCode);
			}else{
				experimentTypes = ExperimentType.find.findAll();
			}
			
			if(experimentTypesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ExperimentType>(experimentTypes, experimentTypes.size()))); 
			}else if(experimentTypesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ExperimentType et:experimentTypes){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(experimentTypes));
			}
		}catch (DAOException e) {
			e.printStackTrace();
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
