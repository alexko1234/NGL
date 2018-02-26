package controllers.experiments.api;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;

import static validation.experiment.instance.ExperimentValidationHelper.*;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
// import models.laboratory.common.instance.Comment;
// import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
// import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
// import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
// import models.laboratory.instrument.instance.InstrumentUsed;
// import models.laboratory.reagent.instance.ReagentUsed;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
// import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

// import play.Logger;
// import play.api.modules.spring.Spring;
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
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.NGLContext;

import javax.inject.Inject;

// TODO: cleanup

public class Experiments extends DocumentController<Experiment> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Experiments.class);
	
	final static List<String> defaultKeys =  Arrays.asList("categoryCode","code","inputContainerSupportCodes","instrument","outputContainerSupportCodes","projectCodes","protocolCode","reagents","sampleCodes","state","status","traceInformation","typeCode","atomicTransfertMethods.inputContainerUseds.contents");
	final static List<String> authorizedUpdateFields = Arrays.asList("status", "reagents");
	public static final String calculationsRules = "calculations";
	
	private final Form<State>           stateForm; // = form(State.class);
	private final Form<QueryFieldsForm> updateForm; // = form(QueryFieldsForm.class);
	// private final Form<Experiment> experimentForm; // = form(Experiment.class);
	// private final Form<ExperimentSearchForm> experimentSearchForm; // = form(ExperimentSearchForm.class);
	// final ExpWorkflows workflows = Spring.get BeanOfType(ExpWorkflows.class);
	private final ExpWorkflows          workflows;
	
	@Inject
	public Experiments(NGLContext ctx, ExpWorkflows workflows) {
		super(ctx,InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, defaultKeys);
		stateForm            = ctx.form(State.class);
		updateForm           = ctx.form(QueryFieldsForm.class);
		// experimentForm       = ctx.form(Experiment.class);
		// experimentSearchForm = ctx.form(ExperimentSearchForm.class);
		this.workflows       = workflows;
	}
	
	@Permission(value={"reading"})
	public Result list() {
		ExperimentSearchForm searchForm = filledFormQueryString(ExperimentSearchForm.class);
		if (searchForm.reporting) {
			return nativeMongoDBQuery(searchForm);
		} else {
			DBQuery.Query query = getQuery(searchForm);
			return mongoJackQuery(searchForm, query);			
		}
	}
	
	/*
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
		
		if(CollectionUtils.isNotEmpty(experimentSearch.typeCodes)){
			queryElts.add(DBQuery.in("typeCode", experimentSearch.typeCodes));
		}else if(StringUtils.isNotBlank(experimentSearch.typeCode)){
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
	// @BodyParser.Of(value = BodyParser.Json.class, maxLength = 10000 * 1024)
	@BodyParser.Of(value = IGBodyParsers.Json10MB.class)
	public Result save() throws DAOException{
		Form<Experiment> filledForm = getMainFilledForm();
		Experiment input = filledForm.get();
		
		if (input._id == null) {
			input.code = CodeHelper.getInstance().generateExperimentCode(input);
			input.traceInformation = new TraceInformation();
			input.traceInformation.setTraceInformation(getCurrentUser());
			
			if (input.state == null) 
				input.state = new State();
			
			input.state.code = "N";
			input.state.user = getCurrentUser();
			input.state.date = new Date();	
						
		} else {
			return badRequest("use PUT method to update the experiment");
		}
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		ctxVal.setCreationMode();
//		long t1 = System.currentTimeMillis();
		workflows.applyPreStateRules(ctxVal, input, input.state);
//		long t2 = System.currentTimeMillis();
		ExperimentHelper.doCalculations(input, calculationsRules);
//		long t3 = System.currentTimeMillis();
		input.validate(ctxVal);	
		if (!ctxVal.hasErrors()) {
//			long t4 = System.currentTimeMillis();
			input = saveObject(input);
//			long t5 = System.currentTimeMillis();
			workflows.applySuccessPostStateRules(ctxVal, input);
//			long t6 = System.currentTimeMillis();			
			//Logger.debug((t2-t1)+" - "+(t3-t2)+" - "+(t4-t3)+" - "+(t5-t4)+" - "+(t6-t4));
			return ok(Json.toJson(input));
		} else {
			workflows.applyErrorPostStateRules(ctxVal, input, input.state);
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}				
	}
	
	// TODO: remove dead test code
	/*
	private void eq(String n, String s0, String s1) {
		if (!s0.equals(s1))
			throw new RuntimeException(n + " : " + s0 + " =/= " + s1);
	}
	
	// Should provide some current and fields to swap
	private String swap(Experiment good, Experiment bad, String... fieldNames) {
		for (String fieldName : fieldNames) {
			try {
			java.lang.reflect.Field field = good.getClass().getField(fieldName);
			Object badVal = field.get(bad);
			field.set(bad, field.get(good));
			try {
				updateObject(bad);
				// swap field swap has been enough, we're golden
				return fieldName;
			} catch (Exception e) {
				// failed, reset and try next
				field.set(bad, badVal);
			}
			} catch (Exception e) {
				throw new RuntimeException("bad fail",e);
			}
		}
		throw new RuntimeException("no single swap worked");
	}
	
	// We trace the a's to avoid cycling.
	static class Comp implements java.util.Comparator<Object> {
		public int compare(Object a, Object b) {
			if (a instanceof fr.cea.ig.DBObject) {
				if (b instanceof fr.cea.ig.DBObject) {
					return ((fr.cea.ig.DBObject)a)._id.compareTo(((fr.cea.ig.DBObject)b)._id);
				}
			}
			return 0;
		}
	}
	private void diff(Set<Object> done, String path, Object a, Object b) {
		if (a == null && b == null) {
		} else if (a == null && b != null) {
			logger.debug(path + " null vs non null");
		} else if (a != null && b == null) {
			logger.debug(path + " not null and null");
		} else if (done.contains(a)) {
			// avoid recursion of checked objects
		} else if (a.getClass() != b.getClass()) {
			logger.debug(path + " class diff " + a.getClass() + " " + b.getClass());
		} else {
			// logger.debug("diff " + path + a.getClass() + " " + b.getClass() + " " + a + " / " + b);
			if (a instanceof String) {
				if (!a.equals(b)) 
					logger.debug((path + " string diff " + a + " " + b));
			} else if (a instanceof List) {
				List<Object> l0 = (List<Object>)a;
				List<Object> l1 = (List<Object>)b;
				Collections.sort(l0,new Comp());
				Collections.sort(l1,new Comp());
				// Reorder using some criterion so the lists are the same
				done.add(a);
				for (int i=0; i<l0.size(); i++) 
					diff(done,path+"["+i+"]",l0.get(i),l1.get(i));
			} else if (a instanceof Set) {
				Set<Object> s0 = (Set<Object>)a;
				Set<Object> s1 = (Set<Object>)b;
				if (!s0.containsAll(s1) || !s1.containsAll(s0))
					logger.debug(path + " set diff " + s0 + " " + s1);
			} else if (a instanceof Map) {
				// throw new RuntimeException("map");
			} else {
				for (Field field : a.getClass().getFields()) {
					done.add(a);
					try {
						diff(done,path+"/"+field.getName(),field.get(a),field.get(b));
					} catch (IllegalAccessException e) {
						logger.error("field error",e);
					}
				}
			}
		}
	}
	
	private void compare(Experiment e0, Experiment e1) {
		logger.debug("comparing " + e0 + " and " + e1);
		
		// public String typeCode;
		eq("typeCode",e0.typeCode,e1.typeCode);
		// public String categoryCode;
		eq("categoryCode",e0.categoryCode,e1.categoryCode);
		
		// Expreiment class field names
		public TraceInformation traceInformation = new TraceInformation();
		public Map<String,PropertyValue> experimentProperties;
		
		public Map<String, PropertyValue> instrumentProperties;
		
		public InstrumentUsed instrument;
		public String protocolCode;

		public State state = new State();
		public Valuation status = new Valuation();
		
		public List<AtomicTransfertMethod> atomicTransfertMethods; 
		
		public List<ReagentUsed> reagents;
		
		public List<Comment> comments;
		
		public Set<String> projectCodes;
		public Set<String> sampleCodes;
		
		public Set<String> inputContainerSupportCodes;
		public Set<String> inputContainerCodes;
		public Set<String> inputProcessCodes;
		public Set<String> inputProcessTypeCodes;
		public Set<String> inputFromTransformationTypeCodes;
		
		public Set<String> outputContainerCodes;
		public Set<String> outputContainerSupportCodes;
		
	}
	*/
	
//	private void findBigInts(Set<Object> done, String path, Object a) {
//		if (a == null) {
//		} else if (done.contains(a)) {
//			// avoid recursion of checked objects
//		} else if (a instanceof java.math.BigInteger) {
//			logger.debug("found bi : " + path + " bigint " + a);
//		} else {
//			// logger.debug("diff " + path + a.getClass() + " " + b.getClass() + " " + a + " / " + b);
//			if (a instanceof String) {
//			} else if (a instanceof List) {
//				List<Object> l0 = (List<Object>)a;
//				done.add(a);
//				for (int i=0; i<l0.size(); i++) 
//					findBigInts(done,path+"["+i+"]",l0.get(i));
//			} else if (a instanceof Set) {
//				Set<Object> s0 = (Set<Object>)a;
//				for (Object o : s0)
//					findBigInts(done,path+"[-]",o);
//			} else if (a instanceof Map) {
//				// throw new RuntimeException("map");
//			} else {
//				for (Field field : a.getClass().getFields()) {
//					done.add(a);
//					try {
//						findBigInts(done,path+"/"+field.getName(),field.get(a));
//					} catch (IllegalAccessException e) {
//						logger.error("field error",e);
//					}
//				}
//			}
//		}
//		// throw new RuntimeException("crash");
//	}
	
	@Permission(value={"writing"})
	// @BodyParser.Of(value = BodyParser.Json.class, maxLength = 10000 * 1024)
	@BodyParser.Of(value = IGBodyParsers.Json10MB.class)
	public Result update(String code) throws DAOException {
		logger.debug("update '" + code + "'");
		Experiment objectInDB =  getObject(code);
		if (objectInDB == null) {
			return badRequest("Experiment with code " + code + " does not exist");
		}
		
		Form<Experiment> filledForm = getMainFilledForm();
		Experiment input = filledForm.get();
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		
		if(queryFieldsForm.fields == null || queryFieldsForm.fields.contains("all")){
			
			if (input.code.equals(code)) {
				if(null != input.traceInformation){
					input.traceInformation = getUpdateTraceInformation(input.traceInformation);
				}else{
					logger.error("traceInformation is null !!");
				}
				
				if(!objectInDB.state.code.equals(input.state.code)){
					return badRequest("you cannot change the state code. Please used the state url ! ");
				}
//				long t1 = System.currentTimeMillis();
//				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
				ctxVal.setUpdateMode();
				//todo update in cascading contentProperties, only for administrator
				if (queryFieldsForm.fields != null && queryFieldsForm.fields.contains("updateContentProperties")) {
					ctxVal.putObject("updateContentProperties", Boolean.TRUE);
				}
				ExperimentHelper.doCalculations(input, calculationsRules);
//				long t2 = System.currentTimeMillis();
				workflows.applyPreValidateCurrentStateRules(ctxVal, input);
//				long t3 = System.currentTimeMillis();
				input.validate(ctxVal);			
				if (!ctxVal.hasErrors()) {	
					workflows.applyPostValidateCurrentStateRules(ctxVal, input);
//					long t4 = System.currentTimeMillis();
					updateObject(input);	
//					long t5 = System.currentTimeMillis();
					//Logger.debug((t2-t1)+" - "+(t3-t2)+" - "+(t4-t3)+" - "+(t5-t4));					
					return ok(Json.toJson(input));
				} else {
					// return badRequest(filledForm.errors-AsJson());
					return badRequest(errorsAsJson(ctxVal.getErrors()));
				}
			} else {
				return badRequest("Experiment code are not the same");
			}
		} else {
//			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ContextValidation contextValidation = new ContextValidation(getCurrentUser(), filledForm); 	
			contextValidation.setUpdateMode();
			validateAuthorizedUpdateFields(contextValidation, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(contextValidation, queryFieldsForm.fields, filledForm);
			// if(!filledForm.hasErrors()){
			if (!contextValidation.hasErrors()) {
				TraceInformation ti = objectInDB.traceInformation;
				ti.setTraceInformation(getCurrentUser());
				contextValidation.putObject(FIELD_STATE_CODE , objectInDB.state.code);
				
				if (queryFieldsForm.fields.contains("status")) {
					validateStatus(objectInDB.typeCode, input.status, contextValidation);				
				}
				
				if (queryFieldsForm.fields.contains("reagents")) {
					validateReagents(objectInDB.reagents, contextValidation);				
				}
				
				if (!contextValidation.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
							DBQuery.and(DBQuery.is("code", code)), getBuilder(input, queryFieldsForm.fields).set("traceInformation", ti));
					return ok(Json.toJson(getObject(code)));
				} else {
					// return badRequest(filledForm.errors-AsJson());
					return badRequest(errorsAsJson(contextValidation.getErrors()));
				}				
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(contextValidation.getErrors()));
			}
		}							
	}
	
	@Permission(value={"writing"})
	public Result updateState(String code){
		Experiment objectInDB = getObject(code);
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
	public Result delete(String code){
		Experiment objectInDB =  getObject(code);
		if (objectInDB == null)
			return notFound();
		DynamicForm deleteForm = ctx.form();
//		ContextValidation contextValidation = new ContextValidation(getCurrentUser(),deleteForm.errors());
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(), deleteForm);
		workflows.delete(contextValidation, objectInDB);
		if (!contextValidation.hasErrors()) {
			return ok();
		} else {
			// return badRequest(deleteForm.errors-AsJson());
			return badRequest(errorsAsJson(contextValidation.getErrors()));
		}
	}
	
}
