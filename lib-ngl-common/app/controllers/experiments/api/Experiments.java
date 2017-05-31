package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.api.modules.spring.Spring;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import validation.ContextValidation;
import workflows.experiment.ExpWorkflows;

import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import controllers.NGLControllerHelper;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;

public class Experiments extends DocumentController<Experiment>{
	
	final static Form<State> stateForm = form(State.class);
	
	final Form<Experiment> experimentForm = form(Experiment.class);
	final Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	final static List<String> defaultKeys =  Arrays.asList("categoryCode","code","inputContainerSupportCodes","instrument","outputContainerSupportCodes","projectCodes","protocolCode","reagents","sampleCodes","state","status","traceInformation","typeCode","atomicTransfertMethods.inputContainerUseds.contents");
	
	final ExpWorkflows workflows = Spring.getBeanOfType(ExpWorkflows.class);
	
	public static final String calculationsRules ="calculations";
	
	public Experiments() {
		super(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, defaultKeys);	
	}
	
	
	@Permission(value={"reading"})
	public Result list(){
		ExperimentSearchForm searchForm = filledFormQueryString(ExperimentSearchForm.class);
		if(searchForm.reporting){
			return nativeMongoDBQuery(searchForm);
		}else{
			DBQuery.Query query = getQuery(searchForm);
			return mongoJackQuery(searchForm, query);			
		}
	}
	/*
	@Permission(value={"reading"})
	public Result list(){
		//Form<ExperimentSearchForm> experimentFilledForm = filledFormQueryString(experimentSearchForm,ExperimentSearchForm.class);
		//ExperimentSearchForm experimentsSearch = experimentFilledForm.get();
		ExperimentSearchForm experimentsSearch = filledFormQueryString(ExperimentSearchForm.class);
		BasicDBObject keys = getKeys(updateForm(experimentsSearch));
		DBQuery.Query query = getQuery(experimentsSearch);

		if(experimentsSearch.datatable){
			MongoDBResult<Experiment> results =  mongoDBFinder(experimentsSearch, query, keys);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(new DatatableResponse<Experiment>(experiments, results.count())));
		}else if (experimentsSearch.list){
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			if(null == experimentsSearch.orderBy)experimentsSearch.orderBy = "code";
			if(null == experimentsSearch.orderSense)experimentsSearch.orderSense = 0;				

			MongoDBResult<Experiment> results = mongoDBFinder(experimentsSearch, query, keys);
			List<Experiment> experiments = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Experiment p: experiments){					
				los.add(new ListObject(p.code, p.code));								
			}
			return Results.ok(Json.toJson(los));
		}else{
			if(null == experimentsSearch.orderBy)experimentsSearch.orderBy = "code";
			if(null == experimentsSearch.orderSense)experimentsSearch.orderSense = 0;
			MongoDBResult<Experiment> results = mongoDBFinder(experimentsSearch, query, keys);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(experiments));
		}
	}

	protected DatatableForm updateForm(ExperimentSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		}
		return form;
	}
	*/
	/**
	 * Construct the experiment query
	 * @param experimentSearch
	 * @return the query
	 */
	protected DBQuery.Query getQuery(ExperimentSearchForm experimentSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query=DBQuery.empty();
		
		if(CollectionUtils.isNotEmpty(experimentSearch.codes)){
			queryElts.add(DBQuery.in("code", experimentSearch.codes));
		}else if(StringUtils.isNotBlank(experimentSearch.code)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(experimentSearch.code)));
		}
		
		if(StringUtils.isNotBlank(experimentSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", experimentSearch.typeCode));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.projectCodes)){
			queryElts.add(DBQuery.in("projectCodes", experimentSearch.projectCodes));
		}

		if(StringUtils.isNotBlank(experimentSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", experimentSearch.projectCode));
		}

		if(null != experimentSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", experimentSearch.fromDate));
		}

		if(null != experimentSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(experimentSearch.toDate, 1))));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.sampleCodes)){
			queryElts.add(DBQuery.in("sampleCodes", experimentSearch.sampleCodes));
		}

		
		Set<String> containerCodes = new TreeSet<String>();
		if(MapUtils.isNotEmpty(experimentSearch.atomicTransfertMethodsInputContainerUsedsContentsProperties)){
			List<DBQuery.Query> listContainerQuery = NGLControllerHelper.generateQueriesForProperties(experimentSearch.atomicTransfertMethodsInputContainerUsedsContentsProperties, Level.CODE.Content, "contents.properties");
			
			Query containerQuery = DBQuery.and(listContainerQuery.toArray(new DBQuery.Query[listContainerQuery.size()]));
			BasicDBObject keys = new BasicDBObject();
			keys.append("code", 1);
			List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerQuery,keys).toList();
			
			if(containers.size() == 0){
				containerCodes.add("########"); //to force to have zero results
			}else{
				for(Container p : containers){
					containerCodes.add(p.code);
				}	
			}				
		}
		
		if(StringUtils.isNotBlank(experimentSearch.containerCode)){			
			containerCodes.add(experimentSearch.containerCode);
		}else if(CollectionUtils.isNotEmpty(experimentSearch.containerCodes)){			
			containerCodes.addAll(experimentSearch.containerCodes);
		}
		
		if(containerCodes.size() > 0){
			List<DBQuery.Query> qs = new ArrayList<DBQuery.Query>();
			qs.add(DBQuery.in("inputContainerCodes",containerCodes));
			qs.add(DBQuery.in("outputContainerCodes",containerCodes));
			queryElts.add(DBQuery.or(qs.toArray(new DBQuery.Query[qs.size()])));
		}else if(StringUtils.isNotBlank(experimentSearch.containerCodeRegex)){			
			List<DBQuery.Query> qs = new ArrayList<DBQuery.Query>();
			qs.add(DBQuery.regex("inputContainerCodes",Pattern.compile(experimentSearch.containerCodeRegex)));
			qs.add(DBQuery.regex("outputContainerCodes",Pattern.compile(experimentSearch.containerCodeRegex)));
			queryElts.add(DBQuery.or(qs.toArray(new DBQuery.Query[qs.size()])));
		}
		
		
		if(StringUtils.isNotBlank(experimentSearch.containerSupportCode)){			
			List<DBQuery.Query> qs = new ArrayList<DBQuery.Query>();

			qs.add(DBQuery.in("inputContainerSupportCodes",experimentSearch.containerSupportCode));
			qs.add(DBQuery.in("outputContainerSupportCodes",experimentSearch.containerSupportCode));
			queryElts.add(DBQuery.or(qs.toArray(new DBQuery.Query[qs.size()])));
		}else if(CollectionUtils.isNotEmpty(experimentSearch.containerSupportCodes)){			
			List<DBQuery.Query> qs = new ArrayList<DBQuery.Query>();

			qs.add(DBQuery.in("inputContainerSupportCodes",experimentSearch.containerSupportCodes));
			qs.add(DBQuery.in("outputContainerSupportCodes",experimentSearch.containerSupportCodes));
			queryElts.add(DBQuery.or(qs.toArray(new DBQuery.Query[qs.size()])));
		}else if(StringUtils.isNotBlank(experimentSearch.containerSupportCodeRegex)){			
			List<DBQuery.Query> qs = new ArrayList<DBQuery.Query>();

			qs.add(DBQuery.regex("inputContainerSupportCodes",Pattern.compile(experimentSearch.containerSupportCodeRegex)));
			qs.add(DBQuery.regex("outputContainerSupportCodes",Pattern.compile(experimentSearch.containerSupportCodeRegex)));
			queryElts.add(DBQuery.or(qs.toArray(new DBQuery.Query[qs.size()])));
		}
		
		

		if(StringUtils.isNotBlank(experimentSearch.sampleCode)){
			queryElts.add(DBQuery.in("sampleCodes", experimentSearch.sampleCode));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.users)){
			queryElts.add(DBQuery.in("traceInformation.createUser", experimentSearch.users));
		}

		if(StringUtils.isNotBlank(experimentSearch.reagentOrBoxCode)){
			queryElts.add(DBQuery.or(DBQuery.regex("reagents.boxCode", Pattern.compile(experimentSearch.reagentOrBoxCode+"_|_"+experimentSearch.reagentOrBoxCode)),DBQuery.regex("reagents.code", Pattern.compile(experimentSearch.reagentOrBoxCode+"_|_"+experimentSearch.reagentOrBoxCode))));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.stateCodes)){
			queryElts.add(DBQuery.in("state.code", experimentSearch.stateCodes));
		}else if(StringUtils.isNotBlank(experimentSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", experimentSearch.stateCode));
		}

		if(StringUtils.isNotBlank(experimentSearch.instrument)){
			queryElts.add(DBQuery.is("instrument.code", experimentSearch.instrument));
		}else if(CollectionUtils.isNotEmpty(experimentSearch.instruments)){
			queryElts.add(DBQuery.in("instrument.code", experimentSearch.instruments));
		}else if(StringUtils.isNotBlank(experimentSearch.instrumentCode)){
			queryElts.add(DBQuery.is("instrument.code", experimentSearch.instrumentCode));
		}else if(CollectionUtils.isNotEmpty(experimentSearch.instrumentCodes)){
			queryElts.add(DBQuery.in("instrument.code", experimentSearch.instrumentCodes));
		}
		
		if(CollectionUtils.isNotEmpty(experimentSearch.protocolCodes)){
			queryElts.add(DBQuery.in("protocolCode", experimentSearch.protocolCodes));
		}
		
		// FDS 21/08/2015 ajout filtrage sur les types d'echantillon
		if(CollectionUtils.isNotEmpty(experimentSearch.sampleTypeCodes)){
			queryElts.add(DBQuery.in("atomicTransfertMethods.inputContainerUseds.contents.sampleTypeCode", experimentSearch.sampleTypeCodes));
		}
		
		if(StringUtils.isNotBlank(experimentSearch.containerFromTransformationTypeCode)){
			if(experimentSearch.containerFromTransformationTypeCode.contains("none")){
				queryElts.add(DBQuery.or(DBQuery.size("atomicTransfertMethods.inputContainerUseds.fromTransformationTypeCodes", 0),
						DBQuery.notExists("atomicTransfertMethods.inputContainerUseds.fromTransformationTypeCodes"),
						DBQuery.regex("atomicTransfertMethods.inputContainerUseds.fromTransformationTypeCodes", Pattern.compile("^ext-to.*$"))));
			}else if(!experimentSearch.containerFromTransformationTypeCode.contains("none")){
				queryElts.add(DBQuery.in("atomicTransfertMethods.inputContainerUseds.fromTransformationTypeCodes", experimentSearch.containerFromTransformationTypeCode));				
			}else{
				queryElts.add(DBQuery.or(DBQuery.size("fromTransformationTypeCodes", 0),DBQuery.notExists("fromTransformationTypeCodes"),
						DBQuery.regex("atomicTransfertMethods.inputContainerUseds.fromTransformationTypeCodes", Pattern.compile("^ext-to.*$")),
						DBQuery.in("atomicTransfertMethods.inputContainerUseds.fromTransformationTypeCodes", experimentSearch.containerFromTransformationTypeCode)));
			}
		}
		
		if (CollectionUtils.isNotEmpty(experimentSearch.stateResolutionCodes)) { //all
			queryElts.add(DBQuery.in("state.resolutionCodes", experimentSearch.stateResolutionCodes));
		}
		
		//queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(experimentSearch.atomicTransfertMethodsInputContainerUsedsContentsProperties, Level.CODE.Content, "atomicTransfertMethods.inputContainerUseds.contents.properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(experimentSearch.experimentProperties, Level.CODE.Experiment, "experimentProperties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(experimentSearch.instrumentProperties, Level.CODE.Instrument, "instrumentProperties"));

		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}

		return query;

	}
	
	@Permission(value={"writing"})
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 10000 * 1024)
	public Result save() throws DAOException{
		Form<Experiment> filledForm = getMainFilledForm();
		Experiment input = filledForm.get();
		
		if (null == input._id) {
			input.code = CodeHelper.getInstance().generateExperimentCode(input);
			input.traceInformation = new TraceInformation();
			input.traceInformation.setTraceInformation(getCurrentUser());
			
			if(null == input.state){
				input.state = new State();
			}
			input.state.code = "N";
			input.state.user = getCurrentUser();
			input.state.date = new Date();	
						
		} else {
			return badRequest("use PUT method to update the experiment");
		}
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setCreationMode();
		workflows.applyPreStateRules(ctxVal, input, input.state);		
		ExperimentHelper.doCalculations(input, calculationsRules);
		
		input.validate(ctxVal);	
		if (!ctxVal.hasErrors()) {
			input = saveObject(input);
			workflows.applySuccessPostStateRules(ctxVal, input);
			return ok(Json.toJson(input));
		} else {
			workflows.applyErrorPostStateRules(ctxVal, input, input.state);
			return badRequest(filledForm.errorsAsJson());
		}				
	}
	
	@Permission(value={"writing"})
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 10000 * 1024)
	public Result update(String code) throws DAOException{
		Experiment objectInDB =  getObject(code);
		if(objectInDB == null) {
			return badRequest("Experiment with code "+code+" does not exist");
		}
		//TODO Peux t'on mettre à jour une expérience terminée
		//	=> Oui mais on ne peut plus modifier sa structure juste les valeurs reagents et comments
		//	=> comment vérifier le point précédent ????
		Form<Experiment> filledForm = getMainFilledForm();
		Experiment input = filledForm.get();
		
		if (input.code.equals(code)) {
			if(null != input.traceInformation){
				input.traceInformation = getUpdateTraceInformation(input.traceInformation);
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			if(!objectInDB.state.code.equals(input.state.code)){
				return badRequest("you cannot change the state code. Please used the state url ! ");
			}
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
			ctxVal.setUpdateMode();
			ExperimentHelper.doCalculations(input, calculationsRules);
			workflows.applyPreValidateCurrentStateRules(ctxVal, input);
			input.validate(ctxVal);			
			if (!ctxVal.hasErrors()) {	
				workflows.applyPostValidateCurrentStateRules(ctxVal, input);
				updateObject(input);	
				return ok(Json.toJson(input));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("Experiment code are not the same");
		}
				
	}
	
	@Permission(value={"writing"})
	public Result updateState(String code){
		Experiment objectInDB = getObject(code);
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
		}else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	
	@Permission(value={"writing"})
	public Result delete(String code){
		Experiment objectInDB =  getObject(code);
		if(objectInDB == null) {
			return notFound();
		}
		DynamicForm deleteForm = form();
		ContextValidation contextValidation=new ContextValidation(getCurrentUser(),deleteForm.errors());
		workflows.delete(contextValidation, objectInDB);
		if (!contextValidation.hasErrors()) {
			return ok();
		}else {
			return badRequest(deleteForm.errorsAsJson());
		}
		
		
	}
}
