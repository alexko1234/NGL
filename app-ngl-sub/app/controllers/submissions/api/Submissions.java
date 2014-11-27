package controllers.submissions.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.Run;
import models.sra.experiment.instance.Experiment;
import models.sra.experiment.instance.RawData;
import models.sra.submission.instance.Submission;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.QueryFieldsForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Submissions extends SubmissionsController{

	final static Form<Submission> submissionForm = form(Submission.class);

	public static Result search(String state)
	{
		MongoDBResult<Submission> results = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.is("state.code", state));
		List<Submission> submissions = results.toList();
		return ok(Json.toJson(submissions));
	}

	public static Result update(String code)
	{
		//Get Submission from DB 
		Submission submission = getSubmission(code);
		if (submission == null) {
			return badRequest("Submission with code "+code+" not exist");
		}
		Form<Submission> filledForm = getFilledForm(submissionForm, Submission.class);
		Submission submissionInput = filledForm.get();
		if (code.equals(submissionInput.code)) {
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			submission.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				Logger.info("Update submission state "+submissionInput.state.code);
				MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submissionInput);
				return ok(Json.toJson(submissionInput));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			return badRequest("submission code are not the same");
		}	
	}
	
	public static Result getRawDatas(String code)
	{
		List<RawData> allRawDatas = new ArrayList<RawData>();
		//Get all experiments from submission
		Submission submission = getSubmission(code);
		for(String codeExperiment : submission.experimentCodes){
			//Get List RawData from each experiment
			Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, codeExperiment);
			allRawDatas.addAll(experiment.run.listRawData);
		}
		return ok(Json.toJson(allRawDatas));
		
	}
}
