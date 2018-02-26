package controllers.processes.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.DocumentController;
import controllers.NGLControllerHelper;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ProcessHelper;
import models.utils.instance.SampleHelper;
// import play.Logger;
// import play.api.modules.spring.Spring;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Lang;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.utils.ValidationConstants;
import views.components.datatable.DatatableBatchResponseElement;
// import workflows.process.ProcWorkflowHelper;
import workflows.process.ProcWorkflows;

public class Processes extends DocumentController<Process> {

	private static final play.Logger.ALogger logger = play.Logger.of(Processes.class);
	
	private final Form<State>                 stateForm;// = form(State.class);
	// private final /*static*/ Form<ProcessesSearchForm> processesSearchForm;// = form(ProcessesSearchForm.class);
	private final Form<ProcessesBatchElement> batchElementForm;// = form(ProcessesBatchElement.class);
	// final static ProcWorkflows workflows = Spring.get BeanOfType(ProcWorkflows.class);
	// final static ProcWorkflowHelper workflowHelper = Spring.get BeanOfType(ProcWorkflowHelper.class);
	private final ProcWorkflows               workflows;
	// private final ProcWorkflowHelper workflowHelper;
	
	@Inject
	public Processes(NGLContext ctx, ProcWorkflows workflows /*, ProcWorkflowHelper workflowHelper*/) {
		super(ctx,InstanceConstants.PROCESS_COLL_NAME, Process.class);		
		defaultKeys         =  Arrays.asList("*");
		stateForm           = ctx.form(State.class);
		// processesSearchForm = ctx.form(ProcessesSearchForm.class);
		batchElementForm    = ctx.form(ProcessesBatchElement.class);
		this.workflows      = workflows;
		// this.workflowHelper = workflowHelper;
	}

	@Permission(value={"reading"})
	public Result list() {
		ProcessesSearchForm searchForm = filledFormQueryString(ProcessesSearchForm.class);
		if (searchForm.reporting) {
			return nativeMongoDBQuery(searchForm);
		} else {
			DBQuery.Query query = getQuery(searchForm);
			return mongoJackQuery(searchForm, query);			
		}
	}
	
	

	/**
	 * Construct the process query
	 * @param processesSearch
	 * @return the query
	 */
	private DBQuery.Query getQuery(ProcessesSearchForm processesSearch) throws DAOException{
		List<Query> queryElts = new ArrayList<Query>();
		Query query = null;

		logger.info("Process Query : " + processesSearch);

		if (CollectionUtils.isNotEmpty(processesSearch.projectCodes)) { //all
			queryElts.add(DBQuery.in("projectCodes", processesSearch.projectCodes));
		} else if(StringUtils.isNotBlank(processesSearch.projectCode)) {
			queryElts.add(DBQuery.is("projectCodes", processesSearch.projectCode));
		}

		if (CollectionUtils.isNotEmpty(processesSearch.sampleCodes)) { //all
			queryElts.add(DBQuery.in("sampleCodes", processesSearch.sampleCodes));
		} else if(StringUtils.isNotBlank(processesSearch.sampleCode)) {
			queryElts.add(DBQuery.is("sampleCodes", processesSearch.sampleCode));
		}
		
		if (CollectionUtils.isNotEmpty(processesSearch.sampleTypeCodes)) { //all
			queryElts.add(DBQuery.in("sampleOnInputContainer.sampleTypeCode", processesSearch.sampleTypeCodes));
		}

		if (StringUtils.isNotBlank(processesSearch.code)) {
			queryElts.add(DBQuery.is("code", processesSearch.code));
		} else if (CollectionUtils.isNotEmpty(processesSearch.codes)) {
			queryElts.add(DBQuery.in("code", processesSearch.codes));
		} else if(StringUtils.isNotBlank(processesSearch.codeRegex)) {
			queryElts.add(DBQuery.regex("code", Pattern.compile(processesSearch.codeRegex)));
		}
		
		if (StringUtils.isNotBlank(processesSearch.experimentCode)) {
			queryElts.add(DBQuery.in("experimentCodes", processesSearch.experimentCode));
		} else if(CollectionUtils.isNotEmpty(processesSearch.experimentCodes)) {
			queryElts.add(DBQuery.in("experimentCodes", processesSearch.experimentCodes));
		} else if(StringUtils.isNotBlank(processesSearch.experimentCodeRegex)) {
			queryElts.add(DBQuery.regex("experimentCodes", Pattern.compile(processesSearch.experimentCodeRegex)));
		}

		if (StringUtils.isNotBlank(processesSearch.typeCode)) {
			queryElts.add(DBQuery.is("typeCode", processesSearch.typeCode));
		} else if(CollectionUtils.isNotEmpty(processesSearch.typeCodes)) {
			queryElts.add(DBQuery.in("typeCode", processesSearch.typeCodes));
		}

		if (StringUtils.isNotBlank(processesSearch.categoryCode)) {
			queryElts.add(DBQuery.is("categoryCode", processesSearch.categoryCode));
		} else if(CollectionUtils.isNotEmpty(processesSearch.categoryCodes)) {
			queryElts.add(DBQuery.in("categoryCode", processesSearch.categoryCodes));
		}

		if (CollectionUtils.isNotEmpty(processesSearch.stateCodes)) {
			queryElts.add(DBQuery.in("state.code", processesSearch.stateCodes));
		} else if(StringUtils.isNotBlank(processesSearch.stateCode)) {
			queryElts.add(DBQuery.is("state.code", processesSearch.stateCode));
		}
		if (CollectionUtils.isNotEmpty(processesSearch.users)) {
			queryElts.add(DBQuery.in("traceInformation.createUser", processesSearch.users));
		}

		if (StringUtils.isNotBlank(processesSearch.createUser)) {   
			queryElts.add(DBQuery.is("traceInformation.createUser", processesSearch.createUser));
		}

		if (null != processesSearch.fromDate) {
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", processesSearch.fromDate));
		}

		if (null != processesSearch.toDate) {
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(processesSearch.toDate, 1))));
		}
		/* Old version
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
		 */
		
		if (StringUtils.isNotBlank(processesSearch.supportCode)) {
			queryElts.add(DBQuery.or(DBQuery.is("inputContainerSupportCode",processesSearch.supportCode), DBQuery.is("outputContainerSupportCodes",processesSearch.supportCode)));
		} else if(StringUtils.isNotBlank(processesSearch.supportCodeRegex)) {
			queryElts.add(DBQuery.or(DBQuery.regex("inputContainerSupportCode",Pattern.compile(processesSearch.supportCodeRegex)), 
					DBQuery.regex("outputContainerSupportCodes",Pattern.compile(processesSearch.supportCodeRegex))));			
		} else if(CollectionUtils.isNotEmpty(processesSearch.supportCodes)) {
			queryElts.add(DBQuery.or(DBQuery.in("inputContainerSupportCode",processesSearch.supportCodes), DBQuery.in("outputContainerSupportCodes",processesSearch.supportCodes)));
		}
		
		if (StringUtils.isNotBlank(processesSearch.containerCode)) {
			queryElts.add(DBQuery.or(DBQuery.is("inputContainerCode",processesSearch.containerCode), DBQuery.is("outputContainerCodes",processesSearch.containerCode)));
		} else if(StringUtils.isNotBlank(processesSearch.containerCodeRegex)) {
			queryElts.add(DBQuery.or(DBQuery.regex("inputContainerCode",Pattern.compile(processesSearch.containerCodeRegex)), 
					DBQuery.regex("outputContainerCodes",Pattern.compile(processesSearch.containerCodeRegex))));			
		} else if(CollectionUtils.isNotEmpty(processesSearch.containerCodes)) {
			queryElts.add(DBQuery.or(DBQuery.in("inputContainerCode",processesSearch.containerCodes), DBQuery.in("outputContainerCodes",processesSearch.containerCodes)));
		} else if(CollectionUtils.isNotEmpty(processesSearch.outputContainerCodes)) {
			queryElts.add(DBQuery.in("outputContainerCodes",processesSearch.outputContainerCodes));
		} else if(CollectionUtils.isNotEmpty(processesSearch.inputContainerCodes)) {
			queryElts.add(DBQuery.in("inputContainerCode",processesSearch.inputContainerCodes));
		}
		
		if (CollectionUtils.isNotEmpty(processesSearch.stateResolutionCodes)) { //all
			queryElts.add(DBQuery.in("state.resolutionCodes", processesSearch.stateResolutionCodes));
		}
		
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(processesSearch.properties, Level.CODE.Process, "properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(processesSearch.sampleOnInputContainerProperties, Level.CODE.Content, "sampleOnInputContainer.properties"));

		if (queryElts.size() > 0) {
			query = DBQuery.and(queryElts.toArray(new Query[queryElts.size()]));
		}

		return query;
	}
	
	@Permission(value={"writing"})
	public Result save(){	
		Form<Process> filledForm = getMainFilledForm();
		Process input = filledForm.get();		
		if (input._id != null)
			return badRequest("use PUT method to update the process");
//		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), filledForm);
		List<Process> processes = saveOneElement(contextValidation, input);
		if (!contextValidation.hasErrors())
			return ok(Json.toJson(processes));
		// return badRequest(filledForm.errors-AsJson());
		return badRequest(errorsAsJson(contextValidation.getErrors()));
	}

	private List<Process> saveOneElement(ContextValidation contextValidation, Process input) {
		//the trace
		input.traceInformation = new TraceInformation();
		input.traceInformation.setTraceInformation(contextValidation.getUser());
		if (input.state == null)
			input.state = new State();
		input.state.code = "N";
		input.state.user = contextValidation.getUser();
		input.state.date = new Date();
		
		contextValidation.setCreationMode();
		contextValidation.putObject(CommonValidationHelper.FIELD_PROCESS_CREATION_CONTEXT, CommonValidationHelper.VALUE_PROCESS_CREATION_CONTEXT_COMMON);
		input.validate(contextValidation);
		
		if (!contextValidation.hasErrors()) {
			List<Process> processes = getNewProcessList(contextValidation, input);
			if (processes.size()>0) {
				processes = ProcessHelper.applyRules(processes, contextValidation, "processCreation");
			}
			contextValidation.putObject(CommonValidationHelper.FIELD_PROCESS_CREATION_CONTEXT, CommonValidationHelper.VALUE_PROCESS_CREATION_CONTEXT_SPECIFIC);
			processes.stream().forEach(p -> p.validate(contextValidation));
			if (!contextValidation.hasErrors()) {
				processes = processes.parallelStream()
						.map(p -> {
							Process newP = saveObject(p);
							workflows.applySuccessPostStateRules(contextValidation, newP);
							return newP;							
						})
						.collect(Collectors.toList());
				
			}	
			return processes;
		}
		return null;
	}
	
	private List<Process> getNewProcessList(ContextValidation contextValidation, Process input) {
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, input.inputContainerCode);
		return container.contents.parallelStream().map(content ->{
				Process process = input.cloneCommon();
				process.sampleCodes = SampleHelper.getSampleParent(content.sampleCode);
				process.projectCodes = SampleHelper.getProjectParent(process.sampleCodes);
				process.sampleOnInputContainer = InstanceHelpers.getSampleOnInputContainer(content, container);
				//need sampleOnInputContainer to generate code
				process.code = CodeHelper.getInstance().generateProcessCode(process);
				return process;
			}).collect(Collectors.toList());		
	}
	
	@Permission(value={"writing"})
	public Result saveBatch() {	
		List<Form<ProcessesBatchElement>> filledForms =  getFilledFormList(batchElementForm, ProcessesBatchElement.class);
		final String user = getCurrentUser();
		final Lang lang = Http.Context.Implicit.lang();
		List<DatatableBatchResponseElement> response = filledForms.parallelStream()
		.map(filledForm -> {
			ProcessesBatchElement element = filledForm.get();
			Process process = element.data;
			if (process._id == null) {
//				ContextValidation contextValidation = new ContextValidation(user, filledForm.errors());
				ContextValidation contextValidation = new ContextValidation(user, filledForm);
				List<Process> processes = saveOneElement(contextValidation, process);
				if (!contextValidation.hasErrors()) {
					return new DatatableBatchResponseElement(OK,  processes, element.index);
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
	public Result update(String code){
		Process objectInDB = getObject(code);
		if (objectInDB == null)
			return notFound("Process with code "+code+" does not exist");

		Form<Process> filledForm = getMainFilledForm();
		Process input = filledForm.get();
		if (input.code.equals(code)) {
			if (input.traceInformation != null) {
				input.traceInformation = getUpdateTraceInformation(input.traceInformation);
			} else {
				logger.error("traceInformation is null !!");
			}
			if (!objectInDB.state.code.equals(input.state.code)) {
				return badRequest("you cannot change the state code. Please used the state url ! ");
			}
//			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
			ctxVal.setUpdateMode();
			workflows.applyPreValidateCurrentStateRules(ctxVal, input);
			input.validate(ctxVal);			
			if (!ctxVal.hasErrors()) {	
				updateObject(input);
				workflows.applyPostValidateCurrentStateRules(ctxVal, input);
				return ok(Json.toJson(input));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("process code are not the same");
		}
	}
	
	@Permission(value={"writing"})
	public Result updateState(String code){
		Process objectInDB = getObject(code);
		if (objectInDB == null) 
			return notFound();
		
		Form<State> filledForm =  getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		state.date = new Date();
		state.user = getCurrentUser();
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		workflows.setState(ctxVal, objectInDB, state);
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(getObject(code)));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	@Permission(value={"writing"})
	public Result updateStateBatch(){
		List<Form<ProcessesBatchElement>> filledForms =  getFilledFormList(batchElementForm, ProcessesBatchElement.class);
		final String user = getCurrentUser();
//		final Lang lang = Http.Context.Implicit.lang();
		List<DatatableBatchResponseElement> response = filledForms.parallelStream()
		.map(filledForm -> {
			ProcessesBatchElement element = filledForm.get();
			Process process = getObject(element.data.code);
			if (process != null) {
				State state = element.data.state;
				state.date = new Date();
				state.user = user;
//				ContextValidation ctxVal = new ContextValidation(user, filledForm.errors());
				ContextValidation ctxVal = new ContextValidation(user, filledForm);
				workflows.setState(ctxVal, process, state);
				if (!ctxVal.hasErrors()) {
					return new DatatableBatchResponseElement(OK,  getObject(process.code), element.index);
				} else {
					//return new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errors-AsJson(lang), element.index);
					return new DatatableBatchResponseElement(BAD_REQUEST, errorsAsJson(ctxVal.getErrors()), element.index);
				}
			} else {
				return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
			}
		}).collect(Collectors.toList());
		return ok(Json.toJson(response));
	}
	
	@Permission(value={"writing"})
	public Result delete(String code) throws DAOException{
		Process process = getObject(code);
		if (process == null) {
			return notFound("Process with code "+code+" does not exist");
		}
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,process.inputContainerCode);
		if (container == null) {
			return notFound("Container process "+code+"with code "+process.inputContainerCode+" does not exist");
		}
		DynamicForm deleteForm = getNGLContext().form();
//		ContextValidation contextValidation=new ContextValidation(getCurrentUser(),deleteForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(),deleteForm);
		if (process.state.code.equals("IP")) {
			contextValidation.addErrors("process.state.code", ValidationConstants.ERROR_BADSTATE_MSG, container.code);
		} else if(CollectionUtils.isNotEmpty(process.experimentCodes)) {
			contextValidation.addErrors("process.experimentCodes", ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, process.experimentCodes);
		} else if(!"IS".equals(container.state.code) && !"UA".equals(container.state.code)
				 && !"IW-P".equals(container.state.code)) {
			contextValidation.addErrors("process.inputContainerCode", ValidationConstants.ERROR_BADSTATE_MSG, container.state.code);
		}
		if (!contextValidation.hasErrors()) {
			return super.delete(code);
		} else {
			//return badRequest(deleteForm.errors-AsJson());
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		}	
	}
	
}
