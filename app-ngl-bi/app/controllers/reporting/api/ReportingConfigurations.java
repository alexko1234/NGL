package controllers.reporting.api;


import static play.data.Form.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import com.mongodb.BasicDBObject;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reporting.instance.ReportingConfiguration;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.readsets.api.ReadSetsSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class ReportingConfigurations extends CommonController {
	final static Form<ReportingConfiguration> reportConfigForm = form(ReportingConfiguration.class);
	final static Form<ReportingConfigurationsSearchForm> searchForm = form(ReportingConfigurationsSearchForm.class);
	
	public static Result list() {
		Form<ReportingConfigurationsSearchForm> filledForm = filledFormQueryString(searchForm, ReportingConfigurationsSearchForm.class);
		ReportingConfigurationsSearchForm form = filledForm.get();
		
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<ReportingConfiguration> results = mongoDBFinder(InstanceConstants.REPORTING_CONFIG_COLL_NAME, form, ReportingConfiguration.class, q, keys);				
			List<ReportingConfiguration> reportingConfigurations = results.toList();
			return ok(Json.toJson(new DatatableResponse<ReportingConfiguration>(reportingConfigurations, results.count())));
		}else{
			MongoDBResult<ReportingConfiguration> results = mongoDBFinder(InstanceConstants.REPORTING_CONFIG_COLL_NAME, form, ReportingConfiguration.class, q, keys);							
			List<ReportingConfiguration> reportingConfigurations = results.toList();
			return ok(Json.toJson(reportingConfigurations));
		}
	}
	
	private static Query getQuery(ReportingConfigurationsSearchForm form) {
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
		ReportingConfiguration reportingConfiguration =  getReportingConfiguration(code);		
		if(reportingConfiguration != null) {
			return ok(Json.toJson(reportingConfiguration));	
		} 		
		else {
			return notFound();
		}			
	}
	
	public static Result save() {
		Form<ReportingConfiguration> filledForm = getFilledForm(reportConfigForm, ReportingConfiguration.class);
		ReportingConfiguration reportingConfiguration = filledForm.get();

		if (null == reportingConfiguration._id) {
			reportingConfiguration.traceInformation = new TraceInformation();
			reportingConfiguration.traceInformation
					.setTraceInformation(getCurrentUser());
			reportingConfiguration.code = generateReportingConfigurationCode();
		} else {
			return badRequest("use PUT method to update the run");
		}

		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.setCreationMode();
		reportingConfiguration.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			reportingConfiguration = MongoDBDAO.save(InstanceConstants.REPORTING_CONFIG_COLL_NAME, reportingConfiguration);
			return ok(Json.toJson(reportingConfiguration));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public static Result update(String code) {
		ReportingConfiguration reportingConfiguration =  getReportingConfiguration(code);
		if(reportingConfiguration == null) {
			return badRequest("ReportingConfiguration with code "+reportingConfiguration+" does not exist");
		}
		Form<ReportingConfiguration> filledForm = getFilledForm(reportConfigForm, ReportingConfiguration.class);
		ReportingConfiguration reportingConfigurationInput = filledForm.get();

		if (reportingConfiguration.code.equals(code)) {
			if(null != reportingConfigurationInput.traceInformation){
				reportingConfigurationInput.traceInformation.setTraceInformation(getCurrentUser());
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			ContextValidation ctxVal = new ContextValidation(filledForm.errors());
			ctxVal.setCreationMode();
			reportingConfiguration.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.REPORTING_CONFIG_COLL_NAME, reportingConfigurationInput);
				return ok(Json.toJson(reportingConfigurationInput));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("readset code are not the same");
		}				
	}
	
	public static Result delete(String code) {
		ReportingConfiguration reportingConfiguration =  getReportingConfiguration(code);
		if(reportingConfiguration == null) {
			return badRequest("ReportingConfiguration with code "+reportingConfiguration+" does not exist");
		}
		MongoDBDAO.deleteByCode(InstanceConstants.REPORTING_CONFIG_COLL_NAME,  ReportingConfiguration.class, reportingConfiguration.code);
		return ok();
	}
	
	public static String generateReportingConfigurationCode(){
		return ("RC-"+(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date())).toUpperCase();		
	}
	
	private static ReportingConfiguration getReportingConfiguration(String code) {
		ReportingConfiguration reportingConfiguration = MongoDBDAO.findByCode(InstanceConstants.REPORTING_CONFIG_COLL_NAME, ReportingConfiguration.class, code);
    	return reportingConfiguration;
	}
}
