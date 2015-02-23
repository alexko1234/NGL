package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
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
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Experiments extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);
	final static Form<Comment> commentForm = form(Comment.class);
	final static Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);

	public static final String calculationsRules ="calculations";


	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateExperimentInformations(String code){
		
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		ContextValidation ctx =new ContextValidation(getCurrentUser(),experimentFilledForm.errors());
		ctx.putObject("stateCode", exp.state.code);
		
		if (!ctx.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("typeCode",exp.typeCode);
			builder=builder.set("reagents",exp.reagents);
			builder=builder.set("protocolCode",exp.protocolCode);
			builder=builder.set("state.resolutionCodes",exp.state.resolutionCodes);
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

	/*@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result generateOutput(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());
		ContextValidation contextValidation=new ContextValidation(getCurrentUser(), experimentFilledForm.errors());
		try {
			ExperimentHelper.generateOutputContainerUsed(exp, contextValidation);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ExperimentHelper.saveOutputContainerUsed(exp, contextValidation);

		if(!contextValidation.hasErrors()){
			return ok(Json.toJson(exp));
		}
		else{

			return badRequest(experimentFilledForm.errorsAsJson());
		}
	}
	 */

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateInstrumentInformations(String code) throws DAOException{
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());

		exp.instrument.validate(ctxValidation);

		if (!experimentFilledForm.hasErrors()) {

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

		if (!experimentFilledForm.hasErrors()) {
			if(exp.experimentProperties != null){
				ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());
				ctxValidation.putObject("stateCode", exp.state.code);
				ctxValidation.setUpdateMode();

				Logger.debug("Experiment update properties :"+exp.code);
				ExperimentValidationHelper.validationExperimentType(exp.typeCode, exp.experimentProperties, ctxValidation);
				
				Builder builder = new DBUpdate.Builder();
				builder = builder.set("experimentProperties",exp.experimentProperties);
				builder = builder.set("traceInformation", ExperimentHelper.getUpdateTraceInformation(exp.traceInformation, getCurrentUser()));

				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			}
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
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
		if (!experimentFilledForm.hasErrors()) {

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
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		exp= ExperimentHelper.updateData(exp);
		ContextValidation contextValidation = new ContextValidation(getCurrentUser());
		contextValidation.setUpdateMode();
		contextValidation.putObject("stateCode", exp.state.code);
		contextValidation.putObject("typeCode", exp.typeCode);
		
		ExperimentHelper.cleanContainers(exp, contextValidation);
				
		ExperimentValidationHelper.validateAtomicTransfertMethodes(exp.atomicTransfertMethods, contextValidation);
		ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());
		ExperimentValidationHelper.validateRules(exp, ctxValidation);

		if(!ctxValidation.hasErrors()){
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
	public static Result updateStateCode(String code, String stateCode){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());
		//il faut sauvegarder l'experiment avant de changer d'etat
		if(exp._id == null){
			// A revoir avec la validation
			exp = MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		}

		//TODO if first experiment in the processus then processus state to IP
		State newState = new State();
		newState.code=stateCode;
		newState.date=new Date();
		newState.user=getCurrentUser();

		Workflows.setExperimentState(exp,newState,ctxValidation);


		if (!ctxValidation.hasErrors()) {	 	
			exp = MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
			return ok();
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}


	public static Result nextState(String code){
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);

		Logger.info(Json.toJson(exp).toString());

		ExperimentHelper.doCalculations(exp,calculationsRules);

		Form<Experiment> experimentFilledForm = experimentForm.fill(exp);

		ContextValidation ctxValidation = new ContextValidation(getCurrentUser(), experimentFilledForm.errors());

		Workflows.nextExperimentState(exp, ctxValidation);
		if (!ctxValidation.hasErrors()) {
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	/**
	 * First save for an Experiment object	
	 * @param experimentType
	 * @return the created experiment
	 * @throws DAOException 
	 */
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
					Experiment newExp=MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME,exp);

					MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,Process.class,
							DBQuery.in("code", ExperimentHelper.getAllProcessCodesFromExperiment(exp))
							,DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code),true);

					State state = StateHelper.cloneState(exp.state);
					state.user=getCurrentUser();
					exp.state.code = null;

					Workflows.setExperimentState(exp, state, ctxValidation);
				}
			}
			if (!ctxValidation.hasErrors()) {
				return ok(Json.toJson(exp));
			}else {
				return badRequest(experimentFilledForm.errorsAsJson());
			}
		}

		return badRequest();
	}

	public static Result list(){
		Form<ExperimentSearchForm> experimentFilledForm = filledFormQueryString(experimentSearchForm,ExperimentSearchForm.class);
		ExperimentSearchForm experimentsSearch = experimentFilledForm.get();
		BasicDBObject keys = getKeys(experimentsSearch);
		DBQuery.Query query = getQuery(experimentsSearch);

		if(experimentsSearch.datatable){
			MongoDBResult<Experiment> results =  mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query);
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
			MongoDBResult<Experiment> results = mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query);
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
			for(Entry<Integer, AtomicTransfertMethod> atomicTransfertMethods:experiment.atomicTransfertMethods.entrySet()){
				for(ContainerUsed containerUsed:atomicTransfertMethods.getValue().getOutputContainers()){
					containerSupportCodeOld=containerUsed.locationOnContainerSupport.code;
					//Remplace ancien code par le nouveau dans le nom du container
					containerUsed.code=containerUsed.code.replace(containerSupportCodeOld, containerSupportCode);
					containerUsed.locationOnContainerSupport.code=containerSupportCode;

					MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME,ContainerSupport.class,DBQuery.is("code",containerSupportCodeOld),DBUpdate.set("code",containerSupportCode));
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

		if(StringUtils.isNotBlank(experimentSearch.code)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(experimentSearch.code)));
		}

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
			queryElts.add(DBQuery.or(DBQuery.regex("reagents.boxCode", Pattern.compile(experimentSearch.reagentOrBoxCode)),DBQuery.regex("reagents.code", Pattern.compile(experimentSearch.reagentOrBoxCode))));
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
