package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import play.Logger;
import validation.ContextValidation;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.ExternalSample;
import models.sra.submit.common.instance.ExternalStudy;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.common.instance.UserCloneType;
import models.sra.submit.common.instance.UserExperimentType;
import models.sra.submit.sra.instance.Configuration;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.sra.submit.sra.instance.ReadSpec;
import models.sra.submit.sra.instance.Run;
import models.sra.submit.util.SraCodeHelper;
import models.sra.submit.util.SraException;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;




// todo : implementer recuperation instrumentModel et libraryName.

public class SubmissionServices {

	public String initNewSubmission(List<String> readSetCodes, String studyCode, String configCode, Map<String, UserCloneType>mapUserClones, Map<String, UserExperimentType> mapUserExperiments, String user, ContextValidation contextValidation) throws SraException, IOException {
		// Pour une premiere soumission d'un readSet, on peut devoir utiliser un study ou un sample existant, deja soumis à l'EBI, ou non
		// en revanche on ne doit pas utiliser un experiment ou un run existant
		
	
		// verifier config et initialiser objet submission avec statut private et 
		// pas de validation integrale de config qui a ete stocke dans la base donc valide mais verification
		// de quelques contraintes en lien avec soumission.
		if (StringUtils.isBlank(configCode)) {
			throw new SraException("configCode à null incompatible avec soumission");
		}
		Configuration config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, configCode);
		
		Study study = null;
		
		if (config == null) {
			throw new SraException("config " + config.code + " n'existe pas dans database");
		} 
		//if (StringUtils.isBlank(config.state.code) || !config.state.code.equalsIgnoreCase("uservalidate")){
		if (StringUtils.isBlank(config.state.code)) {
			throw new SraException("config.state.code sans valeur incompatible avec soumission");
		}
		if (config.strategyStudy.equalsIgnoreCase("strategy_internal_study")) {
			if (StringUtils.isBlank(studyCode)) {
				throw new SraException("configuration.strategy_study = 'strategy_internal_study' incompatible avec studyCode vide");
			} else {
				// Recuperer le study si studyCode renseigné et strategy_internal_study :
				if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", studyCode)){
					study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, studyCode);			
				} else {
					throw new SraException("Soumission impossible car studyCode '"+ studyCode+ "' n'existe pas dans database");	
				}	
			}
		} else if (config.strategyStudy.equalsIgnoreCase("strategy_external_study")) {
			if (StringUtils.isNotBlank(studyCode)){
				throw new SraException("configuration.strategy_study 'strategy_external_study' incompatible avec studyCode renseigne : '" + studyCode +"'");
			}
			if (mapUserClones == null || mapUserClones.isEmpty()){
				throw new SraException("configuration.strategy_study = 'strategy_external_study' incompatible avec mapUserClone non renseigné");
			}
		} else {
			throw new SraException("configuration.strategy_study avec valeur non attendue '" + config.strategyStudy + "'");
		}
		if (config.strategySample.equalsIgnoreCase("strategy_external_sample")) {
			if (mapUserClones== null || mapUserClones.isEmpty()){
				throw new SraException("configuration.strategy_sample 'strategy_external_sample' incompatible avec mapUserClone non renseigné");
			}
		} 
		// Renvoie un objet submission 
		// - avec studyCode si strategy_internal_study et date de release correspondant au studyCode.
		// - sans studyCode si strategy_external_study et en confidentiel 
		Submission submission = createSubmissionEntity(config, studyCode, user, mapUserClones);

		// Recuperer la liste des objets ReadSet
		List <ReadSet> readSets = new ArrayList<ReadSet>();
		for (String readSetCode : readSetCodes) {
			if (StringUtils.isNotBlank(readSetCode)) {
				//System.out.println("!!!!!!!!!!!!!         readSetCode = " + readSetCode);
				ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
				if (readSet == null){
					throw new SraException("readSet " + readSet.code + " n'existe pas dans database");
				} else {
					readSets.add(readSet);
				}
			}
		}		
		
		List <Experiment> listExperiments = new ArrayList<Experiment>();
		List <Sample> listSamples = new ArrayList<Sample>();
		List <ExternalSample> listExternalSamples = new ArrayList<ExternalSample>();
		List <ExternalStudy> listExternalStudies = new ArrayList<ExternalStudy>();
		List <Study> listStudies = new ArrayList<Study>();

		int countError = 0;
		String errorMessage = "";

		
		for(ReadSet readSet : readSets) {
			System.out.println("readSet :" + readSet.code);
			// Verifier que c'est une premiere soumission pour ce readSet
			// Verifiez qu'il n'existe pas d'objet experiment referencant deja ce readSet
			//Boolean alreadySubmit = services.SraDbServices.checkCodeReadSetExistInExperimentCollection(readSet.code);
			Boolean alreadySubmit = MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "readSetCode", readSet.code);		
			if ( alreadySubmit ) {
				// signaler erreur et passer au readSet suivant
				System.out.println("?? soumission existante pour :"+readSet.code);
				countError++;
				errorMessage = errorMessage + "  - Soumission deja existante dans base pour : '" + readSet.code + "' \n";
				// Recuperer exp dans mongo
				continue;
			} 	
					
			// Verifier que ce readSet est bien valide avant soumission :
			if (! readSet.bioinformaticValuation.valid.equals(TBoolean.TRUE)) {
				countError++;
				errorMessage = errorMessage + "  - Soumission impossible pour le readset '" + readSet.code + "' parceque non valide pour la bioinformatique \n";
				continue;
			}
					

			// Creer les objets avec leurs alias ou code, les instancier completement et les sauver.

			
			// Creer l'experiment :
			Experiment experiment = createExperimentEntity(readSet, config.projectCode, user);
			experiment.librarySelection = config.librarySelection;
			experiment.librarySource = config.librarySource;
			experiment.libraryStrategy = config.libraryStrategy;
			experiment.libraryConstructionProtocol = config.libraryConstructionProtocol;
			
			String laboratorySampleCode = readSet.sampleCode;
			models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
			String laboratorySampleName = laboratorySample.name;
			String clone = laboratorySample.referenceCollab;
			// Creer le sample si besoin :
			if (config.strategySample.equalsIgnoreCase("strategy_external_sample")){
				if (StringUtils.isBlank(clone)) {
					countError++;
					errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque strategy_external_sample et pas de nom de clone dans ngl \n";
					continue;					
				}
								
				if (! submission.mapUserClone.containsKey(clone)){
					countError++;
					errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque strategy_external_sample et mapUserClone ne contient pas le clone '" + clone +"' \n";
					continue;
				}
				String sampleAc = submission.mapUserClone.get(clone).getSampleAc();
				if (StringUtils.isBlank(sampleAc)){
					countError++;
					errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque mapUserClone pour clone '" + clone + "' ne contient pas de sampleAc";
					continue;					
				}
				// creation ou recuperation dans base de externalSample avec status submitted
				ExternalSample externalSample = fetchExternalSample(sampleAc, user);
				if(!submission.refSampleCodes.contains(externalSample.code)){
					submission.refSampleCodes.add(externalSample.code);
				}
				if(!listExternalSamples.contains(externalSample.code)){
					listExternalSamples.add(externalSample);
				}	
				// mettre à jour l'experiment pour la reference sample :
				experiment.sampleCode = externalSample.code;

			} else {
				Sample sample = fetchSample(readSet, config.projectCode, config.strategySample, user);
				// Renseigner l'objet submission :
				// Verifier que l'objet sample n'a jamais ete soumis et n'est pas en cours de soumission
				System.out.println("sample = " + sample + " et state="+ sample.state.code);
				if(!submission.refSampleCodes.contains(sample.code)){
					submission.refSampleCodes.add(sample.code);
				}
			
				if (sample.state.code.equals("new")) {
					if(!listSamples.contains(sample.code)){
						listSamples.add(sample);
					}
				}	
				// mettre à jour l'experiment pour la reference sample :
				experiment.sampleCode = sample.code;
			}
			

			// Creer le study si besoin et mettre à jour l'experiment pour reference study :
			if (config.strategyStudy.equalsIgnoreCase("strategy_external_study")){
				if (! submission.mapUserClone.containsKey(clone)){
					countError++;
					errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque strategy_external_sample et mapUserClone ne contient pas le clone '" + clone +"' \n";
					continue;
				}
				String studyAc = submission.mapUserClone.get(clone).getStudyAc();
				if (StringUtils.isBlank(studyAc)){
					countError++;
					errorMessage = errorMessage + "Soumission impossible pour le readset '" + readSet.code + "' parceque mapUserClone pour clone '" + clone + "' ne contient pas de studyAc";
					continue;					
				}
				// creation ou recuperation dans base de externalStudy avec status submitted
				ExternalStudy externalStudy = fetchExternalStudy(studyAc, user);
				
				if(!submission.refStudyCodes.contains(externalStudy.code)){
					submission.refStudyCodes.add(externalStudy.code);
				}
				if(!listExternalStudies.contains(externalStudy.code)){
					listExternalStudies.add(externalStudy);
				}	
				// mettre à jour l'experiment pour la reference study :
				experiment.studyCode = externalStudy.code;

			} else {
				// Renseigner l'objet submission :
				if(!submission.refStudyCodes.contains(study.code)){
					submission.refStudyCodes.add(study.code);
				}
				if(!listStudies.contains(study.code)){
					listStudies.add(study);
				}	
				// mettre à jour l'experiment pour la reference study :
				System.out.println("STUDY_CODE=" + study.code);
				experiment.studyCode = study.code;
			}
			
			// surcharger l'experiment avec valeurs de l'utilisateur si mapUserExperiments exist
			
			UserExperimentType userExperiment = mapUserExperiments.get(experiment.code);
			if (userExperiment != null) {
								
				if (StringUtils.isNotBlank(userExperiment.getLibrarySource())){
					experiment.librarySource = userExperiment.getLibrarySource();
				}
				if (StringUtils.isNotBlank(userExperiment.getLibraryStrategy())){
					experiment.libraryStrategy = userExperiment.getLibraryStrategy();
				}
				if (StringUtils.isNotBlank(userExperiment.getLibrarySelection())){
					experiment.librarySelection = userExperiment.getLibrarySelection();
				}
				if (StringUtils.isNotBlank(userExperiment.getLibraryProtocol())){
					experiment.libraryConstructionProtocol = userExperiment.getLibraryProtocol();
				}
				if (StringUtils.isNotBlank(userExperiment.getLibraryName())){
					experiment.libraryName = userExperiment.getLibraryName();
				}
				if (StringUtils.isNotBlank(userExperiment.getNominalLength())){
					experiment.libraryLayoutNominalLength = new Integer(userExperiment.getNominalLength());
				}
				if (StringUtils.isNotBlank(userExperiment.getTitle())){
					experiment.title = userExperiment.getTitle();
				}
			}
					
			
			// Ajouter l'experiment avec le statut forcement à 'new' à l'objet submission :
			if (experiment.state.code.equalsIgnoreCase("new")) { 
				if(!submission.experimentCodes.contains(experiment.code)){
					listExperiments.add(experiment);
					submission.experimentCodes.add(experiment.code);
					System.out.println ("Ajout dans submission du expCode : " + experiment.code);
					if(experiment.run != null) {
						System.out.println ("Ajout dans submission du runCode : " + experiment.run.code);
						submission.runCodes.add(experiment.run.code);
					}// end if
				}// end if
			}// end if
			
			
		} // end for readset
		
		if (countError != 0){
			throw new SraException("Problemes pour creer la soumission \n" + errorMessage);
		}
		
		// valider tous les sample et experiments et les sauver dans la base avec statut
		contextValidation.setCreationMode();
		
		for (Sample sampleElt: listSamples) {
			//sampleElt.state = new State("inwaiting", user);
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", sampleElt.code)){	
				sampleElt.validate(contextValidation);
				MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sampleElt);
				System.out.println ("ok pour sauvegarde dans la base du sample " + sampleElt.code);
			}
		}	
		for (ExternalSample extSampleElt: listExternalSamples) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, ExternalSample.class, "code", extSampleElt.code)){	
				extSampleElt.validate(contextValidation);
				MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, extSampleElt);
				System.out.println ("ok pour sauvegarde dans la base du sample " + extSampleElt.code);
			}
		}
		
		for (ExternalStudy extStudyElt: listExternalStudies) {
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, ExternalStudy.class, "code", extStudyElt.code)){	
				extStudyElt.validate(contextValidation);
				MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, extStudyElt);
				System.out.println ("ok pour sauvegarde dans la base du study " + extStudyElt.code);
			}
		}	
		
		for (Experiment expElt: listExperiments) {
			//expElt.state = new State("inwaiting", user);
			expElt.validate(contextValidation);
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "code", expElt.code)){	
				MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, expElt);
				System.out.println ("sauvegarde dans la base de l'experiment " + expElt.code);
			}
		}		
		// update le study pour le statut 'uservalidate' mais aussi l'objet courant 
		study.state.code="used";
		study.traceInformation.modifyDate = new Date();
		study.traceInformation.modifyUser = VariableSRA.admin;
		MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, 
				DBQuery.is("code", study.code),
				DBUpdate.set("state.code", "uservalidate").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));	
		
		// update la configuration pour le statut 'used' mais aussi l'objet courant qui va etre passé dans contextValidation:
		config.state.code="used";
		config.traceInformation.modifyDate = new Date();
		config.traceInformation.modifyUser = VariableSRA.admin;
		
		MongoDBDAO.update(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, 
				DBQuery.is("code", config.code),
				DBUpdate.set("state.code", "used").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));	

		// valider submission une fois les experiment et sample sauves, et sauver submission
		//submission.state = new State("inwaiting", user);
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		contextValidation.getContextObjects().put("configuration", config);
		submission.validate(contextValidation);

		if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code",submission.code)){	
			submission.validate(contextValidation);
			MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
			System.out.println ("sauvegarde dans la base du submission " + submission.code);
		}
	
		if (contextValidation.hasErrors()){
			System.out.println("submission.validate produit des erreurs");
			// rallBack avec clean sur exp et sample et mise à jour study
			System.out.println("\ndisplayErrors dans SubmissionServices::createNewSubmission :");
			contextValidation.displayErrors(Logger.of("SRA"));
			System.out.println("\n end displayErrors dans SubmissionServices::createNewSubmission :");
			
			// enlever les samples, experiments et submission qui ont ete crées par le service :
			cleanDataBase(submission.code);
			
			throw new SraException("SubmissionServices::initNewSubmission::probleme validation  voir log: ");
			
		} 	
		System.out.println("Creation de la soumission " + submission.code);
		return submission.code;
	
	}


	
	private ExternalStudy fetchExternalStudy(String studyAc, String user) throws SraException {
		String externalStudyCode = SraCodeHelper.getInstance().generateExternalStudyCode(studyAc);
		ExternalStudy externalStudy;
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, ExternalStudy.class, "code", externalStudyCode)){
			System.out.println("Recuperation du study "+ externalStudyCode);
			externalStudy = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.ExternalStudy.class, externalStudyCode);
		} else {
			externalStudy = new ExternalStudy(); // objet avec state.code = submitted
			externalStudy.accession = studyAc;
			externalStudy.code = externalStudyCode;
			externalStudy.traceInformation.setTraceInformation(user);	
			externalStudy.state = new State("submitted", user);
		}
		return externalStudy;
	}
	
	

	
	public void activateSubmission(String submissionCode) throws SraException {
		// creer repertoire de soumission sur disque et faire liens sur données brutes

		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		if (submission == null){
			throw new SraException("aucun objet submission dans la base pour  : " + submissionCode);
		}
		try {
			// Determiner le repertoire de soumission:
			DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
			Date courantDate = new java.util.Date();
			String st_my_date = dateFormat.format(courantDate);
			if (StringUtils.isBlank((submission.projectCode))){
				throw new SraException("Dans activateSubmission: impossible de determiner le repertoire de soumission avec submission.projectCode à nul");
			} 
			submission.submissionDirectory = VariableSRA.submissionRootDirectory + File.separator + submission.projectCode + File.separator + st_my_date;
			File dataRep = new File(submission.submissionDirectory);
			System.out.println("Creation du repertoire de soumission et liens vers donnees brutes " + submission.submissionDirectory);
			if (dataRep.exists()){
				throw new SraException("Le repertoire " + dataRep + " existe deja !!! (soumission concurrente ?)");
			} else {
				if(!dataRep.mkdirs()){	
					throw new SraException("Impossible de creer le repertoire " + dataRep);
				}
			}
			for (String experimentCode: submission.experimentCodes) {
				Experiment expElt =  MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
			
				System.out.println("exp = "+ expElt.code);
				for (RawData rawData :expElt.run.listRawData){
					System.out.println("run = "+ expElt.run.code);
					File fileCible = new File(rawData.directory + File.separator + rawData.relatifName);
					File fileLien = new File(submission.submissionDirectory + File.separator + rawData.relatifName);
					if(fileLien.exists()){
						fileLien.delete();
					}
					if (!fileCible.exists()){
						System.out.println("Le fichier cible n'existe pas  : " + fileCible);
						throw new SraException("Le fichier cible n'existe pas  : " + fileCible);
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
				}
			}
		} catch (SraException e) {
			throw new SraException(" Dans activateSubmission" + e);			
		} catch (SecurityException e) {
			throw new SraException(" Dans activateSubmission pb SecurityException: " + e);
		} catch (UnsupportedOperationException e) {
			throw new SraException(" Dans activateSubmission pb UnsupportedOperationException: " + e);
		} catch (FileAlreadyExistsException e) {
			throw new SraException(" Dans activateSubmission pb FileAlreadyExistsException: " + e);
		} catch (IOException e) {
			throw new SraException(" Dans activateSubmission pb IOException: " + e);		
		}
		// Recuperer objet soumission dans base :
		Configuration config =  MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, submission.configCode);
		if (config == null) {
			throw new SraException("Dans activateSubmission aucun objet config dans la base pour " + submission.configCode);
		}			System.out.println("Aucun objet submission dans la base pour "+ submissionCode);

		if (StringUtils.isBlank(config.strategyStudy)){
			throw new SraException("Dans activateSubmission champs strategyStudy non renseigné pour config "+ submission.configCode);
		}
		if (StringUtils.isBlank(config.strategySample)){
			throw new SraException("Dans activateSubmission champs strategySample non renseigné pour config "+ submission.configCode);
		}
				
		// mettre à jour objet soumission et experiment et sample avec state ="inwaiting" :		
		if (submission.state.code.equalsIgnoreCase("uservalidate")) {
			
			// mettre à jour le champs submission.studyCode si besoin, si study à soumettre, si study avec state=uservalidate
			
			if (config.strategyStudy.equalsIgnoreCase("strategy_external_study")){ 
				// le status des study n'est pas à modifié et rien à soumettre
			} else {
				if (submission.refStudyCodes.size() > 1) {
					throw new SraException(" Dans activateSubmission refStudyCodes > 1 incompatible avec strategy_internal_study");			
				}
				for (String studyCode: submission.refStudyCodes) {
					Study study =  MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.studyCode);
					if (study.state.code.equalsIgnoreCase("uservalidate")){
						study.state.code = "inwaiting";
						// mettre a jour la liste des studies a soumettre dans objet submission :
						submission.studyCode = studyCode;
						// mettre a jour dans base l'objet study à soumettre avec bon state
						MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class,
								DBQuery.is("code", study.code).notExists("accession"),
								DBUpdate.set("state.code", "inwaiting").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 
					}
				}
			}

			// mettre à jour les samples pour le state :
			if (config.strategySample.equalsIgnoreCase("strategy_external_sample")){
				// le status des samples n'est pas modifié et rien a soumettre		
			} else {
				for (String sampleCode: submission.refSampleCodes) {
					Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, sampleCode);
					if ( sample.state.code.equalsIgnoreCase("uservalidate") ) {
						sample.state.code = "inwaiting";
						// mettre a jour la liste des samples a soumettre dans objet submission :
						if (! submission.sampleCodes.contains(sampleCode)){
							submission.sampleCodes.add(sampleCode);
						}
						MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
								DBQuery.is("code", sampleCode).notExists("accession"),
								DBUpdate.set("state.code", "inwaiting").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 			
					
					}
				}
			}
			// mettre à jour les experiment pour le state :
			for (String experimentCode: submission.experimentCodes) {
				Experiment expElt =  MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
				if ( expElt.state.code.equalsIgnoreCase("uservalidate") ) {
					expElt.state.code = "inwaiting";
					MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
							DBQuery.is("code", experimentCode).notExists("accession"),
							DBUpdate.set("state.code", "inwaiting").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 
				}
			}
		
			// mettre à jour la soumission pour le state et pour le directory :
			MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class,
					DBQuery.is("code", submission.code).notExists("accession"),
					DBUpdate.set("state.code", "inwaiting").set("studyCode", submission.studyCode).set("sampleCodes", submission.sampleCodes).set("submissionDirectory", submission.submissionDirectory).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 
		}
	}
		
	
	private Submission createSubmissionEntity(Configuration config, String studyCode, String user, Map<String, UserCloneType> mapUserClones) throws SraException{
		if (config==null) {
			throw new SraException("SubmissionServices::createSubmissionEntity::configuration : config null ???");
		}
		Submission submission = null;
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		submission = new Submission(config.projectCode, user);
		submission.code = SraCodeHelper.getInstance().generateSubmissionCode(config.projectCode);
		submission.submissionDate = courantDate;
		System.out.println("submissionCode="+ submission.code);
		submission.state = new State("new", user);
		submission.release = false;
		submission.configCode = config.code;
		submission.projectCode = config.projectCode;
		if (mapUserClones != null) {
			for (Iterator<Entry<String, UserCloneType>> iterator = mapUserClones.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, UserCloneType> entry = iterator.next();
				String cle = entry.getKey();
				UserCloneType userCloneType = entry.getValue();				
				System.out.println("cle du userClone = '" + entry.getKey() + "'");
				System.out.println("       study_ac : '" + entry.getValue().getStudyAc()+  "'");
				System.out.println("       sample_ac : '" + entry.getValue().getSampleAc()+  "'");
				UserCloneType submission_userClone = new UserCloneType();
				submission_userClone.setAlias(entry.getKey());
				submission_userClone.setStudyAc(userCloneType.getStudyAc());
				submission_userClone.setSampleAc(userCloneType.getSampleAc());
				submission.mapUserClone.put(entry.getKey(), submission_userClone);
			}	
		}

		if (StringUtils.isBlank(studyCode)){
			if (StringUtils.isBlank(config.strategyStudy) || ! config.strategyStudy.equalsIgnoreCase("strategy_external_study")){
				throw new SraException("Aucun studyCode et strategyStudy != 'strategy_external_study'");
			} else {
				// ok submission.release == false et submission sans study.
				//submission.external_study = true;
			}
		} else {
			Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, studyCode);
			if (study==null){
				throw new SraException("study " + study.code + " n'existe pas dans database");
			} 
			if (study.state == null) {
				throw new SraException("study.state== null incompatible avec soumission. =>  study.state.code in ('uservalidate', 'inwaiting', 'submitted')");
			}
			if (study.state.code.equalsIgnoreCase("new")){
			// declencher exception, la soumission ne peut se faire sans un study validé par user ou
			// study deja en cours de soumission voir soumis.
				throw new SraException("study.state.code='new' incompatible avec soumission. =>  study.state.code in ('uservalidate', 'inwaiting', 'submitted')");
			}
			// mettre à jour l'objet submission pour le study et la release_date :
			submission.refStudyCodes.add(study.code);
			submission.studyCode = study.code;
			Date date = new Date();
			if (study.releaseDate == null) {
				submission.release = false;
			} else {
				if (study.releaseDate.compareTo(date)<= 0){
					submission.release = true;
				} else {
					submission.release = false;
				}
			}
		}
		return submission;
	}

	public String getNcbiScientificName(Integer taxonId) throws IOException, XPathExpressionException  {
		Promise<WSResponse> homePage = WS.url("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=taxonomy&id="+taxonId+"&retmote=xml").get();
		Promise<Document> xml = homePage.map(response -> {
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
		});
		
		Document doc = xml.get(1000);
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String expression = "/TaxaSet/Taxon/ScientificName";

		//read a string value
		String scientificName = xPath.compile(expression).evaluate(doc);
		return scientificName;	
	}
		
		


	//todo: il reste scientificName, classification, comonName à renseigner sur la base de idTaxon, et description
	// voir si service web existant au NCBI (ou get_taxonId en interne ou encore base de AGC).
	public Sample fetchSample(ReadSet readSet, String projectCode, String strategySample, String user) throws SraException {
		// Recuperer pour chaque readSet les objets de laboratory qui existent forcemment dans mongoDB, 
		// et qui permettront de renseigner nos objets SRA :
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String laboratorySampleName = laboratorySample.name;

		String clone = laboratorySample.referenceCollab;
		String taxonId = laboratorySample.taxonCode;

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);

		String codeSample = SraCodeHelper.getInstance().generateSampleCode(readSet, projectCode, strategySample);
		Sample sample = null;
		// Si sample existe, prendre l'existant, sinon en creer un nouveau
		//if (services.SraDbServices.checkCodeSampleExistInSampleCollection(codeSample)) {
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", codeSample)){
			System.out.println("Recuperation du sample "+ codeSample);
			sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.Sample.class, codeSample);			
			//System.out.println(sample.clone);
			//System.out.println(sample.taxonId);
			//System.out.println(sample.title);
		} else {
			System.out.println("Creation du sample '"+ codeSample + "'");
			// creation du sample :
			sample = new Sample();
			sample.code = codeSample;
			sample.taxonId = new Integer(taxonId);
			// enrichir le sample avec scientific_name :
			try {
				String scientificName = getNcbiScientificName(sample.taxonId);
				sample.scientificName = scientificName;
			} catch (XPathExpressionException | IOException e) {
				e.printStackTrace();
				throw new SraException("impossible de recuperer le scientificName au ncbi pour le sample.code '"+ sample.code + "' et le taxonId '" + taxonId + "' : \n" + e.getMessage());
			}
			
			sample.clone = laboratorySample.referenceCollab;
			sample.projectCode = projectCode;
			sample.state = new State("new", user);
			sample.traceInformation.setTraceInformation(user);			
		}
		
		System.out.println("readSetCode = " + readSet.code);
		System.out.println("laboratorySampleCode = " + laboratorySampleCode);
		System.out.println("laboratorySampleName = " + laboratorySampleName);
		System.out.println("taxonId = " + taxonId);
		System.out.println("clone = " + clone);
		return sample;
	}
	
	
	
	private ExternalSample fetchExternalSample(String sampleAc, String user) throws SraException {
		String externalSampleCode = SraCodeHelper.getInstance().generateExternalSampleCode(sampleAc);
		ExternalSample externalSample;
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, ExternalSample.class, "code", externalSampleCode)){
			System.out.println("Recuperation du sample "+ externalSampleCode);
			externalSample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.ExternalSample.class, externalSampleCode);
		} else {
			externalSample = new ExternalSample(); // objet avec state.code = submitted
			externalSample.accession = sampleAc;
			externalSample.code = externalSampleCode;
			externalSample.state = new State("submitted", user);			
			externalSample.traceInformation.setTraceInformation(user);		
		}
		return externalSample;
	}
	
	
	
	
	/*
	 * 
	 */
	public Experiment createExperimentEntity(ReadSet readSet, String projectCode, String user) throws SraException {
		// On cree l'experiment pour le readSet demandé.
		// La validite du readSet doit avoir été testé avant.

		Experiment experiment = new Experiment(); 
		
		experiment.code = SraCodeHelper.getInstance().generateExperimentCode(readSet.code);
		experiment.readSetCode = readSet.code;
		experiment.projectCode = projectCode;
		experiment.traceInformation.setTraceInformation(user);
		System.out.println("expCode =" + experiment.code);
		String laboratoryRunCode = readSet.runCode;
		
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		
		/*InstrumentUsed instrumentUsed = laboratoryRun.instrumentUsed;
		System.out.println("instrumentUsed.code = " + instrumentUsed.code);
		System.out.println("instrumentUsed.typeCode = " + instrumentUsed.typeCode);
		System.out.println("instrumentUsed.typeCodeMin = '" + instrumentUsed.typeCode.toLowerCase()+"'");
		*/
		experiment.instrumentModel = VariableSRA.mapInstrumentModel.get(laboratoryRun.typeCode.toLowerCase());
		experiment.libraryLayoutNominalLength = null;
		
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
		if (experiment.libraryLayoutNominalLength != null) {
			System.out.println("valeur calculee libraryLayoutNominalLength  => "  + experiment.libraryLayoutNominalLength);
		}
		if (experiment.libraryLayoutNominalLength == null) {
			// mettre valeur theorique de libraryLayoutNominalLength (valeur a prendre dans readSet.sampleOnContainer.properties.nominalLength) 
			// voir recup un peu plus bas:
			Map<String, PropertyValue> sampleOnContainerProperties = readSet.sampleOnContainer.properties;
			if (sampleOnContainerProperties != null) {
				Set <String> listKeysSampleOnContainerProperties = sampleOnContainerProperties.keySet();  // Obtenir la liste des clés
			
				/*for(String k: listKeysSampleOnContainerProperties){
					System.out.print("cle = '" + k +"'");
					PropertyValue propertyValue = sampleOnContainerProperties.get(k);
					System.out.print(propertyValue.toString());
					System.out.println(", value  => "+propertyValue.value);
				} */
				
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
		if (experiment.libraryLayoutNominalLength == null) {
			throw new SraException("experiment sans libraryLayoutNominalLength : " + experiment.code);
		}
					
		
		experiment.state = new State("new", user); 
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String taxonId = laboratorySample.taxonCode;
		String taxonName = getTaxonName(taxonId);

		//String laboratoryRunCode = readSet.runCode;
		//models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		String technology = laboratoryRun.instrumentUsed.typeCode;
		experiment.title = taxonName + technology + "typeBanqueAmplifiee?";
		
		// Recuperer l'information spotLength, 
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
				experiment.spotLength = (Long) propertyNbCycle.value;
			}
		}
		// Ajouter les read_spec en fonction de l'information SINGLE ou PAIRED et forward-reverse et last_base_coord :
		experiment.libraryLayout = null;
		experiment.libraryLayoutOrientation = null;
		experiment.libraryConstructionProtocol = VariableSRA.libraryConstructionProtocol;
		if (laboratoryRun.properties.containsKey("sequencingProgramType")){	
			String libraryLayout =  (String) laboratoryRun.properties.get("sequencingProgramType").value;
			
			if (StringUtils.isNotBlank(libraryLayout)) { 
				if (libraryLayout.equalsIgnoreCase("SR")){
					experiment.libraryLayout = "SINGLE";
					experiment.libraryLayoutOrientation = "forward";
				} else if( libraryLayout.equalsIgnoreCase("PE") || libraryLayout.equalsIgnoreCase("MP")){
					experiment.libraryLayout = "PAIRED";
					Map<String, PropertyValue> sampleOnContainerProperties = readSet.sampleOnContainer.properties;
					if (sampleOnContainerProperties != null) {
						Set <String> listKeysSampleOnContainerProperties = sampleOnContainerProperties.keySet();  // Obtenir la liste des clés
					
						/*for(String k: listKeysSampleOnContainerProperties){
							System.out.print("cle = " + k);
							PropertyValue propertyValue = sampleOnContainerProperties.get(k);
							System.out.print(propertyValue.toString());
							System.out.println(", value  => "+propertyValue.value);
						} */
						if (sampleOnContainerProperties.containsKey("libProcessTypeCode")) {					
							PropertyValue libProcessTypeCode = sampleOnContainerProperties.get("libProcessTypeCode");
							String libProcessTypeCodeValue = (String) libProcessTypeCode.value;
							if(libProcessTypeCodeValue.equalsIgnoreCase("A")||libProcessTypeCodeValue.equalsIgnoreCase("C")||libProcessTypeCodeValue.equalsIgnoreCase("N")){
								experiment.libraryLayoutOrientation = "reverse-forward";
							} else if (libProcessTypeCodeValue.equalsIgnoreCase("W")||libProcessTypeCodeValue.equalsIgnoreCase("F")
										||libProcessTypeCodeValue.equalsIgnoreCase("H")||libProcessTypeCodeValue.equalsIgnoreCase("L")
										||libProcessTypeCodeValue.equalsIgnoreCase("Z")||libProcessTypeCodeValue.equalsIgnoreCase("MI")
										||libProcessTypeCodeValue.equalsIgnoreCase("K")){
								experiment.libraryLayoutOrientation = "forward-reverse";
							} else {
								throw new SraException("Pour le readSet " + readSet +  ", valeur de libProcessTypeCodeValue differente A,C,N, W, F, H, L ,Z, M, I, K => " + libProcessTypeCodeValue);
							}
						}
					}
				} else {
					throw new SraException("Pour le laboratoryRun " + laboratoryRun.code + " valeur de properties.sequencingProgramType differente de SR ou PE => " + libraryLayout);
				}
			}
			System.out.println("libraryLayout======"+libraryLayout);
		}

		
		experiment.run = createRunEntity(readSet, projectCode);

		// Renseigner l'objet experiment pour lastBaseCoord : Recuperer les lanes associées au
		// run associé au readSet et recuperer le lane contenant le readSet.code. C'est dans les
		// traitement de cette lane que se trouve l'information:
		// Un readSet est sur une unique lane, mais une lane peut contenir plusieurs readSet
		List<Lane> laboratoryLanes = laboratoryRun.lanes;
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
		System.out.println("'"+readSet.code+"'");
		experiment.readSpecs = new ArrayList<ReadSpec>();
		// IF ILLUMINA ET SINGLE  Attention != si nanopore et SINGLE
		if (StringUtils.isNotBlank(experiment.libraryLayout) && experiment.libraryLayout.equalsIgnoreCase("SINGLE") ) {
			ReadSpec readSpec_1 = new ReadSpec();
			readSpec_1.readIndex = 0; 
			readSpec_1.readLabel = "F";
			readSpec_1.readClass = "Application Read";
			readSpec_1.readType = "forward";
			readSpec_1.lastBaseCoord = (Integer) 1;
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
			readSpec_1.lastBaseCoord = (Integer) 1;
			experiment.readSpecs.add(readSpec_1);

			ReadSpec readSpec_2 = new ReadSpec();
			readSpec_2.readIndex = 1;
			readSpec_2.readLabel = "R";
			readSpec_2.readClass = "Application Read";
			readSpec_2.readType = "Reverse";
			readSpec_2.lastBaseCoord = experiment.lastBaseCoord;
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
			readSpec_1.lastBaseCoord = (Integer) 1;
			experiment.readSpecs.add(readSpec_1);

			ReadSpec readSpec_2 = new ReadSpec();
			readSpec_2.readIndex = 1;
			readSpec_2.readLabel ="F";
			readSpec_2.readClass = "Application Read";
			readSpec_2.readType = "Forward";
			readSpec_2.lastBaseCoord = experiment.lastBaseCoord;
			experiment.readSpecs.add(readSpec_2);
		}
		return experiment;
	}

	
	
	
	public String getTaxonName(String taxonId) {
		return "taxonNameFor_" + taxonId;
	}


	public Run createRunEntity(ReadSet readSet, String projectCode) {
		// On cree le run pour le readSet demandé.
		// La validite du readSet doit avoir été testé avant.

		// Recuperer pour le readSet la liste des fichiers associés:

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		InstrumentUsed instrumentUsed = laboratoryRun.instrumentUsed;
		
		List <models.laboratory.run.instance.File> list_files =  readSet.files;
		System.out.println("nbre de fichiers = " + list_files.size());
		
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
			String runInstanceRelatifFileName = runInstanceFile.fullname;
			String runInstanceExtentionFileName = runInstanceFile.extension;
			
			// conditions qui doivent etre suffisantes puisque verification préalable que le readSet
			// est bien valide pour la bioinformatique.
			if (runInstanceFile.usable 
					&& ! runInstanceExtentionFileName.equalsIgnoreCase("fna") && ! runInstanceExtentionFileName.equalsIgnoreCase("qual")
					&& ! runInstanceExtentionFileName.equalsIgnoreCase("fna.gz") && ! runInstanceExtentionFileName.equalsIgnoreCase("qual.gz")) {
					RawData rawData = new RawData();
					System.out.println("fichier " + runInstanceFile.fullname);
					rawData.extention = runInstanceFile.extension;
					rawData.directory = dataDir.replaceFirst("\\/$", ""); // oter / terminal si besoin
					rawData.relatifName = runInstanceFile.fullname;
					run.listRawData.add(rawData);
					if (runInstanceFile.properties != null && runInstanceFile.properties.containsKey("md5")) {
						System.out.println("Recuperation du md5 pour" + rawData.relatifName + rawData.extention);
						rawData.md5 = (String) runInstanceFile.properties.get("md5").value;
					}
					Set<String> listKeys = runInstanceFile.properties.keySet();
					
					for(String k: listKeys) {
						System.out.println("attention cle = " + k);
						PropertyValue propertyValue = runInstanceFile.properties.get(k);
						System.out.println(propertyValue.toString());
						System.out.println(propertyValue.value);
					}
			}
		}
		return run;
	}
	
	// delete la soumission et ses experiments et samples s'ils ne sont pas references par une autre soumission
	// Ne delete pas le study et la config associée car saisis par l'utilisateur qui peut vouloir les utiliser pour une prochaine soumission
	public static void cleanDataBase(String submissionCode) {
		System.out.println("Recherche objet submission dans la base pour "+ submissionCode);
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		
		if (submission==null){
			System.out.println("Aucun objet submission dans la base pour "+ submissionCode);
			return;
		} else {
			System.out.println("objet submission dans database pour "+ submissionCode);
			System.out.println("submission.accession dans cleanDataBase "+ submission.accession);
		}
		// On verifie que la donnée n'est pas connu de l'EBI avant de detruire
		// attention utiliser StringUtils.isNotBlank car le test d'une string null avec equal declenche une erreur sans message.
		if (StringUtils.isNotBlank(submission.accession)){
			System.out.println("objet submission avec AC : submissionCode = "+ submissionCode + " et submissionAC = "+ submission.accession);
			return;
		} 
		if (!StringUtils.isNotBlank(submission.accession)) {
			System.out.println("objet submission sans AC : submissionCode = "+ submissionCode);
			if (! submission.sampleCodes.isEmpty()) {
				for (String sampleCode : submission.refSampleCodes){
					// verifier que sample n'est pas utilisé par autre objet submission avant destruction
					List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("sampleCodes", sampleCode)).toList();
					if (submissionList.size() > 1) {
						for (Submission sub: submissionList) {
							System.out.println(sampleCode + " utilise par objet Submission " + sub.code);
						}
					} else {
						// todo : verifier qu'on ne detruit que des samples en new ou uservalidate
						System.out.println("deletion dans base pour sample "+sampleCode);
						MongoDBDAO.deleteByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.Sample.class, sampleCode);		
					}
				}
			}		
			if (! submission.experimentCodes.isEmpty()) {
				for (String experimentCode : submission.experimentCodes) {
					// verifier que l'experiment n'est pas utilisé par autre objet submission avant destruction
					List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("experimentCodes", experimentCode)).toList();
					if (submissionList.size() > 1) {
						for (Submission sub: submissionList) {
							System.out.println(experimentCode + " utilise par objet Submission " + sub.code);
						}
					} else {
						// todo : verifier qu'on ne detruit que des experiments en new ou uservalidate
						System.out.println("deletion dans base pour experiment "+experimentCode);
						MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);
					}
				}
			}
			// verifier que la config à l'etat used n'est pas utilisé par une autre soumission avant de remettre son etat à 'new'

			// update la configuration pour le statut en remettant le statut new si pas utilisé par ailleurs :
			List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("configCode", submission.configCode)).toList();
			if (submissionList.size() <= 1) {
				MongoDBDAO.update(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, 
						DBQuery.is("code", submission.configCode),
						DBUpdate.set("state.code", "new").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));	
			}
			System.out.println("deletion dans base pour submission "+submissionCode);
			MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		}
	}

	
}

	
