package controllers.commons.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

//import controllers.CommonController;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;
import models.laboratory.parameter.Parameter;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;

public class Parameters extends DocumentController<Parameter> { //CommonController {
	
	// GA 24/07/2015 implementaton de la form +  params list et datatablecd SOL
	private final /*static*/ Form<ParametersSearchForm> form; //= form(ParametersSearchForm.class);

	@Inject
	public Parameters(NGLContext ctx){
		super(ctx,InstanceConstants.PARAMETER_COLL_NAME, Parameter.class);
		this.form = getNGLContext().form(ParametersSearchForm.class);
	}
	
	public Result list() {
	    Form<ParametersSearchForm> filledForm = filledFormQueryString(form, ParametersSearchForm.class);
		ParametersSearchForm parametersSearch = filledForm.get();
		return list(parametersSearch);
				
    }

	public Result listByCode(String typeCode) {
	    Form<ParametersSearchForm> filledForm = filledFormQueryString(form, ParametersSearchForm.class);
		ParametersSearchForm parametersSearch = filledForm.get();
		parametersSearch.typeCode=typeCode;
		return list(parametersSearch);
				
    }
	
	private Result list(ParametersSearchForm parametersSearch) {
		Query query = getQuery(parametersSearch);		
		
		List<Parameter> values=MongoDBDAO.find(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, query).toList();
		
		if (parametersSearch.datatable) {
		    return ok(Json.toJson(new DatatableResponse<>(values, values.size())));
		} else if (parametersSearch.list) {
		    List<ListObject> valuesListObject = new ArrayList<>();
		    for (Parameter s : values) {
		    	valuesListObject.add(new ListObject(s.code, s.name));
		    }
		    return ok(Json.toJson(valuesListObject));
		} else {
			return ok(Json.toJson(values));
		}
	}
	
	
 
	public Result get(String typeCode, String code) throws DAOException {
		Parameter index=MongoDBDAO.findOne(InstanceConstants.PARAMETER_COLL_NAME, Parameter.class, DBQuery.is("typeCode", typeCode).is("code", code));
		if (index != null)
			return ok(Json.toJson(index));
		return notFound();
    }  
	
	private static Query getQuery(ParametersSearchForm form) {
		List<Query> queries = new ArrayList<>();
		Query query = null;
		if (StringUtils.isNotBlank(form.typeCode)) { 
			queries.add(DBQuery.is("typeCode", form.typeCode));
		}else if(CollectionUtils.isNotEmpty(form.typeCodes)){
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}
		
		if (StringUtils.isNotBlank(form.sequence)) { 
			queries.add(DBQuery.is("sequence", form.sequence));
		}
		if(StringUtils.isNotBlank(form.categoryCode)){
			queries.add(DBQuery.is("categoryCode", form.categoryCode));
		}else if(CollectionUtils.isNotEmpty(form.categoryCodes)){
			queries.add(DBQuery.in("categoryCode", form.categoryCodes));
		}
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
}
