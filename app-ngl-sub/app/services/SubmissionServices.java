package services;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.sra.experiment.instance.Experiment;
import models.sra.experiment.instance.RawData;
import models.sra.experiment.instance.ReadSpec;
import models.sra.experiment.instance.Run;
import models.sra.sample.instance.Sample;
import models.sra.study.instance.Study;
import models.sra.submission.instance.Submission;
import models.sra.utils.SraException;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import fr.cea.ig.MongoDBDAO;


public class SubmissionServices {

	public String createNewSubmission(String projectCode, List<ReadSet> readSets, Study study, String strategySample) throws SraException {
	//	public String doNewSubmission(String configurationCode, List<ReadSet> readSets) throws SraException {
		// Pour une premiere soumission d'un readSet, on peut devoir utiliser un study ou un sample existant, deja soumis à l'EBI, ou non
		// en revanche on ne doit pas utiliser un experiment ou un run existant
		// Creation d'un nouvel objet submission avec code qui n'existe pas encore dans db.
		Submission submission = createSubmissionEntity(projectCode);
		submission.strategySample = strategySample;
		//System.out.println("strategySample :"+strategySample);
		Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, projectCode);
		// Soumission qui ne peut se faire que si study complet avec champs saisi par utilisateur
		// study qui doit etre donné en parametre car doit etre completé par l'utilisateur, alors l'utiliser (de la forme 'study_AUP')

		// Si le study a ete validé par l'utilisateur, et qu'il n'a jamais été soumis, alors le charger
		// dans l'objet submission pour envoie du xml à l'EBI.
		// Si le study a ete soumis à l'ebi alors son statut est different de UserValidate.
		if (study.state == null) {
			// declencher exception, la soumission ne peut se faire sans un study validé par user ou
			// study deja en cours de soumission voir soumis.
			throw new SraException("Pour le study " + study.code + "status du study à null incompatible avec soumission");
		}
		if (study.state.equals("validate")) {
			if (!submission.studyCodes.contains(study.code)){
				submission.studyCodes.add(study.code);
			}
		}
		
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
			Sample sample = fetchSample(readSet, projectCode, strategySample);
			// Renseigner l'objet submission :
			// Verifier que l'objet sample n'a jamais ete soumis et n'est pas en cours de soumission
			//if (( sample.state == null) || (sample.state.equals("NewSubmission"))) {
			
				if(!submission.sampleCodes.contains(sample.code)){
					submission.sampleCodes.add(sample.code);
					//sample.state.code = "InWaitingSubmission";
				}
			
			//}
				
			// stoquer le sample dans base si besoin mais peut avoir ete stoquee lors precedente soumission
			//if (! services.SraDbServices.checkCodeSampleExistInSampleCollection(sample.code)) {
			if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", sample.code)){	
				/////////MongoDBDAO.save(InstanceConstants.SRA_SAMPLE_COLL_NAME, sample);
			}
			System.out.println("apres insertion dans db du sample "+ sample.code);

			Run run = createRunEntity(readSet, projectCode);
			System.out.println("bien sorti de createRunEntity" );

			Experiment experiment = createExperimentEntity(readSet, projectCode);

			System.out.println("aliasStudy = " + study.code);
			System.out.println("aliasSample = " + sample.code);
			System.out.println("aliasExperiment = " + experiment.code);
			System.out.println("aliasRun = " + run.code);
			// mettre à jour l'objet experiment 
			experiment.studyCode = study.code;
			experiment.sampleCode = sample.code;
			experiment.run = run;
			
			// Ajouter l'experiment à l'objet submission :
			if(!submission.experimentCodes.contains(experiment.code)){
				submission.experimentCodes.add(experiment.code);
			}
			// stoquer experiment dans base si besoin, sinon declencher erreur	
			////////dbUtil.save(experiment);
		}
		
		// stoquer submission dans base si besoin mais sinon declencher erreur
		/////////dbUtil.save(submission);

		// stoquer study dans base si besoin, car peut avoir ete cree pour precedente soumission
		//if (! services.SraDbServices.checkCodeStudyExistInStudyCollection(study.code)) {
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", study.code)){
			//////////MongoDBDAO.save(InstanceConstants.SRA_STUDY_COLL_NAME, study);
		} 
		return submission.code;
	}

		//submission.projectCode=readSet.projectCode;
		//submission.studyCodes=new ArrayList<String>();
	

	//todo : aucun champs rempli hormis le codeStudy
	public Study fetchStudy(String projectCode) {
		Study study = null;
		String codeStudy = "study_" + projectCode;
		// Si study existe, prendre l'existant, sinon en creer un nouveau
		//if (services.SraDbServices.checkCodeStudyExistInStudyCollection(codeStudy)) {
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_STUDY_COLL_NAME, Study.class, "code", codeStudy)){
			study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, codeStudy);			
		} else {
			study = new Study();
			study.code = codeStudy;
		}
		return study;
	}

	/*
	 * 
	 */
	public Submission createSubmissionEntity(String projectCode){
		Submission submission = null;
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");	
		Date courantDate = new java.util.Date();
		String st_my_date = dateFormat.format(courantDate);	
		String headerSubmissionCode = "cns" + "_" + projectCode + "_" + st_my_date;
		String submissionCode = headerSubmissionCode + "_" + "1";
		// si submissionCode existe deja, alors en creer un nouveau :
		int i = 1;
		while (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, "code", submissionCode)) {
			i++;
			submissionCode = headerSubmissionCode + "_" + i;
		}
		
		submission = new Submission();
		submission.projectCode = projectCode;
		submission.code = submissionCode;
		submission.submissionDate = courantDate;
		System.out.println("submissionCode="+ submissionCode);
		return submission;
	}


	public String getSampleCode(ReadSet readSet, String projectCode, String strategySample) {
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



	//todo: il reste scientificName, classification, comonName à renseigner sur la base de idTaxon, et description
	// voir si service web existant au NCBI (ou get_taxonId en interne ou encore base de AGC).
	public Sample fetchSample(ReadSet readSet, String projectCode, String strategySample) {
		// Recuperer pour chaque readSet les objets de laboratory qui existent forcemment dans mongoDB, 
		// et qui permettront de renseigner nos objets SRA :
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String laboratorySampleName = laboratorySample.name;

		String clone = laboratorySample.referenceCollab;
		String taxonId = laboratorySample.taxonCode;

		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);

		String codeSample = getSampleCode(readSet, projectCode, strategySample);
		Sample sample = null;
		// Si sample existe, prendre l'existant, sinon en creer un nouveau
		//if (services.SraDbServices.checkCodeSampleExistInSampleCollection(codeSample)) {
		if (MongoDBDAO.checkObjectExist(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class, "code", codeSample)){
			System.out.println("Recuperation du sample "+ codeSample);
			sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.sample.instance.Sample.class, codeSample);			
			System.out.println(sample.clone);
			System.out.println(sample.taxonId);
			System.out.println(sample.title);

		} else {
			System.out.println("Creation du sample "+ codeSample);
			// creation du sample :
			sample = new Sample();
			sample.code = codeSample;
			sample.taxonId = new Integer(taxonId);
			sample.clone = laboratorySample.referenceCollab;
			sample.projectCode = projectCode;
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
	public Experiment createExperimentEntity(ReadSet readSet, String projectCode) throws SraException {
		// On cree l'experiment pour le readSet demandé.
		// La validite du readSet doit avoir été testé avant.

		Experiment experiment = new Experiment(); 
		
		experiment.code = "exp_" + readSet.code;
		experiment.readSetCode = readSet.code;
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
						System.out.print("coucou cle = '" + k+"'  => ");
						PropertyValue propertyValue = pairs.get(k);
						System.out.println(propertyValue.value);
					}
					if (pairs.containsKey("estimatedPEInsertSize")) {
						PropertyValue estimatedInsertSize = pairs.get("estimatedPEInsertSize");
						experiment.libraryLayoutNominalLength = (Integer) estimatedInsertSize.value;
					} 
					if (pairs.containsKey("estimatedMPInsertSize")) {
						PropertyValue estimatedInsertSize = pairs.get("estimatedMPInsertSize");
						experiment.libraryLayoutNominalLength = (Integer) estimatedInsertSize.value;
					}	
				}
			}
		}
		// if (experiment.libraryLayoutNominalLength == null) {
		// mettre valeur theorique de libraryLayoutNominalLength a prendre dans le futur dans container
		
		experiment.state.code = "N";
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
					
						for(String k: listKeysSampleOnContainerProperties){
							System.out.print("cle = " + k);
							PropertyValue propertyValue = sampleOnContainerProperties.get(k);
							//System.out.print(propertyValue.toString());
							System.out.println(", value  => "+propertyValue.value);
						} 
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
		}


		// Renseigner l'objet experiment pour lastBaseCoord : Recuperer les lanes associées au
		// run associé au readSet et recuperer le lane contenant le readSet.code. C'est dans les
		// traitement de cette lane que se trouve l'information:
		// Un readSet est sur une unique lane, mais une lane peut contenir plusieurs readSet
		List<Lane> laboratoryLanes = laboratoryRun.lanes;
		Integer last_base_coord = null;
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
						System.out.println("cle = " + k);
						PropertyValue propertyValue = lanengsrg.get(k);
						System.out.println(propertyValue.toString());
						System.out.println(propertyValue.value);
					}*/
					last_base_coord =  (Integer) lanengsrg.get("nbCycleRead1").value + 1;
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
			readSpec_2.lastBaseCoord = last_base_coord;	
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
			readSpec_2.lastBaseCoord = last_base_coord;
			experiment.readSpecs.add(readSpec_2);
		}
		return experiment;
	}

	
	
	
	public String getTaxonName(String taxonId) {
		return "taxonNameFor_" + taxonId;
	}

	//todo releaseDate uniquement au niveau du study ????
	public Run createRunEntity(ReadSet readSet, String projectCode) {
		// On cree le run pour le readSet demandé.
		// La validite du readSet doit avoir été testé avant.

		// Recuperer pour le readSet la liste des fichiers associés:
		String laboratoryRunCode = readSet.runCode;
		List <models.laboratory.run.instance.File> list_files =  readSet.files;
		System.out.println("nbre de fichiers = " + list_files.size());
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		
		// Pour chaque readSet, creer un objet run 
		Integer laneNumber = readSet.laneNumber;
		Date runDate = readSet.runSequencingStartDate;
		Run run = new Run(); // Pas à sauver
		run.code = "run_" + readSet.code;
		run.runDate = runDate;
		
		//run.projectCode = projectCode;
		run.runCenter = VariableSRA.centerName;
		// Renseigner le run pour ces fichiers sur la base des fichiers associes au readSet :
		// chemin des fichiers pour ce readset :
		String dataDir = readSet.path;
		for (models.laboratory.run.instance.File runInstanceFile: list_files) {
			String runInstanceRelatifFileName = runInstanceFile.fullname;
			String runInstanceFileName = dataDir + java.io.File.separator + runInstanceFile.fullname;
			String runInstanceExtentionFileName = runInstanceFile.extension;
			
			// conditions qui doivent etre suffisantes puisque verification préalable que le readSet
			// est bien valide pour la bioinformatique.
			if (runInstanceFile.usable 
					&& ! runInstanceExtentionFileName.equalsIgnoreCase("fna") && ! runInstanceExtentionFileName.equalsIgnoreCase("qual")
					&& ! runInstanceExtentionFileName.equalsIgnoreCase("fna.gz") && ! runInstanceExtentionFileName.equalsIgnoreCase("qual.gz")) {
					System.out.println("nom relatif du fichier : "+ runInstanceFile.fullname);
					System.out.println("extention : " + runInstanceFile.extension);
					System.out.println("usable : " + runInstanceFile.usable);
					System.out.println("nom complet du fichier : " + runInstanceFileName);
					// voir si integration des md5 
					RawData rawData = new RawData();
					rawData.extention = runInstanceExtentionFileName;
					rawData.path = dataDir;
					rawData.relatifName = runInstanceRelatifFileName;
					run.listRawData.add(rawData);
			}
		}
		return run;
	}


	
	
}

	
