package controllers.commons.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class States extends CommonController {
	final static Form<StatesSearchForm> stateForm = form(StatesSearchForm.class);
	
	public static Result list() throws DAOException{
		Form<StatesSearchForm> stateFilledForm = filledFormQueryString(stateForm,StatesSearchForm.class);
		StatesSearchForm statesSearch = stateFilledForm.get();
		
		List<State> values = new ArrayList<State>(0);
		if(null != statesSearch.objectTypeCode){
			values = State.find.findByObjectTypeCode(ObjectType.CODE.valueOf(statesSearch.objectTypeCode));
		}
		
		if(statesSearch.datatable != null){
			return ok(Json.toJson(new DatatableResponse<State>(values, values.size())));
		}else if(statesSearch.list != null){
			List<ListObject> valuesListObject = new ArrayList<ListObject>();
			for(State s: values){
				valuesListObject.add(new ListObject(s.code,s.name));
			}
			return ok(Json.toJson(valuesListObject));
		}else{
			return ok(Json.toJson(values));
		}
	}
	
	
}
