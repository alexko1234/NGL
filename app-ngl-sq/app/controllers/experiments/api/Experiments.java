package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;
import play.Logger;
import play.api.data.validation.ValidationError;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

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


		if (!experimentFilledForm.hasErrors()) {
			for(int i=0;i<exp.atomicTransfertMethods.size();i++){
				Workflows.setInWaitingExperiment(exp.atomicTransfertMethods.get(i).getInputContainers());
				Workflows.setFinalState(exp.atomicTransfertMethods.get(i).getInputContainers());
				Workflows.setFinalState(exp.atomicTransfertMethods.get(i).getOutputContainers());
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
		Workflows.setExperimentStateCode(exp,stateCode,ctxValidation);
		if (!ctxValidation.hasErrors()) {	 	
			Builder builder = new DBUpdate.Builder();
			builder = builder.set("stateCode",stateCode);
			if(exp.stateCode.equals("IP")){
				for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					Workflows.setInUse(exp.atomicTransfertMethods.get(i).getInputContainers());
				}
			}else if(exp.stateCode.equals("F")){
				for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					Workflows.setFinalState(exp.atomicTransfertMethods.get(i).getInputContainers());
					Workflows.setFinalState(exp.atomicTransfertMethods.get(i).getOutputContainers());
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
		Form<ExperimentSearchForm> experimentSearchFilledForm = experimentSearchForm.bindFromRequest();
		ExperimentSearchForm experimentSearch = experimentSearchFilledForm.get();
		DBQuery.Query query = getQuery(experimentSearch);
	    MongoDBResult<Experiment> results = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, query)
				.sort(DatatableHelpers.getOrderBy(experimentSearchFilledForm), FormUtils.getMongoDBOrderSense(experimentSearchFilledForm))
				.page(DatatableHelpers.getPageNumber(experimentSearchFilledForm), DatatableHelpers.getNumberRecordsPerPage(experimentSearchFilledForm)); 
	    List<Experiment> experiment = results.toList();
		return ok(Json.toJson(new DatatableResponse<Experiment>(experiment, results.count())));
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
