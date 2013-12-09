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
import models.laboratory.common.instance.Valuation;
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

	
	final static Form<RunsSearchForm> searchForm = form(RunsSearchForm.class); 
	final static Form<Run> runForm = form(Run.class);
	final static Form<Valuation> valuationForm = form(Valuation.class);
	final static Form<State> stateForm = form(State.class);

	private static ActorRef rulesActor = Akka.system().actorOf(new Props(RulesActor.class));

	//@Permission(value={"reading"})
	public static Result list(){
		Form<RunsSearchForm> filledForm = filledFormQueryString(searchForm, RunsSearchForm.class);
		RunsSearchForm form = filledForm.get();
		
		if(form.datatable){
			MongoDBResult<Run> results = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, getQuery(form)) 
					.sort(form.orderBy, Sort.valueOf(form.orderSense))
					.page(form.pageNumber,form.numberRecordsPerPage); 
			List<Run> runs = results.toList();
			return ok(Json.toJson(new DatatableResponse<Run>(runs, results.count())));
		}else{
			MongoDBResult<Run> results = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, getQuery(form))
					.sort("code", Sort.valueOf(form.orderSense)).limit(form.limit);
			List<Run> runs = results.toList();
			return ok(Json.toJson(runs));
		}
	}

	private static Query getQuery(RunsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.is("state.code", form.stateCode));
		}else if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", form.stateCodes));
		}
		
		if (StringUtils.isNotBlank(form.validCode)) { //all
			queries.add(DBQuery.is("valuation.valid", TBoolean.valueOf(form.validCode)));
		}

		if (CollectionUtils.isNotEmpty(form.projectCodes)) { //all
			queries.add(DBQuery.in("projectCodes", form.projectCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.sampleCodes)) { //all
			queries.add(DBQuery.in("sampleCodes", form.sampleCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.typeCodes)) { //all
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}
		
		if(null != form.fromDate){
			queries.add(DBQuery.greaterThanEquals("traceInformation.creationDate", form.fromDate));
		}
		
		if(null != form.toDate){
			queries.add(DBQuery.lessThanEquals("traceInformation.creationDate", form.toDate));
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
			
			//Historical management
			//if we want the current state in the historical 
			/*
			if (runValue.state.historical == null || runValue.state.historical.size() == 0) {
				Workflows.saveHistorical(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runValue.code, runValue.state);
			} */
			
			
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
			

			
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 			
			ctxVal.setUpdateMode();
			runValue.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				
				boolean bSaveHistorical = false;
				
				//State Historical management
				/*
				if (! run.state.code.equals(runValue.state.code) ) {
					State state2 = Workflows.saveHistorical(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run.code, run.state);
					runValue.state.historical = state2.historical;
					bSaveHistorical = true;
				} */
				
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runValue);
				
				//State Historical management 
				/// if we want the current value of state in the historical...
				/*
				if (bSaveHistorical) {
					Workflows.saveHistorical(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runValue.code, runValue.state);
				} */
				
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
		Workflows.setRunState(ctxVal, run, state, run.state);
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
	
	//@Permission(value={"valuation_run_lane"})
	public static Result valuation(String code, String validCode){
		Run run = getRun(code);
		if(run == null){
			return badRequest();
		}
		Form<Valuation> filledForm =  getFilledForm(valuationForm, Valuation.class);
		Valuation valuation = filledForm.get();
		valuation.date = new Date();
		valuation.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.setUpdateMode();
		RunValidationHelper.validateValuation(run.typeCode, valuation, ctxVal);
		if(!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", code)),
					DBUpdate.set("valuation", valuation));			
			run.valuation = valuation;
			if(isRunCompletelyEvaluate(run)){
				State state = new State();
				state.code = "F-V";
				state.date = new Date();
				state.user = getCurrentUser();				
				Workflows.setRunState(ctxVal, run, state, run.state);
			}
			
		} 
		if(!ctxVal.hasErrors()) {
			return ok();
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}


	private static boolean isRunCompletelyEvaluate(Run run) {

		if(run.valuation.valid.equals(TBoolean.UNSET)){
			return false;
		}
		for(Lane lane : run.lanes){
			if(lane.valuation.valid.equals(TBoolean.UNSET)){
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
