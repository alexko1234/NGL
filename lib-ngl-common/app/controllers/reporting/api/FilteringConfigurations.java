package controllers.reporting.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reporting.instance.FilteringConfiguration;
import models.laboratory.reporting.instance.ReportingConfiguration;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;

/**
 * Controller around ResolutionConfigurations object
 *
 */
// @Controller
public class FilteringConfigurations extends DocumentController<FilteringConfiguration> {
	
	private final /*static*/ Form<ConfigurationsSearchForm> searchForm; // = form(ConfigurationsSearchForm.class); 
	private final /*static*/ Form<FilteringConfiguration> filteringConfigurationsForm;// = form(FilteringConfiguration.class);
	
	@Inject
	public FilteringConfigurations(NGLContext ctx) {
		super(ctx,InstanceConstants.FILTERING_CONFIG_COLL_NAME, FilteringConfiguration.class);
		searchForm = ctx.form(ConfigurationsSearchForm.class);
		filteringConfigurationsForm = ctx.form(FilteringConfiguration.class);
	}


	public Result list() {
		Form<ConfigurationsSearchForm> filledForm = filledFormQueryString(searchForm, ConfigurationsSearchForm.class);
		ConfigurationsSearchForm form = filledForm.get();
		Query q = getQuery(form);
		
		MongoDBResult<FilteringConfiguration> results = mongoDBFinder(form, q);			
		List<FilteringConfiguration> configurations = results.toList();			
		return ok(Json.toJson(configurations));		
	}
	
	private Query getQuery(ConfigurationsSearchForm form) {
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
	public Result save() {
		Form<FilteringConfiguration> filledForm = getMainFilledForm();
		FilteringConfiguration configuration = filledForm.get();

		if (null == configuration._id) {
			configuration.traceInformation = new TraceInformation();
			configuration.traceInformation
					.setTraceInformation(getCurrentUser());
			configuration.code = generateConfigurationCode();
		} else {
			return badRequest("use PUT method to update the filtering config");
		}

		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setCreationMode();
		configuration.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			configuration = saveObject(configuration);
			return ok(Json.toJson(configuration));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	public Result update(String code) {
		FilteringConfiguration configuration = getObject(code);
		if(configuration == null) {
			return badRequest("FilteringConfiguration with code "+code+" does not exist");
		}
		Form<FilteringConfiguration> filledForm = getMainFilledForm();
		FilteringConfiguration configurationInput = filledForm.get();

		if (configurationInput.code.equals(code)) {
			if(null != configurationInput.traceInformation){
				configurationInput.traceInformation = getUpdateTraceInformation(configurationInput.traceInformation);
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
			ctxVal.setCreationMode();
			configurationInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				updateObject(configurationInput);
				return ok(Json.toJson(configurationInput));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("FilteringConfiguration code are not the same");
		}				
	}
	
	public static String generateConfigurationCode(){
		return ("FC-"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())).toUpperCase();		
	}
}