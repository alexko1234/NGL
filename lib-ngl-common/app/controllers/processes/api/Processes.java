package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.CodeHelper;
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
import org.mongojack.DBUpdate;

import play.Logger;
import play.Logger.ALogger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.processes.instance.ProcessValidationHelper;
import validation.utils.ValidationConstants;
import views.components.datatable.DatatableBatchResponseElement;
import views.components.datatable.DatatableForm;
import views.components.datatable.DatatableResponse;
import workflows.process.ProcessWorkflows;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.containers.api.Containers;
import controllers.containers.api.ContainersSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Processes extends CommonController{

	final static Form<ProcessesSaveQueryForm> processSaveQueryForm = form(ProcessesSaveQueryForm.class);
	final static Form<Process> processForm = form(Process.class);
	final static Form<ProcessesSearchForm> processesSearchForm = form(ProcessesSearchForm.class);
	final static Form<QueryFieldsForm> saveForm = form(QueryFieldsForm.class);
	final static Form<ProcessesUpdateForm> processesUpdateForm = form(ProcessesUpdateForm.class);
	final static Form<ProcessesBatchElement> processSaveBatchForm = form(ProcessesBatchElement.class);
	final static List<String> defaultKeys =  Arrays.asList("categoryCode","containerInputCode","sampleCode", "sampleOnInputContainer", "typeCode", "state", "currentExperimentTypeCode", "newContainerSupportCodes", "experimentCodes","projectCode", "code", "traceInformation.creationDate", "traceInformation.createUser", "properties");
	private static final ALogger logger = Logger.of("Processes");

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
		Form<ProcessesSaveQueryForm>  filledQueryFieldsForm = filledFormQueryString(processSaveQueryForm, ProcessesSaveQueryForm.class);
		ProcessesSaveQueryForm queryFieldsForm = filledQueryFieldsForm.get();		
		Form<Process> filledForm = getFilledForm(processForm, Process.class);
		Process process = filledForm.get();		
		List<Process> processes = new ArrayList<Process>();	

		if (null == process._id) {			//init state
			//the trace
			process.traceInformation = new TraceInformation();
			process.traceInformation.setTraceInformation(getCurrentUser());
			//the default status
			process.state = new State("N", getCurrentUser());
		}else {
			return badRequest("use PUT method to update the process");
		}

		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm.errors());

		if (!filledForm.hasErrors()) {
			contextValidation.setCreationMode();
			contextValidation.putObject("workflow", true);
			if(StringUtils.isNotBlank(queryFieldsForm.fromSupportContainerCode) && StringUtils.isBlank(queryFieldsForm.fromContainerInputCode)){			
				processes = saveFromSupport(queryFieldsForm.fromSupportContainerCode, filledForm, contextValidation);
			}else if(StringUtils.isNotBlank(queryFieldsForm.fromContainerInputCode) && StringUtils.isBlank(queryFieldsForm.fromSupportContainerCode)) {							

				if(!contextValidation.hasErrors()){
					Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, process.containerInputCode);
					filledForm.get().containerInputCode = container.code;
					processes.addAll(saveAllContentsProcesses(filledForm, container, contextValidation ));
				}				
			}else{
				return badRequest("Params 'from object' required!");
			}
		}

		if(contextValidation.hasErrors())
		{
			return badRequest(filledForm.errorsAsJson());
		}else {
			return ok(Json.toJson(processes));
		}
	}

	public static Result saveBatch(){
		Form<ProcessesSaveQueryForm>  filledQueryFieldsForm = filledFormQueryString(processSaveQueryForm, ProcessesSaveQueryForm.class);
		List<Form<ProcessesBatchElement>> filledForms =  getFilledFormList(processSaveBatchForm, ProcessesBatchElement.class);

		List<DatatableBatchResponseElement> response = new ArrayList<DatatableBatchResponseElement>(filledForms.size());
		ProcessesSaveQueryForm processesSaveQueryForm=filledQueryFieldsForm.get();

		Form batchForm = new Form(Process.class);
		List<Process> processes = new ArrayList<Process>();
		ContextValidation contextValidation =new ContextValidation(getCurrentUser(), batchForm.errors());

		for(Form<ProcessesBatchElement> filledForm: filledForms){
			ContextValidation ctxVal=new ContextValidation(getCurrentUser(), batchForm.errors());
			ProcessesBatchElement element = filledForm.get();
			if (!filledForm.hasErrors()) {
				ctxVal.setCreationMode();
				Process process = element.data;
				if (null == process._id) {			//init state
					//the trace
					process.traceInformation = new TraceInformation();
					process.traceInformation.setTraceInformation(getCurrentUser());
					//the default status
					process.state = new State("N", getCurrentUser());
				} else {
					return badRequest("use PUT method to update the process");
				}
				process.code = CodeHelper.getInstance().generateProcessCode(process);
				process.sampleOnInputContainer.lastUpdateDate = new Date();
				process.sampleOnInputContainer.referenceCollab = InstanceHelpers.getReferenceCollab(process.sampleCode);
				process.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					Process p = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, process);
					processes.add(p);
					response.add(new DatatableBatchResponseElement(OK,  p, element.index));
				}else{
					contextValidation.errors.putAll(ctxVal.errors);
					response.add(new DatatableBatchResponseElement(BAD_REQUEST, batchForm.errorsAsJson(), element.index));
				}
			}else {
				response.add(new DatatableBatchResponseElement(BAD_REQUEST, processForm.errorsAsJson(), element.index));
			}
		}

		ProcessWorkflows.nextContainerStateFromNewProcesses(processes,processesSaveQueryForm.processTypeCode,new ContextValidation(getCurrentUser(), batchForm.errors()));
		//ProcessWorkflows.nextContainerStateFromNewProcess(process,pt.firstExperimentType.code, pt.firstExperimentType.category.code,new ContextValidation(getCurrentUser(), filledForm.errors()));

		if(processes.size()>0){
			processes = ProcessHelper.applyRules(processes, contextValidation, "processCreation");
		}
		if(!contextValidation.hasErrors()){
			return ok(Json.toJson(response));
		}

		return badRequest(Json.toJson(response));
	}

	private static List<Process> saveFromSupport(String supportCode, Form<Process> filledForm, ContextValidation contextValidation){			
		List<Process> processes = new ArrayList<Process>();
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("support.code", supportCode)).toList();
		for(Container container:containers){			
			filledForm.get().containerInputCode = container.code;
			processes.addAll(saveAllContentsProcesses(filledForm, container, contextValidation ));
		}
		return processes;	
	}


	private static List<Process> saveAllContentsProcesses(Form<Process> filledForm, Container container, ContextValidation contextValidation){	
		Process process = filledForm.get();
		//The process is replicated in each content, so we can validate once
		ProcessValidationHelper.validateProcessType(process.typeCode,process.properties,contextValidation);
		ProcessValidationHelper.validateProcessCategory(process.categoryCode,contextValidation);
		ProcessValidationHelper.validateState(process.typeCode,process.state, contextValidation);
		ProcessValidationHelper.validateTraceInformation(process.traceInformation, contextValidation);
		ProcessValidationHelper.validateContainerCode(process.containerInputCode, contextValidation);
		
		List<Process> processes = new ArrayList<Process>();
		for(Content c:container.contents){
			Process newProcess = new Process();
			//code generation
			newProcess.categoryCode = process.categoryCode;
			newProcess.comments = process.comments;
			newProcess.containerInputCode = process.containerInputCode;
			newProcess.currentExperimentTypeCode = newProcess.currentExperimentTypeCode;
			newProcess.experimentCodes = process.experimentCodes;
			newProcess.newContainerSupportCodes = process.newContainerSupportCodes;
			newProcess.properties = process.properties;
			newProcess.state = process.state;
			newProcess.traceInformation = process.traceInformation;
			newProcess.typeCode = process.typeCode;
			newProcess.sampleCode = c.sampleCode;
			newProcess.projectCode = c.projectCode;
			newProcess.code = CodeHelper.getInstance().generateProcessCode(newProcess);
			newProcess.sampleOnInputContainer = InstanceHelpers.getSampleOnInputContainer(c, container);				
			//Logger.info("New process code : "+newProcess.code);
			processes.add(newProcess);	
			//We don't need to validate all the properties for each creation
			//because the new process is just a copy of the one in the form
			//newProcess.validate(contextValidation);
			//These are the properties that change for each process so we have to validate them each time we create
			//the copy
			ProcessValidationHelper.validateCode(newProcess, InstanceConstants.PROCESS_COLL_NAME, contextValidation);
			ProcessValidationHelper.validateProjectCode(newProcess.projectCode, contextValidation);
			ProcessValidationHelper.validateSampleCode(newProcess.sampleCode, newProcess.projectCode, contextValidation);
		}

		if(!contextValidation.hasErrors()){
			List<Process> savedProcesses = new ArrayList<Process>();
			for(Process p:processes){
				Process savedProcess = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, p);
				if(savedProcess != null){
					savedProcesses.add(savedProcess);
				}
			}
			processes = ProcessHelper.applyRules(savedProcesses, contextValidation, "processCreation");
			ProcessWorkflows.nextContainerStateFromNewProcesses(processes, process.typeCode, contextValidation);
		}
		return processes;
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

	public static Result updateStateCode(String code){
		Form<ProcessesUpdateForm> processesUpdateFilledForm = getFilledForm(processesUpdateForm,ProcessesUpdateForm.class);
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), processesUpdateFilledForm.errors());
		contextValidation.setUpdateMode();
		if(!processesUpdateFilledForm.hasErrors()){
			ProcessesUpdateForm processesUpdateForm = processesUpdateFilledForm.get();
			State state = new State();
			state.code = processesUpdateForm.stateCode;
			state.user = getCurrentUser();
			ProcessWorkflows.setProcessState(code, state, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok();
			}
		}
		return badRequest(processesUpdateFilledForm.errorsAsJson());
	}

	public static Result delete(String code){
		Process process = MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, code);
		Form deleteForm = new Form(Process.class);
		ContextValidation contextValidation=new ContextValidation(getCurrentUser(),deleteForm.errors());
		if(process == null){
			return notFound("Process with code "+code+" does not exist");
		}

		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,process.containerInputCode);
		if(container==null){
			return notFound("Container process "+code+"with code "+process.containerInputCode+" does not exist");
		}
		if(!process.state.code.equals("N")){
			contextValidation.addErrors("process", ValidationConstants.ERROR_BADSTATE_MSG, container.code);
		}else if(CollectionUtils.isNotEmpty(process.experimentCodes)){
			contextValidation.addErrors("process", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, process.experimentCodes);
		}else if(container.state.code.startsWith("A")){
			contextValidation.addErrors("container", ValidationConstants.ERROR_BADSTATE_MSG, container.code);
		}

		if(!contextValidation.hasErrors()){
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code",container.code),DBUpdate.pull("inputProcessCodes", process.code));
			MongoDBDAO.deleteByCode(InstanceConstants.PROCESS_COLL_NAME,Process.class,  process.code);
			return ok();
		}else {
			contextValidation.displayErrors(logger);
			return badRequest(deleteForm.errorsAsJson());
		}
	}

	public static Result list() throws DAOException{
		//Form<ProcessesSearchForm> processesFilledForm = filledFormQueryString(processesSearchForm,ProcessesSearchForm.class);
		ProcessesSearchForm processesSearch = filledFormQueryString(ProcessesSearchForm.class);

		DBQuery.Query query = getQuery(processesSearch);
		BasicDBObject keys = getKeys(updateForm(processesSearch));
		
		if(processesSearch.datatable){
			MongoDBResult<Process> results =  mongoDBFinder(InstanceConstants.PROCESS_COLL_NAME, processesSearch, Process.class, query, keys); 
			List<Process> processes = results.toList();
			return ok(Json.toJson(new DatatableResponse<Process>(processes, results.count())));
		}else if(processesSearch.list){
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
			MongoDBResult<Process> results = mongoDBFinder(InstanceConstants.PROCESS_COLL_NAME, processesSearch, Process.class, query, keys); 
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
		
		if (CollectionUtils.isNotEmpty(processesSearch.sampleTypeCodes)) { //all
			queryElts.add(DBQuery.in("sampleOnInputContainer.sampleTypeCode", processesSearch.sampleTypeCodes));
		}

		if(StringUtils.isNotBlank(processesSearch.processCode)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(processesSearch.processCode)));
		}		

		if(StringUtils.isNotBlank(processesSearch.experimentCode)){
			queryElts.add(DBQuery.regex("experimentCodes",Pattern.compile(processesSearch.experimentCode)));
		}

		if(StringUtils.isNotBlank(processesSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", processesSearch.typeCode));
		}

		if(StringUtils.isNotBlank(processesSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", processesSearch.categoryCode));
		}

		if(CollectionUtils.isNotEmpty(processesSearch.stateCodes)){
			queryElts.add(DBQuery.in("state.code", processesSearch.stateCodes));
		}else if(StringUtils.isNotBlank(processesSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", processesSearch.stateCode));
		}
		if(CollectionUtils.isNotEmpty(processesSearch.users)){
			queryElts.add(DBQuery.in("traceInformation.createUser", processesSearch.users));
		}

		if(StringUtils.isNotBlank(processesSearch.createUser)){   
			queryElts.add(DBQuery.is("traceInformation.createUser", processesSearch.createUser));
		}

		if(null != processesSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", processesSearch.fromDate));
		}

		if(null != processesSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(processesSearch.toDate, 1))));
		}

		if(StringUtils.isNotBlank(processesSearch.supportCode) || StringUtils.isNotBlank(processesSearch.containerSupportCategory) ){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);

			ContainersSearchForm cs = new ContainersSearchForm();
			cs.supportCodeRegex = processesSearch.supportCode;
			cs.containerSupportCategory=processesSearch.containerSupportCategory;

			List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, Containers.getQuery(cs), keys).toList();
			//InputContainer
			List<Query> queryContainer = new ArrayList<Query>();
			for(Container c: containers){
				queryContainer.add(DBQuery.is("containerInputCode", c.code));
			}
			//OutputContainers. We need to find all containers using the protocol.
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

		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(processesSearch.properties, Level.CODE.Process, "properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(processesSearch.sampleOnInputContainerProperties, Level.CODE.Content, "sampleOnInputContainer.properties"));

		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new Query[queryElts.size()]));
		}

		return query;
	}
	
	private static DatatableForm updateForm(ProcessesSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		}
		return form;
	}
}