package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputOutputContainer;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.BusinessValidationHelper;
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
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
	 	
		if (!experimentFilledForm.hasErrors()) {
		 	
			Builder builder = new DBUpdate.Builder();
			builder=builder.set("typeCode",exp.typeCode);
			builder=builder.set("resolutionCode",exp.resolutionCode);
			builder=builder.set("protocolCode",exp.protocolCode);
			
			MongoDBDAO.updateSetArray("Experiment", Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
	 	}
	 	
	 	return badRequest(experimentFilledForm.errorsAsJson());
	}
	
	public static Result updateInstrumentInformations(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
	 	
	 	exp = traceInformation(exp);
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
		
		if (!experimentFilledForm.hasErrors()) {
		 	
			Builder builder = new DBUpdate.Builder();
			builder = builder.set("instrument",exp.instrument);
			
			MongoDBDAO.updateSetArray("Experiment", Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
	 	}
	 	
	 	return badRequest(experimentFilledForm.errorsAsJson());
	}
	
	public static Result updateExperimentProperties(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
	 	
	 	exp = traceInformation(exp);
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
	 	
		if (!experimentFilledForm.hasErrors()) {
		 	
			Builder builder = new DBUpdate.Builder();
			builder = builder.set("experimentProperties",exp.experimentProperties);
			
			MongoDBDAO.updateSetArray("Experiment", Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
	 	}
	 	
	 	return badRequest(experimentFilledForm.errorsAsJson());
	}
	
	public static Result updateInstrumentProperties(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
	 	
	 	exp = traceInformation(exp);
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
	 	
		if (!experimentFilledForm.hasErrors()) {
		 	
			Builder builder = new DBUpdate.Builder();
			builder=builder.set("instrumentProperties",exp.instrumentProperties);
			
			MongoDBDAO.updateSetArray("Experiment", Experiment.class, DBQuery.is("code", code),builder);
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
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
		
		if (!experimentFilledForm.hasErrors()) {
		 	
			Builder builder = new DBUpdate.Builder();
			builder=builder.set("comments",exp.comments);
			
			MongoDBDAO.updateSetArray("Experiment", Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
	 	}
	 	
	 	return badRequest(experimentFilledForm.errorsAsJson());
	}
	
	
	public static Result updateContainers(String code){
		Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
		Experiment exp = experimentFilledForm.get();
	 	
	 	exp = traceInformation(exp);
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
		
	 	//TODO: Output gestion
	 	
		if (!experimentFilledForm.hasErrors()) {
			for(InputOutputContainer iop: exp.listInputOutputContainers) {
				Workflows.setInWaitingExperiment(iop.inputContainers);
			}
			
			Builder builder = new DBUpdate.Builder();
			builder=builder.set("listInputOutputContainers",exp.listInputOutputContainers);
			
			MongoDBDAO.updateSetArray("Experiment", Experiment.class, DBQuery.is("code", code),builder);
			return ok(Json.toJson(exp));
	 	}
	 	
	 	return badRequest(experimentFilledForm.errorsAsJson());
	}
	
	public static Result updateStateCode(String code){
	 	Form<Experiment> experimentFilledForm = getFilledForm(experimentForm,Experiment.class);
	 	Experiment exp = experimentFilledForm.get();
	 	
	 	exp = traceInformation(exp);
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
		
	 	if (!experimentFilledForm.hasErrors()) {	 	
			Builder builder = new DBUpdate.Builder();
			builder = builder.set("stateCode",exp.stateCode);
			
			MongoDBDAO.updateSetArray("Experiment", Experiment.class, DBQuery.is("code", code),builder);
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
	 	
	 	BusinessValidationHelper.validateExperiment(experimentFilledForm.errors(), exp,Constants.EXPERIMENT_COLL_NAME, null);
	 	if (!experimentFilledForm.hasErrors()) {	 	
	 		exp = MongoDBDAO.save("Experiment",exp);
		
	 		return ok(Json.toJson(exp));
	 	} else {
	 		return badRequest(experimentFilledForm.errorsAsJson());
	 	}
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
