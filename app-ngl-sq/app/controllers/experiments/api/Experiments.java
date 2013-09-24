package controllers.experiments.api;

import static play.data.Form.form;

import java.util.List;
import java.util.Map;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

import validation.ContextValidation;
import workflows.Workflows;
import controllers.CodeHelper;
import controllers.CommonController;
import controllers.Constants;
import controllers.authorisation.PermissionHelper;
import fr.cea.ig.MongoDBDAO;

public class Experiments extends CommonController{

	final static Form<Experiment> experimentForm = form(Experiment.class);

	public static Result updateExperimentInformations(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		exp = traceInformation(exp);

		Workflows.setExperimentStateCode(exp,new ContextValidation(experimentFilledForm.errors()));	

		if (!experimentFilledForm.hasErrors()) {

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("typeCode",exp.typeCode);
			builder=builder.set("resolutionCode",exp.resolutionCode);
			builder=builder.set("protocolCode",exp.protocolCode);

			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result generateOutput(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
		if(exp.stateCode.equals("IP")){			
			List<Container> containers = null;
			if (!experimentFilledForm.hasErrors()) {
				for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					containers = exp.atomicTransfertMethods.get(i).createOutputContainerUsed(exp);
				}
	
	
				Builder builder = new DBUpdate.Builder();
				builder=builder.set("atomicTransfertMethods",exp.atomicTransfertMethods);
	
				MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
				
				exp = traceInformation(exp);
				InstanceHelpers.save(Constants.CONTAINER_COLL_NAME, containers,new ContextValidation( experimentFilledForm.errors()));
				return ok(Json.toJson(exp));
			}
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

			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
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

			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
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

			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
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

			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
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
			}

			Builder builder = new DBUpdate.Builder();
			builder=builder.set("atomicTransfertMethods",exp.atomicTransfertMethods);

			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}

	public static Result updateStateCode(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		//TODO if first experiment in the processus then processus state to IP
		Workflows.setExperimentStateCode(exp,new ContextValidation(experimentFilledForm.errors()));
		if (!experimentFilledForm.hasErrors()) {	 	
			Builder builder = new DBUpdate.Builder();
			builder = builder.set("stateCode",exp.stateCode);//TODO: validation? Business validation
			if(exp.stateCode.equals("IP")){
				for(int i=0;i<exp.atomicTransfertMethods.size();i++){
					Workflows.setInUse(exp.atomicTransfertMethods.get(i).getInputContainers());
				}
			}
			exp = traceInformation(exp);
			MongoDBDAO.update(Constants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
		}

		return badRequest(experimentFilledForm.errorsAsJson());
	}


	/**
	 * Save or update an Experiment object	
	 * @param experimentType
	 * @return the created experiment
	 */
	public static Result save(String experimentType){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();

		if(exp._id == null){
			exp.code = CodeHelper.generateExperiementCode(exp);
			exp.typeCode = experimentType;
			exp.stateCode = "N";
		}

		exp = traceInformation(exp);

		if (!experimentFilledForm.hasErrors()) {	 	
			exp = (Experiment) InstanceHelpers.save(Constants.EXPERIMENT_COLL_NAME, exp, new ContextValidation(experimentFilledForm.errors()));
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
		
		exp = (Experiment) MongoDBDAO.save(Constants.EXPERIMENT_COLL_NAME, exp);
		
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
}
