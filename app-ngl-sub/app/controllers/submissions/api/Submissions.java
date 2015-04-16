package controllers.submissions.api;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mail.MailServiceException;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import services.FileAcServices;
import services.SubmissionServices;
import services.XmlServices;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Submissions extends DocumentController<Submission>{

	public Submissions() {
		super(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class);
	}

	final static Form<Submission> submissionForm = form(Submission.class);
	final static Form<File> pathForm = form(File.class);
	// declaration d'une instance submissionCreationForm qui permet de recuperer les
	// données du formulaire initSubmission pour realiser la creation de la soumission => utilisee dans save()
	final static Form<SubmissionsCreationForm> submissionsCreationForm = form(SubmissionsCreationForm.class);
	// declaration d'une instance submissionSearchForm qui permet de recuperer la liste des soumissions => utilisee dans list()
	final static Form<SubmissionsSearchForm> submissionsSearchForm = form(SubmissionsSearchForm.class);


	public Result search(String state)
	{
		MongoDBResult<Submission> results = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.is("state.code", state));
		List<Submission> submissions = results.toList();
		return ok(Json.toJson(submissions));
	}

	// methode appelee avec url suivante :
	// http://localhost:9000/api/submissions?datatable=true&paginationMode=local&projCode=BCZ&state=new
	// url construite dans services.js 
	//search : function(){
	//	this.datatable.search({projCode:this.form.projCode, state:'new'});
	//},
	
	public Result list(){	
		SubmissionsSearchForm submissionsSearchFilledForm = filledFormQueryString(SubmissionsSearchForm.class);
		Logger.debug(submissionsSearchFilledForm.state);
		Query query = getQuery(submissionsSearchFilledForm);
		MongoDBResult<Submission> results = mongoDBFinder(submissionsSearchFilledForm, query);				
		List<Submission> submissionsList = results.toList();
		if(submissionsSearchFilledForm.datatable){
			return ok(Json.toJson(new DatatableResponse<Submission>(submissionsList, submissionsList.size())));
		}else{
			return ok(Json.toJson(submissionsList));
		}
	}

	
	private Query getQuery(SubmissionsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.projCode)) { //all
			queries.add(DBQuery.in("projectCode", form.projCode));
		}
		if (StringUtils.isNotBlank(form.state)) { //all
			queries.add(DBQuery.in("state.code", form.state));
		}	
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
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
			ctxVal.getContextObjects().put("type","sra");
			submissionInput.traceInformation.setTraceInformation(getCurrentUser());
			submissionInput.validate(ctxVal);
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


	public Result save() throws SraException, IOException
	{
		Form<SubmissionsCreationForm> filledForm = getFilledForm(submissionsCreationForm, SubmissionsCreationForm.class);
		Logger.debug("filledForm "+filledForm);
		SubmissionsCreationForm submissionsCreationForm = filledForm.get();
		Logger.debug("readsets "+submissionsCreationForm.readSetCodes);

		List<String> readSetCodes = submissionsCreationForm.readSetCodes;

		//String codeReadSet1 = "BCZ_BGOSW_2_H9M6KADXX.IND15"; 
		//String codeReadSet2 = "BCZ_BIOSW_2_H9M6KADXX.IND19"; 

		String user = getCurrentUser();

		SubmissionServices submissionServices = new SubmissionServices();
		String submissionCode;
		ContextValidation contextValidation = new ContextValidation(user);
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		try {
			submissionCode = submissionServices.initNewSubmission(submissionsCreationForm.projCode, readSetCodes, submissionsCreationForm.studyCode, submissionsCreationForm.configurationCode, user, contextValidation);
			if (contextValidation.hasErrors()){
				contextValidation.displayErrors(Logger.of("SRA"));
				return badRequest("Voir Display Error");
			}
		} catch (SraException e) {
			return badRequest(e.getMessage());
		}
		return ok(Json.toJson(submissionCode));
	}

	
	public Result activate(String submissionCode) throws SraException, IOException
	{
		SubmissionServices submissionServices = new SubmissionServices();
		Submission submission = null;
		try {
			submissionServices.activateSubmission(submissionCode);			
			submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		} catch (SraException e) {
			return badRequest(e.getMessage());
		}
		//return ok();	
		return ok(Json.toJson(submission));
	}

}

