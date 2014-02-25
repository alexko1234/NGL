package controllers.runs.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
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
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

import com.mongodb.BasicDBObject;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.Props;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.ValidationError;
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
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;
/**
 * Controller around Run object
 *
 */
public class Runs extends RunsController {

	
	final static Form<RunsSearchForm> searchForm = form(RunsSearchForm.class); 
	final static Form<Run> runForm = form(Run.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static Form<Valuation> valuationForm = form(Valuation.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("keep");
	
	private static ActorRef rulesActor = Akka.system().actorOf(new Props(RulesActor.class));

	//@Permission(value={"reading"})
	public static Result list(){
		Form<RunsSearchForm> filledForm = filledFormQueryString(searchForm, RunsSearchForm.class);
		RunsSearchForm form = filledForm.get();
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<Run> results = mongoDBFinder(InstanceConstants.RUN_ILLUMINA_COLL_NAME, form, Run.class, getQuery(form), keys);			
			List<Run> runs = results.toList();
			return ok(Json.toJson(new DatatableResponse<Run>(runs, results.count())));
		}else if(form.list){
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			if(null == form.orderBy)form.orderBy = "code";
			if(null == form.orderSense)form.orderSense = 0;
			MongoDBResult<Run> results = mongoDBFinder(InstanceConstants.RUN_ILLUMINA_COLL_NAME, form, Run.class, getQuery(form), keys);			
			List<Run> runs = results.toList();			
			return ok(Json.toJson(toListObjects(runs)));
		}else{
			if(null == form.orderBy)form.orderBy = "code";
			if(null == form.orderSense)form.orderSense = 0;
			MongoDBResult<Run> results = mongoDBFinder(InstanceConstants.RUN_ILLUMINA_COLL_NAME, form, Run.class, getQuery(form), keys);	
			List<Run> runs = results.toList();
			return ok(Json.toJson(runs));
		}
	}

	

	private static List<ListObject> toListObjects(List<Run> runs){
		List<ListObject> jo = new ArrayList<ListObject>();
		for(Run r: runs){
			jo.add(new ListObject(r.code, r.code));
		}
		return jo;
	}
	
	private static Query getQuery(RunsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if(CollectionUtils.isNotEmpty(form.codes)){
			queries.add(DBQuery.in("code", form.codes));
		}
		
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
			queries.add(DBQuery.greaterThanEquals("sequencingStartDate", form.fromDate));
		}
		
		if(null != form.toDate){
			queries.add(DBQuery.lessThanEquals("sequencingStartDate", form.toDate));
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
		Run runInput = filledForm.get();

		if (null == runInput._id) { 
			runInput.traceInformation = new TraceInformation();
			runInput.traceInformation.setTraceInformation(getCurrentUser());
			
			if(null == runInput.state){
				runInput.state = new State();
			}
			runInput.state.code = "N";
			runInput.state.user = getCurrentUser();
			runInput.state.date = new Date();		
			
		} else {
			return badRequest("use PUT method to update the readset");
		}

		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		ctxVal.setCreationMode();
		runInput.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			runInput = MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runInput);
			return ok(Json.toJson(runInput));
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

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Form<Run> filledForm = getFilledForm(runForm, Run.class);
		Run runInput = filledForm.get();
		
		if(queryFieldsForm.fields == null){
			if (code.equals(runInput.code)) {
				if(null != runInput.traceInformation){
					runInput.traceInformation.setTraceInformation(getCurrentUser());
				}else{
					Logger.error("traceInformation is null !!");
				}
				
				if(!run.state.code.equals(runInput.state.code)){
					return badRequest("You cannot change the state code. Please used the state url ! ");
				}
				ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 	
				ctxVal.setUpdateMode();
				runInput.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runInput);
					return ok(Json.toJson(runInput));
				}else {
					return badRequest(filledForm.errorsAsJson());
				}
				
			}else{
				return badRequest("run code are not the same");
			}	
		}else{
			//warning no validation !!!
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			if(!filledForm.hasErrors()){
				TraceInformation ti = run.traceInformation;
				ti.setTraceInformation(getCurrentUser());
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", code)), getBuilder(runInput, queryFieldsForm.fields, Run.class).set("traceInformation", ti));
				return ok(Json.toJson(getRun(code)));
			}else{
				return badRequest(filledForm.errorsAsJson());
			}			
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

	
	
	
	//@Permission(value={"valuation_run_lane"})
	public static Result valuation(String code){
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
					DBUpdate.set("valuation", valuation).set("traceInformation", getUpdateTraceInformation(run)));			
			run = getRun(code);
			Workflows.nextRunState(ctxVal, run);
			
		} 
		if(!ctxVal.hasErrors()) {
			return ok(Json.toJson(run));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
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
