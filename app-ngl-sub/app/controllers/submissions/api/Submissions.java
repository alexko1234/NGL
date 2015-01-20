package controllers.submissions.api;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mail.MailServiceException;
import models.sra.experiment.instance.Experiment;
import models.sra.experiment.instance.RawData;
import models.sra.submission.instance.Submission;
import models.sra.utils.SraException;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import services.FileAcServices;
import services.XmlServices;
import validation.ContextValidation;
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


	public static Result createXml(String code)
	{
		//Get Submission from DB 
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		if (submission == null) {
			return badRequest("Submission with code "+code+" not exist");
		}
		try {
			submission = XmlServices.writeAllXml(code);
		} catch (IOException e) {
			return badRequest(e.getMessage());
		} catch (SraException e) {
			return badRequest(e.getMessage());
		}
		return ok(Json.toJson(submission));
	}


	public static Result treatmentAc(String code, String pathEbiFileAc)
	{
		//Get Submission from DB 
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		if (submission == null) {
			return badRequest("Submission with code "+code+" not exist");
		}
		try {
			submission = FileAcServices.traitementFileAC(code, pathEbiFileAc);
		} catch (IOException e) {
			return badRequest(e.getMessage());
		} catch (SraException e) {
			return badRequest(e.getMessage());
		} catch (MailServiceException e) {
			return badRequest(e.getMessage());
		}
		return ok(Json.toJson(submission));
	}
	public static Result updateState(String code, String stateCode)
	{
		Submission submission = getSubmission(code);
		if(submission==null)
			return badRequest("Submission with code "+code+" not exist");
		Map<String, Object> mapKeyValue = new HashMap<String, Object>();
		mapKeyValue.put("state.code", stateCode);
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission, mapKeyValue);
		return ok();
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

