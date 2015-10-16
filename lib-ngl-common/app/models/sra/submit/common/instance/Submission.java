package models.sra.submit.common.instance;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.util.VariableSRA;
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
	public String projectCode = null;     // required pour nos stats //Reference code de la collection project NGL
 	public String accession = null;       // numeros d'accession attribué par ebi */
	public Date submissionDate = null;
	public String refStudyCode = null;    // study referencé par cette soumission, pas forcement à soumettre
	public List<String> refSampleCodes = new ArrayList<String>(); // liste des codes des samples references par cette soumission, pas forcement a soumettre à l'EBI.
	public String studyCode = null;       // study à soumettre à l'ebi
	public String analysisCode = null;       // study à soumettre à l'ebi
	public List<String> sampleCodes = new ArrayList<String>(); // liste des codes des sample à soumettre à l'ebi
	public List<String> experimentCodes = new ArrayList<String>(); // liste des codes des experiments à soumettre à l'ebi
	public List<String> runCodes = new ArrayList<String>(); // liste des codes des runs à soumettre à l'ebi
	public Configuration config = null;
	public String configCode = null;
	public String submissionDirectory = null;
	public Boolean release = false;
	public String type = null; // SRA ou WGS
	public String xmlStudys = null; // nom relatif du fichier xml des studys rempli uniquement si le fichier existe.
	public String xmlSamples = null;
	public String xmlExperiments = null;
	public String xmlRuns = null;
	public String xmlSubmission = null;
	public String xmlAnalysis = null;
	public String resultSendXml = null; // Fichier resultat de la commande curl qui doit contenir les AC attribués par l'EBI
	//public String userSubmission; // login du bioinfo qui a creer ticket.
	
	private Map<String, UserCloneType> mapUserClones = new HashMap<String, UserCloneType>();

	public State state;// = new State(); // Reference sur "models.laboratory.common.instance.state" 
		// pour gerer les differents etats de l'objet.
		// Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
		// Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, submit
	public TraceInformation traceInformation;// .Reference sur "models.laboratory.common.instance.TraceInformation" 
		// pour loguer les dernieres modifications utilisateurs

	public Submission(String projectCode, String user) {
		if (StringUtils.isNotBlank(configCode)){
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
		contextValidation.addKeyToRootKeyName("submission");
		// verifier que projectCode est bien renseigné et existe dans lims :
		SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
		// verifier que champs contraints presents avec valeurs autorisees:
		ValidationHelper.required(contextValidation, this.submissionDate , "submissionDate");
		ValidationHelper.required(contextValidation, this.submissionDirectory , "submissionDirectory");
		// Verifier que status est bien renseigné avec valeurs autorisees.
		SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapStatus, "state.code");
		/*ValidationHelper.required(contextValidation, this.xmlStudys , "xmlStudys");
		ValidationHelper.required(contextValidation, this.xmlSamples , "xmlSamples");
		ValidationHelper.required(contextValidation, this.xmlExperiments , "xmlExperiments");
		ValidationHelper.required(contextValidation, this.xmlRuns , "xmlRuns");
		ValidationHelper.required(contextValidation, this.xmlSubmission , "xmlSubmission");
		ValidationHelper.required(contextValidation, this.resultSendXml , "resultSendXml");
		*/	
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);			
		if (!StringUtils.isNotBlank((CharSequence) contextValidation.getContextObjects().get("type"))){	
			contextValidation.addErrors("submission non evaluable ", "sans type de contexte de validation");
			contextValidation.removeKeyFromRootKeyName("submission");
			return;
		} 
		if (contextValidation.getContextObjects().get("type").equals("sra")) {
			if (this.studyCode == null && this.sampleCodes.size() == 0 &&  this.experimentCodes.size() == 0) {
				contextValidation.addErrors("studyCode, sampleCodes et experimentCodes ::", "Les 3 champs ne peuvent pas etre vides pour une soumission" + "taille des experiments = " +  this.experimentCodes.size() + ", taille des sample = "+ this.sampleCodes.size());
			}
			if (this.config == null) {
				contextValidation.addErrors("config", "objet qui doit etre renseigné");
			} else {
				// pas de validation, on considere que l'objet a ete recupere valide de la base
				if (!config.state.code.equals("userValidate")){
					contextValidation.addErrors("config.state.code", "'" + config.state.code + "' n'est pas à la valeur attendue 'userValidate'");
				}
			}
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SUBMISSION_COLL_NAME, contextValidation);
		} else if (contextValidation.getContextObjects().get("type").equals("wgs")) {
			if (this.studyCode == null || this.analysisCode == null ||this.sampleCodes.size() == 0) {
				contextValidation.addErrors("studyCode, analysisCode et sampleCodes ::", "Les 3 champs doivent etre renseignés pour une soumission WGS" );
			}
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_SUBMISSION_WGS_COLL_NAME, contextValidation);
		} else {
			contextValidation.addErrors("submission non evaluable ", "avec type de contexte de validation " + contextValidation.getContextObjects().get("type"));	
		}
		contextValidation.removeKeyFromRootKeyName("submission");
	}

}
