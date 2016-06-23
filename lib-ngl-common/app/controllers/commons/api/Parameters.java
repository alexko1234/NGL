package controllers.commons.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.parameter.Parameter;
import models.laboratory.parameter.index.Index;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class Parameters extends CommonController {
	
	// GA 24/07/2015 implementaton de la form +  params list et datatablecd SOL
	final static Form<ParametersSearchForm> form = form(ParametersSearchForm.class);

	public static Result list() {
	    Form<ParametersSearchForm> filledForm = filledFormQueryString(form, ParametersSearchForm.class);
		ParametersSearchForm parametersSearch = filledForm.get();
		return list(parametersSearch);
				
    }

	public static Result listByCode(String typeCode) {
	    Form<ParametersSearchForm> filledForm = filledFormQueryString(form, ParametersSearchForm.class);
		ParametersSearchForm parametersSearch = filledForm.get();
		parametersSearch.typeCode=typeCode;
		return list(parametersSearch);
				
    }
	
	private static Result list(ParametersSearchForm parametersSearch) {
		Query query = getQuery(parametersSearch);		
		
		List<Parameter> values=MongoDBDAO.find(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, query).toList();
		
		if (parametersSearch.datatable) {
		    return ok(Json.toJson(new DatatableResponse<Parameter>(values, values.size())));
		} else if (parametersSearch.list) {
		    List<ListObject> valuesListObject = new ArrayList<ListObject>();
		    for (Parameter s : values) {
		    	valuesListObject.add(new ListObject(s.code, s.name));
		    }
		    return ok(Json.toJson(valuesListObject));
		} else {
			return ok(Json.toJson(values));
		}
	}
	
	
 
	public static Result get(String typeCode, String code) throws DAOException {
		Parameter index=MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, DBQuery.is("typeCode", typeCode).is("code", code));
		if(index != null){
			return ok(Json.toJson(index));
		}
		else { return notFound(); }

    }  
	
	private static Query getQuery(ParametersSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.typeCode)) { 
			queries.add(DBQuery.is("typeCode", form.typeCode));
		}else if(CollectionUtils.isNotEmpty(form.typeCodes)){
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}
		
		if (StringUtils.isNotBlank(form.sequence)) { 
			queries.add(DBQuery.is("sequence", form.sequence));
		}
		if(CollectionUtils.isNotEmpty(form.categoryCodes)){
			queries.add(DBQuery.in("categoryCode", form.categoryCodes));
		}
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
}
