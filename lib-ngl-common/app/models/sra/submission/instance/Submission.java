package models.sra.submission.instance;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.configuration.instance.Configuration;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

// objet qui decrit ce qu'on soumet à l'EBI à l'instant t.
public class Submission extends DBObject implements IValidation {

	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = CNS_projectCode_date_num
	public String projectCode;     // required pour nos stats //Reference code de la collection project NGL
 	public String accession;       // numeros d'accession attribué par ebi */
	public Date submissionDate;
	public String studyCode;       // liste des codes des study à soumettre à l'ebi
	public List<String> sampleCodes = new ArrayList<String>(); // liste des codes des sample à soumettre à l'ebi
	public List<String> experimentCodes = new ArrayList<String>(); // liste des codes des experiments à soumettre à l'ebi
	public Configuration config = null;
	public String configCode = null;
	public String submissionDirectory;
	public Boolean release = false;
	public String type = null; // SRA ou WGS
	public String xmlStudys = "study.xml"; // nom relatif du fichier xml des studys 
	public String xmlSamples = "sample.xml";
	public String xmlExperiments = "experiment.xml";
	public String xmlRuns = "run.xml";
	public String xmlSubmission = "submission.xml";
	public String resultSendXml = "ResultSendXml.xml"; // Fichier resultat de la commande curl qui doit contenir les AC attribués par l'EBI
	//public String userSubmission; // login du bioinfo qui a creer ticket.
	public State state;// = new State(); // Reference sur "models.laboratory.common.instance.state" 
		// pour gerer les differents etats de l'objet.
		// Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
		// Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit
	public TraceInformation traceInformation;// .Reference sur "models.laboratory.common.instance.TraceInformation" 
		// pour loguer les dernieres modifications utilisateurs

	public Submission(String projectCode, String user) {
		if (configCode != null) {
			this.config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, configCode);
		}
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		if (projectCode != null ) {
			this.submissionDirectory = VariableSRA.submissionRootDirectory + File.separator + projectCode + File.separator + st_my_date;
			this.projectCode = projectCode;
		}
		this.submissionDate = courantDate;
		this.traceInformation = new TraceInformation();
		this.traceInformation.setTraceInformation(user);
	}

	public Submission() {
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		this.submissionDate = courantDate;
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("submission::");
		// verifier que projectCode est bien renseigné et existe dans lims :
		SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
		// verifier que champs contraints presents avec valeurs autorisees:
		ValidationHelper.required(contextValidation, this.submissionDate , "submissionDate");
		ValidationHelper.required(contextValidation, this.submissionDirectory , "submissionDirectory");
		ValidationHelper.required(contextValidation, this.xmlStudys , "xmlStudys");
		ValidationHelper.required(contextValidation, this.xmlSamples , "xmlSamples");
		ValidationHelper.required(contextValidation, this.xmlExperiments , "xmlExperiments");
		ValidationHelper.required(contextValidation, this.xmlRuns , "xmlRuns");
		ValidationHelper.required(contextValidation, this.xmlSubmission , "xmlSubmission");
		//ValidationHelper.required(contextValidation, this.userSubmission , "userSubmission");
		if (this.studyCode == null && this.sampleCodes.size() == 0 &&  this.experimentCodes.size() == 0) {
			contextValidation.addErrors("studyCode, sampleCodes et experimentCodes ::", "Les 3 champs ne peuvent pas etre vides pour une soumission" + "taille des experiments = " +  this.experimentCodes.size() + ", taille des sample = "+ this.sampleCodes.size());
		}
		if (this.config == null) {
			contextValidation.addErrors("config::", "objet qui doit etre renseigné");
		} else {
			// pas de validation, on considere que l'objet a ete recupere valide de la base
			if (!config.state.code.equals("userValidate")){
				contextValidation.addErrors("config::state.code", "'" + config.state.code + "' n'est pas à la valeur attendue 'userValidate'");
			}
		}
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_SUBMISSION_COLL_NAME, contextValidation);
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);		
		contextValidation.removeKeyFromRootKeyName("submission::");
	}

}
