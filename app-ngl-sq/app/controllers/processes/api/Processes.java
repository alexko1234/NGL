package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
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

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.utils.ValidationConstants;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

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

	public static List<Process> saveFromSupport(String supportCode, Form<Process> filledForm, ContextValidation contextValidation){			
		List<Process> processes = new ArrayList<Process>();
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("support.code", supportCode)).toList();
		for(Container container:containers){			
			filledForm.get().containerInputCode = container.code;
			processes.addAll(saveAllContentsProcesses(filledForm, container, contextValidation ));
		}
		return processes;	
	}


	public static List<Process> saveAllContentsProcesses(Form<Process> filledForm, Container container, ContextValidation contextValidation){	
		Process process = filledForm.get();

		List<Process> processes = new ArrayList<Process>();
		List<String> processCodes=new ArrayList<String>();		
		ContextValidation contextError=new ContextValidation(contextValidation.getUser());

		for(Content c:container.contents){		
			//code generation
			process.sampleCode = c.sampleCode;
			process.code = CodeHelper.getInstance().generateProcessCode(process);
			process.sampleOnInputContainer = InstanceHelpers.getSampleOnInputContainer(c, container);				
			Process p = (Process)InstanceHelpers.save(InstanceConstants.PROCESS_COLL_NAME,process, contextValidation);
			Logger.info("New process code : "+process.code);
			if(p != null){
				processes.add(p);					
				processCodes.add(p.code);
			}
		}
		processes = ProcessHelper.applyRules(processes, contextValidation, "processCreation");
		if(!contextError.hasErrors()){
			ProcessHelper.updateContainer(container,process.typeCode, processCodes,contextValidation);
			ProcessHelper.updateContainerSupportFromContainer(container,contextValidation);
			try {
				ProcessType pt = ProcessType.find.findByCode(process.typeCode);
				Workflows.nextContainerState(process,pt.firstExperimentType.code, pt.firstExperimentType.category.code,new ContextValidation(getCurrentUser(), filledForm.errors()));
			} catch (DAOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			contextValidation.errors.putAll(contextError.errors);
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
		//Form<ProcessesSearchForm> processesFilledForm = filledFormQueryString(processesSearchForm,ProcessesSearchForm.class);
		ProcessesSearchForm processesSearch = filledFormQueryString(ProcessesSearchForm.class);

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

		if(StringUtils.isNotBlank(processesSearch.experimentCode)){
			queryElts.add(DBQuery.in("experimentCodes", processesSearch.experimentCode));
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
			cs.supportCode = processesSearch.supportCode;
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


		if(StringUtils.isNotBlank(processesSearch.experimentCode)){
			queryElts.add(DBQuery.regex("experimentCodes",Pattern.compile(processesSearch.experimentCode)));
		}

		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(processesSearch.properties, Level.CODE.Process, "properties"));

		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new Query[queryElts.size()]));
		}

		return query;
	}
}