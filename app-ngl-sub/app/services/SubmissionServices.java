package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.Logger;
import validation.ContextValidation;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mongojack.DBQuery;

// todo : implementer recuperation instrumentModel et libraryName.
// mettre valeur theorique de libraryLayoutNominalLength si valeur calculée inexistante 

public class SubmissionServices {

	
	public String createNewSubmission(String projectCode, List<ReadSet> readSets, String studyCode, String configCode, String user) throws SraException, IOException {
	//	public String doNewSubmission(String configurationCode, List<ReadSet> readSets) throws SraException {
		// Pour une premiere soumission d'un readSet, on peut devoir utiliser un study ou un sample existant, deja soumis à l'EBI, ou non
		// en revanche on ne doit pas utiliser un experiment ou un run existant
		// Creation d'un nouvel objet submission avec code qui n'existe pas encore dans db.
		// liste des experiments :
		Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, studyCode);
		if (study==null){
			throw new SraException("study à null incompatible avec soumission");
		}

		Configuration config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, configCode);
		if (config==null){
			throw new SraException("config à null incompatible avec soumission");
		}
		//List <Study> listStudys = new ArrayList<Study>();
		List <Experiment> listExperiments = new ArrayList<Experiment>();
		List <Sample> listSamples = new ArrayList<Sample>();
		
		Submission submission = createSubmissionEntity(projectCode, config.code, user);
		if (config.strategySample == null) {
			throw new SraException("strategySample à null incompatible avec soumission");
		}
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
		Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, projectCode);
		// Soumission qui ne peut se faire que si study complet avec champs saisi par utilisateur
		// study qui doit etre donné en parametre car doit etre completé par l'utilisateur, alors l'utiliser (de la forme 'study_AUP')

		// Si le study a ete validé par l'utilisateur, et qu'il n'a jamais été soumis, alors le charger
		// dans l'objet submission pour envoie du xml à l'EBI
		// Si le study a ete soumis à l'ebi alors son statut est different de UserValidate.
		if (study == null) {
			throw new SraException("Study null incompatible avec soumission");
		}
		if ((study.state == null) || (study.state.code.equals("new"))) {
			// declencher exception, la soumission ne peut se faire sans un study validé par user ou
			// study deja en cours de soumission voir soumis.
			throw new SraException("Pour le study " + study.code + "status du study à null ou new incompatible avec soumission");
		}

		if (study.state.code.equals("userValidate")) {
			submission.studyCode = study.code;
		} // else rien c'est peut-etre un study deja soumis ou en attente de soumission
		  // auquel cas pas de changement d'etat ni de sauvegarde et soumission à l'EBI
		DbUtil dbUtil = new DbUtil();
		for(ReadSet readSet : readSets) {
			System.out.println("readSet :" + readSet.code);
			// Verifier que c'est une premiere soumission pour ce readSet
			// Verifiez qu'il n'existe pas d'objet experiment referencant deja ce readSet
			//Boolean alreadySubmit = services.SraDbServices.checkCodeReadSetExistInExperimentCollection(readSet.code);
			Boolean alreadySubmit = MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "readSetCode", readSet.code);		
			if ( alreadySubmit ) {
				// signaler erreur et passer au readSet suivant
				System.out.println("soumission existante pour :"+readSet.code);
				continue;
			} 
			// Verifier que ce readSet est bien valide avant soumission :
			if (! readSet.bioinformaticValuation.valid.equals(TBoolean.TRUE)) {
				System.out.println("Pas de soumission pour le readset " + readSet.code + ", car non valide pour la bioinformatique :");
				continue;
			}

			//String codeRun = "run_" + readSet.code;
			//String codeExperiment = "exp_" + readSet.code;

			// Creer les objets avec leurs alias ou code, les instancier completement et les sauver.

			// Creer le sample si besoin :
			Sample sample = fetchSample(readSet, projectCode, config.strategySample, user);
			// Renseigner l'objet submission :
			// Verifier que l'objet sample n'a jamais ete soumis et n'est pas en cours de soumission
			System.out.println("sample = " + sample + " et state="+ sample.state.code);
			if (sample.state.code.equals("new")) {
				System.out.println ("ok mon sample est bien à new");
				if(!submission.sampleCodes.contains(sample.code)){
					System.out.println ("Ajout du sample code dans submission ");

					submission.sampleCodes.add(sample.code);
					listSamples.add(sample);
				}
			}
				
			//VariableSRA.submissionDirectory + File.separator + projectCode + File.separator + st_my_date;
			Experiment experiment = createExperimentEntity(readSet, projectCode, user);

			// mettre à jour l'objet experiment 
			experiment.studyCode = study.code;
			experiment.sampleCode = sample.code;
			experiment.librarySelection = config.librarySelection;
			experiment.librarySource = config.librarySource;
			experiment.libraryStrategy = config.libraryStrategy;

			// Ajouter l'experiment à l'objet submission :
			if (experiment.state.code.equals("new")) {
				if(!submission.experimentCodes.contains(experiment.code)){
					listExperiments.add(experiment);
					submission.experimentCodes.add(experiment.code);
					if(experiment.run != null) {
						submission.runCodes.add(experiment.run.code);
					}
				}
			}
		}
		
		// valider tous les study et les updater dans la base pour le statut :
		ContextValidation contextValidation = new ContextValidation(user);
		contextValidation.setUpdateMode();
		
			
		study.state = new State("inWaiting", user);
		study.traceInformation.modifyUser = VariableSRA.admin;
		study.traceInformation.modifyDate =  new Date();
		//studyElt.existingStudyType = "toto"; // introduction erreur pour tester rallback
		study.validate(contextValidation);
		// le study est a sauver avec leur nouveau statut inWaiting
		
		if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", study.code)){	
			MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		}
		
		// valider tous les sample et experiments et les sauver dans la base avec statut
		contextValidation.setCreationMode();
		for (Sample sampleElt: listSamples) {
			sampleElt.state = new State("inWaiting", user);
			sampleElt.validate(contextValidation);
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", sampleElt.code)){	
				MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sampleElt);
			}
		}		
		for (Experiment expElt: listExperiments) {
			expElt.state = new State("inWaiting", user);
			expElt.validate(contextValidation);
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "code", expElt.code)){	
				MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, expElt);
			}
		}		
		// valider submission une fois les experiment et sample sauves, et sauver submission
		submission.state = new State("inWaiting", user);
		submission.validate(contextValidation);
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code",submission.code)){	
			MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);
		}
		if (contextValidation.hasErrors()){
			// rallBack avec clean sur exp et sample et mise à jour study
			System.out.println("\ndisplayErrors dans SubmissionServices::createNewSubmission :");
			contextValidation.displayErrors(Logger.of("SRA"));
			System.out.println("\n end displayErrors dans SubmissionServices::createNewSubmission :");
			// resauver les study avec leur statut initial 'userValidate'
			study.state = new State("userValidate", user);
			study.traceInformation.modifyUser = VariableSRA.admin;
			study.traceInformation.modifyDate =  new Date();
			study.validate(contextValidation);
			MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
			// enlever les samples, experiments et submission qui ont ete crées par le service :
			cleanDataBase(submission.code);
		} else {
			// creer repertoire de soumission sur disque et faire liens sur données brutes
			File dataRep = new File(submission.submissionDirectory);
			dataRep.mkdirs();	
			for (Experiment expElt: listExperiments) {
				for (RawData rawData :expElt.run.listRawData){
					
					File fileCible = new File(rawData.directory + File.separator + rawData.relatifName);
					File fileLien = new File(submission.submissionDirectory + File.separator + rawData.relatifName);
					Path lien = Paths.get(fileLien.getPath());
					Path cible = Paths.get(fileCible.getPath());
					Files.createSymbolicLink(lien, cible);
					//String cmd = "ln -s -f " + rawData.directory + File.separator + rawData.relatifName
					//+ " " + submission.submissionDirectory + File.separator + rawData.relatifName;
					//System.out.println("cmd = " + cmd);
				}
			}
			//XmlServices.writeAllXml(submission.code);
			// sauver submission :
			MongoDBDAO.save(InstanceConstants.SRA_SUBMISSION_COLL_NAME, submission);			
		}

		return submission.code;
	}

	
	//todo : aucun champs rempli hormis le codeStudy
	public Study fetchStudy(String projectCode) {
		Study study = null;
		String codeStudy = "study_" + projectCode;
		// Si study existe, prendre l'existant, sinon en creer un nouveau
		//if (services.SraDbServices.checkCodeStudyExistInStudyCollection(codeStudy)) {
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", codeStudy)){
			study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, codeStudy);			
		} else {
			study = new Study();
			study.code = codeStudy;
		}
		return study;
	}


	public Submission createSubmissionEntity(String projectCode, String configCode, String user){
		System.out.println ("config.code = " + configCode);
		Submission submission = null;
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		/*
		String headerSubmissionCode = "cns" + "_" + projectCode + "_" + st_my_date;
		String submissionCode = headerSubmissionCode + "_" + "1";
		// si submissionCode existe deja, alors en creer un nouveau :
		int i = 1;
		while (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code", submissionCode)) {
			i++;
			submissionCode = headerSubmissionCode + "_" + i;
		}*/
		
		submission.code = SraCodeHelper.getInstance().generateSubmissionCode(projectCode);
		submission = new Submission(projectCode, user);
		submission.submissionDate = courantDate;
		System.out.println("submissionCode="+ submission.code);
		submission.state = new State("new", user);
		if (configCode != null) {
			submission.config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, configCode);
		}
		return submission;
	}


/*	public String getSampleCode(ReadSet readSet, String projectCode, String strategySample) {
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String laboratorySampleName = laboratorySample.name;

		String clone = laboratorySample.referenceCollab;
		String taxonId = laboratorySample.taxonCode;

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);

		String codeSample = null;
		if (strategySample.equalsIgnoreCase("STRATEGY_SAMPLE_CLONE")) {
			codeSample = "sample_" + projectCode + "_" + taxonId + "_" + clone;
		} else if (strategySample.equalsIgnoreCase("STRATEGY_SAMPLE_TAXON")) {
			codeSample = "sample_" + projectCode + "_" + taxonId;
		} else if (strategySample.equalsIgnoreCase("STRATEGY_NO_SAMPLE")) {
			//envisager d'avoir des fichiers de correspondance 
		} else {
			// Declencher une erreur.
		}	
		return codeSample;
	}
*/


	//todo: il reste scientificName, classification, comonName à renseigner sur la base de idTaxon, et description
	// voir si service web existant au NCBI (ou get_taxonId en interne ou encore base de AGC).
	public Sample fetchSample(ReadSet readSet, String projectCode, String strategySample, String user) {
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
			System.out.println(sample.clone);
			System.out.println(sample.taxonId);
			System.out.println(sample.title);

		} else {
			System.out.println("Creation du sample '"+ codeSample + "'");
			// creation du sample :
			sample = new Sample();
			sample.code = codeSample;
			sample.taxonId = new Integer(taxonId);
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
		//System.out.println("readSetCode =" + readSet.code);
		
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
						//System.out.println("valeur calculee libraryLayoutNominalLength  => "  + experiment.libraryLayoutNominalLength);
					} 
					if (pairs.containsKey("estimatedMPInsertSize")) {
						PropertyValue estimatedInsertSize = pairs.get("estimatedMPInsertSize");
						experiment.libraryLayoutNominalLength = (Integer) estimatedInsertSize.value;
						//System.out.println("valeur calculee libraryLayoutNominalLength  => "  + experiment.libraryLayoutNominalLength);
					}	
				}
			}
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

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
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
			
			if (libraryLayout != null) {
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
								experiment.libraryLayoutOrientation = "forward-reverse";
							} else if (libProcessTypeCodeValue.equalsIgnoreCase("W")||libProcessTypeCodeValue.equalsIgnoreCase("F")
										||libProcessTypeCodeValue.equalsIgnoreCase("H")||libProcessTypeCodeValue.equalsIgnoreCase("L")
										||libProcessTypeCodeValue.equalsIgnoreCase("Z")||libProcessTypeCodeValue.equalsIgnoreCase("M")
										||libProcessTypeCodeValue.equalsIgnoreCase("I")||libProcessTypeCodeValue.equalsIgnoreCase("K")){
								experiment.libraryLayoutOrientation = "reverse-forward";
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
			experiment.run = createRunEntity(readSet, projectCode);
		}


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
		// IF ILLUMINA ET SINGLE
		if (experiment.libraryLayout != null && experiment.libraryLayout.equalsIgnoreCase("SINGLE") ) {
			ReadSpec readSpec_1 = new ReadSpec();
			readSpec_1.readIndex = 0; 
			readSpec_1.readClass = "Application Read";
			readSpec_1.readType = "Forward";
			readSpec_1.lastBaseCoord = (Integer) 1;
			experiment.readSpecs.add(readSpec_1);
		}

		// IF ILLUMINA ET PAIRED ET "forward-reverse"
		if (experiment.libraryLayout != null && experiment.libraryLayout.equalsIgnoreCase("PAIRED") 
				&& experiment.libraryLayoutOrientation != null && experiment.libraryLayoutOrientation.equalsIgnoreCase("forward-reverse") ) {
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
		if (experiment.libraryLayout!= null && experiment.libraryLayout.equalsIgnoreCase("PAIRED") 
				&& experiment.libraryLayoutOrientation!= null && experiment.libraryLayoutOrientation.equalsIgnoreCase("reverse-forward") ) {
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

		//String laboratoryRunCode = readSet.runCode;
		//models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);

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
					rawData.extention = runInstanceFile.extension;
					rawData.directory = dataDir.replaceFirst("\\/$", ""); // oter / terminal si besoin
					rawData.relatifName = runInstanceFile.fullname;
					run.listRawData.add(rawData);
					if (runInstanceFile.properties != null && runInstanceFile.properties.containsKey("md5")) {
						rawData.md5 = (String) runInstanceFile.properties.get("md5").value;
					}					
			}
		}
		return run;
	}
	
	
	public static void cleanDataBase(String submissionCode) {
		System.out.println("Recherche objet submission dans la base pour "+ submissionCode);

		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);

		if (submission==null){
			System.out.println("Aucun objet submission dans la base pour "+ submissionCode);
			return;
		}
		// On verifie que la donnée n'est pas connu de l'EBI avant de detruire
		if (submission.accession == null || submission.accession.equals("")) {
			System.out.println("L'objet submission contient un AC. submissionCode = "+ submissionCode + " et submissionAC = "+ submission.accession);
			if (! submission.sampleCodes.isEmpty()) {
				for (String sampleCode : submission.sampleCodes){
					// verifier que sample n'est pas utilisé par autre objet submission avant destruction
					List <Submission> submissionList = MongoDBDAO.find(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, DBQuery.in("sampleCodes", sampleCode)).toList();
					if (submissionList.size() > 1) {
						for (Submission sub: submissionList) {
							System.out.println(sampleCode + " utilise par objet Submission " + sub.code);
						}
					} else {
						System.out.println("deletion dans base pour "+sampleCode);
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
						System.out.println("deletion dans base pour "+experimentCode);
						MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);
					}
				}
			}			
			MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		}
	}

	
}

	
