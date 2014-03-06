package services;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.ReadSet;
import models.sra.experiment.instance.Experiment;
import models.sra.experiment.instance.RawData;
import models.sra.experiment.instance.Run;
import models.sra.sample.instance.Sample;
import models.sra.study.instance.Study;
import models.sra.submission.instance.Submission;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
import fr.cea.ig.MongoDBDAO;

public class SubmissionServices {

	public void createNewSubmission(String projectCode, List<ReadSet> readSets, Study study, String acStudy, String strategySample) {
		// Pour une premiere soumission d'un readSet, on peut devoir utiliser un study ou un sample existant, deja soumis à l'EBI, ou non
		// en revanche on ne doit pas utiliser un experiment ou un run existant
		Submission submission = new Submission();	
		submission.strategySample = strategySample;
		System.out.println("strategySample :"+strategySample);
		
		
		Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, projectCode);
	
		// Soumission qui ne peut se faire que si study complet avec champs saisi par utilisateur
		if (study == null) {
			study = fetchStudy(projectCode);
		}
		
		
		for(ReadSet readSet : readSets) {
			System.out.println("readSet :" + readSet.code);
			// Verifier que c'est une premiere soumission pour ce readSet
			// Verifiez qu'il n'existe pas d'objet experiment referencant deja ce readSet
			Boolean alreadySubmit = models.sra.utils.HelperSRA.checkCodeReadSetExistInExperimentCollection(readSet.code);
			if ( alreadySubmit ) {
				// signaler erreur et passer au readSet suivant
				System.out.println("soumission existante pour :"+readSet.code);
				continue;
			} 
			// Verifier que ce readSet est bien valide avant soumission :
			if (! readSet.bioinformaticValuation.valid.equals(TBoolean.TRUE)) {
				System.out.println("soumission d'un readSet non valide pour la bioinformatique :"+readSet.code);
				continue;
			}
		

			//String codeRun = "run_" + readSet.code;
			//String codeExperiment = "exp_" + readSet.code;

			// Creer les objets avec leurs alias ou code, les instancier completement et les sauver.
			// Creer le sample si besoin :
			Sample sample = fetchSample(readSet, projectCode, strategySample);
			Run run = createRun(readSet, projectCode);
			Experiment experiment = createExperiment(readSet, projectCode);
			System.out.println("aliasStudy = " + study.code);
			System.out.println("aliasSample = " + sample.code);
			System.out.println("aliasExperiment = " + experiment.code);
			System.out.println("aliasRun = " + run.code);
			
			
			experiment.run = run;
			
			//MongoDBDAO.save(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, experiment);
		}
		
		//submission.projectCode=readSet.projectCode;
		//submission.studyCodes=new ArrayList<String>();
	}
	
	//todo : aucun champs rempli hormis le codeStudy
	public Study fetchStudy(String projectCode) {
		Study study = null;
		String codeStudy = "study_" + projectCode;
		// Si study existe, prendre l'existant, sinon en creer un nouveau
		if (models.sra.utils.HelperSRA.checkCodeStudyExistInStudyCollection(codeStudy)) {
			study = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.sra.study.instance.Study.class, codeStudy);			
		} else {
			study = new Study();
			study.code = codeStudy;
		}
		return study;
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
			codeSample = "sample_" + projectCode + taxonId;
		} else if (strategySample.equalsIgnoreCase("STRATEGY_NO_SAMPLE")) {
			//envisager d'avoir des fichiers de correspondance 
		} else {
			// Declencher une erreur.
		}	
		return codeSample;
	}
	
	
	
	//todo : il reste scientificName, classification, comonName à renseigner sur la base de idTaxon, et description
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
		if (models.sra.utils.HelperSRA.checkCodeSampleExistInSampleCollection(codeSample)) {
			sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.sra.sample.instance.Sample.class, codeSample);			
		} else {
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

	
	public Experiment createExperiment(ReadSet readSet, String projectCode) {
		// On cree l'experiment pour le readSet demandé.
		// La validite du readSet doit avoir été testé avant.
		Experiment experiment = new Experiment(); 
		experiment.code = "exp_" + readSet.code;
		String laboratorySampleCode = readSet.sampleCode;
		models.laboratory.sample.instance.Sample laboratorySample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, models.laboratory.sample.instance.Sample.class, laboratorySampleCode);
		String taxonId = laboratorySample.taxonCode;
		String taxonName = getTaxonName(taxonId);
		
		String laboratoryRunCode = readSet.runCode;
		models.laboratory.run.instance.Run  laboratoryRun = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, models.laboratory.run.instance.Run.class, laboratoryRunCode);
		//String machineName = laboratoryRun.instrumentUsed.code;
		String technology = laboratoryRun.instrumentUsed.typeCode;
		experiment.title = taxonName + technology + "typeBanqueAmplifiee?";
		//String runTypeCode = readSet.runTypeCode;
		models.laboratory.run.instance.Treatment treatment = (laboratoryRun.treatments.get("ngsrg"));
		Map <String, Map<String, PropertyValue>> results = treatment.results();
		Map<String, PropertyValue> ngsrg = results.get("default");
		Set listKeys=ngsrg.keySet();  // Obtenir la liste des clés
		Iterator iterateur=listKeys.iterator();
		/* Parcourir les clés et afficher les entrées de chaque clé;
		while(iterateur.hasNext()) {
			Object key= iterateur.next();
			System.out.println (key+"=>"+ngsrg.get(key).value);
		}
		*/
		
		PropertyValue propertyNbCycle = ngsrg.get("nbCycle");
		Long nbCycle =  (Long) propertyNbCycle.value;
		System.out.println("P/ nbCycle = " + nbCycle);
		
		//	treatment.results().containsKey
	//	((laboratoryRun.treatments.get("ngsrg")).results.get(default).nbCycle;
		//laboratoryRun.treatments.ngsrg.default.nbCycle.value 
		return experiment;
		
	}
	
	public String getTaxonName(String taxonId) {
		return "taxonNameFor_" + taxonId;
	}
	//todo releaseDate ou bien ou laisse uniquement au niveau du study ????
	public Run createRun(ReadSet readSet, String projectCode) {
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
		
		run.projectCode = projectCode;
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
				&& ! runInstanceExtentionFileName.equalsIgnoreCase("fna.gz") && ! runInstanceExtentionFileName.equalsIgnoreCase("qual.gz")){
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
