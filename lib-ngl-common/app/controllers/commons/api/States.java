package controllers.commons.api;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class States extends CommonController {
	
	
	public static Result list() throws DAOException{
		DynamicForm filledForm =  listForm.bindFromRequest();
		
		List<State> values = new ArrayList<State>(0);
		if(null != filledForm.get("objectTypeCode")){
			values = State.find.findByObjectTypeCode(ObjectType.CODE.valueOf(filledForm.get("objectTypeCode")));
		}
		
		if(filledForm.get("datatable") != null){
			return ok(Json.toJson(new DatatableResponse<State>(values, values.size())));
		}else if(filledForm.get("list") != null){
			return ok(Json.toJson(values));
		}else{
			return ok(Json.toJson(values));
		}
	}
	
	
}
