package controllers.commons.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.State;
import models.laboratory.common.description.Value;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class Values extends CommonController {
    final static Form<ValuesSearchForm> form = form(ValuesSearchForm.class);

    public static Result list() throws DAOException {
		Form<ValuesSearchForm> filledForm = filledFormQueryString(
				form, ValuesSearchForm.class);
		ValuesSearchForm valuesSearch = filledForm.get();
	
		List<Value> values = new ArrayList<Value>(0);
		if (StringUtils.isNotBlank(valuesSearch.propertyDefinitionCode)) 
		    values = Value.find.findUnique(valuesSearch.propertyDefinitionCode);
		else 
			return notFound();
	
		if (valuesSearch.datatable) {
		    return ok(Json.toJson(new DatatableResponse<Value>(values, values.size())));
		} else if (valuesSearch.list) {
		    List<ListObject> valuesListObject = new ArrayList<ListObject>();
		    for (Value s : values) {
		    	valuesListObject.add(new ListObject(s.code, s.name));
		    }
		    return ok(Json.toJson(valuesListObject));
		} else {
		    return ok(Json.toJson(values));
		}
    }
    
}
