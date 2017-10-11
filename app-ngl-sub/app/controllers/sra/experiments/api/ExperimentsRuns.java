package controllers.sra.experiments.api;

import static play.data.Form.form;

import java.util.Arrays;
import java.util.List;

import org.mongojack.DBQuery;

import controllers.DocumentController;
import controllers.QueryFieldsForm;
import fr.cea.ig.MongoDBDAO;
import models.sra.submit.sra.instance.Experiment;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;

public class ExperimentsRuns extends DocumentController<Experiment> {

	final static Form<Experiment> experimentForm = form(Experiment.class);
	final static Form<QueryFieldsForm> updateForm = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("accession");
	
	public ExperimentsRuns() {
		super(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class);
	}

	public Result get(String code)
	{
		Experiment exp  = getExperiment(code);
		if (exp != null) {
			return ok(Json.toJson(exp.run));
		} else{
			return notFound();
		}		
	}
	
	public Result update(String code)
	{
		Form<Experiment> filledForm = getFilledForm(experimentForm, Experiment.class);
		Experiment userExperiment = filledForm.get();
		
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		Logger.debug(""+queryFieldsForm.fields);
		ContextValidation ctxVal = new ContextValidation(this.getCurrentUser());
		Experiment experiment = getExperiment(code);
		if (experiment == null) {
			//return badRequest("Submission with code "+code+" not exist");
			ctxVal.addErrors("experiments ", " not exist");
			return badRequest(filledForm.errorsAsJson());
		}

		if(queryFieldsForm.fields != null){
			ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	

			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);

			if(!ctxVal.hasErrors()){
				updateObject(DBQuery.and(DBQuery.is("run.code", code)), 
						getBuilder(userExperiment, queryFieldsForm.fields, Experiment.class, "run").set("traceInformation", getUpdateTraceInformation(experiment.traceInformation)));

				return ok(Json.toJson(getExperiment(code)));
			}else{
				return badRequest(filledForm.errorsAsJson());
			}		
		}
		return ok();
	}
	
	private Experiment getExperiment(String code)
	{
		Experiment exp  = MongoDBDAO.findOne(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.is("run.code", code));
		return exp;
	}
}