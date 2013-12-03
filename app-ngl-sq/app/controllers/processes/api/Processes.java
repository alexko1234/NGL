package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOHelpers;
import net.vz.mongodb.jackson.DBQuery;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import validation.ContextValidation;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;
import controllers.CodeHelper;
import controllers.CommonController;
import controllers.authorisation.PermissionHelper;
import controllers.containers.api.ContainersSearchForm;
import controllers.utils.FormUtils;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;


public class Processes extends CommonController{

	final static Form<Process> processForm = form(Process.class);
	final static Form<ProcessesSearchForm> processesSearchForm = form(ProcessesSearchForm.class);


	public static Result save(){
		Form<Process> filledForm = getFilledForm(processForm,Process.class);
		Process value = null;
		if (!filledForm.hasErrors()) {
			value = filledForm.get();
			if (null == value._id) {
				//init state
				//the trace
				value.traceInformation = new TraceInformation();

				value.traceInformation.setTraceInformation(getCurrentUser());
				//the default status
				value.stateCode = "N";

				Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, value.containerInputCode);
				if(container.fromExperimentTypeCodes == null || container.fromExperimentTypeCodes.size() == 0){
					container.fromExperimentTypeCodes = new ArrayList<String>();
					container.fromExperimentTypeCodes.add(value.getProcessType().voidExperimentType.code);
					MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME,container);
				}

				//code and name generation
				value.code = CodeHelper.generateProcessCode(value);
				Logger.info("New process code : "+value.code);
			} else {
				value.traceInformation.setTraceInformation(getCurrentUser());
			}

			if (!filledForm.hasErrors()) {
				if(value._id == null){
					//Workflows Implementation
					Workflows.setAvailable(value.containerInputCode,value.typeCode);
				}

				value = (Process) InstanceHelpers.save(InstanceConstants.PROCESS_COLL_NAME,value, new ContextValidation(filledForm.errors()));
			}
		}
		if (!filledForm.hasErrors()) {
			filledForm = filledForm.fill(value);
			return ok(Json.toJson(filledForm.get()));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}			
	}

	public static Result list(){
		Form<ProcessesSearchForm> processesFilledForm = filledFormQueryString(processesSearchForm,ProcessesSearchForm.class);
		ProcessesSearchForm processesSearch = processesFilledForm.get();

		DBQuery.Query query = getQuery(processesSearch);
		if(processesSearch.datatable){
			MongoDBResult<Process> results = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, query)
					.sort(DatatableHelpers.getOrderBy(processesFilledForm), FormUtils.getMongoDBOrderSense(processesFilledForm))
					.page(DatatableHelpers.getPageNumber(processesFilledForm), DatatableHelpers.getNumberRecordsPerPage(processesFilledForm)); 
			List<Process> processes = results.toList();
			return ok(Json.toJson(new DatatableResponse(processes, results.count())));
		}else{
			MongoDBResult<Process> results = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, query)
					.sort(DatatableHelpers.getOrderBy(processesFilledForm), FormUtils.getMongoDBOrderSense(processesFilledForm));
			List<Process> processes = results.toList();
			return ok(Json.toJson(processes));
		}
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