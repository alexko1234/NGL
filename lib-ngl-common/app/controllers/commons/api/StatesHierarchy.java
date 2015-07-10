package controllers.commons.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.StateHierarchy;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class StatesHierarchy extends CommonController {
    final static Form<StatesHierarchySearchForm> statesHierarchyForm = form(StatesHierarchySearchForm.class);

    public static Result list() throws DAOException {
		Form<StatesHierarchySearchForm> statesHierarchyFilledForm = filledFormQueryString(
				statesHierarchyForm, StatesHierarchySearchForm.class);
		StatesHierarchySearchForm statesHierarchySearch = statesHierarchyFilledForm.get();
	
		List<StateHierarchy> values = new ArrayList<StateHierarchy>(0);

		if (StringUtils.isNotBlank(statesHierarchySearch.objectTypeCode)) 
		    values = StateHierarchy.find.findByObjectTypeCode(ObjectType.CODE.valueOf(statesHierarchySearch.objectTypeCode));
		else 
			return notFound();
		
	
		if (statesHierarchySearch.datatable) {
		    return ok(Json.toJson(new DatatableResponse<StateHierarchy>(values, values
			    .size())));
		} else if (statesHierarchySearch.list) {
		    List<ListObject> valuesListObject = new ArrayList<ListObject>();
		    for (StateHierarchy s : values) {
		    	valuesListObject.add(new ListObject(s.childStateCode, s.parentStateCode));
		    }
		    return ok(Json.toJson(valuesListObject));
		} else {
		    return ok(Json.toJson(values));
		}
    }
}

