package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

import com.mongodb.BasicDBObject;

import controllers.CodeHelper;
import controllers.CommonController;
import controllers.containers.api.Containers;
import controllers.containers.api.ContainersSearchForm;
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
				value.state = new State("N", getCurrentUser());
				//code and name generation
				value.code = CodeHelper.generateProcessCode(value);
				
				Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, value.containerInputCode);
				if(container.fromExperimentTypeCodes == null || container.fromExperimentTypeCodes.size() == 0){
					container.fromExperimentTypeCodes = new ArrayList<String>();
					container.fromExperimentTypeCodes.add(value.getProcessType().voidExperimentType.code);
				}
				container.processTypeCode = value.typeCode;
				container.inputProcessCodes=InstanceHelpers.addCode(value.code, container.inputProcessCodes);
				MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME,container);

				
				Logger.info("New process code : "+value.code);
			} else {
				value.traceInformation.setTraceInformation(getCurrentUser());
			}

			if (!filledForm.hasErrors()) {
				ContextValidation contextValidation=new ContextValidation(filledForm.errors());
				contextValidation.setCreationMode();
				value = (Process) InstanceHelpers.save(InstanceConstants.PROCESS_COLL_NAME,value, contextValidation);
				if(!contextValidation.hasErrors()){
					Workflows.nextContainerState(value,new ContextValidation(filledForm.errors()));
				}
			}
		}
		if (!filledForm.hasErrors()) {
			filledForm = filledForm.fill(value);
			return ok(Json.toJson(filledForm.get()));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}			
	}

	public static Result update(String code){
		Process process = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, code);
		if(process == null){
			return badRequest("Process with code "+code+" does not exist");
		}
		
		Form<Process> filledForm = getFilledForm(processForm, Process.class);
		Process processInput = filledForm.get();
		if (processInput.code.equals(code)) {
			processInput.traceInformation.modifyDate = new Date();
			processInput.traceInformation.modifyUser = getCurrentUser();
			
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
			ctxVal.setUpdateMode();
			processInput.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, processInput);
				
				return ok(Json.toJson(processInput));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("process code are not the same");
		}
	}
	
	public static Result delete(String code){
		Process process = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, code);
		if(process == null){
			return badRequest("Process with code "+code+" does not exist");
		}
		
		MongoDBDAO.delete(InstanceConstants.PROCESS_COLL_NAME, process);
		
		return ok();
	}
	
	public static Result list() throws DAOException{
		Form<ProcessesSearchForm> processesFilledForm = filledFormQueryString(processesSearchForm,ProcessesSearchForm.class);
		ProcessesSearchForm processesSearch = processesFilledForm.get();

		DBQuery.Query query = getQuery(processesSearch);
		if(processesSearch.datatable){
			MongoDBResult<Process> results =  mongoDBFinder(InstanceConstants.PROCESS_COLL_NAME, processesSearch, Process.class, query); 
			List<Process> processes = results.toList();
			return ok(Json.toJson(new DatatableResponse<Process>(processes, results.count())));
		}else if(processesSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);

			MongoDBResult<Process> results = mongoDBFinder(InstanceConstants.PROCESS_COLL_NAME, processesSearch, Process.class, query, keys); 
			List<Process> processes = results.toList();

			List<ListObject> los = new ArrayList<ListObject>();
			for(Process p: processes){
				los.add(new ListObject(p.code, p.code));
			}

			return ok(Json.toJson(los));
		}else{
			MongoDBResult<Process> results = mongoDBFinder(InstanceConstants.PROCESS_COLL_NAME, processesSearch, Process.class, query); 
			List<Process> processes = results.toList();
			return ok(Json.toJson(processes));
		}
	}

	/**
	 * Construct the process query
	 * @param processesSearch
	 * @return the query
	 */
	private static DBQuery.Query getQuery(ProcessesSearchForm processesSearch) throws DAOException{
		List<Query> queryElts = new ArrayList<Query>();
		Query query = null;

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
		
		if(StringUtils.isNotEmpty(processesSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", processesSearch.categoryCode));
		}
		
		if(StringUtils.isNotEmpty(processesSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", processesSearch.stateCode));
		}
		if(processesSearch.users != null){
			queryElts.add(DBQuery.in("traceInformation.createUser", processesSearch.stateCode));
		}
		
		if(null != processesSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", processesSearch.fromDate));
		}

		if(null != processesSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(processesSearch.toDate, 1))));
		}
		
		if(StringUtils.isNotEmpty(processesSearch.supportCode)){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			
			ContainersSearchForm cs = new ContainersSearchForm();
			cs.supportCode = processesSearch.supportCode;
			
			List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, Containers.getQuery(cs), keys).toList();
			for(Container c: containers){
				queryElts.add(DBQuery.or(DBQuery.is("containerInputCode", c.code)));
			}
		}
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new Query[queryElts.size()]));
		}
		
		return query;
	}
}