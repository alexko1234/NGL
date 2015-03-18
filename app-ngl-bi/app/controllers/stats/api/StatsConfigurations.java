package controllers.stats.api;


import static play.data.Form.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.stats.StatsConfiguration;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import controllers.reporting.api.ConfigurationsSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class StatsConfigurations extends CommonController {
	final static Form<StatsConfiguration> reportConfigForm = form(StatsConfiguration.class);
	final static Form<ConfigurationsSearchForm> searchForm = form(ConfigurationsSearchForm.class);
	
	public static Result list() {
		Form<ConfigurationsSearchForm> filledForm = filledFormQueryString(searchForm, ConfigurationsSearchForm.class);
		ConfigurationsSearchForm form = filledForm.get();
		
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<StatsConfiguration> results = mongoDBFinder(InstanceConstants.STATS_CONFIG_COLL_NAME, form, StatsConfiguration.class, q, keys);				
			List<StatsConfiguration> statsConfigurations = results.toList();
			return ok(Json.toJson(new DatatableResponse<StatsConfiguration>(statsConfigurations, results.count())));
		}else if(form.count){
			MongoDBResult<StatsConfiguration> results = mongoDBFinder(InstanceConstants.STATS_CONFIG_COLL_NAME, form, StatsConfiguration.class, q, keys);
			int count = results.count();
			Map<String, Integer> m = new HashMap<String, Integer>(1);
			m.put("result", count);
			return ok(Json.toJson(m));
		}else{
			MongoDBResult<StatsConfiguration> results = mongoDBFinder(InstanceConstants.STATS_CONFIG_COLL_NAME, form, StatsConfiguration.class, q, keys);							
			List<StatsConfiguration> statsConfigurations = results.toList();
			return ok(Json.toJson(statsConfigurations));
		}
	}
	
	private static Query getQuery(ConfigurationsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if (CollectionUtils.isNotEmpty(form.pageCodes)) { //all
			queries.add(DBQuery.in("pageCodes", form.pageCodes));
		}
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		
		return query;
	}

	public static Result get(String code) {
		StatsConfiguration statsConfiguration =  getStatsConfiguration(code);		
		if(statsConfiguration != null) {
			return ok(Json.toJson(statsConfiguration));	
		} 		
		else {
			return notFound();
		}			
	}
	
	public static Result save() {
		Form<StatsConfiguration> filledForm = getFilledForm(reportConfigForm, StatsConfiguration.class);
		StatsConfiguration statsConfiguration = filledForm.get();

		if (null == statsConfiguration._id) {
			statsConfiguration.traceInformation = new TraceInformation();
			statsConfiguration.traceInformation
					.setTraceInformation(getCurrentUser());
			statsConfiguration.code = generateStatsConfigurationCode();
		} else {
			return badRequest("use PUT method to update the run");
		}

		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setCreationMode();
		statsConfiguration.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			statsConfiguration = MongoDBDAO.save(InstanceConstants.STATS_CONFIG_COLL_NAME, statsConfiguration);
			return ok(Json.toJson(statsConfiguration));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public static Result update(String code) {
		StatsConfiguration statsConfiguration =  getStatsConfiguration(code);
		if(statsConfiguration == null) {
			return badRequest("StatsConfiguration with code "+code+" does not exist");
		}
		Form<StatsConfiguration> filledForm = getFilledForm(reportConfigForm, StatsConfiguration.class);
		StatsConfiguration statsConfigurationInput = filledForm.get();

		if (statsConfigurationInput.code.equals(code)) {
			if(null != statsConfigurationInput.traceInformation){
				statsConfigurationInput.traceInformation.setTraceInformation(getCurrentUser());
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
			ctxVal.setUpdateMode();
			statsConfigurationInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.STATS_CONFIG_COLL_NAME, statsConfigurationInput);
				return ok(Json.toJson(statsConfigurationInput));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("readset code are not the same");
		}				
	}
	
	public static Result delete(String code) {
		StatsConfiguration statsConfiguration =  getStatsConfiguration(code);
		if(statsConfiguration == null) {
			return badRequest("StatsConfiguration with code "+statsConfiguration+" does not exist");
		}
		MongoDBDAO.deleteByCode(InstanceConstants.STATS_CONFIG_COLL_NAME,  StatsConfiguration.class, statsConfiguration.code);
		return ok();
	}
	
	public static String generateStatsConfigurationCode(){
		return ("RC-"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())).toUpperCase();		
	}
	
	private static StatsConfiguration getStatsConfiguration(String code) {
		StatsConfiguration statsConfiguration = MongoDBDAO.findByCode(InstanceConstants.STATS_CONFIG_COLL_NAME, StatsConfiguration.class, code);
    	return statsConfiguration;
	}
}
