package controllers.commons.api;

import static play.data.Form.form;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.description.Resolution;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

@Deprecated
public class Resolutions extends CommonController{
	final static Form<ResolutionsSearchForm> resolutionForm = form(ResolutionsSearchForm.class);

	public static Result list() throws DAOException{
		Form<ResolutionsSearchForm> resolutionFilledForm = filledFormQueryString(resolutionForm,ResolutionsSearchForm.class);
		ResolutionsSearchForm resolutionsSearch = resolutionFilledForm.get();

		List<Resolution> resolutions;

		try{		
			if(StringUtils.isNotBlank(resolutionsSearch.typeCode)){
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