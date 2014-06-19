package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.StateHelper;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;

import com.mongodb.BasicDBObject;

import controllers.CodeHelper;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Experiments extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);
	final static Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);

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

	public static Result updateInstrumentInformations(String code) throws DAOException{
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder = builder.set("instrument",exp.instrument);
			exp=ExperimentHelper.updateInstrumentCategory(exp);
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateExperimentProperties(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder = builder.set("experimentProperties",exp.experimentProperties);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateInstrumentProperties(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		
		Logger.debug("Experiment update properties :"+exp.code);
		
		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());
		try {
		exp= ExperimentHelper.updateInstrumentCategory(exp);
		} catch (DAOException e) {
			Logger.error(e.getMessage());
		}
		if (!experimentFilledForm.hasErrors()) {

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, 
						DBQuery.is("code", exp.code),
						DBUpdate.set("instrumentProperties",exp.instrumentProperties).set("instrument",exp.instrument));
			
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

	public static Result updateComments(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());

		if (!experimentFilledForm.hasErrors()) {
			if(exp._id == null){
				exp = MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
			}else{
				Builder builder = new DBUpdate.Builder();
				builder=builder.set("comments",exp.comments);

				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			}
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateContainers(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = ExperimentHelper.traceInformation(exp,getCurrentUser());
		exp= ExperimentHelper.setProjetAndSamples(exp);
	
		if (!experimentFilledForm.hasErrors()) {
		
			MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
			//Workflows.nextInputContainerState(exp, new ContextValidation(experimentFilledForm.errors()));
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

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

		if(StringUtils.isNotEmpty(experimentSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", experimentSearch.typeCode));
		}

		if(experimentSearch.projectCodes != null){
			queryElts.add(DBQuery.in("projectCodes", experimentSearch.projectCodes));
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
		
		if(experimentSearch.users != null){
			queryElts.add(DBQuery.in("traceInformation.createUser", experimentSearch.users));
		}
		
		if(experimentSearch.stateCode != null){
			queryElts.add(DBQuery.in("state.code", experimentSearch.stateCode));
		}

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}
