package controllers.reception.api;


import static play.data.Form.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reception.instance.ReceptionConfiguration;
import models.laboratory.reporting.instance.ReportingConfiguration;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.ListObject;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import controllers.authorisation.Permission;
import controllers.samples.api.SamplesSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class ReceptionConfigurations extends DocumentController<ReceptionConfiguration> {
	final Form<ReceptionConfiguration> reportConfigForm = form(ReceptionConfiguration.class);
	
	public ReceptionConfigurations() {
		super(InstanceConstants.RECEPTION_CONFIG_COLL_NAME, ReceptionConfiguration.class);	
	}
	
	@Permission(value={"reading"})
	public Result list(){
		SamplesSearchForm samplesSearch = filledFormQueryString(SamplesSearchForm.class);
		
		DBQuery.Query query = getQuery(samplesSearch);
		if(samplesSearch.datatable){
			MongoDBResult<Sample> results = mongoDBFinder(samplesSearch, query);
			List<Sample> samples = results.toList();
			return ok(Json.toJson(new DatatableResponse<Sample>(samples, results.count())));
		}
		else if(samplesSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			MongoDBResult<Sample> results = mongoDBFinder(samplesSearch,query).sort("code");
			List<Sample> samples = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Sample p: samples){
				los.add(new ListObject(p.code, p.name));
			}
			
			return Results.ok(Json.toJson(los));
		}else{
			MongoDBResult<Sample> results = mongoDBFinder(samplesSearch, query);
			List<Sample> samples = results.toList();
			return Results.ok(Json.toJson(samples));
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

	
	
	public Result save() {
		Form<ReportingConfiguration> filledForm = getFilledForm(reportConfigForm, ReportingConfiguration.class);
		ReportingConfiguration reportingConfiguration = filledForm.get();

		if (null == reportingConfiguration._id) {
			reportingConfiguration.traceInformation = new TraceInformation();
			reportingConfiguration.traceInformation
					.setTraceInformation(getCurrentUser());
			reportingConfiguration.code = generateReportingConfigurationCode();
		} else {
			return badRequest("use PUT method to update the ReportingConfiguration");
		}

		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setCreationMode();
		reportingConfiguration.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			reportingConfiguration = MongoDBDAO.save(InstanceConstants.REPORTING_CONFIG_COLL_NAME, reportingConfiguration);
			return ok(Json.toJson(reportingConfiguration));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public Result update(String code) {
		ReportingConfiguration reportingConfiguration =  getReportingConfiguration(code);
		if(reportingConfiguration == null) {
			return badRequest("ReportingConfiguration with code "+code+" does not exist");
		}
		Form<ReportingConfiguration> filledForm = getFilledForm(reportConfigForm, ReportingConfiguration.class);
		ReportingConfiguration reportingConfigurationInput = filledForm.get();

		if (reportingConfigurationInput.code.equals(code)) {
			if(null != reportingConfigurationInput.traceInformation){
				reportingConfigurationInput.traceInformation.setTraceInformation(getCurrentUser());
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
			ctxVal.setUpdateMode();
			reportingConfigurationInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.REPORTING_CONFIG_COLL_NAME, reportingConfigurationInput);
				return ok(Json.toJson(reportingConfigurationInput));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("ReportingConfiguration code are not the same");
		}				
	}
	
	public Result delete(String code) {
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
