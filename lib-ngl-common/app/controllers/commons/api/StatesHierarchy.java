package controllers.commons.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

//import controllers.CommonController;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import controllers.APICommonController;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.description.StateHierarchy;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

public class StatesHierarchy extends APICommonController<StatesHierarchySearchForm> {// CommonController {
	
    private final /*static*/ Form<StatesHierarchySearchForm> statesHierarchyForm; // = form(StatesHierarchySearchForm.class);

    @Inject
    public StatesHierarchy(NGLContext ctx){
    	super(ctx, StatesHierarchySearchForm.class);
    	this.statesHierarchyForm = ctx.form(StatesHierarchySearchForm.class);
    }
    
    public Result list() throws DAOException {
		Form<StatesHierarchySearchForm> statesHierarchyFilledForm = filledFormQueryString(
				statesHierarchyForm, StatesHierarchySearchForm.class);
		StatesHierarchySearchForm statesHierarchySearch = statesHierarchyFilledForm.get();
	
		List<StateHierarchy> values = new ArrayList<>(0);

		if (StringUtils.isNotBlank(statesHierarchySearch.objectTypeCode)) 
		    values = StateHierarchy.find.findByObjectTypeCode(ObjectType.CODE.valueOf(statesHierarchySearch.objectTypeCode));
		else 
			return notFound();
		
		if (statesHierarchySearch.datatable) {
		    return ok(Json.toJson(new DatatableResponse<>(values, values.size())));
		} else if (statesHierarchySearch.list) {
		    List<ListObject> valuesListObject = new ArrayList<>();
		    for (StateHierarchy s : values) {
		    	valuesListObject.add(new ListObject(s.childStateCode, s.parentStateCode));
		    }
		    return ok(Json.toJson(valuesListObject));
		} else {
		    return ok(Json.toJson(values));
		}
    }
    
}

