package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Instrument;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.InstrumentCategory;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;
import controllers.CodeHelper;
import controllers.CommonController;
import controllers.authorisation.PermissionHelper;
import controllers.utils.FormUtils;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Experiments extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);
	final static Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);

	public static Result updateExperimentInformations(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = traceInformation(exp);

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("typeCode",exp.typeCode);
			builder=builder.set("resolutionCode",exp.resolutionCode);
			builder=builder.set("protocolCode",exp.protocolCode);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result generateOutput(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		exp = traceInformation(exp);
		if(exp.stateCode.equals("IP")){			
			List<Container> containers = new ArrayList<Container>();
			if (!experimentFilledForm.hasErrors()) {
				for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					containers.addAll(exp.atomicTransfertMethods.get(i).createOutputContainerUsed(exp));
				}


				Builder builder = new DBUpdate.Builder();
				builder=builder.set("atomicTransfertMethods",exp.atomicTransfertMethods);

				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);


				InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers,new ContextValidation( experimentFilledForm.errors()));

				return ok(Json.toJson(exp));
			}
		}else{
			//TODO: Add errors to form (state not IP)

		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateInstrumentInformations(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = traceInformation(exp);

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder = builder.set("instrument",exp.instrument);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateExperimentProperties(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = traceInformation(exp);

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

		exp = traceInformation(exp);

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("instrumentProperties",exp.instrumentProperties);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
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

		exp = traceInformation(exp);

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("comments",exp.comments);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateContainers(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = traceInformation(exp);
		exp.sampleCodes = new ArrayList<String>();
		exp.projectCodes  = new ArrayList<String>();

		if (!experimentFilledForm.hasErrors()) {
			for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					Workflows.setContainersInWaitingExperiment(exp.atomicTransfertMethods.get(i).getInputContainers());
					Workflows.setContainersFinalState(exp.atomicTransfertMethods.get(i).getInputContainers());
					if(exp.atomicTransfertMethods.get(i).getOutputContainers() != null){
						Workflows.setContainersFinalState(exp.atomicTransfertMethods.get(i).getOutputContainers());
					}
					for(ContainerUsed c:exp.atomicTransfertMethods.get(i).getInputContainers()){
						Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, c.containerCode);
						exp.sampleCodes = InstanceHelpers.addCodesList(exp.sampleCodes, container.sampleCodes);
						exp.projectCodes = InstanceHelpers.addCodesList(exp.projectCodes, container.projectCodes);
					}
				}
			/*Builder builder = new DBUpdate.Builder();
			builder=builder.set("atomicTransfertMethods",exp.atomicTransfertMethods);

			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);*/
		
			
			MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateStateCode(String code, String stateCode){
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, code);

		//TODO if first experiment in the processus then processus state to IP
		ContextValidation ctxValidation = new ContextValidation();
		Workflows.setExperimentState(exp,stateCode,ctxValidation);
		if (!ctxValidation.hasErrors()) {	 	
			Builder builder = new DBUpdate.Builder();
			builder = builder.set("stateCode",stateCode);
			if(stateCode.equals("IP")){
				for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					Workflows.setContainerInUse(exp.atomicTransfertMethods.get(i).getInputContainers());
				}
			}else if(stateCode.equals("F")){
				for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					Workflows.setContainersFinalState(exp.atomicTransfertMethods.get(i).getInputContainers());
					Workflows.setContainersFinalState(exp.atomicTransfertMethods.get(i).getOutputContainers());
				}
			}
			exp = traceInformation(exp);

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);

			return ok();
		}

		return badRequest(Json.toJson(ctxValidation.errors));
	}


	/**
	 * Save or update an Experiment object	
	 * @param experimentType
	 * @return the created experiment
	 */
	public static Result save(String experimentType){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		if(exp._id == null && exp.code == null){
			exp.code = CodeHelper.generateExperiementCode(exp);
			exp.typeCode = experimentType;
			exp.stateCode = "N";
		}

		exp = traceInformation(exp);

		if (!experimentFilledForm.hasErrors()) {
			if(exp.instrument != null){
				//set the categoryCode
			}
			//exp = (Experiment) InstanceHelpers.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp, new ContextValidation(experimentFilledForm.errors()));
			exp = MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);
		}

		if (!experimentFilledForm.hasErrors()) {
			return ok(Json.toJson(exp));
		}else {
			return badRequest(experimentFilledForm.errorsAsJson());
		}
	}


	public static Result create(){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		if(exp._id == null){
			exp.code = CodeHelper.generateExperiementCode(exp);
			exp.stateCode = "N";
		}

		exp = traceInformation(exp);

		//exp = (Experiment) MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, exp);

		return ok(Json.toJson(exp));
	}

	/**
	 * Add/Create trace informations to the experiment object
	 * @param exp: the Experiment object
	 * @return the new experiment object with traces
	 */
	private static Experiment traceInformation(Experiment exp){
		if (null == exp._id) {
			//init
			exp.traceInformation = new TraceInformation();
			exp.traceInformation.setTraceInformation(PermissionHelper.getCurrentUser(session()));
		} else {
			exp.traceInformation.setTraceInformation(PermissionHelper.getCurrentUser(session()));
		}

		return exp;
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

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}
