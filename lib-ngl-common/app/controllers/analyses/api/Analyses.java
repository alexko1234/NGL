package controllers.analyses.api;

//import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form;
import static fr.cea.ig.play.IGGlobals.akkaSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;
import org.springframework.stereotype.Controller;

import com.mongodb.BasicDBObject;

import akka.actor.ActorRef;
import akka.actor.Props;
import controllers.DocumentController;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import play.Logger;
import play.Play;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Result;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import views.components.datatable.DatatableBatchResponseElement;
import views.components.datatable.DatatableResponse;
import workflows.analyses.AnalysisWorkflows;


//@Controller
public class Analyses extends DocumentController<Analysis> {

	//final static Form<AnalysesSearchForm> searchForm = form(AnalysesSearchForm.class);
	final static Form<Valuation> valuationForm = form(Valuation.class);
	final static Form<State> stateForm = form(State.class);
	final static Form<AnalysesBatchElement> batchElementForm = form(AnalysesBatchElement.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("code","masterReadSetCodes","readSetCodes");
	
	final static AnalysisWorkflows workflows = Spring.getBeanOfType(AnalysisWorkflows.class);
	// private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	private static ActorRef rulesActor = akkaSystem().actorOf(Props.create(RulesActor6.class));
	
	@Inject
	public Analyses(NGLContext ctx) {
		super(ctx,InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class);		
	}
	
	@Permission(value={"reading"})
	public Result list() {
		//Form<AnalysesSearchForm> filledForm = filledFormQueryString(searchForm, AnalysesSearchForm.class);
		//AnalysesSearchForm form = filledForm.get();
		
		AnalysesSearchForm form = filledFormQueryString( AnalysesSearchForm.class);
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<Analysis> results = mongoDBFinder(form, q, keys);				
			List<Analysis> list = results.toList();
			return ok(Json.toJson(new DatatableResponse<Analysis>(list, results.count())));
		}else{
			MongoDBResult<Analysis> results = mongoDBFinder(form, q, keys);							
			List<Analysis> list = results.toList();
			return ok(Json.toJson(list));
		}
	}
	
	private Query getQuery(AnalysesSearchForm form) {
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
		if (CollectionUtils.isNotEmpty(form.resolutionCodes)) { //all
			queries.add(DBQuery.in("valuation.resolutionCodes", form.resolutionCodes));
		}
		
		if (CollectionUtils.isNotEmpty(form.projectCodes)) { //all
			queries.add(DBQuery.in("projectCodes", form.projectCodes));
		}else if (StringUtils.isNotBlank(form.projectCode)) { //all
			queries.add(DBQuery.in("projectCodes", form.projectCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.sampleCodes)) { //all
			queries.add(DBQuery.in("sampleCodes", form.sampleCodes));
		}else if (StringUtils.isNotBlank(form.sampleCode)) { //all
			queries.add(DBQuery.in("sampleCodes", form.sampleCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.typeCodes)) { //all
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}
				
		if (StringUtils.isNotBlank(form.regexCode)) { //all
			queries.add(DBQuery.regex("code", Pattern.compile(form.regexCode)));
		}
		
		if (StringUtils.isNotBlank(form.analyseValuationUser)) {
			queries.add(DBQuery.is("valuation.user", form.analyseValuationUser));
		}
		
		queries.addAll(NGLControllerHelper.generateQueriesForProperties(form.properties, Level.CODE.Analysis, "properties"));
		queries.addAll(NGLControllerHelper.generateQueriesForTreatmentProperties(form.treatmentProperties, Level.CODE.Analysis, "treatments"));
		
		
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
	
	@Permission(value={"writing"})
	public Result save(){
		
		Form<Analysis> filledForm = getMainFilledForm();
		Analysis input = filledForm.get();
		
		if (null == input._id) { 
			input.traceInformation = new TraceInformation();
			input.traceInformation.setTraceInformation(getCurrentUser());
			
			if(null == input.state){
				input.state = new State();
			}
			input.state.code = "N";
			input.state.user = getCurrentUser();
			input.state.date = new Date();	
						
		} else {
			return badRequest("use PUT method to update the analysis");
		}
		if(null != input.masterReadSetCodes && input.masterReadSetCodes.size() > 0){
			updateAnalysis(input);			
		}
		
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setCreationMode();
		input.validate(ctxVal);	
		
		if (!ctxVal.hasErrors()) {
			input = saveObject(input);
			//TODO Update ReadSet
			return ok(Json.toJson(input));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	private void updateAnalysis(Analysis input) {
		for(String code: input.masterReadSetCodes){
			ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, code);
			input.projectCodes.add(readSet.projectCode);
			input.sampleCodes.add(readSet.sampleCode);
		}
		
	}

	@Permission(value={"writing"})
	public Result update(String code){
		Analysis objectInDB =  getObject(code);
		if(objectInDB == null) {
			return badRequest("Analysis with code "+code+" does not exist");
		}
		
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Form<Analysis> filledForm = getMainFilledForm();
		Analysis input = filledForm.get();
		
		if(queryFieldsForm.fields == null){
			if (input.code.equals(code)) {
				if (null != input.traceInformation) {
					input.traceInformation = getUpdateTraceInformation(input.traceInformation);
				} else {
					Logger.error("traceInformation is null !!");
				}
				if (!objectInDB.state.code.equals(input.state.code)) {
					return badRequest("you cannot change the state code. Please used the state url ! ");
				}
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
				ctxVal.setUpdateMode();
				input.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					updateObject(input);
					//TODO Update READSET
					return ok(Json.toJson(input));
				} else {
					// return badRequest(filledForm.errors-AsJson());
					return badRequest(errorsAsJson(ctxVal.getErrors()));
				}
			} else {
				return badRequest("Analysis code are not the same");
			}
		}else{ //update only some authorized properties
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			
			if(!ctxVal.hasErrors() && queryFieldsForm.fields.contains("code")){
				ctxVal.setCreationMode();
				CommonValidationHelper.validateCode(input, collectionName, ctxVal);
				//TODO Update READSET
			}
			
			if (!ctxVal.hasErrors()) {
				updateObject(DBQuery.and(DBQuery.is("code", code)), 
						getBuilder(input, queryFieldsForm.fields).set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
				if (queryFieldsForm.fields.contains("code") && null != input.code) {
					code = input.code;
				}
				return ok(Json.toJson(getObject(code)));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}			
		}
	}
	
	
	@Permission(value={"writing"})
	public Result state(String code){
		Analysis objectInDB = getObject(code);
		if(objectInDB == null) {
			return notFound();
		}
		Form<State> filledForm =  getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		state.date = new Date();
		state.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		workflows.setState(ctxVal, objectInDB, state);
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(getObject(code)));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
	}
	
	@Permission(value={"writing"})
	public Result stateBatch(){
		List<Form<AnalysesBatchElement>> filledForms =  getFilledFormList(batchElementForm, AnalysesBatchElement.class);
		List<DatatableBatchResponseElement> response = new ArrayList<DatatableBatchResponseElement>(filledForms.size());
		
		for(Form<AnalysesBatchElement> filledForm: filledForms){
			AnalysesBatchElement element = filledForm.get();
			Analysis objectInDB = getObject(element.data.code);
			if (null != objectInDB) {
				State state = element.data.state;
				state.date = new Date();
				state.user = getCurrentUser();
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
				workflows.setState(ctxVal, objectInDB, state);
				if (!ctxVal.hasErrors()) {
					response.add(new DatatableBatchResponseElement(OK, getObject(objectInDB.code), element.index));
				} else {
					//response.add(new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errors-AsJson(), element.index));
					response.add(new DatatableBatchResponseElement(BAD_REQUEST,errorsAsJson(ctxVal.getErrors()), element.index));
				}
			}else {
				response.add(new DatatableBatchResponseElement(BAD_REQUEST, element.index));
			}
			
		}		
		return ok(Json.toJson(response));
	}
	
	@Permission(value={"writing"})
	public Result valuation(String code){
		Analysis objectInDB = getObject(code);
		if(objectInDB == null) {
			return notFound();
		}
		Form<Valuation> filledForm =  getFilledForm(valuationForm, Valuation.class);
		Valuation input = filledForm.get();
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setUpdateMode();
		input.date = new Date();
		input.user = getCurrentUser();
		
		CommonValidationHelper.validateValuation(objectInDB.typeCode, input, ctxVal);
		if (!ctxVal.hasErrors()) {
			updateObject(DBQuery.and(DBQuery.is("code", code)), DBUpdate.set("valuation", input)
					.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
										
			objectInDB = getObject(code);
			workflows.nextState(ctxVal, objectInDB);
			return ok(Json.toJson(objectInDB));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
	}

	@Permission(value={"writing"})
	public Result valuationBatch(){
		List<Form<AnalysesBatchElement>> filledForms =  getFilledFormList(batchElementForm, AnalysesBatchElement.class);
		List<DatatableBatchResponseElement> response = new ArrayList<DatatableBatchResponseElement>(filledForms.size());
		
		for(Form<AnalysesBatchElement> filledForm: filledForms){
			AnalysesBatchElement element = filledForm.get();
			Analysis objectInDB = getObject(element.data.code);
			if(null != objectInDB){
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
				ctxVal.setUpdateMode();
				element.data.valuation.date = new Date();
				element.data.valuation.user = getCurrentUser();
				CommonValidationHelper.validateValuation(objectInDB.typeCode, element.data.valuation, ctxVal);
				if (!ctxVal.hasErrors()) {
					updateObject(DBQuery.and(DBQuery.is("code", objectInDB.code)), DBUpdate.set("valuation", element.data.valuation)
							.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
													
					objectInDB = getObject(objectInDB.code);
					workflows.nextState(ctxVal, objectInDB);
					response.add(new DatatableBatchResponseElement(OK, objectInDB, element.index));
				} else {
					// response.add(new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errors-AsJson(), element.index));
					response.add(new DatatableBatchResponseElement(BAD_REQUEST,errorsAsJson(ctxVal.getErrors()), element.index));
				}
			} else {
				response.add(new DatatableBatchResponseElement(BAD_REQUEST, element.index));
			}
			
		}		
		return ok(Json.toJson(response));
	}
	
	@Permission(value={"writing"})
	public Result properties(String code){
		Analysis objectInDB = getObject(code);
		if(objectInDB == null) {
			return notFound();
		}
			
		Form<Analysis> filledForm = getMainFilledForm();
		Map<String, PropertyValue> properties = filledForm.get().properties;
		
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ctxVal.setUpdateMode();
		//TODO AnalysisValidationHelper.validateAnalysisType(objectInDB.typeCode, properties, ctxVal);
		
		if (!ctxVal.hasErrors()) {
		    updateObject(DBQuery.and(DBQuery.is("code", objectInDB.code)), DBUpdate.set("properties", properties)
					.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
			objectInDB = getObject(objectInDB.code);
			return ok(Json.toJson(objectInDB));		
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}		
	}
	
	@Permission(value={"writing"})
	public Result propertiesBatch() {
		List<Form<AnalysesBatchElement>> filledForms =  getFilledFormList(batchElementForm, AnalysesBatchElement.class);
		List<DatatableBatchResponseElement> response = new ArrayList<DatatableBatchResponseElement>(filledForms.size());
		
		for(Form<AnalysesBatchElement> filledForm: filledForms) {
			AnalysesBatchElement element = filledForm.get();
			Analysis objectInDB = getObject(element.data.code);
			if(null != objectInDB){
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
				Map<String, PropertyValue> properties = element.data.properties;
				ctxVal.setUpdateMode();
				//TODO AnalysisValidationHelper.validateAnalysisType(objectInDB.typeCode, properties, ctxVal);
				if (!ctxVal.hasErrors()) {
					updateObject(DBQuery.and(DBQuery.is("code", objectInDB.code)), DBUpdate.set("properties", properties)
							.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));				   							
				    response.add(new DatatableBatchResponseElement(OK, getObject(element.data.code), element.index));
				} else {
					// response.add(new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errors-AsJson(), element.index));
					response.add(new DatatableBatchResponseElement(BAD_REQUEST, errorsAsJson(ctxVal.getErrors()), element.index));
				}
			} else {
				response.add(new DatatableBatchResponseElement(BAD_REQUEST, element.index));
			}
			
		}		
		return ok(Json.toJson(response));
	}
	
	@Permission(value={"writing"})
	public Result applyRules(String code, String rulesCode)	{
		Analysis objectInDB = getObject(code);
		if(objectInDB == null) {
			return notFound();
		}		
		// Outside of an actor and if no reply is needed the second argument can be null
		rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),rulesCode,objectInDB),null);
		return ok();
	}
}
