package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ProcessHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
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

	public static Result head(String processCode) {
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, processCode)){			
			return ok();					
		}else{
			return notFound();
		}	
	}
	
	public static Result get(String code){
		Process process = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, code);
		if(process == null){
			return notFound();
		}
		return ok(Json.toJson(process));
	}
	
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
				Logger.info("New process code : "+value.code);
			} else {
				value.traceInformation.setTraceInformation(getCurrentUser());
			}

			if (!filledForm.hasErrors()) {
				ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm.errors());
				contextValidation.setCreationMode();
				value = (Process) InstanceHelpers.save(InstanceConstants.PROCESS_COLL_NAME,value, contextValidation);
				if(!contextValidation.hasErrors()){
					Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, value.containerInputCode);
					List<String> processCodes=new ArrayList<String>();
					processCodes.add(value.code);
					ProcessHelper.updateContainer(container,value.typeCode, processCodes,contextValidation);
					ProcessHelper.updateContainerSupportFromContainer(container,contextValidation);
					Workflows.nextContainerState(value,null,new ContextValidation(getCurrentUser(), filledForm.errors()));
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
	
	public static Result saveSupport(String supportCode){
		Form<Process> filledForm = getFilledForm(processForm,Process.class);
		Process value = null;
		if (!filledForm.hasErrors()) {
			value = filledForm.get();
			//value = ps.process;
			ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm.errors());
			if (null == value._id) {
				//init state
				//the trace
				value.traceInformation = new TraceInformation();

				value.traceInformation.setTraceInformation(getCurrentUser());
				//the default status
				value.state = new State("N", getCurrentUser());
				
				List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("support.code", supportCode)).toList();
				for(Container container:containers){
					List<String> processCodes=new ArrayList<String>();
					value.containerInputCode = container.code;
					for(String s:container.sampleCodes){
						//ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm.errors());
						contextValidation.setCreationMode();
						value.sampleCode = s;
						//code and name generation
						value.code = CodeHelper.generateProcessCode(value);
						InstanceHelpers.save(InstanceConstants.PROCESS_COLL_NAME,value, contextValidation);
						processCodes.add(value.code);
					}
					ProcessHelper.updateContainer(container,value.typeCode, processCodes,contextValidation);
					ProcessHelper.updateContainerSupportFromContainer(container,contextValidation);
					
				}
				
				Logger.info("New process code : "+value.code);
			} else {
				value.traceInformation.setTraceInformation(getCurrentUser());
			}

			if (!filledForm.hasErrors()) {
				/*ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm.errors());
				contextValidation.setCreationMode();
				value = (Process) InstanceHelpers.save(InstanceConstants.PROCESS_COLL_NAME,value, contextValidation);*/
				if(!contextValidation.hasErrors()){
					Workflows.nextContainerState(value,null,new ContextValidation(getCurrentUser(), filledForm.errors()));
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
			return notFound("Process with code "+code+" does not exist");
		}
		
		Form<Process> filledForm = getFilledForm(processForm, Process.class);
		Process processInput = filledForm.get();
		if (processInput.code.equals(code)) {
			processInput.traceInformation.setTraceInformation(getCurrentUser());
			
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
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
		ContextValidation contextValidation=new ContextValidation(getCurrentUser());
		if(process == null){
			return notFound("Process with code "+code+" does not exist");
		}
		
		List<String> processCode=new ArrayList<String>();
		processCode.add(process.code);
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("inputProcessCodes", processCode)).toList();
		for(Container container:containers){
			if(!container.state.code.equals("A")){
				contextValidation.addErrors("container", ValidationConstants.ERROR_BADSTATE_MSG, container.code);
			}
		}
		if(!contextValidation.hasErrors()){
			MongoDBDAO.delete(InstanceConstants.PROCESS_COLL_NAME, process);
			return ok();
		}else {
			return badRequest();
		}
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

		if (CollectionUtils.isNotEmpty(processesSearch.projectCodes)) { //all
			queryElts.add(DBQuery.in("projectCode", processesSearch.projectCodes));
		}else if(StringUtils.isNotBlank(processesSearch.projectCode)){
			queryElts.add(DBQuery.is("projectCode", processesSearch.projectCode));
		}
		
		if (CollectionUtils.isNotEmpty(processesSearch.sampleCodes)) { //all
			queryElts.add(DBQuery.in("sampleCode", processesSearch.sampleCodes));
		}else if(StringUtils.isNotBlank(processesSearch.sampleCode)){
			queryElts.add(DBQuery.is("sampleCode", processesSearch.sampleCode));
		}

		if(StringUtils.isNotEmpty(processesSearch.experimentCode)){
			queryElts.add(DBQuery.in("experimentCodes", processesSearch.experimentCode));
		}
		
		if(StringUtils.isNotEmpty(processesSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", processesSearch.typeCode));
		}
		
		if(StringUtils.isNotEmpty(processesSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", processesSearch.categoryCode));
		}
		
		if(processesSearch.stateCodes != null ){
			queryElts.add(DBQuery.in("state.code", processesSearch.stateCodes));
		}else if(processesSearch.stateCode != null){
			queryElts.add(DBQuery.is("state.code", processesSearch.stateCode));
		}
		if(processesSearch.users != null){
			queryElts.add(DBQuery.in("traceInformation.createUser", processesSearch.users));
		}
		
		if(null != processesSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", processesSearch.fromDate));
		}

		if(null != processesSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(processesSearch.toDate, 1))));
		}
		
		if(StringUtils.isNotEmpty(processesSearch.supportCode) || StringUtils.isNotEmpty(processesSearch.containerSupportCategory) ){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			
			ContainersSearchForm cs = new ContainersSearchForm();
			cs.supportCode = processesSearch.supportCode;
			cs.containerSupportCategory=processesSearch.containerSupportCategory;

			List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, Containers.getQuery(cs), keys).toList();
			
			List<Query> queryContainer = new ArrayList<Query>();
			for(Container c: containers){
				queryContainer.add(DBQuery.is("containerInputCode", c.code));
			}

			Logger.debug("newContainerSupportCodes :"+processesSearch.supportCode);
			queryContainer.add(DBQuery.regex("newContainerSupportCodes",Pattern.compile(processesSearch.supportCode)));

			
			if(queryContainer.size()!=0){
				queryElts.add(DBQuery.or(queryContainer.toArray(new Query[queryContainer.size()])));
			}
			else {
				queryElts.add(DBQuery.exists("code"));
			}
			
			Logger.debug("Nb containers find"+containers.size());
		}
		
		
		if(StringUtils.isNotEmpty(processesSearch.experimentCode)){
			queryElts.add(DBQuery.regex("experimentCodes",Pattern.compile(processesSearch.experimentCode)));
		}
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new Query[queryElts.size()]));
		}
		
		return query;
	}
}