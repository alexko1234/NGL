package controllers.sra.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import models.sra.submit.sra.instance.Experiment;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;

public class Experiments extends DocumentController<Experiment> {

	final static Form<ExperimentsSearchForm> experimentsSearchForm = form(ExperimentsSearchForm.class);
	final static Form<Experiment> experimentForm = form(Experiment.class);

	public Experiments() {
		super(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class);
	}

	public Result get(String code)
	{
		Experiment exp  = MongoDBDAO.findOne(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, 
				DBQuery.is("code", code));
		if (exp != null) {
			return ok(Json.toJson(exp));
		} else{
			return notFound();
		}	
	}
	
	public Result list()
	{
		ExperimentsSearchForm form = filledFormQueryString(ExperimentsSearchForm.class);
		Query query = getQuery(form);
		MongoDBResult<Experiment> results = mongoDBFinder(form, query);							
		List<Experiment> list = results.toList();
		return ok(Json.toJson(list));
	}
	
	// Met a jour l'experiment dont le code est indiqué avec les valeurs presentes dans l'experiment recuperé du formulaire (userExperiment)
	public Result update(String code)
	{
		Form<Experiment> filledForm = getFilledForm(experimentForm, Experiment.class);
		Experiment userExperiment = filledForm.get();
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!updateExperiment: " +userExperiment.code );
		
		if (code.equals(userExperiment.code)) {
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			ctxVal.getContextObjects().put("type", "sra");
			userExperiment.traceInformation.setTraceInformation(getCurrentUser());
			
			//userExperiment.state = new State("V-SUB", getCurrentUser());
			userExperiment.validate(ctxVal);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!updateExperiment: " +userExperiment.code );
			System.out.println("experiment.state: " +userExperiment.state.code );
			//System.out.println(Json.toJson(userExperiment));
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, userExperiment);
				return ok(Json.toJson(userExperiment));
			}else {
				System.out.println("contextValidation.errors pour experiment :"  +userExperiment.code);
				ctxVal.displayErrors(Logger.of("SRA"));
				System.out.println(filledForm.errorsAsJson());
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			filledForm.reject("experiment code " + code + " and userExperiment.code " + userExperiment.code , " are not the same");
			return badRequest(filledForm.errorsAsJson());
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
		
		if (CollectionUtils.isNotEmpty(form.listExperimentCodes)) { //all
			queries.add(DBQuery.in("code", form.listExperimentCodes));
		}	
		if (StringUtils.isNotBlank(form.experimentCode)) { //all
			queries.add(DBQuery.in("code", form.experimentCode));
		}
		
		// ajout pour interface release study :
		if (StringUtils.isNotBlank(form.studyCode)) { //all
			queries.add(DBQuery.in("studyCode", form.studyCode));
		}
		
		// end ajout .
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
}
