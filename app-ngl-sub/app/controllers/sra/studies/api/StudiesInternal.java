package controllers.sra.studies.api;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery;

import controllers.DocumentController;
import controllers.QueryFieldsForm;
import fr.cea.ig.play.migration.NGLContext;
import models.sra.submit.common.instance.Study;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;

public class StudiesInternal extends DocumentController<Study> {
	
//	private static final play.Logger.ALogger logger = play.Logger.of(StudiesInternal.class);

	final /*static*/ Form<Study> studyForm;// = form(Study.class);
	final /*static*/ Form<QueryFieldsForm> updateForm;// = form(QueryFieldsForm.class);
	final static List<String> authorizedUpdateFields = Arrays.asList("accession","externalId","firstSubmissionDate","releaseDate");
	
	@Inject
	public StudiesInternal(NGLContext ctx) {
		super(ctx,InstanceConstants.SRA_STUDY_COLL_NAME, Study.class);
		studyForm = ctx.form(Study.class);
		updateForm = ctx.form(QueryFieldsForm.class);
	}

	public Result update(String code) {
		Study study = getObject(code);

		Form<Study> filledForm = getFilledForm(studyForm, Study.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();

		if (study == null) {
			//return badRequest("Study with code "+code+" not exist");
			ctxVal.addErrors("study ", " not exist");
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}
		Study studyInput = filledForm.get();

		if (queryFieldsForm.fields != null) {
//			ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			
			if (!ctxVal.hasErrors()) {
				updateObject(DBQuery.and(DBQuery.is("code", code)), 
						getBuilder(studyInput, queryFieldsForm.fields).set("traceInformation", getUpdateTraceInformation(study.traceInformation)));
				return ok(Json.toJson(getObject(code)));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}	
		}
		return ok(Json.toJson(getObject(code)));
	}


}
