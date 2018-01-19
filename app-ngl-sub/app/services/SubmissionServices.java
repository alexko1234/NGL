package services;

import static fr.cea.ig.play.IGGlobals.ws;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//import play.Logger;
import validation.ContextValidation;
import workflows.sra.submission.ConfigurationWorkflows;
import workflows.sra.submission.SubmissionWorkflows;
import workflows.sra.submission.SubmissionWorkflowsHelper;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.AbstractSample;
import models.sra.submit.common.instance.AbstractStudy;
import models.sra.submit.common.instance.ExternalSample;
import models.sra.submit.common.instance.ExternalStudy;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.common.instance.UserCloneType;
import models.sra.submit.common.instance.UserExperimentType;
import models.sra.submit.common.instance.UserSampleType;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.sra.submit.sra.instance.ReadSpec;
import models.sra.submit.sra.instance.Run;
import models.sra.submit.util.SraCodeHelper;
import models.sra.submit.util.SraException;
import models.sra.submit.util.SraParameter;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import fr.cea.ig.MongoDBDAO;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.StringReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// import play.api.modules.spring.Spring;
// import play.libs.F.Promise;
// import play.libs.ws.WS;
import play.libs.ws.WSResponse;
//import specs2.run;



public class SubmissionServices {
	
	private static final play.Logger.ALogger logger = play.Logger.of(SubmissionServices.class);

	// final ConfigurationWorkflows configWorkflows  = Spring.get BeanOfType(ConfigurationWorkflows.class);
	// final SubmissionWorkflows submissionWorkflows = Spring.get BeanOfType(SubmissionWorkflows.class);
	// SubmissionWorkflowsHelper submissionWorkflowsHelper;

	private final ConfigurationWorkflows    configWorkflows;
	private final SubmissionWorkflows       submissionWorkflows;
	private final SubmissionWorkflowsHelper submissionWorkflowsHelper;
	
	@Inject
	public SubmissionServices(ConfigurationWorkflows configWorkflows, 
			                  SubmissionWorkflows submissionWorkflows, 
			                  SubmissionWorkflowsHelper submissionWorkflowsHelper) {
		this.configWorkflows           = configWorkflows;
		this.submissionWorkflows       = submissionWorkflows;
		this.submissionWorkflowsHelper = submissionWorkflowsHelper;
	}
	
	public String updateLaboratorySampleForNcbiScientificName(String taxonCode, ContextValidation contextValidation) throws SraException {		
		try {
			String scientificName = getNcbiScientificName(new Integer(taxonCode));
			// Met a jour dans la base les laboratorySample qui n'ont pas encore de ncbiScientificName.
			// Pas de gestion des incoherences de mise à jour !!!
			MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,
					DBQuery.is("taxonCode", taxonCode).notExists("ncbiScientificName"),
					DBUpdate.set("ncbiScientificName", scientificName).set("traceInformation.modifyUser", contextValidation.getUser()).set("traceInformation.modifyDate", new Date()));
			return scientificName;
			
		} catch (XPathExpressionException | IOException | InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			throw new SraException("impossible de recuperer le scientificName au ncbi pour le taxonId '" + taxonCode + "' : \n" + e.getMessage());
		}
	}
	
	public String initReleaseSubmission(String studyCode, ContextValidation contextValidation) throws SraException {
		System.out.println("Dans SubmissionServices.java.initReleaseSubmission");
		String user = contextValidation.getUser();
		Study study = null;
		if (StringUtils.isNotBlank(studyCode)) {
			study = MongoDBDAO.findOne(InstanceConstants.SRA_STUDY_COLL_NAME,
				Study.class, DBQuery.and(DBQuery.is("code", studyCode)));
		} 
		if (study == null){
			throw new SraException("SubmissionServices::initReleaseSubmission::impossible de recuperer le study '" + studyCode +"' dans la base");
		} 
		if ( ! study.state.code.equals("F-SUB")) {
			throw new SraException("Study " + study.code + " avec status incompatible avec demande de release " + study.state.code );
		} 
		
		if ( study.releaseDate == null){
			throw new SraException("Study " + study.code + " non renseigné dans base pour date de release" );
		}
		if (study.releaseDate.before(new Date())){
			throw new SraException("release date du Study " + study.releaseDate + " inferieure à date du jour: (" + new Date()+") => Le study doit deja etre public à l'EBI");
		}
		
		Submission submission = createSubmissionEntityforRelease(study, user, study.projectCodes);
		//System.out.println("AVANT submission.validate="+contextValidation.errors);

		
		// updater dans base si besoin le study pour le statut 'N-R' 
		if (study != null && StringUtils.isNotBlank(submission.studyCode)){
			study.state.code = "IW-SUB-R";
			study.traceInformation.modifyDate = new Date();
			study.traceInformation.modifyUser = user;
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
					DBQuery.is("code", study.code),
					DBUpdate.set("state.code", study.state.code).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date()));	
			}
		
		// puis valider et sauver submission
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		
		System.out.println("AVANT submission.validate="+contextValidation.errors);

		submission.validate(contextValidation);
		System.out.println("APRES submission.validate="+contextValidation.errors);
		
		if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code",submission.code)){	
			submission.validate(contextValidation);
			MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
			System.out.println ("sauvegarde dans la base du submission " + submission.code);
		}
		
		// equivalent de activate :
		
		File dirSubmission = createDirSubmission(submission);
		// Avancer le status de submission et study à IW-SUB-R
		State state = new State("IW-SUB-R", user);
		submissionWorkflows.setState(contextValidation, submission, state);
		if (contextValidation.hasErrors()){
			System.out.println("submission.validate produit des erreurs");
			// destruction de la submission et rallback pour etat du study: 
			submissionWorkflowsHelper.rollbackSubmission(submission, contextValidation);	
			contextValidation.displayErrors(logger);
			throw new SraException("SubmissionServices::initReleaseSubmission::probleme validation  voir log: ");
		} else {	
			// updater la soumission dans la base pour le repertoire de soumission (la date de soumission sera mise à la reception des AC)
			MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME,  Submission.class, 
			DBQuery.is("code", submission.code),
			DBUpdate.set("submissionDirectory", submission.submissionDirectory));
		}
		return submission.code;
	}
	
	private Submission createSubmissionEntityforRelease(Study study, String user, List<String> projectCodes) throws SraException{
		
		Submission submission = null;
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		submission = new Submission(user, projectCodes);
		submission.code = SraCodeHelper.getInstance().generateSubmissionCode(projectCodes);
		submission.creationDate = courantDate;
		System.out.println("submissionCode="+ submission.code);

		submission.release = true;
		
		
		if (study != null){	
			System.out.println("study != null");

			// mettre à jour l'objet submission pour le study  :
			if (! submission.refStudyCodes.contains("study.code")){
				submission.refStudyCodes.add(study.code);
				System.out.println("ajout de studyCode dans submission.refStudyCode");

			}
			submission.studyCode = study.code;
			System.out.println("ajout de studyCode dans submission.studyCode");

			if ( ! study.state.code.equals("F-SUB")) {
				throw new SraException("Study " + study.code + " avec status incompatible avec demande de release " + study.state.code );
			} 
			
			if ( study.releaseDate == null){
				throw new SraException("Study " + study.code + " non renseigné dans base pour date de release" );
			}
			if (study.releaseDate.before(new Date())){
				throw new SraException("release date du Study " + study.releaseDate + " inferieure à date du jour: (" + new Date()+") => Le study doit deja etre public à l'EBI");
			}
			System.out.println("fin de study != null");
		}
		System.out.println("submissionCode="+ submission.code);

		submission.state = new State("N-R", user);
		System.out.println("en sortie submissionCode="+ submission.code);

		return submission;
	}
	
	
	public String initPrimarySubmission(List<String> readSetCodes, String studyCode, String configCode, String acStudy, String acSample, Map<String, UserCloneType>mapUserClones, Map<String, UserExperimentType> mapUserExperiments, Map< String, UserSampleType> mapUserSamples, ContextValidation contextValidation) throws SraException, IOException {
		//public String initNewSubmission(List<String> readSetCodes, String studyCode, String configCode, Map<String, UserCloneType>mapUserClones, Map<String, UserExperimentType> mapUserExperiments, ContextValidation contextValidation) throws SraException, IOException {
		// Cree en base un objet submission avec state.code=N, met dans la base la configuration avec state.code='U-SUB'
		// met les readSet avec state.code = 'N', les experiments avec state.code='N', les samples à soumettre 
		// avec state.code='N' sinon laisse les samples dans leurs state, et met le study à soumettre (study avec state.code='N') avec state.code=V-SUB 

		// Pour simplifier workflow on n'autorise pas a la creation d'une soumission, l'utilisation d'un sample crée par une autre soumission
		// mais toujours dans le processus de soumission. Idem pour les study. Si la creation d'une soumission utilise un study ou un sample deja existant
		// dans la base, alors le state.code doit etre à F-SUB.
		// Pour une premiere soumission d'un readSet, on peut devoir utiliser un study ou un sample existant, deja soumis à l'EBI.
		// En revanche on ne doit pas utiliser un experiment ou un run existant
		
		// Verifier config et initialiser objet submission avec release a false(private) state.code='N'et 
		// pas de validation integrale de config qui a ete stocke dans la base donc valide mais verification
		// de quelques contraintes en lien avec soumission.
		

		System.out.println("\ntaille de la map des userClone dans init = " + mapUserClones.size());
		
		
		String user = contextValidation.getUser();
		
		System.out.println("Dans init : acStudy = " + acStudy + " et acSample= " + acSample);
		
		if (StringUtils.isBlank(configCode)) {
			throw new SraException("la configuration a un code à null");
		}
		Configuration config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, configCode);
		
		Study study = null;
		ExternalStudy uniqExternalStudy = null;
		
		if (config == null) {
			throw new SraException("Configuration " + configCode + " n'existe pas dans database");
		} 
		if (StringUtils.isBlank(config.state.code)) {
			throw new SraException("Configuration " + config.code + " avec champs state.code non renseigne");
		}
		System.out.println("dans init, config.state.code='"+config.state.code+"'");
		
		if (! "N".equalsIgnoreCase(config.state.code) && ! "U-SUB".equalsIgnoreCase(config.state.code)) {
			throw new SraException("Configuration " + config.code + " avec state.code = '"+ config.state.code+"'");
		}
		if (config.strategyStudy.equalsIgnoreCase("strategy_internal_study")) {
			if (StringUtils.isNotBlank(acStudy)) {
				// Recuperer le study si acStudy renseigné et strategy_internal_study :
				if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "accession", acStudy)){
					study = MongoDBDAO.findOne(InstanceConstants.SRA_STUDY_COLL_NAME,
							Study.class, DBQuery.and(DBQuery.is("accession", acStudy)));
				} else {
					throw new SraException("strategy_internal_study et acStudy passé en parametre "+ acStudy + "n'existe pas dans database");	
				}	
			} else {
				if (StringUtils.isNotBlank(studyCode)) {
					// Recuperer le study si studyCode renseigné et strategy_internal_study :
					if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", studyCode)){
						study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, studyCode);			
					} else {
						throw new SraException("strategy_internal_study et studyCode passé en parametre "+ studyCode + "n'existe pas dans database");	
					}	
				}
			}
		} else if (config.strategyStudy.equalsIgnoreCase("strategy_external_study")) {
			if (StringUtils.isNotBlank(acStudy)) {
				uniqExternalStudy = fetchExternalStudy(acStudy, user);
			} else {
				if (StringUtils.isNotBlank(studyCode)){
					throw new SraException("Configuration " + config.code + " avec strategy_external_study incompatible avec studyCode renseigne : '" + studyCode +"'");
				}
				if (mapUserClones == null || mapUserClones.isEmpty()){
					throw new SraException("Configuration " + config.code + " avec strategy_study = 'strategy_external_study' incompatible avec acStudy et mapUserClone non renseignés");
				}
			}
		} else {
			throw new SraException("Configuration " + config.code + " avec strategy_study = '"+ config.strategyStudy + "' (valeur non attendue)");
		}
		
		ExternalSample uniqExternalSample = null;
		Sample uniqSample = null;

		if (config.strategySample.equalsIgnoreCase("strategy_external_sample")) {
			//System.out.println ("Dans init, cas strategy_external_sample");
			if (StringUtils.isNotBlank(acSample)) {
				uniqExternalSample = fetchExternalSample(acSample, user);
			} else {
				if (mapUserClones== null || mapUserClones.isEmpty()){
					throw new SraException("Configuration " + config.code + "avec configuration.strategy_sample='strategy_external_sample' incompatible avec acSample nul et mapUserClone non renseigné");
				}
			}
		} 
		
		if (config.strategySample.equalsIgnoreCase("strategy_internal_sample")) {
			if (StringUtils.isNotBlank(acSample)) {
				//System.out.println ("Dans init, cas strategy_internal_sample");
				uniqSample = MongoDBDAO.findOne(InstanceConstants.SRA_SAMPLE_COLL_NAME,
						Sample.class, DBQuery.and(DBQuery.is("accession", acSample)));
			} /*else {
				if (mapUserClones== null || mapUserClones.isEmpty()){
					throw new SraException("Configuration " + config.code + "avec configuration.strategy_sample='strategy_external_sample' incompatible avec mapUserClone non renseigné");
				}
			}*/
		} 
		

		// Verifier que tous les readSetCode passes en parametres correspondent bien a des objets en base avec
		// submissionState='NONE' cad des readSets qui n'interviennent dans aucune soumission.
		List <ReadSet> readSets = new ArrayList<ReadSet>();
		for (String readSetCode : readSetCodes) {
			if (StringUtils.isNotBlank(readSetCode)) {
				//System.out.println("!!!!!!!!!!!!!         readSetCode = " + readSetCode);
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
				if (readSet == null) {
					throw new SraException("Le readSet " + readSetCode + " n'existe pas dans database");
				} else if ((readSet.submissionState == null)||(StringUtils.isBlank(readSet.submissionState.code))) {
					throw new SraException("Le readSet " + readSet.code + " n'a pas de submissionState.code");
				} else if(!readSet.submissionState.code.equalsIgnoreCase("NONE")){
					throw new SraException("Le readSet " + readSet.code + " a un submissionState.code à la valeur " + readSet.submissionState.code);
				} else {
					readSets.add(readSet);
				}
			}
		}	
		
		// Renvoie un objet submission avec state.code='N'
		// et submission.studyCode renseigné (study à envoyer à l'EBI) si config.strategyStudy=strategy_internal_study et studyCode.state.code="N"
		// submission.release = false si config.strategyStudy == strategy_external_study
		// submission.release = studyCode.release si config.strategyStudy == strategy_internal_study
		//String user = contextValidation.getUser();
		Submission submission = createSubmissionEntity(config, studyCode, acStudy, user);
				
		// Liste des sous-objets utilisés dans submission
		List <Experiment> listExperiments = new ArrayList<Experiment>(); // liste des experiments utilisés et crees dans soumission avec state.code='N' à sauver dans database
		List <AbstractSample> listAbstractSamples = new ArrayList<AbstractSample>();//liste des AbstractSample(Sample ou ExternalSample) avec state.code='F-SUB' ou 'N' utilises dans soumission à sauver ou non dans database		
		List <AbstractStudy> listAbstractStudies = new ArrayList<AbstractStudy>();//liste des AbstractStudy(Study ou ExternalStudy) avec state.code='F-SUB' ou 'N' utilises dans soumission à sauver ou non dans database		

		int countError = 0;
		String errorMessage = "";
	
		for(ReadSet readSet : readSets) {
			System.out.println("readSet :" + readSet.code);
			// Verifier que c'est une premiere soumission pour ce readSet
			// Verifiez qu'il n'existe pas d'objet experiment referencant deja ce readSet
			Boolean alreadySubmit = MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "readSetCode", readSet.code);		
			if ( alreadySubmit ) {
				// signaler erreur et passer au readSet suivant
				countError++;
				errorMessage = errorMessage + "  - Soumission deja existante dans base pour : '" + readSet.code + "' \n";
				// Recuperer exp dans mongo
				continue;
			} /*else {
				System.out.println("Aucun experiment ne reference le readSet " + readSet.code);
			}*/
					
			// Verifier que ce readSet est bien valide avant soumission :
			if (! readSet.bioinformaticValuation.valid.equals(TBoolean.TRUE)) {
				countError++;
				errorMessage = errorMessage + "  - Soumission impossible pour le readset '" + readSet.code + "' parceque non valide pour la bioinformatique \n";
				continue;
			}	else {
				System.out.println("Le readset est bien valide :" + readSet.code);
			}
			
			// Recuperer scientificName via NCBI pour ce readSet. Le scientificName est utilisé dans la construction
			// des samples et des experiments 
			String laboratorySampleCode = readSet.sampleCode;
			models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
			String taxonId = laboratorySample.taxonCode;
			String scientificName = laboratorySample.ncbiScientificName;
			if (StringUtils.isBlank(scientificName)){
				//scientificName=updateLaboratorySampleForNcbiScientificName(taxonId, contextValidation);
				throw new SraException("Pas de recuperation du nom scientifique pour le sample "+ laboratorySampleCode);
			} else {
				System.out.println("Nom du scientific Name = "+ laboratorySample.ncbiScientificName);
			}
			// Creer les objets avec leurs alias ou code, les instancier completement et les sauver.
			
			// Creer l'experiment avec un state.code = 'N'
			//System.out.println("scientificName =" + scientificName);
			//System.out.println("librarySelection = " + config.librarySelection);
			//System.out.println("librarySource = " + config.librarySource);
			//System.out.println("libraryStrategy = "+config.libraryStrategy);
			//System.out.println("libraryConstructionProtocol = "+ config.libraryConstructionProtocol);
			
			Experiment experiment = createExperimentEntity(readSet, scientificName, user);
			experiment.librarySelection = config.librarySelection;
			experiment.librarySource = config.librarySource;
			experiment.libraryStrategy = config.libraryStrategy;
			experiment.libraryConstructionProtocol = config.libraryConstructionProtocol;
			
			//System.out.println("scientificName =" + scientificName);
			//System.out.println("librarySelection = " + config.librarySelection);
			//System.out.println("librarySource = " + config.librarySource);
			//System.out.println("libraryStrategy = "+config.libraryStrategy);
			//System.out.println("libraryConstructionProtocol = "+ config.libraryConstructionProtocol);
			
			//String laboratorySampleName = laboratorySample.name;
			String clone = laboratorySample.referenceCollab;
			/*for (Iterator<Entry<String, UserCloneType>> iterator = mapUserClones.entrySet().iterator(); iterator.hasNext();) {
				  Entry<String, UserCloneType> entry = iterator.next();
				  if (entry.getKey().equals(clone)) {
					  System.out.println("dans initPrimarySubmission 1 : cle du userClone = '" + entry.getKey() + "'");
					  System.out.println("       study_ac : '" + entry.getValue().getStudyAc()+  "'");
					  System.out.println("       sample_ac : '" + entry.getValue().getSampleAc()+  "'");
				  }	
			}*/
			//System.out.println("name = '" + laboratorySample.name +"'");
			//System.out.println("clone = '" + clone +"'");
			// Creer le sample si besoin et ajouter dans submission.mapUserClone
			if (config.strategySample.equalsIgnoreCase("strategy_external_sample")){
				//System.out.println ("Dans init, cas strategy_external_sample");

				ExternalSample externalSample = null;
				if (mapUserClones!=null && !mapUserClones.isEmpty()){
					if (StringUtils.isBlank(clone)) {
						countError++;
						errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque strategy_external_sample avec mapUserClone et pas de nom de clone dans ngl \n";
						continue;					
					}
					if (! mapUserClones.containsKey(clone)){
						countError++;
						errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque strategy_external_sample avec mapUserClone qui ne contient pas le clone '" + clone +"' \n";
						continue;
					}
					String sampleAc = mapUserClones.get(clone).getSampleAc();
					//System.out.println("Pour le clone "+ clone + " sample = " + sampleAc);
					
					if (StringUtils.isBlank(sampleAc)){
						countError++;
						errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque mapUserClone pour clone '" + clone + "' ne contient pas de sampleAc";
						continue;					
					}
					// recuperation dans base ou création du sample avec status F-SUB (si AC alors F-sub)
					externalSample = fetchExternalSample(sampleAc, user);
					//System.out.println("recuperation dans base du sample " +externalSample.accession);
					//System.out.println("recuperation dans base du sample " +externalSample.code);
					// Mise a jour de l'objet submission pour mapUserClone :
					UserCloneType submission_userClone = new UserCloneType();
					//System.out.println("alias="+mapUserClones.get(clone).getAlias());
					//System.out.println("sampleAC="+mapUserClones.get(clone).getSampleAc());
					//System.out.println("studyAC="+mapUserClones.get(clone).getStudyAc());
					submission_userClone.setAlias(mapUserClones.get(clone).getAlias());
					submission_userClone.setSampleAc(mapUserClones.get(clone).getSampleAc());
				    submission_userClone.setStudyAc(mapUserClones.get(clone).getStudyAc());
				    submission.mapUserClone.put(submission_userClone.getAlias(), submission_userClone);
				    
				} else if (StringUtils.isNotBlank(acSample)) {
					externalSample = fetchExternalSample(acSample, user);
					
				} else {
					// bug
				}
				
				// Mise a jour de l'objet submission pour les samples references
				//System.out.println("Mise a jour de l'objet submission pour les samples references");
				if(!submission.refSampleCodes.contains(externalSample.code)){
					submission.refSampleCodes.add(externalSample.code);
				}
				
				// Pas de mise a jour de l'objet submission pour les samples à soumettre.
				
				//System.out.println("Mise a jour de listAbstractSamples pour les samples references");

				if(!listAbstractSamples.contains(externalSample)){
					listAbstractSamples.add(externalSample);
				}
				//System.out.println("Mise a jour de experiment pour les samples references");

				// mettre à jour l'experiment pour la reference sample :
				experiment.sampleCode = externalSample.code;
				experiment.sampleAccession = externalSample.accession;
				
			} else { // strategie internal_sample
				System.out.println ("Dans init, cas strategy_internal_sample");

				Sample sample= null;

				if (StringUtils.isNotBlank(acSample)){
					sample = MongoDBDAO.findOne(InstanceConstants.SRA_SAMPLE_COLL_NAME,
							Sample.class, DBQuery.and(DBQuery.is("accession", acSample)));
					if (sample == null){
						// bug
					}
					
				} else {
				
					// Recuperer le sample existant avec son state.code ou bien en creer un nouveau avec state.code='N'
					sample = fetchSample(readSet, config.strategySample, scientificName, user);
					// Renseigner l'objet submission :
					// Verifier que l'objet sample n'a jamais ete soumis et n'est pas en cours de soumission
					System.out.println("sample = " + sample + " et state="+ sample.state.code);
					if (!("F-SUB").equalsIgnoreCase(sample.state.code) && !("N").equalsIgnoreCase(sample.state.code)) {
						throw new SraException("Tentative d'utilisation dans la soumission du sample "+ sample.code +" en cours de soumission avec state.code==" + sample.state.code);
					}
					// surcharger le sample si besoin pour les champs autorises :
					//----------------------------------------------------------
					UserSampleType userSample = mapUserSamples.get(sample.code);
					if (userSample != null) {				
						if (StringUtils.isNotBlank(userSample.getAnonymizedName())){
							sample.anonymizedName = userSample.getAnonymizedName();
						}
						if (StringUtils.isNotBlank(userSample.getTitle())){
							sample.title = userSample.getTitle();
						}
						if (StringUtils.isNotBlank(userSample.getDescription())){
						sample.description = userSample.getDescription();
						}
						if (StringUtils.isNotBlank(userSample.getCommonName())){
							sample.commonName = userSample.getCommonName();
						}	
						// Le champs scientificName est rempli automatiquement et n'est pas surchargeable.
					}
				
}
				// Mise a jour de l'objet submission pour les samples references
				if(!submission.refSampleCodes.contains(sample.code)){
					submission.refSampleCodes.add(sample.code);
				}
				// Mise a jour de l'objet submission pour les samples à soumettre :
				//------------------------------------------------------------------
				if (("N").equalsIgnoreCase(sample.state.code)){
					if (! submission.sampleCodes.contains(sample.code)){
						submission.sampleCodes.add(sample.code);
					}
				}
				if(!listAbstractSamples.contains(sample)){
					listAbstractSamples.add(sample);
				}
				// mettre à jour l'experiment pour la reference sample :
				experiment.sampleCode = sample.code;
				experiment.sampleAccession = sample.accession;
			
			}
			
			
			if (config.strategyStudy.equalsIgnoreCase("strategy_external_study")){
				//System.out.println ("Dans init, cas strategy_external_study");

				ExternalStudy externalStudy = null;
				
				if (mapUserClones!=null && !mapUserClones.isEmpty()){
					
					if (! mapUserClones.containsKey(clone)){
						countError++;
						errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque strategy_external_study et mapUserClone ne contient pas le clone '" + clone +"' \n";
						continue;
					} else {
						
						//System.out.println("alias="+mapUserClones.get(clone).getAlias());
						//System.out.println("sampleAC="+mapUserClones.get(clone).getSampleAc());
						//System.out.println("studyAC="+mapUserClones.get(clone).getStudyAc());
						// mettre a jour submission.mapUserClone :
						UserCloneType submission_userClone = new UserCloneType();
						submission_userClone.setAlias(mapUserClones.get(clone).getAlias());
						submission_userClone.setSampleAc(mapUserClones.get(clone).getSampleAc());
						submission_userClone.setStudyAc(mapUserClones.get(clone).getStudyAc());
						submission.mapUserClone.put(submission_userClone.getAlias(), submission_userClone);
					}
					
					String studyAc = mapUserClones.get(clone).getStudyAc();
					if (StringUtils.isBlank(studyAc)){
						countError++;
						errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque strategy_external_study et mapUserClone.get(clone).getStudyAc() non renseigne pour le clone '" + clone + "'\n";
						continue;					
					}
					
					// creation ou recuperation dans base de externalStudy avec state.code = F-SUB
					externalStudy = fetchExternalStudy(studyAc, user);
				} else if (StringUtils.isNotBlank(acStudy)) {
					externalStudy = fetchExternalStudy(acStudy, user);
				} 
				// Mise à jour de l'objet submission pour les study references :
				//System.out.println("Mise à jour de l'objet submission pour les study references :");
				if(!submission.refStudyCodes.contains(externalStudy.code)){
					submission.refStudyCodes.add(externalStudy.code);
				}
				//System.out.println("Mise à jour de listAbstractStudies pour les study references :");
				if(!listAbstractStudies.contains(externalStudy.code)){
					listAbstractStudies.add(externalStudy);
				}	
				// mettre à jour l'experiment pour la reference study :
				//System.out.println("Mise à jour de experiment pour les study references :");
				experiment.studyCode = externalStudy.code;	
				experiment.studyAccession = externalStudy.accession;	
					
			} else { // strategie internal_study
				if (StringUtils.isNotBlank(acStudy)) {
					study = MongoDBDAO.findOne(InstanceConstants.SRA_STUDY_COLL_NAME,
							Study.class, DBQuery.and(DBQuery.is("accession", acStudy)));
				} else if (StringUtils.isNotBlank(studyCode)) {
					study = MongoDBDAO.findOne(InstanceConstants.SRA_STUDY_COLL_NAME,
							Study.class, DBQuery.and(DBQuery.is("code", studyCode)));
				} else {
					// bug
				}
				// Mise à jour de l'objet submission pour les study references :
				if(!submission.refStudyCodes.contains(study.code)){
					submission.refStudyCodes.add(study.code);
				}
				if(!listAbstractStudies.contains(study)){
					listAbstractStudies.add(study);
				}	
				// mettre à jour l'experiment pour la reference study :
				experiment.studyCode = study.code;
				experiment.studyAccession = study.accession;
				
				
				// Mise a jour de l'objet submission pour le study à soumettre :// normalement deja fait dans createSubmissionEntity
				//--------------------------------------------------------------
				if (StringUtils.isBlank(acStudy) && ("N").equalsIgnoreCase(study.state.code)) {
					submission.studyCode = study.code;
				}
			}
			
			// surcharger l'experiment avec valeurs de l'utilisateur si mapUserExperiments exist
			
//			UserExperimentType userExperiment = mapUserExperiments.get(experiment.code);
//			if (userExperiment != null) {
//								
//				if (StringUtils.isNotBlank(userExperiment.getLibrarySource())){
//					experiment.librarySource = userExperiment.getLibrarySource();
//				}
//				if (StringUtils.isNotBlank(userExperiment.getLibraryStrategy())){
//					experiment.libraryStrategy = userExperiment.getLibraryStrategy();
//				}
//				if (StringUtils.isNotBlank(userExperiment.getLibrarySelection())){
//					experiment.librarySelection = userExperiment.getLibrarySelection();
//				}
//				if (StringUtils.isNotBlank(userExperiment.getLibraryProtocol())){
//					experiment.libraryConstructionProtocol = userExperiment.getLibraryProtocol();
//				}
//				if (StringUtils.isNotBlank(userExperiment.getLibraryName())){
//					experiment.libraryName = userExperiment.getLibraryName();
//				}
//				if (StringUtils.isNotBlank(userExperiment.getNominalLength())){
//					experiment.libraryLayoutNominalLength = new Integer(userExperiment.getNominalLength());
//				}
//				if (StringUtils.isNotBlank(userExperiment.getTitle())){
//					experiment.title = userExperiment.getTitle();
//				}
//			}
//					
			
			// Ajouter l'experiment avec le statut forcement à 'N' à l'objet submission :
			// Mise à jour de l'objet submission avec les experiments à soumettre :
			//---------------------------------------------------------------------
			if (experiment.state.code.equalsIgnoreCase("N")) { 
				if(!submission.experimentCodes.contains(experiment.code)){
					listExperiments.add(experiment);
					submission.experimentCodes.add(experiment.code);
					//System.out.println ("Ajout dans submission du expCode : " + experiment.code);
					if(experiment.run != null) {
						System.out.println ("Ajout dans submission du runCode TOROTOTO: " + experiment.run.code);
						submission.runCodes.add(experiment.run.code);
					}// end if
				}// end if
			}// end if
		} // end for readset
		
		if (countError != 0){
			throw new SraException("Problemes pour creer la soumission \n" + errorMessage);
		}

		contextValidation.setCreationMode();
			
		// On ne sauvent que les study qui n'existent pas dans la base donc des ExternalStudy avec state.code='F-SUB'	
		for (AbstractStudy absStudyElt: listAbstractStudies) {
			if (! MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, AbstractStudy.class, "code", absStudyElt.code)){	
				absStudyElt.validate(contextValidation);
				MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, absStudyElt);
				System.out.println ("ok pour sauvegarde dans la base du study " + absStudyElt.code);
			}
		}	

		// On ne sauvent que les samples qui n'existent pas dans la base donc des Samples avec state.code='N'
		// ou bien des ExternalSample avec state.code='F-SUB'
		for (AbstractSample sampleElt: listAbstractSamples) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, AbstractSample.class, "code", sampleElt.code)){	
				sampleElt.validate(contextValidation);
				MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sampleElt);
				System.out.println ("ok pour sauvegarde dans la base du sample " + sampleElt.code);
			}
		}
		// On ne sauvent que les experiment qui n'existent pas dans la base donc des experiment avec state.code='F-SUB'	
		for (Experiment expElt: listExperiments) {
			expElt.validate(contextValidation);
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "code", expElt.code)){	
				MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, expElt);
				System.out.println ("sauvegarde dans la base de l'experiment " + expElt.code);
			}
		}

		contextValidation.setUpdateMode();
		// updater la configuration pour le statut 'used' 
		State confState = new State();
		confState.date = new Date();
		confState.code = "U-SUB";
		confState.user = user;
		configWorkflows.setState(contextValidation, config, confState);
		System.out.println("on est passé par configWorkflows.setState et state.code='" + config.state.code+"'");		

		// updater si besoin le study pour le statut 'V-SUB'
		if (study != null && StringUtils.isNotBlank(submission.studyCode)){
			study.state.code = "U-SUB";
			study.traceInformation.modifyDate = new Date();
			study.traceInformation.modifyUser = user;
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
					DBQuery.is("code", study.code),
					DBUpdate.set("state.code", study.state.code).set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date()));	
		}
		// Updater les readSets pour le status dans la base: 
		for (ReadSet readSet : readSets) {
			if (readSet == null){
				throw new SraException("readSet " + readSet.code + " n'existe pas dans database");
			} else {
				readSet.submissionState.code = "N";
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,
						DBQuery.is("code", readSet.code),
						DBUpdate.set("submissionState.code", "N").set("traceInformation.modifyUser", user).set("traceInformation.modifyDate", new Date()));
			}
		}		
	
		// valider submission une fois les experiments et samples sauves, et sauver submission
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		submission.validate(contextValidation);
		if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code",submission.code)){			
			submission.validate(contextValidation);
			System.out.println("\nDEBUG  displayErrors dans **444*******SubmissionServices::initPrimarySubmission :");
			contextValidation.displayErrors(logger);
			System.out.println("\nDEBUG  end displayErrors dans **********SubmissionServices::initPrimarySubmission :");
		
			MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
			//System.out.println ("sauvegarde dans la base du submission " + submission.code);
		}

		if (contextValidation.hasErrors()){
			System.out.println("submission.validate produit des erreurs");
			// rallBack avec clean sur exp et sample et mise à jour study
			System.out.println("\ndisplayErrors dans SubmissionServices::initPrimarySubmission :");
			contextValidation.displayErrors(logger);
			System.out.println("\n end displayErrors dans SubmissionServices::initPrimarySubmission :");
			
			// enlever les samples, experiments et submission qui ont ete crées par le service et remettre
			// readSet.submissionState à NONE, et si studyCode utilisé par cette seule soumission remettre studyCode.state.code=N
			// et si config utilisé par cette seule soumission remettre configCode.state.code=N
			//cleanDataBase(submission.code, contextValidation);		
			//throw new SraException("SubmissionServices::initPrimarySubmission::probleme validation  voir log: ");
			submissionWorkflowsHelper.rollbackSubmission(submission, contextValidation);	
			contextValidation.displayErrors(logger);
			throw new SraException("SubmissionServices::initReleaseSubmission::probleme validation  voir log: ");			
		} 	
		System.out.println("Creation de la soumission " + submission.code);
		return submission.code;


	}

	public ExternalStudy fetchExternalStudy(String studyAc, String user) throws SraException {
		ExternalStudy externalStudy;
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, AbstractStudy.class, "accession", studyAc)){
			// verifier que si objet existe dans base avec cet AC c'est bien un type externalStudy et non un type study
			AbstractStudy absStudy;
			absStudy = MongoDBDAO.findOne(InstanceConstants.SRA_STUDY_COLL_NAME,
				AbstractStudy.class, DBQuery.and(DBQuery.is("accession", studyAc)));
			//if (! (absStudy instanceof ExternalStudy)) {
			if ( ExternalStudy.class.isInstance(absStudy)) {
				//System.out.println("Recuperation dans base du study avec Ac = " + studyAc +" qui est du type externalStudy ");
			} else {
				throw new SraException("Recuperation dans base du study avec Ac = " + studyAc +" qui n'est pas du type externalStudy ");
			}
			externalStudy = MongoDBDAO.findOne(InstanceConstants.SRA_STUDY_COLL_NAME,
					ExternalStudy.class, DBQuery.and(DBQuery.is("accession", studyAc)));
		} else {
			// Creer objet externalStudy
			String externalStudyCode = SraCodeHelper.getInstance().generateExternalStudyCode(studyAc);
			externalStudy = new ExternalStudy(); // objet avec state.code = submitted
			externalStudy.accession = studyAc;
			externalStudy.code = externalStudyCode;
			externalStudy.traceInformation.setTraceInformation(user);	
			externalStudy.state = new State("F-SUB", user);
		}
		return externalStudy;
	}
	
	public File createDirSubmission(Submission submission) throws SraException{
		// Determiner le repertoire de soumission:
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);
					
		String syntProjectCode = submission.code;
		/*
		for (String projectCode: submission.projectCodes) {
			if (StringUtils.isNotBlank(projectCode)) {
				syntProjectCode += "_" + projectCode;
			}
		}
		if (StringUtils.isNotBlank(syntProjectCode)){
			syntProjectCode = syntProjectCode.replaceFirst("_", "");
		}
		*/			
		//submission.submissionDirectory = VariableSRA.submissionRootDirectory + File.separator + syntProjectCode + File.separator + st_my_date;
		//submission.submissionTmpDirectory = VariableSRA.submissionRootDirectory + File.separator + syntProjectCode + File.separator + "tmp_" + st_my_date;
		submission.submissionDirectory = VariableSRA.submissionRootDirectory + File.separator + submission.code; 
		if (submission.release) {
			submission.submissionDirectory = submission.submissionDirectory + "_release"; 
		}
		File dataRep = new File(submission.submissionDirectory);
		System.out.println("Creation du repertoire de soumission : " + submission.submissionDirectory);
		logger.info("Creation du repertoire de soumission" + submission.submissionDirectory);
		if (dataRep.exists()){
			throw new SraException("Le repertoire " + dataRep + " existe deja !!! (soumission concurrente ?)");
		} else {
			if(!dataRep.mkdirs()){	
				throw new SraException("Impossible de creer le repertoire " + dataRep + " ");
			}
		}
		return (dataRep);
	}

	
	public void activatePrimarySubmission(ContextValidation contextValidation, String submissionCode) throws SraException {
		// creer repertoire de soumission sur disque et faire liens sur données brutes

		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		if (submission == null){
			throw new SraException("aucun objet submission dans la base pour  : " + submissionCode);
		}
		String user = contextValidation.getUser();
		try {
			// creation repertoire de soumission :
			File dataRep = createDirSubmission(submission);
			// creation liens donnees brutes vers repertoire de soumission
			for (String experimentCode: submission.experimentCodes) {
				Experiment expElt =  MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);

				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, expElt.readSetCode);
				Project p = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, readSet.projectCode);
	
				System.out.println("exp = "+ expElt.code);
				
				for (RawData rawData :expElt.run.listRawData) {
					// Partie de code deportée dans activate : on met la variable location à CNS
					// si les données sont physiquement au CNS meme si elles sont aussi au CCRT
					// et on change le chemin pour remplacer /ccc/genostore/.../rawdata par /env/cns/proj/ 
					String cns_directory = rawData.directory;
					if(rawData.directory.startsWith("/ccc/genostore")){
						int index = rawData.directory.indexOf("/rawdata/");
						String lotseq_dir = rawData.directory.substring(index + 9);
						cns_directory="/env/cns/proj/"+lotseq_dir;
					}
					
					File fileCible = new File(cns_directory + File.separator + rawData.relatifName);
					if(fileCible.exists()){
						System.out.println("le fichier "+ fileCible +"existe bien");
						rawData.location = "CNS";
						rawData.directory = cns_directory;
					} else {
						System.out.println("le fichier "+ fileCible +"n'existe pas au CNS");
						if ("CNS".equalsIgnoreCase(readSet.location)) {
							if (p.archive){
								throw new SraException(rawData.relatifName + " n'existe pas sur les disques CNS, et Projet " + p.code + " avec archive=true, et readSet= " + readSet.code + " avec location = CNS");
							} else {
								throw new SraException(rawData.relatifName + " n'existe pas sur les disques CNS, et Projet " + p.code + " avec archive=false, et readSet " + readSet.code  + " localisée au CNS");
							}
						} else if ("CCRT".equalsIgnoreCase(readSet.location)) {
							rawData.location = readSet.location;
						} else {
							throw new SraException(rawData.relatifName + " avec location inconnue => " + readSet.location);
						}
					}
					if (rawData.extention.equalsIgnoreCase("fastq")) {
						rawData.gzipForSubmission = true;
					} else {
						rawData.gzipForSubmission = false;
						if (StringUtils.isBlank(rawData.md5)){
							contextValidation.addErrors("md5", " valeur à null alors que donnée deja zippée pour "+ rawData.relatifName);
						}
					}
					// On ne cree les liens dans repertoire de soumission vers rep des projets que si la 
					// donnée est au CNS et si elle n'est pas à zipper
					if ("CNS".equalsIgnoreCase(rawData.location) && ! rawData.gzipForSubmission) {
						System.out.println("run = "+ expElt.run.code);
						File fileLien = new File(submission.submissionDirectory + File.separator + rawData.relatifName);
						if(fileLien.exists()){
							fileLien.delete();
						}
						
						System.out.println("fileCible = " + fileCible);
						System.out.println("fileLien = " + fileLien);

						Path lien = Paths.get(fileLien.getPath());
						Path cible = Paths.get(fileCible.getPath());
						Files.createSymbolicLink(lien, cible);
						System.out.println("Lien symbolique avec :  lien= "+lien+" et  cible="+cible);
						//String cmd = "ln -s -f " + rawData.directory + File.separator + rawData.relatifName
						//+ " " + submission.submissionDirectory + File.separator + rawData.relatifName;
						//System.out.println("cmd = " + cmd);
					} else {
						System.out.println("Donnée "+ rawData.relatifName + " localisée au " + rawData.location);
					}
				}
				// sauver dans base la liste des rawData avec bonne location et bon directory:
				MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME,  Experiment.class, 
					DBQuery.is("code", experimentCode),
					DBUpdate.set("run.listRawData", expElt.run.listRawData));
			}
		} catch (SraException e) {
			throw new SraException(" Dans activatePrimarySubmission" + e);			
		} catch (SecurityException e) {
			throw new SraException(" Dans activatePrimarySubmission pb SecurityException: " + e);
		} catch (UnsupportedOperationException e) {
			throw new SraException(" Dans activatePrimarySubmission pb UnsupportedOperationException: " + e);
		} catch (FileAlreadyExistsException e) {
			throw new SraException(" Dans activatePrimarySubmission pb FileAlreadyExistsException: " + e);
		} catch (IOException e) {
			throw new SraException(" Dans activatePrimarySubmission pb IOException: " + e);		
		}
		
		
		// mettre à jour le champs submission.studyCode si besoin, si study à soumettre, si study avec state=uservalidate
		State state = new State("IW-SUB", user);
		submissionWorkflows.setState(contextValidation, submission, state);
		if (! contextValidation.hasErrors()) {
		// updater la soumission dans la base pour le repertoire de soumission (la date de soumission sera mise à la reception des AC)
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME,  Submission.class, 
				DBQuery.is("code", submission.code),
				DBUpdate.set("submissionDirectory", submission.submissionDirectory));
		} else {
			System.out.println("Probleme pour passer la soumission avec le state IW-SUB");
			contextValidation.displayErrors(logger);
			throw new SraException(" Dans activatePrimarySubmission Erreurs : " + contextValidation.errors.toString());		
		}
		
	}
		
	
	//private Submission createSubmissionEntity(Configuration config, String studyCode, String user, Map<String, UserCloneType> mapUserClones) throws SraException{
	private Submission createSubmissionEntity(Configuration config, String studyCode, String studyAc, String user) throws SraException{
		if (config==null) {
			throw new SraException("SubmissionServices::createSubmissionEntity::configuration : config null ???");
		}
		Submission submission = null;
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		submission = new Submission(user, config.projectCodes);
		submission.code = SraCodeHelper.getInstance().generateSubmissionCode(config.projectCodes);
		submission.creationDate = courantDate;
		System.out.println("submissionCode="+ submission.code);
		submission.state = new State("N", user);
		submission.release = false; // soumission toujours en confidentielle et levee de confidentialite du
		                            // study par l'utilisateur via interface.
		submission.configCode = config.code;
		// Creation de la map deportéé dans methode initPrimarySubmission pour ne mettre dans la map
		// que les noms de clones utilisee par la soumission

		
		if (config.strategyStudy.equalsIgnoreCase("strategy_external_study")) {
			// pas de study à soumettre et par defaut pas de release.
			// ok submission.release == false et submission sans studyCode.
		} else if (config.strategyStudy.equalsIgnoreCase("strategy_internal_study")) {
		
			Study study = null;
			
			if (StringUtils.isNotBlank(studyAc)) {
				study = MongoDBDAO.findOne(InstanceConstants.SRA_STUDY_COLL_NAME,
						Study.class, DBQuery.and(DBQuery.is("accession", studyAc)));
			} else if (StringUtils.isNotBlank(studyCode)) {
				study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, studyCode);
			} else {
				// rien : cela peut etre une soumission avec mapCloneToAc
			}
			if (study!=null){
				if (study.state == null) {
					throw new SraException("study.state== null incompatible avec soumission. =>  study.state.code in ('N','F-SUB')");
				}
			
				// mettre à jour l'objet submission pour le study  :
				if (! submission.refStudyCodes.contains("study.code")){
					submission.refStudyCodes.add(study.code);
				}
				if (study.state.code.equals("N")) {
					submission.studyCode = study.code; // studyCode à soumettre
				
				} else if (study.state.code.equals("F-SUB")) {
				    // pas de soumission du study
				} else {
					throw new SraException("study.state.code not in ('N', 'F-SUB') => utilisation pour cette soumission d'un study en cours de soumission ?");
				}
			} 
		
		}
		return submission;
	}
	
	

	public String getNcbiScientificName(Integer taxonId) 
			throws IOException, 
			XPathExpressionException, 
			InterruptedException,
			ExecutionException,
			TimeoutException {
		//Promise<WSResponse> homePage = WS.url("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&id="+taxonId+"&retmote=xml").get();
		CompletionStage<WSResponse> homePage = ws().url("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&id="+taxonId+"&retmote=xml").get();
		// Promise<Document> xml = homePage.map(response -> {
		CompletionStage<Document> xml = homePage.thenApplyAsync(response -> {
			try {
				System.out.println("response "+response.getBody());
				//Document d = XML.fromString(response.getBody());
				//Node n = scala.xml.XML.loadString(response.getBody());
				//System.out.println("J'ai une reponse ?"+ n.toString());
				DocumentBuilderFactory dbf =
						DocumentBuilderFactory.newInstance();
				//dbf.setValidating(false);
				//dbf.setSchema(null);
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(new InputSource(new StringReader(response.getBody())));
				return doc;
			} catch (SAXException e) {
				throw new RuntimeException("xml parsing failed",e);
			} catch (IOException e) {
				throw new RuntimeException("io error while parsing xml",e);
			} catch (ParserConfigurationException e) {
				throw new RuntimeException("xml parser configuration error",e);
			}

		});
		
		Document doc = xml.toCompletableFuture().get(1000,TimeUnit.MILLISECONDS);
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "/TaxaSet/Taxon/ScientificName";

		//read a string value
		String scientificName = xPath.compile(expression).evaluate(doc);
		return scientificName;	
	}
		
		


	private Sample fetchSample(ReadSet readSet, String strategySample, String scientificName, String user) throws SraException {
		// Recuperer pour chaque readSet les objets de laboratory qui existent forcemment dans mongoDB, 
		// et qui permettront de renseigner nos objets SRA :
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String laboratorySampleName = laboratorySample.name;

		String clone = laboratorySample.referenceCollab;
		String taxonId = laboratorySample.taxonCode;

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);

		String codeSample = SraCodeHelper.getInstance().generateSampleCode(readSet, readSet.projectCode, strategySample);
		Sample sample = null;
		// Si sample existe, prendre l'existant, sinon en creer un nouveau
		//if (services.SraDbServices.checkCodeSampleExistInSampleCollection(codeSample)) {
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", codeSample)){
			//System.out.println("Recuperation du sample "+ codeSample);
			sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.Sample.class, codeSample);			
			//System.out.println(sample.clone);
			//System.out.println(sample.taxonId);
			//System.out.println(sample.title);
		} else {
			//System.out.println("Creation du sample '"+ codeSample + "'");
			// creation du sample :
			sample = new Sample();
			sample.code = codeSample;
			sample.taxonId = new Integer(taxonId);
			// enrichir le sample avec scientific_name:
			sample.scientificName = scientificName;
			sample.clone = laboratorySample.referenceCollab;
			sample.projectCode = readSet.projectCode;
			sample.state = new State("N", user);
			sample.traceInformation.setTraceInformation(user);			
		}
		
		System.out.println("readSetCode = " + readSet.code);
		System.out.println("laboratorySampleCode = " + laboratorySampleCode);
		System.out.println("laboratorySampleName = " + laboratorySampleName);
		System.out.println("taxonId = " + taxonId);
		System.out.println("clone = " + clone);
		return sample;
	}
	
	
	
	/*public ExternalSample fetchExternalSample(String sampleAc, String user) throws SraException {
		String externalSampleCode = SraCodeHelper.getInstance().generateExternalSampleCode(sampleAc);
		ExternalSample externalSample;
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, ExternalSample.class, "code", externalSampleCode)){
			//System.out.println("Recuperation du sample "+ externalSampleCode);
			externalSample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.ExternalSample.class, externalSampleCode);
		} else {
			externalSample = new ExternalSample(); // objet avec state.code = submitted
			externalSample.accession = sampleAc;
			externalSample.code = externalSampleCode;
			externalSample.state = new State("F-SUB", user);			
			externalSample.traceInformation.setTraceInformation(user);		
		}
		return externalSample;
	}
	*/
	public ExternalSample fetchExternalSample(String sampleAc, String user) throws SraException {
		System.out.println("Entree dans fetchExternalSample");

		ExternalSample externalSample;

		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, AbstractSample.class, "accession", sampleAc)){
			AbstractSample absSample;
			absSample = MongoDBDAO.findOne(InstanceConstants.SRA_SAMPLE_COLL_NAME,
				AbstractSample.class, DBQuery.and(DBQuery.is("accession", sampleAc)));
			
			if (ExternalSample.class.isInstance(absSample)) {
				//System.out.println("Recuperation dans base du sample avec Ac = " + sampleAc +" qui est du type externalSample ");
			} else {
				throw new SraException("Recuperation dans base du sample avec Ac = " + sampleAc +" qui n'est pas du type externalSample ");
			}
			externalSample = MongoDBDAO.findOne(InstanceConstants.SRA_SAMPLE_COLL_NAME,
					ExternalSample.class, DBQuery.and(DBQuery.is("accession", sampleAc)));
		} else {
			System.out.println("Creation dans base du sample avec Ac" + sampleAc);
			String externalSampleCode = SraCodeHelper.getInstance().generateExternalSampleCode(sampleAc);
			externalSample = new ExternalSample(); // objet avec state.code = submitted
			externalSample.accession = sampleAc;
			externalSample.code = externalSampleCode;
			externalSample.traceInformation.setTraceInformation(user);	
			externalSample.state = new State("F-SUB", user);
		}
		return externalSample;
	}
	
	
	

	// methode mise en public car utilisee dans test mais devrait etre private
	public Experiment createExperimentEntity(ReadSet readSet, String scientificName, String user) throws SraException {
		// On cree l'experiment pour le readSet demandé.
		// La validite du readSet doit avoir été testé avant.

		Experiment experiment = new Experiment(); 
		//SraParameter sraParam = new SraParameter();
		Map<String, String> mapLibProcessTypeCodeVal_orientation = VariableSRA.mapLibProcessTypeCodeVal_orientation();

		experiment.code = SraCodeHelper.getInstance().generateExperimentCode(readSet.code);
		experiment.readSetCode = readSet.code;
		experiment.projectCode = readSet.projectCode;
		experiment.traceInformation.setTraceInformation(user);
		System.out.println("expCode =" + experiment.code);
		String laboratoryRunCode = readSet.runCode;
		System.out.println("laboratoryRunCode =" + laboratoryRunCode);

		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		Map<String, PropertyValue> sampleOnContainerProperties = readSet.sampleOnContainer.properties;
		Set <String> listKeysSampleOnContainerProperties = null;

		if (sampleOnContainerProperties != null) {
			listKeysSampleOnContainerProperties = sampleOnContainerProperties.keySet();  // Obtenir la liste des clés
			for(String k: listKeysSampleOnContainerProperties) {
				//System.out.print("lulu cle = '" + k+"'  => ");
				PropertyValue propertyValue = sampleOnContainerProperties.get(k);
			}
			if (sampleOnContainerProperties.containsKey("libProcessTypeCode")) {
				String libProcessTypeCode = (String) sampleOnContainerProperties.get("libProcessTypeCode").getValue();
				//System.out.print(" !!! libProcessTypeCode="+ libProcessTypeCode);
			}
		}
		String libProcessTypeCodeVal = (String) sampleOnContainerProperties.get("libProcessTypeCode").getValue();
		String typeCode = readSet.typeCode;
		//System.out.println("libProcessTypeCodeVal = "+libProcessTypeCodeVal);
		//System.out.println("typeCode = "+typeCode);

		if (readSet.typeCode.equalsIgnoreCase("rsillumina")){
			typeCode = "Illumina";
		} else if (readSet.typeCode.equalsIgnoreCase("rsnanopore")){
			typeCode = "oxford_nanopore";
		} else if (readSet.typeCode.equalsIgnoreCase("default-readset")){
			// rien condition à eliminer au plus tard, lors de la mise en prod.
		} else {
			throw new SraException("readset.typeCode inconnu " + typeCode);
		}
		experiment.typePlatform = typeCode.toLowerCase();
		experiment.title = scientificName + "_" + typeCode + "_" + libProcessTypeCodeVal;
		experiment.libraryName = readSet.sampleCode + "_" +libProcessTypeCodeVal;			

		//System.out.println("typePlatform="+ experiment.typePlatform);
		//System.out.println("title="+ experiment.title);
		//System.out.println("libraryName="+ experiment.libraryName);
		
		if(laboratoryRun==null){
			throw new SraException("Pas de laboratoryRun pour " + readSet.code);
		}
		if (laboratoryRun.instrumentUsed == null ){
			throw new SraException("Pas de champs instrumentUsed pour le laboratoryRun " + laboratoryRun.code);
		} 
		//System.out.println("Recuperation de instrumentUsed = "+ laboratoryRun.instrumentUsed);
		InstrumentUsed instrumentUsed = laboratoryRun.instrumentUsed;
		
		//System.out.println(" !!!!!!!!! instrumentUsed.code = " + instrumentUsed.code);
		//System.out.println("!!!!!!!!!!!! instrumentUsed.typeCode = '" + instrumentUsed.typeCode+"'");
		//System.out.println("!!!!!!!!! instrumentUsed.typeCodeMin = '" + instrumentUsed.typeCode.toLowerCase()+"'");
		experiment.instrumentModel = VariableSRA.mapInstrumentModel().get(instrumentUsed.typeCode.toLowerCase());
		if (StringUtils.isBlank(experiment.instrumentModel)) {
			System.err.println("Pas de correspondance existante pour instrumentUsed.typeCodeMin = '" + instrumentUsed.typeCode.toLowerCase()+"'");	
		}
		
		experiment.libraryLayoutNominalLength = null;		
		if( ! "rsnanopore".equalsIgnoreCase(readSet.typeCode)){
			// Rechercher libraryLayoutNominalLength pour les single illumina (paired)
			// mettre la valeur calculée de libraryLayoutNominalLength
			models.laboratory.run.instance.Treatment treatmentMapping = readSet.treatments.get("mapping");
			if (treatmentMapping != null) {
				Map <String, Map<String, PropertyValue>> resultsMapping = treatmentMapping.results();
				if ( resultsMapping != null && (resultsMapping.containsKey("pairs"))){
					Map<String, PropertyValue> pairs = resultsMapping.get("pairs");
					if (pairs != null) {
						Set <String> listKeysMapping = pairs.keySet();  // Obtenir la liste des clés
						for(String k: listKeysMapping) {
							//System.out.print("coucou cle = '" + k+"'  => ");
							PropertyValue propertyValue = pairs.get(k);
							//System.out.println(propertyValue.value);
						}
						if (pairs.containsKey("estimatedPEInsertSize")) {
							PropertyValue estimatedInsertSize = pairs.get("estimatedPEInsertSize");
							experiment.libraryLayoutNominalLength = (Integer) estimatedInsertSize.value;
							System.out.println("valeur calculee libraryLayoutNominalLength  => "  + experiment.libraryLayoutNominalLength);
						} 
						if (pairs.containsKey("estimatedMPInsertSize")) {
							PropertyValue estimatedInsertSize = pairs.get("estimatedMPInsertSize");
							experiment.libraryLayoutNominalLength = (Integer) estimatedInsertSize.value;
							System.out.println("valeur calculee libraryLayoutNominalLength  => "  + experiment.libraryLayoutNominalLength);
						}	
					}
				}
			}
			
			if (experiment.libraryLayoutNominalLength == null) {
				// mettre valeur theorique de libraryLayoutNominalLength (valeur a prendre dans readSet.sampleOnContainer.properties.nominalLength) 
				// voir recup un peu plus bas:
				//Map<String, PropertyValue> sampleOnContainerProperties = readSet.sampleOnContainer.properties;
				if (sampleOnContainerProperties != null) {
					//Set <String> listKeysSampleOnContainerProperties = sampleOnContainerProperties.keySet();  // Obtenir la liste des clés

					for(String k: listKeysSampleOnContainerProperties){
						//System.out.print("MA cle = '" + k +"'");
						PropertyValue propertyValue = sampleOnContainerProperties.get(k);
						//System.out.print(propertyValue.toString());
						//System.out.println(", MA value  => "+propertyValue.value);
					} 

					if (sampleOnContainerProperties.containsKey("libLayoutNominalLength")) {	
						//System.out.println("recherche valeur theorique possible");
						PropertyValue nominalLengthTypeCode = sampleOnContainerProperties.get("libLayoutNominalLength");
						Integer nominalLengthCodeValue = (Integer) nominalLengthTypeCode.value;
						if ((nominalLengthCodeValue != null) && (nominalLengthCodeValue!= -1)){
							experiment.libraryLayoutNominalLength = nominalLengthCodeValue;
							System.out.println("valeur theorique libraryLayoutNominalLength  => "  + experiment.libraryLayoutNominalLength);
						}
					}
				}
			}
		}
		//System.out.println("valeur de experiment.libLayoutExpLength"+ experiment.libraryLayoutNominalLength);			
		experiment.state = new State("N", user); 
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String taxonId = laboratorySample.taxonCode;
		//System.out.println("sampleCode=" +laboratorySampleCode); 
		//System.out.println("taxonId=" +taxonId); 
		//String laboratoryRunCode = readSet.runCode;
		//models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		String technology = laboratoryRun.instrumentUsed.typeCode;
		
		if( ! "rsnanopore".equalsIgnoreCase(readSet.typeCode)){
			// Recuperer l'information spotLength pour les illumina
			models.laboratory.run.instance.Treatment treatmentNgsrg = (laboratoryRun.treatments.get("ngsrg"));
			if (treatmentNgsrg != null) {
				Map <String, Map<String, PropertyValue>> resultsNgsrg = treatmentNgsrg.results();
				if (resultsNgsrg != null && resultsNgsrg.containsKey("default")) {
					Map<String, PropertyValue> ngsrg = resultsNgsrg.get("default");
					Set <String> listKeys = ngsrg.keySet();  // Obtenir la liste des clés
					/*for(String k: listKeys){
					System.out.print("cle = " + k);
					PropertyValue propertyValue = ngsrg.get(k);
					//System.out.print(propertyValue.toString());
					System.out.println(", value  => "+propertyValue.value);
				} */
					PropertyValue propertyNbCycle = ngsrg.get("nbCycle");
					if (ngsrg.get("nbCycle") != null){
						experiment.spotLength = (Long) propertyNbCycle.value;
					}
				}
			}
		}
		
		if ("rsnanopore".equalsIgnoreCase(readSet.typeCode)){
			// Pas de spot_descriptor
			experiment.libraryLayout = "SINGLE";
			experiment.libraryLayoutOrientation = VariableSRA.mapLibraryLayoutOrientation().get("forward");
		} else {
			
			// Ajouter les read_spec (dans SPOT_DESCRIPTOR ) en fonction de l'information SINGLE ou PAIRED et forward-reverse et last_base_coord :
			// les rsnanopore sont normalement des single forward.
			experiment.libraryLayout = null;
			experiment.libraryLayoutOrientation = null;

			if (laboratoryRun.properties.containsKey("sequencingProgramType")){	
				String libraryLayout =  (String) laboratoryRun.properties.get("sequencingProgramType").value;

				if (StringUtils.isNotBlank(libraryLayout)) { 
					if (libraryLayout.equalsIgnoreCase("SR")){
						experiment.libraryLayout = "SINGLE";
						experiment.libraryLayoutOrientation = VariableSRA.mapLibraryLayoutOrientation().get("forward");
					} else if( libraryLayout.equalsIgnoreCase("PE") || libraryLayout.equalsIgnoreCase("MP")){
						experiment.libraryLayout = "PAIRED";
						//Map<String, PropertyValue> sampleOnContainerProperties = readSet.sampleOnContainer.properties;

						if (sampleOnContainerProperties != null) {
							//Set <String> listKeysSampleOnContainerProperties = sampleOnContainerProperties.keySet();  // Obtenir la liste des clés

							/*for(String k: listKeysSampleOnContainerProperties){
							System.out.print("cle = " + k);
							PropertyValue propertyValue = sampleOnContainerProperties.get(k);
							System.out.print(propertyValue.toString());
							System.out.println(", value  => "+propertyValue.value);
						} */
							
							if (sampleOnContainerProperties.containsKey("libProcessTypeCode")) {					
								PropertyValue libProcessTypeCode = sampleOnContainerProperties.get("libProcessTypeCode");
								String libProcessTypeCodeValue = (String) libProcessTypeCode.value;
								/*if(libProcessTypeCodeValue.equalsIgnoreCase("A")||libProcessTypeCodeValue.equalsIgnoreCase("C")||libProcessTypeCodeValue.equalsIgnoreCase("N")){
									experiment.libraryLayoutOrientation = "reverse-forward";
								} else if (libProcessTypeCodeValue.equalsIgnoreCase("W")||libProcessTypeCodeValue.equalsIgnoreCase("F")
										||libProcessTypeCodeValue.equalsIgnoreCase("H")||libProcessTypeCodeValue.equalsIgnoreCase("L")
										||libProcessTypeCodeValue.equalsIgnoreCase("Z")||libProcessTypeCodeValue.equalsIgnoreCase("MI")
										||libProcessTypeCodeValue.equalsIgnoreCase("K")||libProcessTypeCodeValue.equalsIgnoreCase("DA") 
										||libProcessTypeCodeValue.equalsIgnoreCase("U")||libProcessTypeCodeValue.equalsIgnoreCase("DB")
										||libProcessTypeCodeValue.equalsIgnoreCase("DC")||libProcessTypeCodeValue.equalsIgnoreCase("DD")
										||libProcessTypeCodeValue.equalsIgnoreCase("DE")||libProcessTypeCodeValue.equalsIgnoreCase("RA")
										||libProcessTypeCodeValue.equalsIgnoreCase("RB")||libProcessTypeCodeValue.equalsIgnoreCase("TA")
										||libProcessTypeCodeValue.equalsIgnoreCase("TB")){
									experiment.libraryLayoutOrientation = "forward-reverse";
								} else {
									throw new SraException("Pour le readSet " + readSet.code +  ", valeur de libProcessTypeCodeValue differente A,C,N, W, F, H, L ,Z, M, I, K => " + libProcessTypeCodeValue);
								}*/
								if (mapLibProcessTypeCodeVal_orientation.get(libProcessTypeCodeValue)==null){
									throw new SraException("Pour le readSet " + readSet.code +  ", valeur de libProcessTypeCodeValue inconnue :" + libProcessTypeCodeValue);
								} else {
									experiment.libraryLayoutOrientation = mapLibProcessTypeCodeVal_orientation.get(libProcessTypeCodeValue);
								}
							}
						}
					} else {
						System.out.println("Pour le laboratoryRun " + laboratoryRun.code + " valeur de properties.sequencingProgramType differente de SR ou PE => " + libraryLayout);
						throw new SraException("Pour le laboratoryRun " + laboratoryRun.code + " valeur de properties.sequencingProgramType differente de SR ou PE => " + libraryLayout);
					}
				}
				System.out.println("libraryLayout======"+libraryLayout);
			}
		}
		experiment.libraryConstructionProtocol = VariableSRA.defaultLibraryConstructionProtocol;
		experiment.run = createRunEntity(readSet);
		experiment.run.expCode=experiment.code;

		// Renseigner l'objet experiment pour lastBaseCoord : Recuperer les lanes associées au
		// run associé au readSet et recuperer le lane contenant le readSet.code. C'est dans les
		// traitement de cette lane que se trouve l'information:
		// Un readSet est sur une unique lane, mais une lane peut contenir plusieurs readSet
		List<Lane> laboratoryLanes = laboratoryRun.lanes;
		if (laboratoryLanes != null) {
			for (Lane ll : laboratoryLanes) {
				List<String> readSetCodes = ll.readSetCodes;
				for (String rsc : readSetCodes){
					if (rsc.equalsIgnoreCase(readSet.code)){
						// bonne lane = lane correspondant au run associé au readSet
						models.laboratory.run.instance.Treatment laneTreatment = (ll.treatments.get("ngsrg"));
						Map<String, Map<String, PropertyValue>> laneResults = laneTreatment.results();
						Map<String, PropertyValue> lanengsrg = laneResults.get("default");
						Set<String> laneListKeys = lanengsrg.keySet();  // Obtenir la liste des clés
						/*for(String k: laneListKeys) {
							System.out.println("attention cle = " + k);
							PropertyValue propertyValue = lanengsrg.get(k);
							System.out.println(propertyValue.toString());
							System.out.println(propertyValue.value);
						}*/
						experiment.lastBaseCoord =  (Integer) lanengsrg.get("nbCycleRead1").value + 1;
						break;
					}
				}
			}
		}
		System.out.println("'"+readSet.code+"'");
		experiment.readSpecs = new ArrayList<ReadSpec>();
		
		if( ! "rsnanopore".equalsIgnoreCase(readSet.typeCode)){
			// IF ILLUMINA ET SINGLE  Attention != si nanopore et SINGLE
			if (StringUtils.isNotBlank(experiment.libraryLayout) && experiment.libraryLayout.equalsIgnoreCase("SINGLE") ) {
				ReadSpec readSpec_1 = new ReadSpec();
				readSpec_1.readIndex = 0; 
				readSpec_1.readLabel = "F";
				readSpec_1.readClass = "Application Read";
				readSpec_1.readType = "forward";
				readSpec_1.baseCoord = (Integer) 1;
				experiment.readSpecs.add(readSpec_1);
			}

			// IF ILLUMINA ET PAIRED ET "forward-reverse"
			if (StringUtils.isNotBlank(experiment.libraryLayout) && experiment.libraryLayout.equalsIgnoreCase("PAIRED") 
					&& StringUtils.isNotBlank(experiment.libraryLayoutOrientation) && experiment.libraryLayoutOrientation.equalsIgnoreCase("forward-reverse") ) {
				ReadSpec readSpec_1 = new ReadSpec();
				readSpec_1.readIndex = 0;
				readSpec_1.readLabel = "F";
				readSpec_1.readClass = "Application Read";
				readSpec_1.readType = "Forward";
				readSpec_1.baseCoord = (Integer) 1;
				experiment.readSpecs.add(readSpec_1);

				ReadSpec readSpec_2 = new ReadSpec();
				readSpec_2.readIndex = 1;
				readSpec_2.readLabel = "R";
				readSpec_2.readClass = "Application Read";
				readSpec_2.readType = "Reverse";
				readSpec_2.baseCoord = experiment.lastBaseCoord;
				experiment.readSpecs.add(readSpec_2);

			}
			// IF ILLUMINA ET PAIRED ET "reverse-forward"
			if (StringUtils.isNotBlank(experiment.libraryLayout) && experiment.libraryLayout.equalsIgnoreCase("PAIRED") 
					&& StringUtils.isNotBlank(experiment.libraryLayoutOrientation) && experiment.libraryLayoutOrientation.equalsIgnoreCase("reverse-forward") ) {
				ReadSpec readSpec_1 = new ReadSpec();
				readSpec_1.readIndex = 0;
				readSpec_1.readLabel = "R";
				readSpec_1.readClass = "Application Read";
				readSpec_1.readType = "Reverse";
				readSpec_1.baseCoord = (Integer) 1;
				experiment.readSpecs.add(readSpec_1);

				ReadSpec readSpec_2 = new ReadSpec();
				readSpec_2.readIndex = 1;
				readSpec_2.readLabel ="F";
				readSpec_2.readClass = "Application Read";
				readSpec_2.readType = "Forward";
				readSpec_2.baseCoord = experiment.lastBaseCoord;
				experiment.readSpecs.add(readSpec_2);
			}
		} else {
			//on ne cree pas de readSpec dans le cas de nanopore car pas de spot_descriptor
		}
		return experiment;
	}

	


	public Run createRunEntity(ReadSet readSet) {
		// On cree le run pour le readSet demandé.
		// La validite du readSet doit avoir été testé avant.

		// Recuperer pour le readSet la liste des fichiers associés:

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		InstrumentUsed instrumentUsed = laboratoryRun.instrumentUsed;
		
		List <models.laboratory.run.instance.File> list_files =  readSet.files;
		if (list_files == null) {
			System.out.println("Aucun fichier pour le readSet " + readSet.code +"???");
		} else {
			//System.out.println("nbre de fichiers = " + list_files.size());
		}
		// Pour chaque readSet, creer un objet run 
		Date runDate = readSet.runSequencingStartDate;
		Run run = new Run();
		run.code = SraCodeHelper.getInstance().generateRunCode(readSet.code);
		run.runDate = runDate;

		//run.projectCode = projectCode;
		run.runCenter = VariableSRA.centerName;
		// Renseigner le run pour ces fichiers sur la base des fichiers associes au readSet :
		// chemin des fichiers pour ce readset :
		String dataDir = readSet.path;

		for (models.laboratory.run.instance.File runInstanceFile: list_files) {
			String runInstanceExtentionFileName = runInstanceFile.extension;
			// conditions qui doivent etre suffisantes puisque verification préalable que le readSet
			// est bien valide pour la bioinformatique.
			if (runInstanceFile.usable 
					//&& ! runInstanceExtentionFileName.equalsIgnoreCase("fna") && ! runInstanceExtentionFileName.equalsIgnoreCase("qual")
					//&& ! runInstanceExtentionFileName.equalsIgnoreCase("fna.gz") && ! runInstanceExtentionFileName.equalsIgnoreCase("qual.gz")) {
					&&  (runInstanceExtentionFileName.equalsIgnoreCase("fastq.gz") || runInstanceExtentionFileName.equalsIgnoreCase("fastq"))) {
					RawData rawData = new RawData();
					//System.out.println("fichier " + runInstanceFile.fullname);
					rawData.extention = runInstanceFile.extension;
					System.out.println("dataDir "+dataDir);
					rawData.directory = dataDir.replaceFirst("\\/$", ""); // oter / terminal si besoin
					System.out.println("raw data directory"+rawData.directory);
					rawData.relatifName = runInstanceFile.fullname;
					rawData.location = readSet.location;
					if (runInstanceFile.properties != null && runInstanceFile.properties.containsKey("md5")) {
						rawData.md5 = (String) runInstanceFile.properties.get("md5").value;
						System.out.println("Recuperation du md5 pour" + rawData.relatifName +"= " + rawData.md5);
					}
					run.listRawData.add(rawData);
					Set<String> listKeys = runInstanceFile.properties.keySet();
					
					for(String k: listKeys) {
						//System.out.println("attention cle = " + k);
						PropertyValue propertyValue = runInstanceFile.properties.get(k);
						//System.out.println(propertyValue.toString());
						//System.out.println(propertyValue.value);
					}
			}
		}
		return run;
	}
	
	// delete la soumission et ses experiments et samples s'ils ne sont pas references par une autre soumission
	// Ne delete pas le study et la config associée car saisis par l'utilisateur qui peut vouloir les utiliser pour une prochaine soumission
	public static void cleanDataBase(String submissionCode, ContextValidation contextValidation) throws SraException {
		System.out.println("Recherche objet submission dans la base pour "+ submissionCode);

		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		if (submission==null){
			System.out.println("Aucun objet submission dans la base pour "+ submissionCode);
			return;
		} else {
			System.out.println("objet submission dans database pour "+ submissionCode);
			System.out.println("submission.accession dans cleanDataBase "+ submission.accession);
		}

		// Si la soumission est connu de l'EBI on ne pourra pas l'enlever de la base :
		if (StringUtils.isNotBlank(submission.accession)){
			System.out.println("objet submission avec AC : submissionCode = "+ submissionCode + " et submissionAC = "+ submission.accession);
			return;
		} 
		// Si la soumission concerne une release avec status "N-R" ou IW-SUB-R:
		if (submission.release && (submission.state.code.equalsIgnoreCase("N-R")||(submission.state.code.equalsIgnoreCase("IW-SUB-R")))) {
			// detruire la soumission :
			MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submission.code);
			// remettre le status du study avec un status F-SUB
			// La date de release du study est modifié seulement si retour posifit de l'EBI pour release donc si status F-SUB-R
			MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class,
					DBQuery.is("code", submission.studyCode),
					DBUpdate.set("state.code", "F-SUB").set("traceInformation.modifyUser", contextValidation.getUser()).set("traceInformation.modifyDate", new Date()));
			//System.out.println("state.code remis à 'N' pour le readSet "+experiment.readSetCode);
			return;
		} 


		if (! submission.experimentCodes.isEmpty()) {
			for (String experimentCode : submission.experimentCodes) {
				// verifier que l'experiment n'est pas utilisé par autre objet submission avant destruction
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
				// mettre le status pour la soumission des readSet à NONE si possible: 
				if (experiment != null){
					//System.out.println(" !!!! experimentCode = "+ experimentCode);
					//System.out.println(" !!!! experiment.code = "+ experiment.code);

					//System.out.println(" !!!! submissionState.code remis à 'N' pour "+experiment.readSetCode);

					// remettre les readSet dans la base avec submissionState à "NONE":
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class,
							DBQuery.is("code", experiment.readSetCode),
							DBUpdate.set("submissionState.code", "NONE").set("traceInformation.modifyUser", contextValidation.getUser()).set("traceInformation.modifyDate", new Date()));
					//System.out.println("submissionState.code remis à 'N' pour le readSet "+experiment.readSetCode);

					List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("experimentCodes", experimentCode)).toList();
					if (submissionList.size() > 1) {
						for (Submission sub: submissionList) {
							System.out.println(experimentCode + " utilise par objet Submission " + sub.code);
						}
						throw new SraException(experimentCode + " utilise par plusieurs objets submissions");	
					} else {
						// todo : verifier qu'on ne detruit que des experiments en new ou uservalidate
						if ("N".equals(experiment.state.code) ||"V-SUB".equals(experiment.state.code)){
							MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);
							//System.out.println("deletion dans base pour experiment "+experimentCode);
						} else {
							System.out.println(experimentCode + " non delété dans base car status = " + experiment.state.code);
						}
					}
				}
			}
		}

		if (! submission.refSampleCodes.isEmpty()) {	
			for (String sampleCode : submission.refSampleCodes){
				// verifier que sample n'est pas utilisé par autre objet submission avant destruction
				// normalement sample crees dans init de type external avec state=F-SUB ou sample avec state='N'
				List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("refSampleCodes", sampleCode)).toList();
				if (submissionList.size() > 1) {
					for (Submission sub: submissionList) {
						System.out.println(sampleCode + " utilise par objet Submission " + sub.code);
					}
				} else {
					MongoDBDAO.deleteByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.Sample.class, sampleCode);		
					System.out.println("deletion dans base pour sample "+sampleCode);
				}
			}
		}

		// verifier que la config à l'etat used n'est pas utilisé par une autre soumission avant de remettre son etat à 'N'
		// update la configuration pour le statut en remettant le statut new si pas utilisé par ailleurs :
		List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("configCode", submission.configCode)).toList();
		if (submissionList.size() <= 1) {
			MongoDBDAO.update(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, 
					DBQuery.is("code", submission.configCode),
					DBUpdate.set("state.code", "N").set("traceInformation.modifyUser", contextValidation.getUser()).set("traceInformation.modifyDate", new Date()));	
			System.out.println("state.code remis à 'N' pour configuration "+submission.configCode);
		}

		// verifier que le study à l'etat userValidate n'est pas utilisé par une autre soumission avant de remettre son etat à 'N'
		if (StringUtils.isNotBlank(submission.studyCode)){
			List <Submission> submissionList2 = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("studyCode", submission.studyCode)).toList();
			if (submissionList2.size() == 1) {
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
						DBQuery.is("code", submission.studyCode),
						DBUpdate.set("state.code", "N").set("traceInformation.modifyUser", contextValidation.getUser()).set("traceInformation.modifyDate", new Date()));	
				System.out.println("state.code remis à 'N' pour study "+submission.studyCode);
			}	
		}

		if (! submission.refStudyCodes.isEmpty()) {	
			// On ne peut detruire que des ExternalStudy crées et utilisés seulement par la soumission courante.
			for (String studyCode : submission.refStudyCodes){
				// verifier que study n'est pas utilisé par autre objet submission avant destruction
				// normalement study crees dans init de type external avec state=F-SUB ou study avec state='N'
				List <Submission> submissionList2 = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("refStudyCodes", studyCode)).toList();
				if (submissionList2.size() > 1) {
					for (Submission sub: submissionList2) {
						System.out.println(studyCode + " utilise par objet Submission " + sub.code);
					}
				} else {
					AbstractStudy study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.AbstractStudy.class, studyCode);
					if (study != null){
						if ( ExternalStudy.class.isInstance(study)) {
							// on ne veut enlever que les external_study cree par cette soumission, si internalStudy cree, on veut juste le remettre avec bon state.
							//System.out.println("Recuperation dans base du study avec Ac = " + studyAc +" qui est du type externalStudy ");
							if("F-SUB".equalsIgnoreCase(study.state.code) ){
								MongoDBDAO.deleteByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, studyCode);		
								System.out.println("deletion dans base pour study "+studyCode);
							}
						}
					}
				}
			}
		}

		System.out.println("deletion dans base pour submission "+submissionCode);
		MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
	}
	
}

	
