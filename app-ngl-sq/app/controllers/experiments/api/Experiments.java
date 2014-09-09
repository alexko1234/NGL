package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.State;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.StateHelper;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.DBUpdate.Builder;

import play.Logger;
import play.Play;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import rules.services.RulesException;
import rules.services.RulesServices;
import validation.ContextValidation;
import validation.experiment.instance.ExperimentValidationHelper;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

import com.mongodb.BasicDBObject;

import controllers.CodeHelper;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Experiments extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);
	final static Form<Comment> commentForm = form(Comment.class);
	final static Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	
	private static final String calculationsRules ="calculations";
	

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateExperimentInformations(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("typeCode",exp.typeCode);
			builder=builder.set("protocolCode",exp.protocolCode);
			builder=builder.set("state.resolutionCodes",exp.state.resolutionCodes);

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
		ContextValidation contextValidation=new ContextValidation(experimentFilledForm.errors());
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

		ContextValidation ctxValidation = new ContextValidation(experimentFilledForm.errors());
		
		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());

		exp.instrument.validate(ctxValidation);
		
		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder = builder.set("instrument",exp.instrument);
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
		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());

		if (!experimentFilledForm.hasErrors()) {
			if(exp.experimentProperties != null){
				Builder builder = new DBUpdate.Builder();
				builder = builder.set("experimentProperties",exp.experimentProperties);

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
		
		ContextValidation ctxValidation = new ContextValidation(experimentFilledForm.errors());
		ctxValidation.putObject("stateCode", exp.state.code);
		ctxValidation.setUpdateMode();
		
		Logger.debug("Experiment update properties :"+exp.code);
		
		ExperimentValidationHelper.validateInstrumentUsed(exp.instrument,exp.instrumentProperties,ctxValidation);
		
		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());
		try {
			exp= ExperimentHelper.updateInstrumentCategory(exp);
		} catch (DAOException e) {
			Logger.error(e.getMessage());
		}
		if (!experimentFilledForm.hasErrors()) {
			
			if(exp.instrumentProperties != null){
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
						DBQuery.is("code", exp.code),
						DBUpdate.set("instrumentProperties",exp.instrumentProperties).set("instrument",exp.instrument));
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

		if (!commentFilledForm.hasErrors()) {
			Builder builder = new DBUpdate.Builder();
			builder=builder.push("comments",com);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			
			return ok(Json.toJson(com));
		}

		return badRequest(commentFilledForm.errorsAsJson());
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateContainers(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		
		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());
		exp= ExperimentHelper.setProjetAndSamples(exp);
	
		ContextValidation contextValidation = new ContextValidation();
		contextValidation.setUpdateMode();
		contextValidation.putObject("stateCode", exp.state.code);
		contextValidation.putObject("typeCode", exp.typeCode);
		
		ExperimentValidationHelper.validateAtomicTransfertMethodes(exp.atomicTransfertMethods, contextValidation);
		ContextValidation ctxValidation = new ContextValidation(experimentFilledForm.errors());
		ExperimentValidationHelper.validateRules(exp, ctxValidation);
		
		if(!ctxValidation.hasErrors()){
				
				doCalculations(exp);
				
				Builder builder = new DBUpdate.Builder();
				builder=builder.set("atomicTransfertMethods",exp.atomicTransfertMethods);
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
				
				return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result updateStateCode(String code, String stateCode){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		
		ContextValidation ctxValidation = new ContextValidation(experimentFilledForm.errors());
		//il faut sauvegarder l'experiment avant de changer d'etat
		if(exp._id == null){
			// A revoir avec la validation
			exp = MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		}

		//TODO if first experiment in the processus then processus state to IP
		State newState = new State();
		newState.code=stateCode;
		newState.date=new Date();
		newState.user=InstanceHelpers.getUser();

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
		
		doCalculations(exp);
		
		Form<Experiment> experimentFilledForm = experimentForm.fill(exp);
		
		ContextValidation ctxValidation = new ContextValidation(experimentFilledForm.errors());
		
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
			ContextValidation ctxValidation = new ContextValidation(experimentFilledForm.errors());
			
			exp.code = CodeHelper.generateExperiementCode(exp);
			exp = ExperimentHelper.traceInformation(exp,getCurrentUser());
			ExperimentValidationHelper.validateRules(exp, ctxValidation);
			
			if (!experimentFilledForm.hasErrors()) {
				exp = MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
			}
			State state = StateHelper.cloneState(exp.state);
			state.user=getCurrentUser();
			exp.state.code = null;
			
			Workflows.setExperimentState(exp, state, ctxValidation);
	
			if (!experimentFilledForm.hasErrors()) {
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

		DBQuery.Query query = getQuery(experimentsSearch);
		if(experimentsSearch.datatable){
			MongoDBResult<Experiment> results =  mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(new DatatableResponse<Experiment>(experiments, results.count())));
		}else if (experimentsSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			MongoDBResult<Experiment> results = mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query, keys);
			List<Experiment> experiments = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Experiment p: experiments){
				los.add(new ListObject(p.code, p.code));
			}

			return Results.ok(Json.toJson(los));
		}else{
			MongoDBResult<Experiment> results = mongoDBFinder(InstanceConstants.EXPERIMENT_COLL_NAME, experimentsSearch, Experiment.class, query);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(experiments));
		}
	}

	/**
	 * Construct the experiment query
	 * @param experimentSearch
	 * @return the query
	 */
	private static DBQuery.Query getQuery(ExperimentSearchForm experimentSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();

		Logger.info("Experiment Query : "+experimentSearch);

		if(StringUtils.isNotEmpty(experimentSearch.code)){
			queryElts.add(DBQuery.is("code", experimentSearch.code));
		}
		
		if(StringUtils.isNotEmpty(experimentSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", experimentSearch.typeCode));
		}

		if(experimentSearch.projectCodes != null){
			queryElts.add(DBQuery.in("projectCodes", experimentSearch.projectCodes));
		}

		if(experimentSearch.projectCode != null){
			queryElts.add(DBQuery.in("projectCodes", experimentSearch.projectCode));
		}
		
		if(null != experimentSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", experimentSearch.fromDate));
		}

		if(null != experimentSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(experimentSearch.toDate, 1))));
		}

		if(experimentSearch.sampleCodes != null){
			queryElts.add(DBQuery.in("sampleCodes", experimentSearch.sampleCodes));
		}
		
		if(experimentSearch.containerSupportCode != null){
			queryElts.add(DBQuery.is("instrumentProperties.containerSupportCode.value", experimentSearch.containerSupportCode));
		}
		
		if(experimentSearch.sampleCode != null){
			queryElts.add(DBQuery.in("sampleCodes", experimentSearch.sampleCode));
		}
		
		if(experimentSearch.users != null){
			queryElts.add(DBQuery.in("traceInformation.createUser", experimentSearch.users));
		}
		
		if(experimentSearch.stateCode != null){
			queryElts.add(DBQuery.in("state.code", experimentSearch.stateCode));
		}

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
	
	public static void doCalculations(Experiment exp){
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(exp);
		for(int i=0;i<exp.atomicTransfertMethods.size();i++){
			if(ManytoOneContainer.class.isInstance(exp.atomicTransfertMethods.get(i))){
				ManytoOneContainer atomic = (ManytoOneContainer) exp.atomicTransfertMethods.get(i);
				facts.add(atomic);
			}
		}
		
		RulesServices rulesServices = new RulesServices();
        List<Object> factsAfterRules = null;
        try {
            factsAfterRules = rulesServices.callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), calculationsRules, facts);
        } catch (RulesException e) {
            throw new RuntimeException();
        }

        for(Object obj:factsAfterRules){
            if(ManytoOneContainer.class.isInstance(obj)){
              exp.atomicTransfertMethods.remove(((ManytoOneContainer)obj).position-1);
              exp.atomicTransfertMethods.put(((ManytoOneContainer)obj).position-1,(ManytoOneContainer) obj);
            }
        }
		
	}
}
