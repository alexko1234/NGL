package controllers.processes.api;

// import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form; 
import fr.cea.ig.mongo.MongoStreamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.instance.Process;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ProcessHelper;


import models.utils.instance.SampleHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;
import org.mongojack.DBUpdate.Builder;



import play.Logger;
import play.Logger.ALogger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.i18n.Lang;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import validation.ContextValidation;
import validation.processes.instance.ProcessValidationHelper;
import validation.utils.ValidationConstants;
import views.components.datatable.DatatableBatchResponseElement;
import views.components.datatable.DatatableForm;
import workflows.process.ProcWorkflows;
import workflows.process.ProcessWorkflows;



import com.mongodb.BasicDBObject;



import controllers.CommonController;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import controllers.containers.api.Containers;
import controllers.containers.api.ContainersSearchForm;
import fr.cea.ig.MongoDBDAO;
// import fr.cea.ig.MongoDBDatatableResponseChunks;
// import fr.cea.ig.MongoDBResponseChunks;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;

// TODO: cleanup

public class ProcessesOld extends CommonController {

	final static Form<ProcessesSaveQueryForm> processSaveQueryForm = form(ProcessesSaveQueryForm.class);
	final static Form<Process> processForm = form(Process.class);
	final static Form<ProcessesSearchForm> processesSearchForm = form(ProcessesSearchForm.class);
	final static Form<QueryFieldsForm> saveForm = form(QueryFieldsForm.class);
	final static Form<ProcessesUpdateForm> processesUpdateForm = form(ProcessesUpdateForm.class);
	final static Form<ProcessesBatchElement> batchElementForm = form(ProcessesBatchElement.class);
	final static List<String> defaultKeys =  Arrays.asList("categoryCode","inputContainerCode","inputContainerSupportCode","sampleCodes", "sampleOnInputContainer", "typeCode", "state", "currentExperimentTypeCode", "outputContainerSupportCodes", "experimentCodes","projectCodes", "code", "traceInformation", "comments", "properties");
	private static final ALogger logger = Logger.of("Processes");
	final static Form<State> stateForm = form(State.class);
	final static ProcWorkflows workflows = Spring.getBeanOfType(ProcWorkflows.class);
	
	@Permission(value={"reading"})
	public static Result head(String processCode) {
		if (MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, processCode)) {			
			return ok();					
		} else {
			return notFound();
		}	
	}
	
	@Permission(value={"reading"})
	public static Result get(String code){
		Process process = getProcess(code);
		if (process == null) {
			return notFound();
		}
		return ok(Json.toJson(process));
	}

	@Permission(value={"writing"})
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
		} else {
			return badRequest("use PUT method to update the process");
		}

		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm.errors());

		if (!filledForm.hasErrors()) {
			contextValidation.setCreationMode();
			contextValidation.putObject("workflow", true);
			if (StringUtils.isNotBlank(queryFieldsForm.fromSupportContainerCode) && StringUtils.isBlank(queryFieldsForm.fromContainerInputCode)) {			
				processes = saveFromSupport(queryFieldsForm.fromSupportContainerCode, filledForm.get(), contextValidation);
			} else if(StringUtils.isNotBlank(queryFieldsForm.fromContainerInputCode) && StringUtils.isBlank(queryFieldsForm.fromSupportContainerCode)) {							

				if (!contextValidation.hasErrors()) {
					Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, queryFieldsForm.fromContainerInputCode);
					Process p = filledForm.get();
					p.inputContainerCode = container.code;
					p.inputContainerSupportCode = container.support.code;
					valdateCommonProcessAttribut(p, contextValidation);
					processes.addAll(saveAllContentsProcesses(p, container, contextValidation ));
				}				
			} else {
				return badRequest("Params 'from object' required!");
			}
		}

		if (contextValidation.hasErrors()) {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(contextValidation.getErrors()));
		} else {
			return ok(Json.toJson(processes));
		}
	}

	private static List<Process> saveFromSupport(String supportCode, Process p, ContextValidation contextValidation) {			
		List<Process> processes = new ArrayList<Process>();
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("support.code", supportCode).is("state.code","IW-P")).toList();
		
		valdateCommonProcessAttribut(p, contextValidation);
		
		containers.parallelStream().forEach(container -> {
			ContextValidation newContextValidation = new ContextValidation(contextValidation.getUser());
			newContextValidation.setCreationMode();
			newContextValidation.putObject("workflow", true);
			processes.addAll(saveAllContentsProcesses(p, container, newContextValidation));
			if(newContextValidation.hasErrors()){
				contextValidation.addErrors(newContextValidation.errors);
			}
		});
		
		return processes;	
	}
	
	
	private static void valdateCommonProcessAttribut(Process process, ContextValidation contextValidation) {
		ProcessValidationHelper.validateProcessType(process.typeCode,process.properties,contextValidation);
		ProcessValidationHelper.validateProcessCategory(process.categoryCode,contextValidation);
		ProcessValidationHelper.validateState(process.typeCode,process.state, contextValidation);
		ProcessValidationHelper.validateTraceInformation(process.traceInformation, contextValidation);			
	}

	// TODO: fix the bad mix between forms and contextvalidations.
	@Permission(value={"writing"})
	public static Result saveBatch() {
		if (true) throw new RuntimeException("fix the code");
		Form<ProcessesSaveQueryForm>  filledQueryFieldsForm = filledFormQueryString(processSaveQueryForm, ProcessesSaveQueryForm.class);
		List<Form<ProcessesBatchElement>> filledForms =  getFilledFormList(batchElementForm, ProcessesBatchElement.class);

		List<DatatableBatchResponseElement> response = new ArrayList<DatatableBatchResponseElement>(filledForms.size());
		ProcessesSaveQueryForm processesSaveQueryForm=filledQueryFieldsForm.get();

		// Form<Process> batchForm = new Form<Process>(Process.class);
		// Form<Process> batchForm = new Form<Process>(Process.class,null,null,null);
		Form<Process> batchForm = fr.cea.ig.play.IGGlobals.form(Process.class);
		
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
				process.sampleOnInputContainer.referenceCollab = InstanceHelpers.getReferenceCollab(process.sampleCodes.iterator().next());
				//TO-DO NGL-119
				process.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					Process p = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, process);
					processes.add(p);
					response.add(new DatatableBatchResponseElement(OK,  p, element.index));
				} else {
					contextValidation.errors.putAll(ctxVal.errors);
					response.add(new DatatableBatchResponseElement(BAD_REQUEST, batchForm.errorsAsJson( ), element.index)); // check source 
				}
			} else {
				response.add(new DatatableBatchResponseElement(BAD_REQUEST, processForm.errorsAsJson( ), element.index)); // check source
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

	


	private static List<Process> saveAllContentsProcesses(Process process, Container container, ContextValidation contextValidation){	
		//The process is replicated in each content, so we can validate once
		ProcessValidationHelper.validateContainerCode(container.code, contextValidation, "inputContainerCode");
		
		final List<Process> processes = new ArrayList<Process>();
		container.contents.parallelStream().forEach(c -> {
			Process newProcess = new Process();
			//code generation
			newProcess.categoryCode = process.categoryCode;
			newProcess.comments = process.comments;
			newProcess.inputContainerCode = container.code;
			newProcess.inputContainerSupportCode = container.support.code;
			newProcess.currentExperimentTypeCode = newProcess.currentExperimentTypeCode;
			newProcess.experimentCodes = process.experimentCodes;
			newProcess.outputContainerSupportCodes = process.outputContainerSupportCodes;
			newProcess.properties = process.properties;
			newProcess.state = process.state;
			newProcess.traceInformation = process.traceInformation;
			newProcess.typeCode = process.typeCode;
			newProcess.sampleCodes = new HashSet<String>();
			newProcess.projectCodes = new HashSet<String>();
			newProcess.sampleCodes=SampleHelper.getSampleParent(c.sampleCode);
			newProcess.projectCodes=SampleHelper.getProjectParent(newProcess.sampleCodes);
			newProcess.sampleOnInputContainer = InstanceHelpers.getSampleOnInputContainer(c, container);				
			newProcess.code = CodeHelper.getInstance().generateProcessCode(newProcess);
			//Logger.info("New process code : "+newProcess.code);
			processes.add(newProcess);	
			//We don't need to validate all the properties for each creation
			//because the new process is just a copy of the one in the form
			//newProcess.validate(contextValidation);
			//These are the properties that change for each process so we have to validate them each time we create
			//the copy
			ProcessValidationHelper.validateCode(newProcess, InstanceConstants.PROCESS_COLL_NAME, contextValidation);
			ProcessValidationHelper.validateProjectCodes(newProcess.projectCodes, contextValidation);
			ProcessValidationHelper.validateSampleCodes(newProcess.sampleCodes,contextValidation);
		});
		
		if(!contextValidation.hasErrors()){
			List<Process> savedProcesses = new ArrayList<Process>();
			for(Process p:processes){
				Process savedProcess = MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, p);
				if(savedProcess != null){
					savedProcesses.add(savedProcess);
				}else{
					Logger.error("Process null !!!");
				}
			}
			List<Process> processes2 = ProcessHelper.applyRules(savedProcesses, contextValidation, "processCreation");
			ProcessWorkflows.nextContainerStateFromNewProcesses(processes2, process.typeCode, contextValidation);
			return processes2;
		}
		return processes;
	}

	@Permission(value={"writing"})
	public static Result update(String code){
		Process process = getProcess(code);
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
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("process code are not the same");
		}
	}
	
	@Permission(value={"writing"})
	@Deprecated
	public static Result updateStateCode(String code){
		Form<ProcessesUpdateForm> processesUpdateFilledForm = getFilledForm(processesUpdateForm,ProcessesUpdateForm.class);
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), processesUpdateFilledForm.errors());
		contextValidation.setUpdateMode();
		if (!processesUpdateFilledForm.hasErrors()) {
			ProcessesUpdateForm processesUpdateForm = processesUpdateFilledForm.get();
			State state = new State();
			state.code = processesUpdateForm.stateCode;
			state.user = getCurrentUser();
			ProcessWorkflows.setProcessState(code, state, contextValidation);
			if(!contextValidation.hasErrors()){
				return ok();
			}
		}
		// return badRequest(processesUpdateFilledForm.errors-AsJson());
		return badRequest(NGLContext._errorsAsJson(contextValidation.getErrors()));
	}

	@Permission(value={"writing"})
	public static Result updateState(String code){
		Process process = getProcess(code);
		if(process == null){
			return badRequest();
		}
		Form<State> filledForm =  getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		state.date = new Date();
		state.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		workflows.setState(ctxVal, process, state);
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(getProcess(code)));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

	private static Process getProcess(String code) {
		return MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME, Process.class, code);
	}
	
	@Permission(value={"writing"})
	public static Result updateStateBatch(){
		List<Form<ProcessesBatchElement>> filledForms =  getFilledFormList(batchElementForm, ProcessesBatchElement.class);
		final String user = getCurrentUser();
		final Lang lang = Http.Context.Implicit.lang();
		List<DatatableBatchResponseElement> response = filledForms.parallelStream()
		.map(filledForm -> {
			ProcessesBatchElement element = filledForm.get();
			Process process = getProcess(element.data.code);
			if(null != process){
				State state = element.data.state;
				state.date = new Date();
				state.user = user;
				ContextValidation ctxVal = new ContextValidation(user, filledForm.errors());
				workflows.setState(ctxVal, process, state);
				if (!ctxVal.hasErrors()) {
					return new DatatableBatchResponseElement(OK,  getProcess(process.code), element.index);
				} else {
					return new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errorsAsJson(lang), element.index);
				}
			} else {
				return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
			}
		}).collect(Collectors.toList());
		
		return ok(Json.toJson(response));
	}
	
	@Permission(value={"writing"})
	public static Result delete(String code) throws DAOException{
		Process process = getProcess(code);
		// Form deleteForm = new Form(Process.class);
		// Form deleteForm = new Form(Process.class,null,null,null);
		Form deleteForm = fr.cea.ig.play.IGGlobals.form(Process.class);
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(),deleteForm.errors());
		if (process == null) {
			return notFound("Process with code "+code+" does not exist");
		}

		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,process.inputContainerCode);
		if (container == null) {
			return notFound("Container process "+code+"with code "+process.inputContainerCode+" does not exist");
		}
		if (!process.state.code.equals("N")) {
			contextValidation.addErrors("process", ValidationConstants.ERROR_BADSTATE_MSG, container.code);
		} else if (CollectionUtils.isNotEmpty(process.experimentCodes)) {
			contextValidation.addErrors("process", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, process.experimentCodes);
		} else if (!"IS".equals(container.state.code) && !"UA".equals(container.state.code)) {
			contextValidation.addErrors("container", ValidationConstants.ERROR_BADSTATE_MSG, container.state.code);
		}

		if (!contextValidation.hasErrors()) {
			MongoDBDAO.deleteByCode(InstanceConstants.PROCESS_COLL_NAME,Process.class,  process.code);
			return ok();
		} else {
			contextValidation.displayErrors(logger);
			// return badRequest(deleteForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(contextValidation.getErrors()));
		}
	}

	@Permission(value={"reading"})
	public static Result list() throws DAOException {
		//Form<ProcessesSearchForm> processesFilledForm = filledFormQueryString(processesSearchForm,ProcessesSearchForm.class);
		ProcessesSearchForm processesSearch = filledFormQueryString(ProcessesSearchForm.class);

		DBQuery.Query query = getQuery(processesSearch);
		BasicDBObject keys = getKeys(updateForm(processesSearch));
		
		if(processesSearch.datatable){
			MongoDBResult<Process> results =  mongoDBFinder(InstanceConstants.PROCESS_COLL_NAME, processesSearch, Process.class, query, keys); 
			//return ok(new MongoDBDatatableResponseChunks<Process>(results)).as("application/json");
			return ok(MongoStreamer.streamUDT(results)).as("application/json");
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
			// return ok(new MongoDBResponseChunks<Process>(results)).as("application/json");
			return ok(MongoStreamer.stream(results)).as("application/json");
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
			queryElts.add(DBQuery.in("projectCodes", processesSearch.projectCodes));
		}else if(StringUtils.isNotBlank(processesSearch.projectCode)){
			queryElts.add(DBQuery.is("projectCodes", processesSearch.projectCode));
		}

		if (CollectionUtils.isNotEmpty(processesSearch.sampleCodes)) { //all
			queryElts.add(DBQuery.in("sampleCodes", processesSearch.sampleCodes));
		}else if(StringUtils.isNotBlank(processesSearch.sampleCode)){
			queryElts.add(DBQuery.is("sampleCodes", processesSearch.sampleCode));
		}
		
		if (CollectionUtils.isNotEmpty(processesSearch.sampleTypeCodes)) { //all
			queryElts.add(DBQuery.in("sampleOnInputContainer.sampleTypeCode", processesSearch.sampleTypeCodes));
		}

		if(StringUtils.isNotBlank(processesSearch.code)){
			queryElts.add(DBQuery.is("code", processesSearch.code));
		}else if(CollectionUtils.isNotEmpty(processesSearch.codes)){
			queryElts.add(DBQuery.in("code", processesSearch.codes));
		}else if(StringUtils.isNotBlank(processesSearch.codeRegex)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(processesSearch.codeRegex)));
		}
		
		if(StringUtils.isNotBlank(processesSearch.experimentCode)){
			queryElts.add(DBQuery.in("experimentCodes", processesSearch.experimentCode));
		}else if(CollectionUtils.isNotEmpty(processesSearch.experimentCodes)){
			queryElts.add(DBQuery.in("experimentCodes", processesSearch.experimentCodes));
		}else if(StringUtils.isNotBlank(processesSearch.experimentCodeRegex)){
			queryElts.add(DBQuery.regex("experimentCodes", Pattern.compile(processesSearch.experimentCodeRegex)));
		}

		if(StringUtils.isNotBlank(processesSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", processesSearch.typeCode));
		}else if(CollectionUtils.isNotEmpty(processesSearch.typeCodes)){
			queryElts.add(DBQuery.in("typeCode", processesSearch.typeCodes));
		}

		if(StringUtils.isNotBlank(processesSearch.categoryCode)){
			queryElts.add(DBQuery.is("categoryCode", processesSearch.categoryCode));
		}else if(CollectionUtils.isNotEmpty(processesSearch.categoryCodes)){
			queryElts.add(DBQuery.in("categoryCode", processesSearch.categoryCodes));
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

		if(StringUtils.isNotBlank(processesSearch.supportCode) || 
				StringUtils.isNotBlank(processesSearch.supportCodeRegex) ||
					CollectionUtils.isNotEmpty(processesSearch.supportCodes) ||	
						StringUtils.isNotBlank(processesSearch.containerSupportCategory) ){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);

			ContainersSearchForm cs = new ContainersSearchForm();
			cs.supportCodeRegex = processesSearch.supportCodeRegex;
			cs.supportCode = processesSearch.supportCode;
			cs.supportCodes = processesSearch.supportCodes;
			cs.containerSupportCategory=processesSearch.containerSupportCategory;

			List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, Containers.getQuery(cs), keys).toList();
			//InputContainer
			List<Query> queryContainer = new ArrayList<Query>();
			if(containers.size() > 0){
				queryContainer.add(DBQuery.in("inputContainerCode", containers.stream().map(c -> c.code).collect(Collectors.toList())));
			}
			
			if(StringUtils.isNotBlank(processesSearch.supportCode)){
				queryContainer.add(DBQuery.is("outputContainerSupportCodes",processesSearch.supportCode));
			} else if(StringUtils.isNotBlank(processesSearch.supportCodeRegex)){
				queryContainer.add(DBQuery.regex("outputContainerSupportCodes",Pattern.compile(processesSearch.supportCodeRegex)));
			} else if(CollectionUtils.isNotEmpty(processesSearch.supportCodes)){
				queryContainer.add(DBQuery.in("outputContainerSupportCodes",processesSearch.supportCodes));
			}
			
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