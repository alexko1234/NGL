package controllers.commons.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.ValuationCriteria;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ValuationCriterias extends CommonController{
	final static Form<ValuationCriteriasSearchForm> valuationCriteriaTypeForm = form(ValuationCriteriasSearchForm.class);

	public static Result list() throws DAOException{
		Form<ValuationCriteriasSearchForm> processTypeFilledForm = filledFormQueryString(valuationCriteriaTypeForm,ValuationCriteriasSearchForm.class);
		ValuationCriteriasSearchForm valuationCriteriasSearch = processTypeFilledForm.get();

		List<ValuationCriteria> valuationCriterias;

		try{		
			if(valuationCriteriasSearch.typeCode != null){
				valuationCriterias =   ValuationCriteria.find.findByTypeCode(valuationCriteriasSearch.typeCode);
			}else{
				valuationCriterias = ValuationCriteria.find.findAll();
			}
			if(valuationCriteriasSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ValuationCriteria>(valuationCriterias, valuationCriterias.size()))); 
			}else if(valuationCriteriasSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ValuationCriteria et:valuationCriterias){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(valuationCriterias));
			}
		}catch (DAOException e) {
			e.printStackTrace();
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
