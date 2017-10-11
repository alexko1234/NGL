package controllers.sra.samples.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.DocumentController;
import controllers.QueryFieldsForm;
import fr.cea.ig.MongoDBDAO;
import models.sra.submit.common.instance.AbstractSample;
import models.utils.InstanceConstants;

//import com.gargoylesoftware.htmlunit.javascript.host.Console;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;

public class Samples extends DocumentController<AbstractSample>{


	final static Form<SamplesSearchForm> samplesSearchForm = form(SamplesSearchForm.class);
	final static Form<AbstractSample> sampleForm = form(AbstractSample.class);

	public Samples() {
		super(InstanceConstants.SRA_SAMPLE_COLL_NAME, AbstractSample.class);
	}

	public Result get(String code)
	{
		return ok(Json.toJson(getSample(code)));
	}

	public Result list()
	{
		SamplesSearchForm form = filledFormQueryString(SamplesSearchForm.class);
		Query query = getQuery(form);
		//MongoDBResult<AbstractSample> results = mongoDBFinder(form, query);							
		//List<AbstractSample> list = results.toList();
		List<AbstractSample> list = MongoDBDAO.find(InstanceConstants.SRA_SAMPLE_COLL_NAME, AbstractSample.class, query).toList();
		return ok(Json.toJson(list));
	}

	public Result update(String code)
	{
		//Get Submission from DB 
		AbstractSample sample = getSample(code);
		Form<AbstractSample> filledForm = getFilledForm(sampleForm, AbstractSample.class);

		if (sample == null) {
			filledForm.reject("Sample " +  code, "not exist in database");  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());
		}
		System.out.println(" ok je suis dans Samples.update\n");
		AbstractSample sampleInput = filledForm.get();

		if (code.equals(sampleInput.code)) {
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			ctxVal.getContextObjects().put("type", "sra");
			sampleInput.traceInformation.setTraceInformation(getCurrentUser());
			sampleInput.validate(ctxVal);	
			if (!ctxVal.hasErrors()) {
				Logger.debug(" ok je suis dans Samples.update et pas d'erreur\n");
				Logger.info("Update sample "+sample.code);
				MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, sampleInput);
				Logger.debug(Json.toJson(sampleInput).toString());

				return ok(Json.toJson(sampleInput));
			}else {
				System.out.println(" ok je suis dans Samples.update et erreurs \n");
				Logger.debug(Json.toJson(sampleInput).toString());
				return badRequest(filledForm.errorsAsJson());
			}		
		}else{
			filledForm.reject("sample code " + code + " and sampleInput.code " + sampleInput.code , " are not the same");
			return badRequest(filledForm.errorsAsJson());
		}
	}

	private AbstractSample getSample(String code)
	{
		AbstractSample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, AbstractSample.class, code);
		return sample;
	}


	private Query getQuery(SamplesSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;

		if (CollectionUtils.isNotEmpty(form.listSampleCodes)) { //all
			queries.add(DBQuery.in("code", form.listSampleCodes));
		}
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}
}
