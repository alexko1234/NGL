package controllers.commons.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Resolution;
import models.laboratory.processes.description.ProcessType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class Resolutions extends CommonController{
	final static Form<ResolutionsSearchForm> resolutionForm = form(ResolutionsSearchForm.class);

	public static Result list() throws DAOException{
		Form<ResolutionsSearchForm> resolutionFilledForm = filledFormQueryString(resolutionForm,ResolutionsSearchForm.class);
		ResolutionsSearchForm resolutionsSearch = resolutionFilledForm.get();

		List<Resolution> resolutions;

		try{		
			if(resolutionsSearch.typeCode != null){
				resolutions = Resolution.find.findByTypeCode(resolutionsSearch.typeCode);
			}
			else if(resolutionsSearch.objectTypeCode != null){
				resolutions = Resolution.find.findByObjectTypeCode(resolutionsSearch.objectTypeCode);
			}
			else{
				resolutions = Resolution.find.findAll();
			}
			if(resolutionsSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<Resolution>(resolutions, resolutions.size()))); 
			/*}else if(resolutionsSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(Resolution et:resolutions){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			*/}else{
				return Results.ok(Json.toJson(resolutions));
			}
		}catch (DAOException e) {
			e.printStackTrace();
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
