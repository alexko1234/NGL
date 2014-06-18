package controllers.resolutions.api;

import static play.data.Form.form;

import java.util.List;

import models.laboratory.common.description.Resolution;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ResolutionsOld extends CommonController{
	final static Form<ResolutionsSearchFormOld> resolutionForm = form(ResolutionsSearchFormOld.class);

	public static Result list() throws DAOException{
		Form<ResolutionsSearchFormOld> resolutionFilledForm = filledFormQueryString(resolutionForm,ResolutionsSearchFormOld.class);
		ResolutionsSearchFormOld resolutionsSearch = resolutionFilledForm.get();

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
			}else{
				return ok(Json.toJson(resolutions));
			}
		}catch (DAOException e) {
			Logger.error(e.getMessage());
			return internalServerError(e.getMessage());
		}	
	}
}