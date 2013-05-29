package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import net.vz.mongodb.jackson.DBQuery;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

import controllers.CodeHelper;
import controllers.CommonController;
import controllers.Constants;
import controllers.authorisation.PermissionHelper;

import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import workflows.Workflows;


public class Processes extends CommonController{
	
	final static Form<Process> processForm = form(Process.class);
	final static Form<ProcessesSearchForm> processesSearchForm = form(ProcessesSearchForm.class);
	
	
	public static Result save(){
		Form<Process> filledForm = getFilledForm();
		if (!filledForm.hasErrors()) {
			Process value = filledForm.get();
			if (null == value._id) {
				//init state
				//the trace
				value.traceInformation = new TraceInformation();
			
				value.traceInformation.setTraceInformation(PermissionHelper.getCurrentUser(session()));
				//the default status
				value.stateCode = "N";
				
				Container container = MongoDBDAO.findByCode("Container", Container.class, value.containerInputCode);
				if(container.fromExperimentTypeCodes == null || container.fromExperimentTypeCodes.size() == 0){
					container.fromExperimentTypeCodes.add(value.getProcessType().voidExperimentType.code);
					MongoDBDAO.save(Constants.CONTAINER_COLL_NAME,container);
				}
				
				//code and name generation
				value.code = CodeHelper.generateProcessCode(value);
				Logger.info("New process code : "+value.code);
			} else {
				value.traceInformation.setTraceInformation(PermissionHelper.getCurrentUser(session()));
			}
			
			//Business Validation
			BusinessValidationHelper.validateProcess(filledForm.errors(), value, Constants.PROCESS_COLL_NAME,null);
			
			if (!filledForm.hasErrors()) {
				if(value._id == null){
					//Workflows Implementation
					Workflows.setAvailable(value.containerInputCode);
				}
				
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
	
	public static Result list(){
		Form<ProcessesSearchForm> processesSearchFilledForm = processesSearchForm.bindFromRequest();
		ProcessesSearchForm processSearch = processesSearchFilledForm.get();
		DBQuery.Query query = getQuery(processSearch);
	    MongoDBResult<Process> results = MongoDBDAO.find(Constants.PROCESS_COLL_NAME, Process.class, query)
				.sort(DatatableHelpers.getOrderBy(processesSearchFilledForm), getMongoDBOrderSense(processesSearchFilledForm))
				.page(DatatableHelpers.getPageNumber(processesSearchFilledForm), DatatableHelpers.getNumberRecordsPerPage(processesSearchFilledForm)); 
		List<Process> process = results.toList();
		return ok(Json.toJson(new DatatableResponse(process, results.count())));
	}
	
	/**
	 * Construct the process query
	 * @param processesSearch
	 * @return the query
	 */
	private static DBQuery.Query getQuery(ProcessesSearchForm processesSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		
		Logger.info("Process Query : "+processesSearch);
		
		if(StringUtils.isNotEmpty(processesSearch.projectCode)){
			queryElts.add(DBQuery.is("projectCode", processesSearch.projectCode));
	    }
		
		if(StringUtils.isNotEmpty(processesSearch.sampleCode)){
			queryElts.add(DBQuery.is("sampleCode", processesSearch.sampleCode));
	    }
		
		if(StringUtils.isNotEmpty(processesSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", processesSearch.typeCode));
	    }
		
		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}