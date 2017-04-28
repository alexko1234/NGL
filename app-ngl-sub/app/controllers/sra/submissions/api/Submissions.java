package controllers.sra.submissions.api;

import static play.data.Form.form;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import controllers.DocumentController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import mail.MailServiceException;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.common.instance.UserCloneType;
import models.sra.submit.common.instance.UserExperimentType;
import models.sra.submit.common.instance.UserSampleType;
import models.sra.submit.util.SraException;
import models.utils.InstanceConstants;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import services.FileAcServices;
import services.SubmissionServices;
import services.Tools;
import services.UserCloneTypeParser;
import services.UserExperimentTypeParser;
import services.UserSampleTypeParser;
import services.XmlServices;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;
import workflows.sra.submission.SubmissionWorkflows;

public class Submissions extends DocumentController<Submission>{
	private Map<String, UserCloneType> mapUserClones = new HashMap<String, UserCloneType>();
	private Map<String, UserExperimentType> mapUserExperiments = new HashMap<String, UserExperimentType>();
	private Map<String, UserSampleType> mapUserSamples = new HashMap<String, UserSampleType>();

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
	final SubmissionWorkflows subWorkflows = Spring.getBeanOfType(SubmissionWorkflows.class);
	final static Form<State> stateForm = form(State.class);


	// methode appelee avec url suivante :
	//localhost:9000/api/sra/submissions?datatable=true&paginationMode=local&projCode=BCZ&state=N
	// url construite dans services.js 
	//search : function(){
	//	this.datatable.search({projCode:this.form.projCode, state:'N'});
	//},
	
	public Result list(){	
		Form<SubmissionsSearchForm> submissionsSearchFilledForm = filledFormQueryString(submissionsSearchForm, SubmissionsSearchForm.class);
		SubmissionsSearchForm submissionsSearchForm = submissionsSearchFilledForm.get();
		//modif 		Logger.debug(submissionsSearchForm.state);

		Logger.debug(submissionsSearchForm.stateCode);
		Query query = getQuery(submissionsSearchForm);
		MongoDBResult<Submission> results = mongoDBFinder(submissionsSearchForm, query);				
		List<Submission> submissionsList = results.toList();
		if(submissionsSearchForm.datatable){
			return ok(Json.toJson(new DatatableResponse<Submission>(submissionsList, submissionsList.size())));
		}else{
			return ok(Json.toJson(submissionsList));
		}
	}

	
	private Query getQuery(SubmissionsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (CollectionUtils.isNotEmpty(form.projCodes)) { //
			queries.add(DBQuery.in("projectCodes", form.projCodes));
		}
		//modif		if (StringUtils.isNotBlank(form.state)) { //all

		//if (StringUtils.isNotBlank(form.state.code)) { //all
		if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.in("state.code", form.stateCode));
		}	
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		return query;
	}

	public Result update(String code) {
		//Get Submission from DB 
		Submission submission = getSubmission(code);
		Form<Submission> filledForm = getFilledForm(submissionForm, Submission.class);
		ContextValidation ctxVal = new ContextValidation(this.getCurrentUser());
		if (submission == null) {
			//return badRequest("Submission with code "+code+" not exist");
			ctxVal.addErrors("submission ", " not exist");
			return badRequest(filledForm.errorsAsJson());
		}
		Submission submissionInput = filledForm.get();
		if (code.equals(submissionInput.code)) {	
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
			//return badRequest("submission code are not the same");
			ctxVal.addErrors("submission "+code, "submission code  " +code + " and submissionInput.code "+ submissionInput.code + "are not the same");
			return badRequest(filledForm.errorsAsJson());
		}	
	}


	public Result updateState(String code){
		ContextValidation ctxVal = new ContextValidation(this.getCurrentUser());
		//Get Submission from DB 
		Submission submission = getSubmission(code); // ou bien Submission submission2 = getObject(code);
		Form<State> filledForm = getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		state.date = new Date();
		state.user = getCurrentUser();
		
		if (submission == null) {
			//return badRequest("Submission with code "+code+" not exist");
			ctxVal.addErrors("submission " + code,  " not exist in database");	
			return badRequest(filledForm.errorsAsJson());
		}
		subWorkflows.setState(ctxVal, submission, state);
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(getObject(code)));
		}else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	
	public Result createXml(String code)
	{
		//Get Submission from DB 
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		
		// declaration d'un filledForm uniquement pour utiliser messages.addDetails au niveau des javascript et 
		// pouvoir afficher l'ensemble des erreurs.
		//Form initialise avec l'objet submission car pas d'objet submission dans le body
		//Form<Submission> filledForm = getFilledForm(submissionForm, Submission.class);
		Form<Submission> filledForm = Form.form(Submission.class);
		filledForm.fill(submission);
		
		if (submission == null) {
			//return badRequest("Submission with code "+code+" not exist");
			filledForm.reject("Submission " + code," not exist");  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());
		}
		try {
			submission = XmlServices.writeAllXml(code);
		} catch (IOException e) {
			//return badRequest(e.getMessage());
			filledForm.reject("Submission " + code, e.getMessage());  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());
		} catch (SraException e) {
			//return badRequest(e.getMessage());
			filledForm.reject("Submission " + code, e.getMessage());  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());
		}
		return ok(Json.toJson(submission));
	}


	public Result treatmentAc(String code)
	{
		//Get Submission from DB 
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		Form<File> filledForm = getFilledForm(pathForm, File.class);
		if (submission == null) {
			filledForm.reject("Submission with code "+code, " not exist");
			return badRequest(filledForm.errorsAsJson());
		}
		Logger.debug("filledForm "+filledForm);
		File ebiFileAc = filledForm.get();
		ContextValidation ctxVal = new ContextValidation(this.getCurrentUser());
		try {
			submission = FileAcServices.traitementFileAC(ctxVal, code, ebiFileAc);
		} catch (IOException e) {
			//return badRequest(e.getMessage());
			filledForm.reject("Submission " + code + " et ebiFileAc " +ebiFileAc, e.getMessage());  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());

		} catch (SraException e) {
			//return badRequest(e.getMessage());
			filledForm.reject("Submission " + code + " et ebiFileAc " +ebiFileAc, e.getMessage());  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());
		} catch (MailServiceException e) {
			//return badRequest(e.getMessage());
			filledForm.reject("Submission " + code + " et ebiFileAc " +ebiFileAc, e.getMessage());  // si solution filledForm.reject
			return badRequest(filledForm.errorsAsJson());
		}
		return ok(Json.toJson(submission));
	}
	
	private Submission getSubmission(String code)
	{
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, code);
		return submission;
	}


	// methode appelée  depuis interface submissions.create-ctrl.js (submissions.create.scala.html)
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 15000 * 1024)
	public Result save() throws SraException, IOException {
	
		if(request().body().isMaxSizeExceeded()){
			return badRequest("Max size exceeded");
		}
		
		Form<SubmissionsCreationForm> filledForm = getFilledForm(submissionsCreationForm, SubmissionsCreationForm.class);
		Logger.debug("filledForm "+filledForm);
		SubmissionsCreationForm submissionsCreationForm = filledForm.get();
		Logger.debug("readsets "+submissionsCreationForm.readSetCodes);
		
		String user = getCurrentUser();
		ContextValidation contextValidation = new ContextValidation(user, filledForm.errors());
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		
		String submissionCode;
		try {
			if (StringUtils.isBlank(submissionsCreationForm.base64UserFileExperiments)){
				submissionsCreationForm.base64UserFileExperiments="";
			}
			if (StringUtils.isBlank(submissionsCreationForm.base64UserFileSamples)){
				submissionsCreationForm.base64UserFileSamples="";
			}
			if (StringUtils.isBlank(submissionsCreationForm.base64UserFileClonesToAc)){
				submissionsCreationForm.base64UserFileClonesToAc="";
			}
			if (StringUtils.isBlank(submissionsCreationForm.base64UserFileReadSet)){
				submissionsCreationForm.base64UserFileReadSet="";
			}
			Logger.debug("Read base64UserFileExperiments");
			InputStream inputStreamUserFileExperiments = Tools.decodeBase64(submissionsCreationForm.base64UserFileExperiments);
			UserExperimentTypeParser userExperimentsParser = new UserExperimentTypeParser();
			mapUserExperiments = userExperimentsParser.loadMap(inputStreamUserFileExperiments);		
			/*for (Iterator<Entry<String, UserExperimentType>> iterator = mapUserExperiments.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, UserExperimentType> entry = iterator.next();
				System.out.println("  cle de exp = '" + entry.getKey() + "'");
				System.out.println("       nominal_length : '" + entry.getValue().getNominalLength()+  "'");
				System.out.println("       title : '" + entry.getValue().getTitle()+  "'");
				System.out.println("       lib_name : '" + entry.getValue().getLibraryName()+  "'");
				System.out.println("       lib_source : '" + entry.getValue().getLibrarySource()+  "'");
			}*/
			Logger.debug("Read base64UserFileSamples");
			InputStream inputStreamUserFileSamples = Tools.decodeBase64(submissionsCreationForm.base64UserFileSamples);
			UserSampleTypeParser userSamplesParser = new UserSampleTypeParser();
			mapUserSamples = userSamplesParser.loadMap(inputStreamUserFileSamples);		
			for (Iterator<Entry<String, UserSampleType>> iterator = mapUserSamples.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, UserSampleType> entry = iterator.next();
				System.out.println("       title : '" + entry.getValue().getTitle()+  "'");
			}			
			Logger.debug("Read base64UserFileClonesToAc");
			InputStream inputStreamUserFileClonesToAc = Tools.decodeBase64(submissionsCreationForm.base64UserFileClonesToAc);
			UserCloneTypeParser userClonesParser = new UserCloneTypeParser();
			mapUserClones = userClonesParser.loadMap(inputStreamUserFileClonesToAc);		
			System.out.println("\ntaille de la map des userClone = " + mapUserClones.size());
			/*for (Iterator<Entry<String, UserCloneType>> iterator = mapUserClones.entrySet().iterator(); iterator.hasNext();) {
			  Entry<String, UserCloneType> entry = iterator.next();
			  System.out.println("cle du userClone = '" + entry.getKey() + "'");
			  System.out.println("       study_ac : '" + entry.getValue().getStudyAc()+  "'");
			  System.out.println("       sample_ac : '" + entry.getValue().getSampleAc()+  "'");
			}*/
						
			List<String> readSetCodes;
			InputStream inputStreamUserFileReadSet = Tools.decodeBase64(submissionsCreationForm.base64UserFileReadSet);
			Logger.debug("Read base64UserFileReadSet : "+inputStreamUserFileReadSet);
			Tools tools = new Tools();
			// Recuperer readSetCodes à partir du fichier utilisateur :		
			readSetCodes = tools.loadReadSet(inputStreamUserFileReadSet);	
			
			if (readSetCodes.isEmpty()) {
				// Recuperer readSetCodes à partir de la selection de readset des utilisateurs			
				readSetCodes = submissionsCreationForm.readSetCodes; 
				// remplir la liste readSetCodes
				// à partir de la selection de readset des utilisateurs
			}
			
			//String codeReadSet1 = "BCZ_BGOSW_2_H9M6KADXX.IND15"; 
			//String codeReadSet2 = "BCZ_BIOSW_2_H9M6KADXX.IND19"; 

			SubmissionServices submissionServices = new SubmissionServices();
				
			//submissionCode = submissionServices.initNewSubmission(readSetCodes, submissionsCreationForm.studyCode, submissionsCreationForm.configurationCode, mapUserClones, mapUserExperiments, mapUserSamples, contextValidation);
			submissionCode = submissionServices.initPrimarySubmission(readSetCodes, submissionsCreationForm.studyCode, submissionsCreationForm.configurationCode, submissionsCreationForm.acStudy,submissionsCreationForm.acSample, mapUserClones, mapUserExperiments, mapUserSamples, contextValidation);
			if (contextValidation.hasErrors()){
				contextValidation.displayErrors(Logger.of("SRA"));
				return badRequest(filledForm.errorsAsJson());
			}	
			
		} catch (SraException e) {
			contextValidation.addErrors("save submission ", e.getMessage()); // si solution avec ctxVal
			return badRequest(filledForm.errorsAsJson());
		}
		return ok(Json.toJson(submissionCode));
	}

		
	public Result activate(String submissionCode) throws SraException, IOException
	{
		SubmissionServices submissionServices = new SubmissionServices();
		Submission submission = null;

		// affichage des erreurs via messages.addDetails qui passe par 
		// solution filledForm et reject 
		// ou bien solution ctxVal.addErrors
		String user = this.getCurrentUser();
		ContextValidation contextValidation = new ContextValidation(user);
		Form<Submission> filledForm = getFilledForm(submissionForm, Submission.class); 
		//ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); // si solution avec ctxVal
		try {
			submissionServices.activatePrimarySubmission(contextValidation, submissionCode);			
			submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		} catch (SraException e) {
			//return badRequest(Json.toJson(e.getMessage()));
			filledForm.reject("Submission "+submissionCode, e.getMessage());  // si solution filledForm.reject
			//ctxVal.addErrors("Submission "+submissionCode, e.getMessage()); // si solution avec ctxVal
			Logger.debug("filled form "+filledForm.errorsAsJson());
			return badRequest(filledForm.errorsAsJson());
		}
		return ok(Json.toJson(submission));
	}

}

