package controllers.sra.experiments.api;

//import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.DocumentController;
import controllers.QueryFieldsForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.NGLContext;
import models.sra.submit.sra.instance.Experiment;
import models.utils.InstanceConstants;
//import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;

// TODO: remove System.out.print

public class Experiments extends DocumentController<Experiment> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Experiments.class);

	private static final List<String> authorizedUpdateFields = Arrays.asList("accession");

	final /*static*/ Form<ExperimentsSearchForm> experimentsSearchForm;// = form(ExperimentsSearchForm.class);
	final /*static*/ Form<Experiment> experimentForm;// = form(Experiment.class);
	final /*static*/ Form<QueryFieldsForm> updateForm ;//= form(QueryFieldsForm.class);

	@Inject
	public Experiments(NGLContext ctx) {
		super(ctx,InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class);
		experimentsSearchForm = ctx.form(ExperimentsSearchForm.class);
		experimentForm = ctx.form(Experiment.class);
		updateForm = ctx.form(QueryFieldsForm.class);
	}

	public Result get(String code) {
		Experiment exp  = MongoDBDAO.findOne(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, 
				                             Experiment.class, 
				                             DBQuery.is("code", code));
		if (exp != null) {
			return ok(Json.toJson(exp));
		} else {
			return notFound();
		}	
	}

	/*public Result get(String code) {
		return ok(Json.toJson(getSample(code)));
	}
*/

	public Result list() {
		//if (true){return ok(Json.toJson(new ArrayList<Experiment>()));}
		Form<ExperimentsSearchForm> experimentssSearchFilledForm = filledFormQueryString(experimentsSearchForm, ExperimentsSearchForm.class);
		ExperimentsSearchForm form = experimentssSearchFilledForm.get();
		Query query = getQuery(form);
		MongoDBResult<Experiment> results = mongoDBFinder(form, query);							
		List<Experiment> list = results.toList();
		if (form.datatable) {
			return ok(Json.toJson(new DatatableResponse<Experiment>(list, list.size())));
		} else {
			return ok(Json.toJson(list));
		}
	}

	// Met a jour l'experiment dont le code est indiqué avec les valeurs presentes dans l'experiment recuperé du formulaire (userExperiment)
	public Result update(String code) {
		
		Form<Experiment> filledForm = getFilledForm(experimentForm, Experiment.class);
		Experiment userExperiment = filledForm.get();

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();

//		ContextValidation ctxVal = new ContextValidation(this.getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(this.getCurrentUser(), filledForm);
		Experiment experiment = getObject(code);
		if (experiment == null) {
			//return badRequest("Submission with code "+code+" not exist");
			ctxVal.addErrors("experiments ", " not exist");
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!updateExperiment: " +userExperiment.code );
		logger.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!! updateExperiment : {}", userExperiment.code );
		if (queryFieldsForm.fields == null) {
			if (code.equals(userExperiment.code)) {
//				ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
				ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	
				ctxVal.setUpdateMode();
				ctxVal.getContextObjects().put("type", "sra");
				userExperiment.traceInformation.setTraceInformation(getCurrentUser());

				//userExperiment.state = new State("V-SUB", getCurrentUser());
				userExperiment.validate(ctxVal);
//				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!updateExperiment: " +userExperiment.code );
//				System.out.println("experiment.state: " +userExperiment.state.code );
				logger.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!! updateExperiment : {}", userExperiment.code );
				logger.debug("experiment.state : {}", userExperiment.state.code );
				//System.out.println(Json.toJson(userExperiment));
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, userExperiment);
					return ok(Json.toJson(userExperiment));
				} else {
//					System.out.println("contextValidation.errors pour experiment :"  +userExperiment.code);
					logger.debug("contextValidation.errors pour experiment : {}", userExperiment.code);
					ctxVal.displayErrors(logger);
					// System.out.println(filledForm.errors-AsJson());
					// return badRequest(filledForm.errors-AsJson());
//					System.out.println(errorsAsJson(ctxVal.getErrors()));
					return badRequest(errorsAsJson(ctxVal.getErrors()));
				}
			} else {
//				filledForm.reject("experiment code " + code + " and userExperiment.code " + userExperiment.code , " are not the same");
				ctxVal.addError("experiment code " + code + " and userExperiment.code " + userExperiment.code , " are not the same");
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}	
		} else {
//			ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			if (!ctxVal.hasErrors()) {
				updateObject(DBQuery.and(DBQuery.is("code", code)), 
						getBuilder(userExperiment, queryFieldsForm.fields).set("traceInformation", getUpdateTraceInformation(experiment.traceInformation)));

				return ok(Json.toJson(getObject(code)));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}		
		}
	}

	/*	// Renvoie l'experiment present dans la base repondant au code indiqué
	private Experiment getExperiment(String code)
	{
		Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, code);
		return experiment;
	}
	 */

	private Query getQuery(ExperimentsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.studyCode)) { //all
			queries.add(DBQuery.in("studyCode", form.studyCode));
		}
		if (CollectionUtils.isNotEmpty(form.projCodes)) { //
			queries.add(DBQuery.in("projectCode", form.projCodes)); 
		}
		if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", form.stateCodes));
		} else if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.in("state.code", form.stateCode));
		}
		if(CollectionUtils.isNotEmpty(form.codes)) {
			queries.add(DBQuery.in("code", form.codes));
		} else if(StringUtils.isNotBlank(form.codeRegex)) {
			queries.add(DBQuery.regex("code", Pattern.compile(form.codeRegex)));
		}
		if(CollectionUtils.isNotEmpty(form.accessions)) {
			for (String ac: form.accessions) {
				logger.debug("accession=" + ac);
			}
			queries.add(DBQuery.in("accession", form.accessions));
		} else if(StringUtils.isNotBlank(form.accessionRegex)){
			queries.add(DBQuery.regex("accession", Pattern.compile(form.accessionRegex)));
		}
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
	
}
