package controllers.sra.api;



import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.parameter.Parameter;
import models.sra.submit.util.SraParameter;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;


public class Variables extends CommonController{

	final static Form<VariablesSearchForm> form = form(VariablesSearchForm.class);

	public static Result list()
	{
		Form<VariablesSearchForm> filledForm = filledFormQueryString(form, VariablesSearchForm.class);
		VariablesSearchForm variableSearch = filledForm.get();
		return list(variableSearch);
	}

	public static Result get(String type, String code){
		SraParameter parameter=MongoDBDAO.findOne(InstanceConstants.SRA_PARAMETER_COLL_NAME, SraParameter.class, DBQuery.is("type", type).is("code", code));
		if(parameter != null){
			return ok(Json.toJson(parameter));
		}
		else { return notFound(); }
	}

	private static Query getQuery(VariablesSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.type)) { 
			queries.add(DBQuery.is("type", form.type));
		}
		if (StringUtils.isNotBlank(form.code)) { 
			queries.add(DBQuery.is("code", form.code));
		}
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}


	private static Result list(VariablesSearchForm variableSearch) {
		Query query = getQuery(variableSearch);		

		List<SraParameter> values=MongoDBDAO.find(InstanceConstants.SRA_PARAMETER_COLL_NAME, SraParameter.class, query).toList();


		List<ListObject> valuesListObject = new ArrayList<ListObject>();
		for (SraParameter s : values) {
			valuesListObject.add(new ListObject(s.code, s.value));
		}
		return ok(Json.toJson(valuesListObject));

	}

}
