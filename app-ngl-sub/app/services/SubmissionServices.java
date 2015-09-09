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
import models.laboratory.run.instance.InstrumentUsed;
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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
// todo : implementer recuperation instrumentModel et libraryName.

public class SubmissionServices {

	
	public String initNewSubmission(String projectCode, List<String> readSetCodes, String studyCode, String configCode, String user, ContextValidation contextValidation) throws SraException, IOException {
		// Pour une premiere soumission d'un readSet, on peut devoir utiliser un study ou un sample existant, deja soumis à l'EBI, ou non
		// en revanche on ne doit pas utiliser un experiment ou un run existant
		
		//System.out.println("studyCode = " + studyCode);
		if (StringUtils.isBlank(studyCode)) {
			throw new SraException("studyCode à null incompatible avec soumission");
		}
		Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, studyCode);
		if (study==null){
			throw new SraException("study " + study.code + " n'existe pas dans database");
		} 
		if (study.state == null) {
			throw new SraException("study.state== null incompatible avec soumission. =>  study.state.code in ('userValidate', 'inWaiting', 'submitted')");
		}
		if (study.state.code.equalsIgnoreCase("new")){
		// declencher exception, la soumission ne peut se faire sans un study validé par user ou
		// study deja en cours de soumission voir soumis.
			throw new SraException("study.state.code='new' incompatible avec soumission. =>  study.state.code in ('userValidate', 'inWaiting', 'submitted')");
		}
		
		if (StringUtils.isBlank(configCode)) {
			throw new SraException("configCode à null incompatible avec soumission");
		}
		Configuration config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, configCode);
		if (config==null){
			throw new SraException("config " + config.code + " n'existe pas dans database");
		}
		if (!config.state.code.equalsIgnoreCase("userValidate")){
			throw new SraException("config.state.code != 'userValidate' incompatible avec soumission");
		}		
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

		Submission submission = createSubmissionEntity(projectCode, config.code, user);
		if (StringUtils.isBlank(config.strategySample)) {
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

		
		submission.refStudyCode = study.code;
		/*if (!study.state.code.equals("userValidate")) { // study.state.code in ('new', 'userValidate', 'inWaiting', 'submitted').
			submission.studyCode = study.code;
		}*/
		
		for(ReadSet readSet : readSets) {
			System.out.println("readSet :" + readSet.code);
			// Verifier que c'est une premiere soumission pour ce readSet
			// Verifiez qu'il n'existe pas d'objet experiment referencant deja ce readSet
			//Boolean alreadySubmit = services.SraDbServices.checkCodeReadSetExistInExperimentCollection(readSet.code);
			Boolean alreadySubmit = MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "readSetCode", readSet.code);		
			if ( alreadySubmit ) {
				// signaler erreur et passer au readSet suivant
				System.out.println("?? soumission existante pour :"+readSet.code);
				// Recuperer exp dans mongo
				continue;
			} 
			// Verifier que ce readSet est bien valide avant soumission :
			if (! readSet.bioinformaticValuation.valid.equals(TBoolean.TRUE)) {
				System.out.println("Pas de soumission pour le readset " + readSet.code + ", car non valide pour la bioinformatique :");
				continue;
			}


			// Creer les objets avec leurs alias ou code, les instancier completement et les sauver.

			// Creer le sample si besoin :
			Sample sample = fetchSample(readSet, projectCode, config.strategySample, user);
			// Renseigner l'objet submission :
			// Verifier que l'objet sample n'a jamais ete soumis et n'est pas en cours de soumission
			System.out.println("sample = " + sample + " et state="+ sample.state.code);
			if(!submission.refSampleCodes.contains(sample.code)){
				submission.refSampleCodes.add(sample.code);
			}
			
			if (sample.state.code.equals("new")) {
				//System.out.println ("ok mon sample est bien à new ou userValidate");
				/*if(!submission.sampleCodes.contains(sample.code)){
					System.out.println ("Ajout dans submission du sampleCode : " + sample.code);
					//submission.sampleCodes.add(sample.code);
					
				}*/
				if(!listSamples.contains(sample.code)){
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
			experiment.libraryConstructionProtocol = config.libraryConstructionProtocol;
			// Ajouter l'experiment avec le statut forcement à 'new' à l'objet submission :
			if (experiment.state.code.equalsIgnoreCase("new")) { 
				if(!submission.experimentCodes.contains(experiment.code)){
					listExperiments.add(experiment);
					submission.experimentCodes.add(experiment.code);
					System.out.println ("Ajout dans submission du expCode : " + experiment.code);
					if(experiment.run != null) {
						System.out.println ("Ajout dans submission du runCode : " + experiment.run.code);
						submission.runCodes.add(experiment.run.code);
					}
				}
			}
		}
		
		// le study avec un state in ('userValidate', 'inWaiting', 'submitted') a deja ete validé :
		// inutile :valider  le study et l'updater dans la base pour le statut :
		/*contextValidation = new ContextValidation(user);
		contextValidation.setUpdateMode();
		contextValidation.getContextObjects().put("type", "sra");
      
		//study.state = new State("inWaiting", user);
		
		study.traceInformation.modifyUser = VariableSRA.admin;
		study.traceInformation.modifyDate =  new Date();
		study.validate(contextValidation);
		System.out.println("contextValidation.errors pour study dans SubmissionServices:");
		contextValidation.displayErrors(Logger.of("SRA"));
		
		// le study est a sauver avec leur nouveau statut inWaiting
		MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		System.out.println ("sauvegarde dans la base avec status 'inWainting' du study " + study.code);
		*/
		
		// valider tous les sample et experiments et les sauver dans la base avec statut
		contextValidation.setCreationMode();
		
		for (Sample sampleElt: listSamples) {
			//sampleElt.state = new State("inWaiting", user);
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", sampleElt.code)){	
				sampleElt.validate(contextValidation);
				MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sampleElt);
				System.out.println ("ok pour sauvegarde dans la base du sample " + sampleElt.code);
			}
		}		
		for (Experiment expElt: listExperiments) {
			//expElt.state = new State("inWaiting", user);
			expElt.validate(contextValidation);
			if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, "code", expElt.code)){	
				MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, expElt);
				System.out.println ("sauvegarde dans la base de l'experiment " + expElt.code);
			}
		}		
		
		// valider submission une fois les experiment et sample sauves, et sauver submission
		//submission.state = new State("inWaiting", user);
		contextValidation.setCreationMode();
		contextValidation.getContextObjects().put("type", "sra");
		submission.validate(contextValidation);

		if (!MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code",submission.code)){	
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
			
			throw new SraException("SubmissionServices::createNewSubmission::probleme validation  voir log: ");
			
		} else {
			System.out.println("Creation de la soumission " + submission.code);
			return submission.code;
		}
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

	
	
	public void activateSubmission(String submissionCode) throws SraException {
		// creer repertoire de soumission sur disque et faire liens sur données brutes

		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, submissionCode);
		if (submission == null){
			throw new SraException("aucun objet submission dans la base pour  : " + submissionCode);
		}
		
		try {
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
		
		// mettre à jour objet soumission et experiment et sample avec state ="inWaiting" :		
		if (submission.state.code.equalsIgnoreCase("userValidate")) {
			// mettre à jour le champs submission.studyCode si besoin, si study à soumettre, si study avec stateuserValidate
			Study study =  MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, submission.refStudyCode);
			if (study.state.code.equalsIgnoreCase("userValidate")){
				submission.studyCode = submission.refStudyCode;
				study.state.code = "inWaiting";
				MongoDBDAO.update(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class,
						DBQuery.is("code", study.code).notExists("accession"),
						DBUpdate.set("state.code", "inWaiting").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 
			}

			// mettre à jour les samples pour le state :
			for (String sampleCode: submission.refSampleCodes) {
				Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, sampleCode);
				if ( sample.state.code.equalsIgnoreCase("userValidate") ) {
					sample.state.code = "inWaiting";
					// mettre a jour la liste des samples a soumettre dans objet submission :
					if (! submission.sampleCodes.contains(sampleCode)){
						submission.sampleCodes.add(sampleCode);
					}
					MongoDBDAO.update(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class,
							DBQuery.is("code", sampleCode).notExists("accession"),
							DBUpdate.set("state.code", "inWaiting").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 			
					
				}
			}
			// mettre à jour les experiment pour le state :
			for (String experimentCode: submission.experimentCodes) {
				Experiment expElt =  MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
				if ( expElt.state.code.equalsIgnoreCase("userValidate") ) {
					expElt.state.code = "inWaiting";
					MongoDBDAO.update(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, Experiment.class,
							DBQuery.is("code", experimentCode).notExists("accession"),
							DBUpdate.set("state.code", "inWaiting").set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 
				}
			}
			// mettre à jour la soumission pour le state :
			MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class,
					DBQuery.is("code", submission.code).notExists("accession"),
					DBUpdate.set("state.code", "inWaiting").set("studyCode", submission.studyCode).set("sampleCodes", submission.sampleCodes).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date())); 

		}
	}
		
	
	public Submission createSubmissionEntity(String projectCode, String configCode, String user){
		System.out.println ("config.code = " + configCode);
		Submission submission = null;
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		submission = new Submission(projectCode, user);
		submission.code = SraCodeHelper.getInstance().generateSubmissionCode(projectCode);
		submission.submissionDate = courantDate;
		System.out.println("submissionCode="+ submission.code);
		submission.state = new State("new", user);
		if (StringUtils.isNotBlank(configCode)) {
			submission.config = MongoDBDAO.findByCode(InstanceConstants.SRA_CONFIGURATION_COLL_NAME, Configuration.class, configCode);
		}
		return submission;
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
						// todo : verifier qu'on ne detruit que des samples en new ou userValidate
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
						// todo : verifier qu'on ne detruit que des experiments en new ou userValidate
						System.out.println("deletion dans base pour experiment "+experimentCode);
						MongoDBDAO.deleteByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);
					}
				}
			}
			System.out.println("deletion dans base pour submission "+submissionCode);
			MongoDBDAO.deleteByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		}
	}

	
}

	
