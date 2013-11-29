package controllers.commons.api;

import java.util.ArrayList;
import java.util.List;

import controllers.CommonController;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.common.description.ObjectType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

public class CommonInfoTypes extends CommonController {
	public static Result list() throws DAOException{
		DynamicForm filledForm =  listForm.bindFromRequest();
		
		List<CommonInfoType> values = new ArrayList<CommonInfoType>(0);
		if(null != filledForm.get("objectTypeCode")){
			values = CommonInfoType.find.findByObjectTypeCode(ObjectType.CODE.valueOf(filledForm.get("objectTypeCode")));
		}
		
		if(filledForm.get("datatable") != null){
			return ok(Json.toJson(new DatatableResponse<CommonInfoType>(values, values.size())));
		}else if(filledForm.get("list") != null){
			return ok(Json.toJson(ListObject.from(values)));
		}else{
			return ok(Json.toJson(values));
		}
	}
	
}
