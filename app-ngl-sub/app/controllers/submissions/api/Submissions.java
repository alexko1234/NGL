package controllers.submissions.api;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mail.MailServiceException;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.stereotype.Controller;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Content;
import services.FileAcServices;
import services.SubmissionServices;
import services.XmlServices;
import validation.ContextValidation;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Submissions extends DocumentController<Submission>{
	// declaration d'une instance submissionCreationForm qui permet de recuperer les
	// données du formulaire startSubmission pour realiser la creation de la soumission.
	final static Form<SubmissionCreationForm> submissionCreationForm = form(SubmissionCreationForm.class);

	public Submissions() {
		super(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class);
	}

	final static Form<Submission> submissionForm = form(Submission.class);
	final static Form<File> pathForm = form(File.class);
	
	public Result search(String state)
	{
		MongoDBResult<Submission> results = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.is("state.code", state));
		List<Submission> submissions = results.toList();
		return ok(Json.toJson(submissions));
	}

	public Result update(String code)
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


	public Result createXml(String code)
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


	public Result treatmentAc(String code)
	{
		//Get Submission from DB 
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		if (submission == null) {
			return badRequest("Submission with code "+code+" not exist");
		}
		Form<File> filledForm = getFilledForm(pathForm, File.class);
		Logger.debug("filledForm "+filledForm);
		File ebiFileAc = filledForm.get();
		try {
			submission = FileAcServices.traitementFileAC(code, ebiFileAc);
		} catch (IOException e) {
			return badRequest(e.getMessage());
		} catch (SraException e) {
			return badRequest(e.getMessage());
		} catch (MailServiceException e) {
			return badRequest(e.getMessage());
		}
		return ok(Json.toJson(submission));
	}
	public Result updateState(String code, String stateCode)
	{
		Submission submission = getSubmission(code);
		if(submission==null)
			return badRequest("Submission with code "+code+" not exist");
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", code), 
				DBUpdate.set("state.code", stateCode));
		return ok();
	}
	
	public Result getRawDatas(String code)
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
	
	private Submission getSubmission(String code)
	{
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		return submission;
	}
	
	
	//public Result save(String projectCode, List<ReadSet> readSets, String studyCode, String configCode, String user) throws SraException, IOException
	public Result save() throws SraException, IOException
	{
		Form<SubmissionCreationForm> filledForm = getFilledForm(submissionCreationForm, SubmissionCreationForm.class);
		Logger.debug("filledForm "+filledForm);
		SubmissionCreationForm submissionCreationForm = filledForm.get();
		Logger.debug("readsets "+submissionCreationForm.readSetCodes);
		String codeReadSet1 = "BCZ_BGOSW_2_H9M6KADXX.IND15"; 
		ReadSet readSet1 = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, codeReadSet1);
		List<String> readSetCodes = submissionCreationForm.readSetCodes;
		readSetCodes.add(codeReadSet1);
		
		String user = getCurrentUser();
		
		SubmissionServices submissionServices = new SubmissionServices();
		String submissionCode;
		ContextValidation contextValidation = new ContextValidation(user);
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		try {
			//submissionCode = submissionServices.createNewSubmission(submissionCreationForm.projCode, readSets, submissionCreationForm.studyCode, submissionCreationForm.configurationCode, user, contextValidation);
			submissionCode = submissionServices.createNewSubmission(submissionCreationForm.projCode, readSetCodes, submissionCreationForm.studyCode, submissionCreationForm.configurationCode, user, contextValidation);
			if (contextValidation.hasErrors()){
				contextValidation.displayErrors(Logger.of("SRA"));
				return badRequest("Voir Display Error");
			}
		} catch (SraException e) {
			return badRequest(e.getMessage());
		}
		return ok(Json.toJson(submissionCode));
	}
	
	
}

