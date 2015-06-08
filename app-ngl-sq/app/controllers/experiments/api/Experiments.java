package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ExperimentUpdateState;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.processes.instance.Process;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.StateHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;
import org.mongojack.DBUpdate.Builder;

import play.Logger;
import play.Play;
import play.Logger.ALogger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import validation.experiment.instance.ExperimentValidationHelper;
import validation.processes.instance.ProcessValidationHelper;
import views.components.datatable.DatatableForm;
import views.components.datatable.DatatableResponse;
import workflows.container.ContainerWorkflows;
import workflows.experiment.ExperimentWorkflows;
import workflows.process.ProcessWorkflows;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import controllers.processes.api.ProcessesSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Experiments extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);
	final static Form<Comment> commentForm = form(Comment.class);
	final static Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	final static Form<ExperimentUpdateForm> experimentUpdateForm = form(ExperimentUpdateForm.class);
	final static List<String> defaultKeys =  Arrays.asList("categoryCode","code","inputContaierSupportCodes","instrument","outputContainerSupportCodes","projectCodes","protocolCode","reagents","sampleCodes","state","traceInformation","typeCode");

	public static final String calculationsRules ="calculations";

	static ALogger logger=Logger.of("Experiments");

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateExperimentInformations(String code){

		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		ContextValidation ctx =new ContextValidation(getCurrentUser(),experimentFilledForm.errors());
		ctx.putObject("stateCode", exp.state.code);

		ExperimentValidationHelper.validateReagents(exp.reagents, ctx);
		ExperimentValidationHelper.validateResolutionCodes(exp.typeCode, exp.state.resolutionCodes, ctx);		
		ExperimentValidationHelper.validationProtocol(exp.typeCode,exp.protocolCode,ctx);

		if (!ctx.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("typeCode",exp.typeCode);
			if(CollectionUtils.isNotEmpty(exp.reagents)){
				builder=builder.set("reagents",exp.reagents);
			}else { builder=builder.unset("reagents");}
			builder=builder.set("protocolCode",exp.protocolCode);
			if(CollectionUtils.isNotEmpty(exp.state.resolutionCodes)){
				builder=builder.set("state.resolutionCodes",exp.state.resolutionCodes);
			}else{ builder=builder.unset("state.resolutionCodes");}
			builder=builder.set("traceInformation", ExperimentHelper.getUpdateTraceInformation(exp.traceInformation, getCurrentUser()));

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result get(String code){
		Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		if(experiment == null){
			return notFound();
		}
		return ok(Json.toJson(experiment));
	}


	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateInstrumentInformations(String code) throws DAOException{
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());

		exp.instrument.validate(ctxValidation);

		if (!ctxValidation.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder = builder.set("instrument",exp.instrument);
			builder = builder.set("traceInformation", ExperimentHelper.getUpdateTraceInformation(exp.traceInformation, getCurrentUser()));
			exp=ExperimentHelper.updateInstrumentCategory(exp);
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateExperimentProperties(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		if(exp.experimentProperties != null){

			ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());
			ctxValidation.putObject("stateCode", exp.state.code);
			ctxValidation.setUpdateMode();

			Logger.debug("Experiment update properties :"+exp.code);
			ExperimentValidationHelper.validationExperimentType(exp.typeCode, exp.experimentProperties, ctxValidation);

			if (!ctxValidation.hasErrors()) {

				Builder builder = new DBUpdate.Builder();
				builder = builder.set("experimentProperties",exp.experimentProperties);
				builder = builder.set("traceInformation", ExperimentHelper.getUpdateTraceInformation(exp.traceInformation, getCurrentUser()));

				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
				return ok(Json.toJson(exp));
			}else {	
				return badRequest(experimentFilledForm.errorsAsJson());
			}
		}

		return ok(Json.toJson(exp));
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateInstrumentProperties(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());
		ctxValidation.putObject("stateCode", exp.state.code);
		ctxValidation.setUpdateMode();

		Logger.debug("Experiment update properties :"+exp.code);

		ExperimentValidationHelper.validateInstrumentUsed(exp.instrument,exp.instrumentProperties,ctxValidation);

		try {
			exp= ExperimentHelper.updateInstrumentCategory(exp);
		} catch (DAOException e) {
			Logger.error(e.getMessage());
		}
		if (!ctxValidation.hasErrors()) {

			if(exp.instrumentProperties != null){
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
						DBQuery.is("code", exp.code),
						DBUpdate.set("instrumentProperties",exp.instrumentProperties).set("instrument",exp.instrument).set("traceInformation", ExperimentHelper.getUpdateTraceInformation(exp.traceInformation, getCurrentUser())));
			}
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result getInstrumentProperties(String instrumentUsedTypeCode){
		InstrumentUsedTypeDAO instrumentUsedTypesDAO = Spring.getBeanOfType(InstrumentUsedTypeDAO.class);
		InstrumentUsedType instrumentUsedType = null;

		try {
			instrumentUsedType = instrumentUsedTypesDAO.findByCode(instrumentUsedTypeCode);
		} catch (DAOException e) {
			Logger.error("DAO error",e);
		}

		return ok(Json.toJson(instrumentUsedType.propertiesDefinitions));
	}

	public static Result addComment(String code){
		Form<Comment> commentFilledForm = getFilledForm(commentForm,Comment.class);
		Comment com = commentFilledForm.get();

		com.createUser = getCurrentUser();
		com.creationDate = new Date();
		com.code = CodeHelper.getInstance().generateExperimentCommentCode(com);

		if (!commentFilledForm.hasErrors()) {
			Builder builder = new DBUpdate.Builder();
			builder=builder.push("comments",com);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);

			return ok(Json.toJson(com));
		}

		return badRequest(commentFilledForm.errorsAsJson());
	}

	public static Result deleteComment(String code, String commentCode){
		Experiment exp = MongoDBDAO.findOne(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code));
		Comment com = null;
		for(Comment c : exp.comments){
			if(c.code.equals(commentCode)){
				com = c;
			}
		}
		if(getCurrentUser().equals(com.createUser)){
			Builder builder = new DBUpdate.Builder();
			builder=builder.pull("comments",com);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code).and(DBQuery.is("comments.code", commentCode)),builder);

			return ok();
		}
		return forbidden();
	}

	public static Result updateComment(String code){
		Form<Comment> commentFilledForm = getFilledForm(commentForm,Comment.class);
		Comment comment = commentFilledForm.get();
		if(getCurrentUser().equals(comment.createUser)){
			if (!commentFilledForm.hasErrors()) {
				Builder builder = new DBUpdate.Builder();
				builder=builder.set("comments.$",comment);

				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code).and(DBQuery.is("comments.code", comment.code)),builder);

				return ok(Json.toJson(comment));
			}

			return badRequest(commentFilledForm.errorsAsJson());
		}
		return forbidden();
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateContainers(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = ExperimentHelper.updateData(exp);
		ContextValidation contextValidation = new ContextValidation(getCurrentUser(),  experimentFilledForm.errors());
		contextValidation.setUpdateMode();
		contextValidation.putObject("stateCode", exp.state.code);
		contextValidation.putObject("typeCode", exp.typeCode);

		ExperimentValidationHelper.validateAtomicTransfertMethodes(exp.atomicTransfertMethods, contextValidation);
		ExperimentValidationHelper.validateRules(exp, contextValidation);

		if(!contextValidation.hasErrors()){
			ExperimentHelper.cleanContainers(exp, contextValidation);

			ExperimentHelper.doCalculations(exp,calculationsRules);

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("atomicTransfertMethods",exp.atomicTransfertMethods);
			builder=builder.set("projectCodes",exp.projectCodes).set("sampleCodes",exp.sampleCodes).set("inputContainerSupportCodes", exp.inputContainerSupportCodes);
			builder = builder.set("traceInformation", ExperimentHelper.getUpdateTraceInformation(exp.traceInformation, getCurrentUser()));

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);

			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateStateCode(String code){
		Form<ExperimentUpdateForm> experimentUpdateFilledForm = getFilledForm(experimentUpdateForm,ExperimentUpdateForm.class);
		ExperimentUpdateForm expUpdateForm = experimentUpdateFilledForm.get();
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);

		Logger.info(Json.toJson(exp).toString());

		ExperimentHelper.doCalculations(exp,calculationsRules);

		Form<Experiment> experimentFilledForm = experimentForm.fill(exp);

		ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());

		State nextState = new State();
		nextState.code = expUpdateForm.nextStateCode;
		nextState.user=getCurrentUser();
		nextState.resolutionCodes = exp.state.resolutionCodes;

		//Validation Experiment State
		ctxValidation.getContextObjects().put("stateCode", nextState.code);
		ExperimentValidationHelper.validateState(exp.typeCode, nextState, ctxValidation);
		ExperimentValidationHelper.validateNewState(exp, ctxValidation);

		ExperimentUpdateState experimentUpdateState=new ExperimentUpdateState();

		if(nextState.code.equals("IP")){
			experimentUpdateState.nextStateProcesses="IP";
			experimentUpdateState.nextStateInputContainers="IU";
		}else if (nextState.code.equals("F")){
			experimentUpdateState.nextStateProcesses="IP";
			experimentUpdateState.nextStateInputContainers="IS";
			experimentUpdateState.nextStateOutputContainers="A";
		}

		if (!ctxValidation.hasErrors()) {
			ExperimentWorkflows.setExperimentState(exp,nextState,ctxValidation);
		}

		ExperimentWorkflows.setExperimentUpdateState(exp,experimentUpdateState,ctxValidation);

		if (!ctxValidation.hasErrors()) {
			return ok(Json.toJson(exp));
		}

		ctxValidation.displayErrors(logger);
		return badRequest(experimentFilledForm.errorsAsJson());
	}


	public static Result retry(String code){
		Form form = new Form(Experiment.class);
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), form.errors());
		//verifie que l'experience est terminee
		if(!exp.state.code.equals("F")){
			contextValidation.addErrors("experiment.state","",code);
			return badRequest(form.errorsAsJson());
		}

		ExperimentUpdateState experimentUpdateState=new ExperimentUpdateState();
		experimentUpdateState.nextStateInputContainers=ContainerWorkflows.getNextContainerStateFromExperimentCategory(exp.categoryCode);
		experimentUpdateState.nextStateOutputContainers="UA";

		ExperimentWorkflows.setExperimentUpdateState(exp,experimentUpdateState,contextValidation);

		if (!contextValidation.hasErrors()) {
			return ok();
		}
		return badRequest(form.errorsAsJson());
	}


	public static Result stopProcess(String code){
		Form<ExperimentUpdateForm> experimentUpdateFilledForm = getFilledForm(experimentUpdateForm,ExperimentUpdateForm.class);
		ExperimentUpdateForm expUpdateForm = experimentUpdateFilledForm.get();
		
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), experimentUpdateFilledForm.errors());
		//verifie que l'experience est terminee
		if(!exp.state.code.equals("F")){
			contextValidation.addErrors("experiment.state","",code);
			return badRequest(experimentUpdateFilledForm.errorsAsJson());
		}

		ExperimentUpdateState experimentUpdateState=new ExperimentUpdateState();
		experimentUpdateState.nextStateProcesses="F";
		experimentUpdateState.processResolutionCodes=expUpdateForm.processResolutionCodes;
		experimentUpdateState.nextStateInputContainers="IS";
		experimentUpdateState.nextStateOutputContainers="UA";

		ExperimentWorkflows.setExperimentUpdateState(exp,experimentUpdateState,contextValidation);
		if (!contextValidation.hasErrors()) {
			return ok();
		}
		return badRequest(experimentUpdateFilledForm.errorsAsJson());
	}

	public static Result endOfProcess(String code){
		Form form = new Form(Experiment.class);
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);
		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), form.errors());
		//verifie que l'experience est terminee
		if(!exp.state.code.equals("F")){
			contextValidation.addErrors("experiment.state","",code);
			return badRequest(form.errorsAsJson());
		}

		ExperimentUpdateState experimentUpdateState=new ExperimentUpdateState();
		experimentUpdateState.nextStateProcesses="F";
		//Calcul volume preleve alors UA
		try {
			/*if(ExperimentType.find.findByCode(exp.typeCode).atomicTransfertMethod.endsWith("ToVoid")){
				experimentUpdateState.nextStateInputContainers="UA";
			} else*/
			if(ExperimentType.find.findNextExperimentTypeForAnExperimentTypeCode(exp.typeCode).size()==0 && exp.categoryCode.equals("transformation")){
				logger.debug("Not next Experiment Type");
				experimentUpdateState.nextStateInputContainers="IS";
				experimentUpdateState.nextStateOutputContainers="UA";
			} else {					
				experimentUpdateState.nextStateInputContainers="IS";
				experimentUpdateState.nextStateOutputContainers="IW-P";
			}

		} catch (DAOException e) {
			Logger.error(e.getMessage());
		}

		ExperimentWorkflows.setExperimentUpdateState(exp,experimentUpdateState,contextValidation);

		if (!contextValidation.hasErrors()) {
			return ok();
		}
		return badRequest(form.errorsAsJson());
	}


	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result save() throws DAOException{
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		if(exp._id == null || exp._id.equals("")){
			ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());

			exp.code = CodeHelper.getInstance().generateExperiementCode(exp);
			exp.traceInformation = ExperimentHelper.getUpdateTraceInformation(null, getCurrentUser());
			exp.state.user=getCurrentUser();
			ExperimentValidationHelper.validateRules(exp, ctxValidation);

			if (!ctxValidation.hasErrors()) {
				ctxValidation.setCreationMode();
				exp.validate(ctxValidation);
				ExperimentHelper.doCalculations(exp,calculationsRules);

				ExperimentHelper.updateData(exp);

				if(!ctxValidation.hasErrors()){
					MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME,exp);

					MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,Process.class,
							DBQuery.in("code", ExperimentHelper.getAllProcessCodesFromExperiment(exp))
							,DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code),true);

					List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code",exp.inputContainerSupportCodes)).toList();
					ContainerWorkflows.setContainerState(inputContainers,"IW-E",ctxValidation);
				}
			}
			if (!ctxValidation.hasErrors()) {
				return ok(Json.toJson(exp));
			}else {
				ctxValidation.displayErrors(logger);
				return badRequest(experimentFilledForm.errorsAsJson());
			}
		}

		return badRequest();
	}


	public static Result list(){
		Form<ExperimentSearchForm> experimentFilledForm = filledFormQueryString(experimentSearchForm,ExperimentSearchForm.class);
		ExperimentSearchForm experimentsSearch = experimentFilledForm.get();
		BasicDBObject keys = getKeys(updateForm(experimentsSearch));
		DBQuery.Query query = getQuery(experimentsSearch);

		if(experimentsSearch.datatable){
			MongoDBResult<Experiment> results =  mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query, keys);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(new DatatableResponse<Experiment>(experiments, results.count())));
		}else if (experimentsSearch.list){
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			if(null == experimentsSearch.orderBy)experimentsSearch.orderBy = "code";
			if(null == experimentsSearch.orderSense)experimentsSearch.orderSense = 0;				

			MongoDBResult<Experiment> results = mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query, keys);
			List<Experiment> experiments = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Experiment p: experiments){					
				los.add(new ListObject(p.code, p.code));								
			}
			return Results.ok(Json.toJson(los));
		}else{
			if(null == experimentsSearch.orderBy)experimentsSearch.orderBy = "code";
			if(null == experimentsSearch.orderSense)experimentsSearch.orderSense = 0;
			MongoDBResult<Experiment> results = mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query, keys);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(experiments));
		}
	}



	public static Result updateData(String experimentCode){
		Experiment experiment= MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
		if(experiment!=null){
			ExperimentHelper.updateData(experiment);
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class,DBQuery.is("code",experimentCode)
					,DBUpdate.set("projectCodes",experiment.projectCodes).set("sampleCodes",experiment.sampleCodes).set("inputContainerSupportCodes", experiment.inputContainerSupportCodes));
			return ok(Json.toJson(experiment));
		}else {
			return notFound();
		}

	}


	public static Result updateContainerSupportCode(String experimentCode,String containerSupportCode){

		Experiment experiment= MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);

		if(experiment!=null){
			String containerSupportCodeOld;

			//Experiment
			for(AtomicTransfertMethod atomicTransfertMethods:experiment.atomicTransfertMethods){
				for(ContainerUsed containerUsed:atomicTransfertMethods.getOutputContainers()){
					containerSupportCodeOld=containerUsed.locationOnContainerSupport.code;
					//Remplace ancien code par le nouveau dans le nom du container
					containerUsed.code=containerUsed.code.replace(containerSupportCodeOld, containerSupportCode);
					containerUsed.locationOnContainerSupport.code=containerSupportCode;

					MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,ContainerSupport.class,DBQuery.is("code",containerSupportCodeOld),DBUpdate.set("code",containerSupportCode));
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code",containerSupportCodeOld),DBUpdate.set("support.code", containerSupportCode).set("code",containerUsed.code ));

				}
			}
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", experimentCode), DBUpdate.set("atomicTransfertMethods", experiment.atomicTransfertMethods));

			PropertySingleValue containserSupportCodeProperty=(PropertySingleValue) experiment.instrumentProperties.get("containerSupportCode");
			containserSupportCodeProperty.value=containerSupportCode;
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", experimentCode).notEquals("instrumentProperties.containerSupportCode", containerSupportCode), DBUpdate.set("instrumentProperties.containerSupportCode", containserSupportCodeProperty));			

			return ok(Json.toJson(experiment));
		}
		else  {
			return notFound();
		}
	}

	/**
	 * Construct the experiment query
	 * @param experimentSearch
	 * @return the query
	 */
	private static DBQuery.Query getQuery(ExperimentSearchForm experimentSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query=null;

		Logger.info("Experiment Query : "+experimentSearch);

		
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


		if(CollectionUtils.isNotEmpty(experimentSearch.codes)){
			queryElts.add(DBQuery.in("codes", experimentSearch.codes));
		}

		if(StringUtils.isNotBlank(experimentSearch.containerSupportCode)){			
			List<DBQuery.Query> qs = new ArrayList<DBQuery.Query>();

			qs.add(DBQuery.regex("inputContainerSupportCodes",Pattern.compile(experimentSearch.containerSupportCode)));
			qs.add(DBQuery.regex("outputContainerSupportCodes",Pattern.compile(experimentSearch.containerSupportCode)));
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
		}else if(experimentSearch.stateCode != null){
			queryElts.add(DBQuery.in("state.code", experimentSearch.stateCode));
		}

		if(StringUtils.isNotBlank(experimentSearch.instrument)){
			queryElts.add(DBQuery.in("instrument.code", experimentSearch.instrument));
		}

		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}

		return query;

	}
	
	private static DatatableForm updateForm(ExperimentSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		}
		return form;
	}

	/*private static String findRegExpFromStringList(List<String> searchList) {
		String regex = ".*("; 
		for (String itemList : searchList) {
			regex += itemList + "|"; 
		}
		regex = regex.substring(0,regex.length()-1);
		regex +=  ").*";
		return regex;
	}*/
}
