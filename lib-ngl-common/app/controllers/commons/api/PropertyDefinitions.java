package controllers.commons.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import controllers.APICommonController;
import models.laboratory.common.description.Level;
import models.laboratory.common.description.PropertyDefinition;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

public class PropertyDefinitions extends APICommonController<PropertyDefinition> {

	public PropertyDefinitions(Class<PropertyDefinition> type) {
		super(type);		
	}

	public  Result list() throws DAOException {
		DynamicForm filledForm =  listForm.bindFromRequest();
		
		List<PropertyDefinition> values = new ArrayList<PropertyDefinition>(0);
		if(null != filledForm.get("levelCode")){
			values = PropertyDefinition.find.findUnique(Level.CODE.valueOf(filledForm.get("levelCode")));
		}
		
		if(filledForm.get("datatable") != null){
			return ok(Json.toJson(new DatatableResponse<PropertyDefinition>(values, values.size())));
		}else if(filledForm.get("list") != null){
			return ok(Json.toJson(values.parallelStream().map(pd -> new ListObject(pd.code,pd.name)).collect(Collectors.toList())));
		}else{
			return ok(Json.toJson(values));
		}				
	}
	
	
}
