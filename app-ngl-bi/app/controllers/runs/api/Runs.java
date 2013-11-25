package controllers.runs.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
/**
 * Controller around Run object
 *
 */
public class Runs extends CommonController {

	final static Form<Run> runForm = form(Run.class);
	final static DynamicForm listForm = new DynamicForm();
	final static Form<Treatment> treatmentForm = form(Treatment.class);
	final static Form<Validation> validationForm = form(Validation.class);
	final static Form<State> stateForm = form(State.class);

	private static ActorRef rulesActor = Akka.system().actorOf(new Props(RulesActor.class));

	//@Permission(value={"reading"})
	public static Result list(){
		DynamicForm filledForm =  listForm.bindFromRequest();
		MongoDBResult<Run> results = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, getQuery(filledForm)) 
				.sort(DatatableHelpers.getOrderBy(filledForm), FormUtils.getMongoDBOrderSense(filledForm))
				.page(DatatableHelpers.getPageNumber(filledForm), DatatableHelpers.getNumberRecordsPerPage(filledForm)); 
		List<Run> runs = results.toList();

		if(filledForm.get("datatable") != null){
			return ok(Json.toJson(new DatatableResponse<Run>(runs, results.count())));
		}else{
			return ok(Json.toJson(runs));
		}
	}

	private static Query getQuery(DynamicForm filledForm) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(filledForm.get("stateCode"))) { //all
			queries.add(DBQuery.is("state.code", filledForm.get("stateCode")));
		}

		if (StringUtils.isNotBlank(filledForm.get("validCode"))) { //all
			queries.add(DBQuery.is("validation.valid", TBoolean.valueOf(filledForm.get("validCode"))));
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
		Form<State> filledForm = stateForm.bindFromRequest();
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
		Form<Validation> filledForm = validationForm.bindFromRequest();
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
				return state(code, "F-V");
			}
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

	public static Result checkRules(String code, String rulesCode){
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
