package controllers.processes.api;

import static play.data.Form.form;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.processes.instance.Process;

import org.codehaus.jackson.JsonNode;

import controllers.CodeHelper;
import controllers.Constants;

import fr.cea.ig.MongoDBDAO;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import validation.BusinessValidationHelper;

public class Processes extends Controller{
	
	final static Form<Process> processForm = form(Process.class);
	
	public static Result save(){
		Form<Process> filledForm = getFilledForm();
		if (!filledForm.hasErrors()) {
			Process value = filledForm.get();
			if (null == value._id) {
				//init state
				//the trace
				value.traceInformation = new TraceInformation();
				value.traceInformation.setTraceInformation("ngsrg");
				//the default status
				value.stateCode = "N";
				//code and name generation
				value.code = CodeHelper.generateProcessCode(value);
				Logger.info("New process code : "+value.code);
			} else {
				value.traceInformation.setTraceInformation("ngsrg");
			}
			//TODO Business Validation
			BusinessValidationHelper.validateProcess(filledForm.errors(), value, Constants.PROCESS_COLL_NAME);
			//TODO Workflows Implementation
			//container -> A
			if (!filledForm.hasErrors()) {
				value = MongoDBDAO.save(Constants.PROCESS_COLL_NAME,value);
				filledForm = filledForm.fill(value);
			}
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(filledForm.get()));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}			
	}
	
	private static Form<Process> getFilledForm() {
		JsonNode json = request().body().asJson();
		Process input = Json.fromJson(json, Process.class);
		Form<Process> filledForm = processForm.fill(input); // bindJson ne marche pas
		return filledForm;
	}
}