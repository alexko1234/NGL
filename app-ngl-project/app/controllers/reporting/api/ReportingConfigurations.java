package controllers.reporting.api;


import static play.data.Form.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.reporting.instance.ReportingConfiguration;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class ReportingConfigurations extends CommonController {
	final static Form<ReportingConfiguration> reportConfigForm = form(ReportingConfiguration.class);
	
	public static Result list() {
		return ok();
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
