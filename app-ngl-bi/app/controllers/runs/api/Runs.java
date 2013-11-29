package controllers.runs.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.State;
//import models.laboratory.common.description.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Validation;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.Props;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import rules.services.RulesActor;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;
import controllers.CommonController;
import controllers.authorisation.Permission;
import controllers.utils.FormUtils;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
/**
 * Controller around Run object
 *
 */
public class Runs extends CommonController {

	
	final static Form<RunSearchForm> searchForm = form(RunSearchForm.class); 
	final static Form<Run> runForm = form(Run.class);
	final static Form<Treatment> treatmentForm = form(Treatment.class);
	final static Form<Validation> validationForm = form(Validation.class);
	final static Form<State> stateForm = form(State.class);

	private static ActorRef rulesActor = Akka.system().actorOf(new Props(RulesActor.class));

	//@Permission(value={"reading"})
	public static Result list(){
		Form<RunSearchForm> filledForm = filledFormQueryString(searchForm, RunSearchForm.class);
		RunSearchForm form = filledForm.get();
		
		if(form.datatable){
			MongoDBResult<Run> results = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, getQuery(form)) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.page(form.pageNumber,form.numberRecordsPerPage); 
			List<Run> runs = results.toList();
			return ok(Json.toJson(new DatatableResponse<Run>(runs, results.count())));
		}else{
			MongoDBResult<Run> results = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, getQuery(form))
					.sort("code", Sort.valueOf(form.orderSense));
			List<Run> runs = results.toList();
			return ok(Json.toJson(runs));
		}
	}

	private static Query getQuery(RunSearchForm filledForm) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(filledForm.stateCode)) { //all
			queries.add(DBQuery.is("state.code", filledForm.stateCode));
		}else if (CollectionUtils.isNotEmpty(filledForm.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", filledForm.stateCodes));
		}
		if (StringUtils.isNotBlank(filledForm.validCode)) { //all
			queries.add(DBQuery.is("validation.valid", TBoolean.valueOf(filledForm.validCode)));
		}

		if (CollectionUtils.isNotEmpty(filledForm.projectCodes)) { //all
			queries.add(DBQuery.in("projectCodes", filledForm.projectCodes));
		}
		
		if (CollectionUtils.isNotEmpty(filledForm.sampleCodes)) { //all
			queries.add(DBQuery.in("sampleCodes", filledForm.sampleCodes));
		}
		
		if (CollectionUtils.isNotEmpty(filledForm.typeCodes)) { //all
			queries.add(DBQuery.in("typeCode", filledForm.typeCodes));
		}
		
		if(null != filledForm.fromDate){
			queries.add(DBQuery.greaterThanEquals("traceInformation.creationDate", filledForm.fromDate));
		}
		
		if(null != filledForm.toDate){
			queries.add(DBQuery.lessThanEquals("traceInformation.creationDate", filledForm.toDate));
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}
	
	//@Permission(value={"reading"})
	public static Result get(String code) {
		Run runValue = getRun(code);
		if (runValue != null) {		
			return ok(Json.toJson(runValue));					
		} else {
			return notFound();
		}
	}

	//@Permission(value={"reading"})
	public static Result head(String code){
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code)){			
			return ok();					
		}else{
			return notFound();
		}
	}

	//@Permission(value={"creation_update_run_lane"})
	public static Result save() {
		Form<Run> filledForm = getFilledForm(runForm, Run.class);
		Run runValue = filledForm.get();

		if (null == runValue._id) { 
			runValue.traceInformation = new TraceInformation();
			runValue.traceInformation.setTraceInformation(getCurrentUser());
			
			//TODO History management
			if(null != runValue.state){
				runValue.state.user = getCurrentUser();
				runValue.state.date = new Date();		
			}
		} else {
			return badRequest("use PUT method to update the readset");
		}

		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		ctxVal.setCreationMode();
		runValue.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			runValue = MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runValue);
			return ok(Json.toJson(runValue));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}

	//@Permission(value={"creation_update_run_lane"})
	public static Result update(String code) {
		Run run = getRun(code);
		if (run == null) {
			return badRequest("Run with code "+code+" not exist");
		}

		Form<Run> filledForm = getFilledForm(runForm, Run.class);
		Run runValue = filledForm.get();
		if (code.equals(runValue.code)) {
			if(null != runValue.traceInformation){
				runValue.traceInformation.setTraceInformation(getCurrentUser());
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			//TODO State History management ???
			
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 			
			ctxVal.setUpdateMode();
			runValue.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runValue);
				return ok(Json.toJson(run));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			return badRequest("run code are not the same");
		}

	}


	public static Result delete(String code) {
		Run run = getRun(code);
		if (run == null) {
			return badRequest();
		}		
		MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);	
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", code));

		return ok();
	}

	//@Permission(value={"workflow_run_lane"})
	public static Result state(String code, String stateCode){
		Run run = getRun(code);
		if (run == null) {
			return badRequest();
		}
		Form<State> filledForm =  getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		if(null == state.code)state.code = stateCode;
		state.date = new Date();
		state.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		Workflows.setRunState(ctxVal, run, state);
		if (!ctxVal.hasErrors()) {
			return ok();
		}else {
			return badRequest(filledForm.errorsAsJson());
		}
	}

	private static Run getRun(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		return run;
	}
	
	//@Permission(value={"validation_run_lane"})
	public static Result validation(String code, String validCode){
		Run run = getRun(code);
		if(run == null){
			return badRequest();
		}
		Form<Validation> filledForm =  getFilledForm(validationForm, Validation.class);
		Validation validation = filledForm.get();
		validation.date = new Date();
		validation.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.setUpdateMode();
		RunValidationHelper.validateValidation(run.typeCode, validation, ctxVal);
		if(!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", code)),
					DBUpdate.set("validation", validation));			
			run.validation = validation;
			if(isRunCompletelyEvaluate(run)){
				State state = new State();
				state.code = "F-V";
				state.date = new Date();
				state.user = getCurrentUser();
				Workflows.setRunState(ctxVal, run, state);
			}
			
		} 
		if(!ctxVal.hasErrors()) {
			return ok();
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}


	private static boolean isRunCompletelyEvaluate(Run run) {

		if(run.validation.valid.equals(TBoolean.UNSET)){
			return false;
		}
		for(Lane lane : run.lanes){
			if(lane.validation.valid.equals(TBoolean.UNSET)){
				return false;
			}
		}
		return true;
	}

	@Deprecated
	public static Result dispatch(String code) {
		Run run = getRun(code);		
		if (run != null) {
			JsonNode json = request().body().asJson();
			Logger.info("Dispatch run : "+code);
			boolean dispatch = json.get("dispatch").asBoolean();
			MongoDBDAO.updateSet(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run, "dispatch", dispatch);
			//TODO ReadSet dispatch
		} else {
			return badRequest();
		}		
		return ok();	
	}

	public static Result applyRules(String code, String rulesCode){
		Run run = getRun(code);
		if(run!=null){
			//Send run fact
			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(run);
			// Outside of an actor and if no reply is needed the second argument can be null
			rulesActor.tell(new RulesMessage(facts,ConfigFactory.load().getString("rules.key"),rulesCode),null);
		}else
			return badRequest();
		
		return ok();
	}

}
