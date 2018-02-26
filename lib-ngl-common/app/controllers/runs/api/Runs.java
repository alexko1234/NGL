package controllers.runs.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import com.mongodb.BasicDBObject;

import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
// import fr.cea.ig.MongoDBDatatableResponseChunks;
// import fr.cea.ig.MongoDBResponseChunks;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;
//import static fr.cea.ig.play.IGGlobals.akkaSystem;
import fr.cea.ig.mongo.MongoStreamer;
// import fr.cea.ig.play.IGGlobals;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
// import play.Logger;
// import play.Play;
// import play.api.modules.spring.Spring;
import play.data.Form;
// import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import rules.services.LazyRules6Actor;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
import views.components.datatable.DatatableForm;
import workflows.run.RunWorkflows;


/**
 * Controller around Run object
 *
 */
public class Runs extends RunsController {

	private static final play.Logger.ALogger logger = play.Logger.of(Runs.class);
	
	final static List<String> authorizedUpdateFields = Arrays.asList("keep","deleted");
	final static List<String> defaultKeys =  Arrays.asList("code", "typeCode", "sequencingStartDate", "state", "valuation");
	
	//final static Form<RunsSearchForm> searchForm = form(RunsSearchForm.class); 
	private final Form<Run>             runForm;       // = form(Run.class);
	private final Form<QueryFieldsForm> updateForm;    // = form(QueryFieldsForm.class);
	private final Form<Valuation>       valuationForm; // = form(Valuation.class);
	// private final ActorRef              rulesActor;
	// private final String                rulesKey;
	private final RunWorkflows          workflows;
	private final LazyRules6Actor       rulesActor;
	
	// final static RunWorkflows workflows = IGGlobals.instanceOf(RunWorkflows.class); // Spring.get BeanOfType(RunWorkflows.class);
	// private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	// private /*static*/ ActorRef rulesActor;// = akkaSystem().actorOf(Props.create(RulesActor6.class));
	
	@Inject
	public Runs(NGLContext ctx, RunWorkflows workflows) {
		runForm        = ctx.form(Run.class);
		updateForm     = ctx.form(QueryFieldsForm.class);
		valuationForm  = ctx.form(Valuation.class);
		// rulesActor     = ctx.akkaSystem().actorOf(Props.create(RulesActor6.class));
		// rulesKey       = ctx.config().getRulesKey();
		rulesActor     = ctx.rules6Actor();
		this.workflows = workflows;
	}
	
	@Permission(value={"reading"})
//	@Authenticated
//	@Historized
//	@Authorized.Read
	public Result list(){

		//Form<RunsSearchForm> filledForm = filledFormQueryString(searchForm, RunsSearchForm.class);
		//RunsSearchForm form = filledForm.get();
		RunsSearchForm form = filledFormQueryString(RunsSearchForm.class);
		BasicDBObject keys = getKeys(updateForm(form));
		
		if(form.datatable){			
			MongoDBResult<Run> results = mongoDBFinder(InstanceConstants.RUN_ILLUMINA_COLL_NAME, form, Run.class, getQuery(form), keys);			
			// return ok(new MongoDBDatatableResponseChunks<Run>(results)).as("application/json");
			// return ok(MongoStreamer.streamUDT(results)).as("application/json");
			return MongoStreamer.okStreamUDT(results);
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
			// return ok(new MongoDBResponseChunks<Run>(results)).as("application/json");
			// return ok(MongoStreamer.stream(results)).as("application/json");
			return MongoStreamer.okStream(results);
		}
	}

	private /*static*/ DatatableForm updateForm(RunsSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		}
		return form;
	}
	
	private /*static*/ List<ListObject> toListObjects(List<Run> runs){
		List<ListObject> jo = new ArrayList<ListObject>();
		for(Run r: runs){
			jo.add(new ListObject(r.code, r.code));
		}
		return jo;
	}
	
	private /*static*/ Query getQuery(RunsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if(CollectionUtils.isNotEmpty(form.codes)){
			queries.add(DBQuery.in("code", form.codes));
		}else if(StringUtils.isNotBlank(form.code)){
			queries.add(DBQuery.is("code", form.code));
		}
		
		if(CollectionUtils.isNotEmpty(form.categoryCodes)){
			queries.add(DBQuery.in("categoryCode", form.categoryCodes));
		}else if(StringUtils.isNotBlank(form.categoryCode)){
			queries.add(DBQuery.is("categoryCode", form.categoryCode));
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
		}else if(StringUtils.isNotBlank(form.projectCode)){
			queries.add(DBQuery.is("projectCodes", form.projectCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.sampleCodes)) { //all
			queries.add(DBQuery.in("sampleCodes", form.sampleCodes));
		}else if(StringUtils.isNotBlank(form.sampleCode)){
			queries.add(DBQuery.in("sampleCodes", form.sampleCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.typeCodes)) { //all
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}
		
		if(CollectionUtils.isNotEmpty(form.containerSupportCodes)){
			queries.add(DBQuery.in("containerSupportCode", form.containerSupportCodes));
		}else if(StringUtils.isNotBlank(form.containerSupportCode)){
			queries.add(DBQuery.is("containerSupportCode", form.containerSupportCode));
		}
		
		if(null != form.keep){
			queries.add(DBQuery.is("keep", form.keep));
		}
			
		if(null != form.fromDate){
			queries.add(DBQuery.greaterThanEquals("sequencingStartDate", getFromDate(form.fromDate).getTime()));
		}
		
		if(null != form.toDate){
			queries.add(DBQuery.lessThanEquals("sequencingStartDate", getToDate(form.toDate).getTime()));
		}
		
		if(null != form.fromEndRGDate){
			DBQuery.Query fromEndRG = DBQuery.elemMatch("state.historical", 
					DBQuery.is("code", "F-RG").greaterThanEquals("date", getFromDate(form.fromEndRGDate).getTime()));			
			queries.add(fromEndRG);
		}
		
		if(null != form.toEndRGDate){
			DBQuery.Query toEndRG = DBQuery.elemMatch("state.historical", 
					DBQuery.is("code", "F-RG").lessThanEquals("date", getToDate(form.toEndRGDate).getTime()));
			
			queries.add(toEndRG);
		}
		
		
		if (CollectionUtils.isNotEmpty(form.instrumentCodes)) { //all
			queries.add(DBQuery.in("instrumentUsed.code", form.instrumentCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.runResolutionCodes)) { //all
			queries.add(DBQuery.in("valuation.resolutionCodes", form.runResolutionCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.laneResolutionCodes)) { //all
			queries.add(DBQuery.in("lanes.valuation.resolutionCodes", form.laneResolutionCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.resolutionCodes)) { //all
			queries.add(DBQuery.or(DBQuery.in("valuation.resolutionCodes", form.resolutionCodes), 
					DBQuery.in("lanes.valuation.resolutionCodes", form.resolutionCodes)));			
		}
		
		if(null != form.valuationUser){
			queries.add(DBQuery.is("valuation.user", form.valuationUser));
		}
		
		if(null != form.valuationCriteriaCode){
			queries.add(DBQuery.is("valuation.criteriaCode", form.valuationCriteriaCode));
		}
		
		queries.addAll(NGLControllerHelper.generateQueriesForProperties(form.properties, Level.CODE.Run, "properties"));
		queries.addAll(NGLControllerHelper.generateQueriesForTreatmentProperties(form.treatmentProperties, Level.CODE.Run, "treatments"));
		queries.addAll(NGLControllerHelper.generateQueriesForTreatmentProperties(form.treatmentLanesProperties, Level.CODE.Lane, "lanes.treatments"));
		
		
		if (CollectionUtils.isNotEmpty(form.existingFields)) { //all
			for(String field : form.existingFields){
				queries.add(DBQuery.exists(field));
			}		
		}
		
		if (CollectionUtils.isNotEmpty(form.notExistingFields)) { //all
			for(String field : form.notExistingFields){
				queries.add(DBQuery.notExists(field));
			}
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}

	
	@Permission(value={"reading"})
//	@Authenticated
//	@Historized
//	@Authorized.Read
	public /*static*/ Result get(String code) {
		
		DatatableForm form = filledFormQueryString(DatatableForm.class);
		
		Run runValue = getRun(code, form.includes.toArray(new String[0]));
		if (runValue != null) {		
			return ok(Json.toJson(runValue));					
		} else {
			return notFound();
		}
	}

	@Permission(value={"reading"})
//	@Authenticated
//	@Historized
//	@Authorized.Read
	public /*static*/ Result head(String code){
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code)){			
			return ok();					
		}else{
			return notFound();
		}
	}

	
	@Permission(value={"writing"})	
//	@Authenticated
//	@Historized
//	@Authorized.Write
	//@Permission(value={"creation_update_run_lane"})
	public /*static*/ Result save() throws DAOException {
		Form<Run> filledForm = getFilledForm(runForm, Run.class);
		Run runInput = filledForm.get();

		if (null == runInput._id) { 
			runInput.traceInformation = new TraceInformation();
			runInput.traceInformation.setTraceInformation(getCurrentUser());
			
			if (null == runInput.state) {
				runInput.state = new State();
			}
			runInput.state.code = "N";
			runInput.state.user = getCurrentUser();
			runInput.state.date = new Date();
			
			if(null == runInput.categoryCode && null != runInput.typeCode){
				runInput.categoryCode = RunCategory.find.findByTypeCode(runInput.typeCode).code;
			}
			
			
		} else {
			return badRequest("use PUT method to update the readset");
		}

		RunsSaveForm runSaveForm = filledFormQueryString(RunsSaveForm.class);
		
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ctxVal.setCreationMode();
		if(runSaveForm.external!=null)
			ctxVal.putObject("external", runSaveForm.external);
		else
			ctxVal.putObject("external", false);
		runInput.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			runInput = MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runInput);
			return ok(Json.toJson(runInput));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

	@Permission(value={"writing"})
//	@Authenticated
//	@Historized
//	@Authorized.Write
	//@Permission(value={"creation_update_run_lane"})
	public /*static*/ Result update(String code) {
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
				} else {
					logger.error("traceInformation is null !!");
				}
				
				if(!run.state.code.equals(runInput.state.code)){
					return badRequest("You cannot change the state code. Please used the state url ! ");
				}
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
				ctxVal.setUpdateMode();
				runInput.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runInput);
					return ok(Json.toJson(runInput));
				} else {
					//return badRequest(filledForm.errors-AsJson());
					return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
				}
				
			}else{
				return badRequest("run code are not the same");
			}	
		}else{
			//warning no validation !!!
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			// if(!filledForm.hasErrors()){
			if (!ctxVal.hasErrors()) {
				TraceInformation ti = run.traceInformation;
				ti.setTraceInformation(getCurrentUser());
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", code)), getBuilder(runInput, queryFieldsForm.fields, Run.class).set("traceInformation", ti));
				/*
				if(queryFieldsForm.fields.contains("code") && null != runInput.code){
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
							DBQuery.is("runCode", code), 
							DBUpdate.set("runCode", runInput.code));
				}
				*/
				return ok(Json.toJson(getRun(code)));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}			
		}
	}


	@Permission(value={"writing"})
//	@Authenticated
//	@Historized
//	@Authorized.Write
	public /*static*/ Result delete(String code) {
		Run run = getRun(code);
		if (run == null) {
			return badRequest();
		}		
		MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);	
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", code));
		//TODO delete analysis
		return ok();
	}

	
	
	@Permission(value={"writing"})	
//	@Authenticated
//	@Historized
//	@Authorized.Write
	//@Permission(value={"valuation_run_lane"})
	public /*static*/ Result valuation(String code){
		Run run = getRun(code);
		if(run == null){
			return badRequest();
		}
		Form<Valuation> filledForm =  getFilledForm(valuationForm, Valuation.class);
		Valuation valuation = filledForm.get();
		valuation.date = new Date();
		valuation.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setUpdateMode();
		RunValidationHelper.validateValuation(run.typeCode, valuation, ctxVal);
		if(!ctxVal.hasErrors()) {			
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", code)),
					DBUpdate.set("valuation", valuation).set("traceInformation", getUpdateTraceInformation(run)));			
			run = getRun(code);
			workflows.nextState(ctxVal, run);
			
		} 
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(run));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	@Permission(value={"writing"})
//	@Authenticated
//	@Historized
//	@Authorized.Write
	public /*static*/ Result applyRules(String code, String rulesCode){
		Run run = getRun(code);
		if (run != null) {
			//Send run fact			
			// Outside of an actor and if no reply is needed the second argument can be null
			// rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),rulesCode, run),null);
			// rulesActor.tell(new RulesMessage(rulesKey,rulesCode, run),null);
			rulesActor.tellMessage(rulesCode, run);
		} else
			return badRequest();
		return ok();
	}
	
}
