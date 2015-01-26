package controllers.instruments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.instrument.description.InstrumentCategory;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class InstrumentCategories extends CommonController{
	final static Form<InstrumentCategoriesSearchForm> instrumentCategoriesForm = form(InstrumentCategoriesSearchForm.class);

	public static Result list() throws DAOException{
		Form<InstrumentCategoriesSearchForm> instrumentCategoriesTypeFilledForm = filledFormQueryString(instrumentCategoriesForm,InstrumentCategoriesSearchForm.class);
		InstrumentCategoriesSearchForm instrumentCategoriesQueryParams = instrumentCategoriesTypeFilledForm.get();

		List<InstrumentCategory> instrumentCategories;

		try{		
			if(StringUtils.isNotBlank(instrumentCategoriesQueryParams.instrumentTypeCode)){
				instrumentCategories = InstrumentCategory.find.findByInstrumentUsedTypeCode(instrumentCategoriesQueryParams.instrumentTypeCode);
			}else{
				instrumentCategories = InstrumentCategory.find.findAll();
			}
			if(instrumentCategoriesQueryParams.datatable){
				return ok(Json.toJson(new DatatableResponse<InstrumentCategory>(instrumentCategories, instrumentCategories.size()))); 
			}else if(instrumentCategoriesQueryParams.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(InstrumentCategory et:instrumentCategories){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(instrumentCategories));
			}
		}catch (DAOException e) {
			e.printStackTrace();
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
