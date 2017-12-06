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
//import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;


public class Variables extends CommonController{

	final static Form<VariablesSearchForm> form = form(VariablesSearchForm.class);
	private static final play.Logger.ALogger logger = play.Logger.of(Variables.class);
	
	public static Result list() {
		Form<VariablesSearchForm> filledForm = filledFormQueryString(form, VariablesSearchForm.class);
		VariablesSearchForm variableSearch = filledForm.get();
		logger.debug("variableSearch "+variableSearch);
		return list(variableSearch);
	}

	public static Result get(String type, String code) {
		logger.debug("Get " + type + " code " + code);
		if (type.equalsIgnoreCase("strategySample")) {
			SraParameter parameter = new SraParameter();
			parameter.code  = code;
			parameter.type  = type;
			parameter.value = VariableSRA.mapStrategySample.get("code");
			return ok(Json.toJson(parameter));
		} else if (type.equalsIgnoreCase("strategyStudy")) {
			SraParameter parameter = new SraParameter();
			parameter.code  = code;
			parameter.type  = type;
			parameter.value = VariableSRA.mapStrategyStudy.get("code");
			return ok(Json.toJson(parameter));
		} else {
			SraParameter parameter=MongoDBDAO.findOne(InstanceConstants.SRA_PARAMETER_COLL_NAME, SraParameter.class, DBQuery.and(DBQuery.is("code", code),DBQuery.is("type", type)));
			logger.debug("parameter "+parameter);
			if (parameter != null) {
				return ok(Json.toJson(parameter));
			} else { 
				return notFound(); 
			}
		}
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
		logger.debug("variableSearch type " + variableSearch.type);
		if (variableSearch.type!=null && variableSearch.type.equalsIgnoreCase("strategySample")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapStrategySample)));
		} else if (variableSearch.type!=null && variableSearch.type.equalsIgnoreCase("strategyStudy")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapStrategyStudy)));
		}else{
			Query query = getQuery(variableSearch);		

			List<SraParameter> values = MongoDBDAO.find(InstanceConstants.SRA_PARAMETER_COLL_NAME, SraParameter.class, query).toList();

			List<ListObject> valuesListObject = new ArrayList<ListObject>();
			for (SraParameter s : values) {
				valuesListObject.add(new ListObject(s.code, s.value));
			}
			return ok(Json.toJson(valuesListObject));
		}

	}

	private static List<ListObject> toListObjects(Map<String, String> map){
		List<ListObject> lo = new ArrayList<ListObject>();
		for(String key : map.keySet()){
			lo.add(new ListObject(key, map.get(key)));
		}

		//Sort by code
		Collections.sort(lo, new Comparator<ListObject>(){
			public int compare(ListObject lo1, ListObject lo2) {
				return lo1.code.compareTo(lo2.code);
			}
		});
		return lo;
	}	

}
