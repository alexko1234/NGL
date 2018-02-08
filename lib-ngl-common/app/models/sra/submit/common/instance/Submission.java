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

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;
import workflows.sra.submission.TransitionObject;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

// objet qui decrit ce qu'on soumet à l'EBI à l'instant t.
public class Submission extends DBObject implements IValidation, TransitionObject {

	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = CNS_projectCode_date_num
	//public String projectCode = null;     // required pour nos stats //Reference code de la collection project NGL
	public List<String> projectCodes = new ArrayList<String>();
 	public String accession = null;       // numeros d'accession attribué par ebi */
	public Date creationDate = null;
	public Date submissionDate = null;
	public String creationUser = null;
	public List<String> refStudyCodes = new ArrayList<String>();  // Liste de tous les codes des AbstractStudy (ExternalStudy et Study) referencés par cette soumission, pas forcement à soumettre à l'EBI.
	public List<String> refSampleCodes = new ArrayList<String>(); // liste de tous les codes des AbstractSamples (ExternalSample ou Sample) references par cette soumission, pas forcement a soumettre à l'EBI.
	//public List<String> refReadSetCodes = new ArrayList<String>(); // liste des codes des readSet references par cette soumission(pas de soumission).
	public String studyCode = null;          // study à soumettre à l'ebi si strategy_internal_study ou bien study à rendre public
	public String analysisCode = null;       // analysis à soumettre à l'ebi
	public List<String> sampleCodes = new ArrayList<String>(); // liste des codes des sample à soumettre à l'ebi
	public List<String> experimentCodes = new ArrayList<String>(); // liste des codes des experiments à soumettre à l'ebi
	public List<String> runCodes = new ArrayList<String>(); // liste des codes des runs à soumettre à l'ebi
	public String configCode = null;
	public String submissionDirectory = null;
	//public String submissionTmpDirectory = null;
	public Boolean release = false; // si true : soumission pour levée de confidentialite d'un study, 
	                                // si false :  soumission de données (toujours en confidential)
		
	public String xmlStudys = null;  // nom relatif du fichier xml des studys rempli uniquement si le fichier existe.
	public String xmlSamples = null;
	public String xmlExperiments = null;
	public String xmlRuns = null;
	public String xmlSubmission = null;
	public String xmlAnalysis = null;
	public String resultSendXml = null; // Fichier resultat de la commande curl qui doit contenir les AC attribués par l'EBI
	//public String userSubmission; // login du bioinfo qui a creer ticket.
	
	public Map<String, UserCloneType> mapUserClone = new HashMap<String, UserCloneType>();

	public State state = new State(); // Reference sur "models.laboratory.common.instance.state" 
	// pour gerer les differents etats de l'objet en fonction de l'avancement dans le workflow de la soumission
	
	public TraceInformation traceInformation = new TraceInformation();// .Reference sur "models.laboratory.common.instance.TraceInformation" 
	private Object validationDate;
		// pour loguer les dernieres modifications utilisateurs

	public Submission(String user, List<String>projectCodes) {
		//DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		//String st_my_date = dateFormat.format(courantDate);	
		// determination du repertoire de soumission dans methode activate SubmissionServices
		Date courantDate = new java.util.Date();
		this.creationDate = courantDate;
		this.traceInformation = new TraceInformation();
		this.traceInformation.setTraceInformation(user);
		this.creationUser = user;
		for (String projectCode: projectCodes) {
			if (StringUtils.isNotBlank(projectCode)) {
				this.projectCodes.add(projectCode);
			}
		}
	}

	public Submission() {
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		this.creationDate = courantDate;
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("submission");
		
		if(contextValidation.isUpdateMode()){
			ValidationHelper.required(contextValidation, this.creationUser, "creationUser");
		}
		
		// verifier que projectCode est bien renseigné et existe dans lims :
		SraValidationHelper.validateProjectCodes(this.projectCodes, contextValidation);

		// Verifier que status est bien renseigné avec valeurs autorisees et que submissionDirectory est bien renseigné une
		// fois que l'objet est en status "inWaiting" (etape activate de la soumission)
		/*if(SraValidationHelper.requiredAndConstraint(contextValidation, this.state.code , VariableSRA.mapStatus, "state.code")){
			if(this.state.code.equalsIgnoreCase("inwaiting") 
					||this.state.code.equalsIgnoreCase("inprogress")
					|| this.state.code.equalsIgnoreCase("submitted")) {
				ValidationHelper.required(contextValidation, this.	submissionDirectory , "submissionDirectory");
				ValidationHelper.required(contextValidation, this.submissionDate , "submissionDate");
			}
		}*/
		SraValidationHelper.validateState(ObjectType.CODE.SRASubmission, this.state, contextValidation);
		if (StringUtils.isNotBlank(this.state.code)){
			if(this.state.code.equalsIgnoreCase("IW-SUB") 
					||this.state.code.equalsIgnoreCase("IW-SUB-R")
					||this.state.code.equalsIgnoreCase("IP-SUB")
					||this.state.code.equalsIgnoreCase("IP-SUB-R")
					|| this.state.code.equalsIgnoreCase("F-SUB")) {
				ValidationHelper.required(contextValidation, this.submissionDirectory , "submissionDirectory");
				ValidationHelper.required(contextValidation, this.creationDate , "creationDate");
				ValidationHelper.required(contextValidation, this.validationDate , "validationDate");
			}
		}
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
		
		// Dans le cas d'une soumission pour une release, on n'applique pas le reste des validations
		if (this.release){
			return;
		}
		
		if (StringUtils.isBlank(this.configCode)){
			contextValidation.addErrors("submission non evaluable ", "sans configCode dans la soumission '" + this.code + "'");
			contextValidation.removeKeyFromRootKeyName("submission");
			return;
		} 
		Configuration config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, this.configCode);

		if (contextValidation.getContextObjects().get("type").equals("sra")) {
			if (config == null) {
				contextValidation.addErrors("submission.configCode", "objet configuration '" + this.configCode + "' qui n'existe pas dans base");
			} else { 
				// pas de validation integrale de config qui a ete stocke dans la base donc valide mais verification
				// de quelques contraintes en lien avec soumission.
				if (StringUtils.isBlank(config.state.code)){
					contextValidation.addErrors("config.state.code", "'" + config.state.code + "' n'est pas à la valeur attendue 'used'");
				}
				
				if (StringUtils.isBlank(config.strategyStudy)||StringUtils.isBlank(config.strategySample)){
					
				}
				if (StringUtils.isBlank(config.strategyStudy)){
					contextValidation.addErrors("strategy_study", "champs qui doit etre renseigne dans la configuration passee dans le contexte de validation");
				} else if (config.strategyStudy.equalsIgnoreCase("strategy_internal_study")) {
					// Le champs studyCode est rempli ssi le study est à soumettre (state = N)
					/*if (StringUtils.isBlank(this.studyCode)) {
						contextValidation.addErrors("strategy_study", "strategy_internal_study incompatible avec studyCode vide");
					}*/
				} else if (config.strategyStudy.equalsIgnoreCase("strategy_external_study")) {
					if (StringUtils.isNotBlank(this.studyCode)){
						contextValidation.addErrors("strategy_study", "strategy_external_study incompatible avec studyCode renseigne : '" + this.studyCode +"'");
					}
					// On peut avoir donné un seul studyAc externe pour toute la soumission via l'interface
					/*if (this.mapUserClone == null || this.mapUserClone.isEmpty()){
						contextValidation.addErrors("strategy_study", "strategy_external_study incompatible avec mapUserClone non renseigné");
					}*/
				} else {
					contextValidation.addErrors("strategy_study", "valeur non attendue '" + config.strategyStudy + "'");
				}
				if (StringUtils.isBlank(config.strategySample)){
					contextValidation.addErrors("strategy_sample", "champs qui doit etre renseigne dans la configuration passee dans le contexte de validation");
				} else {
					if(config.strategySample.equalsIgnoreCase("strategy_external_sample")) {
						if (this.sampleCodes.size() != 0) {
							contextValidation.addErrors("strategy_external_sample incompatible avec samples à soumettre : ", "taille sampleCode = "  + this.sampleCodes.size());
						}
						// On peut avoir donné un seul sampleAc externe pour toute la soumission via l'interface
						/*if (this.mapUserClone == null || this.mapUserClone.isEmpty()){
							contextValidation.addErrors("strategy_sample", "strategy_external_sample incompatible avec mapUserClone non renseigné");
						}*/
					}
				}
			}
		
			if (StringUtils.isBlank(this.studyCode) && this.sampleCodes.size() == 0 &&  this.experimentCodes.size() == 0) {
				contextValidation.addErrors("studyCode, sampleCodes et experimentCodes ::", "Les 3 champs ne peuvent pas etre vides pour une soumission" + "taille des experiments = " +  this.experimentCodes.size() + ", taille des sample = "+ this.sampleCodes.size());
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
	
	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public TraceInformation getTraceInformation() {
		return traceInformation;
	}

}
