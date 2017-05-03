package services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import models.sra.submit.common.instance.Sample;
import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.sra.instance.RawData;
import models.sra.submit.sra.instance.ReadSpec;
import models.sra.submit.sra.instance.Run;
import models.sra.submit.util.SraException;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;

import org.apache.commons.lang3.StringUtils;

import play.Logger;

public class XmlServices {

	

	public static Submission writeAllXml(String submissionCode) throws IOException, SraException {
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		String resultDirectory = submission.submissionDirectory;
		System.out.println("resultDirectory = " + resultDirectory);
		return writeAllXml(submissionCode, resultDirectory);
	}
	
/*	public static void writeAllXml(String submissionCode, String resultDirectory, Boolean release) throws IOException, SraException {
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		writeAllXml(submissionCode, resultDirectory);
	}
	*/	
	
	public static Submission writeAllXml(String submissionCode, String resultDirectory) throws IOException, SraException {
		System.out.println("creation des fichiers xml pour l'ensemble de la soumission "+ submissionCode);
		System.out.println("resultDirectory = " + resultDirectory);
		// Recuperer l'objet submission:
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submit.common.instance.Submission.class, submissionCode);
		// si on est dans soumission de données :
		if (!submission.release) {
			if (StringUtils.isNotBlank(submission.studyCode)) {	
				File studyFile = new File(resultDirectory + File.separator + VariableSRA.xmlStudys);
				writeStudyXml(submission, studyFile);
			}
			if (submission.sampleCodes.size() != 0){
				File sampleFile = new File(resultDirectory + File.separator + VariableSRA.xmlSamples);
				writeSampleXml(submission, sampleFile); 
			}
			if (submission.experimentCodes.size() != 0){
				File experimentFile = new File(resultDirectory + File.separator + VariableSRA.xmlExperiments);
				writeExperimentXml(submission, experimentFile); 
			} else {
				System.out.println("experimentCodes==0 ??????????");
				Logger.debug("experimentCodes==0 ??????????");
			}
			if (submission.runCodes.size() != 0){
				File runFile = new File(resultDirectory + File.separator + VariableSRA.xmlRuns);
				writeRunXml(submission, runFile); 
			} else {
				System.out.println("runCodes==0 ??????????");
				Logger.debug("runCodes==0 ??????????");
			}
		
			File submissionFile = new File(resultDirectory + File.separator + VariableSRA.xmlSubmission);
			writeSubmissionXml(submission, submissionFile);
		} else {
			File submissionFile = new File(resultDirectory + File.separator + VariableSRA.xmlSubmission);
			writeSubmissionReleaseXml(submission, submissionFile);

		}
		// mettre à jour dans la base l'objet submission pour les champs xml...
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submissionCode),
				DBUpdate.set("xmlSubmission", submission.xmlSubmission).set("xmlStudys", submission.xmlStudys).set("xmlSamples", submission.xmlSamples).set("xmlExperiments", submission.xmlExperiments).set("xmlRuns", submission.xmlRuns).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));
	
		return submission;
	}
	

	public static void writeStudyXml (Submission submission, File outputFile) throws IOException, SraException {	
		if (submission == null) {
			return;
		}
		// Si demande de release pas d'ecriture de study.
		if (submission.release) {
			return;
		}
		if (StringUtils.isNotBlank(submission.studyCode)) {	
			System.out.println("Creation du fichier " + outputFile);
			// ouvrir fichier en ecriture
			BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
			String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
			chaine = chaine + "<STUDY_SET>\n";
			String studyCode = submission.studyCode;
			// Recuperer objet study dans la base :
			Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.submit.common.instance.Study.class, studyCode);
			//output_buffer.write("//\n");
			if (study == null){
				throw new SraException("study impossible à recuperer dans base :"+ studyCode);
			}
			System.out.println("Ecriture du study " + studyCode);

			chaine = chaine + "  <STUDY alias=\""+ studyCode + "\" ";
			if (StringUtils.isNotBlank(study.accession)) {	
				chaine = chaine + "accession=\"" + study.accession + "\" ";
			}
				
			chaine = chaine + ">\n";
			chaine = chaine + "    <DESCRIPTOR>\n";
			chaine = chaine + "      <STUDY_TITLE>" + study.title + "</STUDY_TITLE>\n";
			chaine = chaine + "      <STUDY_TYPE existing_study_type=\""+ study.existingStudyType +"\"/>\n";
			chaine = chaine + "      <STUDY_ABSTRACT>" + study.studyAbstract + "</STUDY_ABSTRACT>\n";
			chaine = chaine + "      <CENTER_PROJECT_NAME>" + study.centerProjectName+"</CENTER_PROJECT_NAME>\n"; 
			//if (study.bioProjectId != 0) {
				chaine = chaine + "      <RELATED_STUDIES>\n";
				chaine = chaine + "        <RELATED_STUDY>\n";
				chaine = chaine + "          <RELATED_LINK>\n";
				chaine = chaine + "            <DB>ENA</DB>\n";
				chaine = chaine + "            <ID>" + study.bioProjectId + "<ID>\n";
				chaine = chaine + "          </RELATED_LINK>\n";
				chaine = chaine + "          <IS_PRIMARY>false</IS_PRIMARY>\n";
				chaine = chaine + "        </RELATED_STUDY>\n";
				chaine = chaine + "      </RELATED_STUDIES>\n";
			//}
				
			chaine = chaine + "      <STUDY_DESCRIPTION>"+study.description+"</STUDY_DESCRIPTION>\n";
			chaine = chaine + "    </DESCRIPTOR>\n";
			chaine = chaine + "  </STUDY>\n";
			chaine = chaine + "</STUDY_SET>\n";
			output_buffer.write(chaine);
			output_buffer.close();
			submission.xmlStudys = outputFile.getName();
		} // end if		
	} // end writeStudyXml
	   
	public static void writeSampleXml (Submission submission, File outputFile) throws IOException, SraException {
		System.out.println("sample = "  + submission.sampleCodes.get(0));
		if (submission == null) {
			return;
		}

		if (! submission.sampleCodes.isEmpty()) {	
			// ouvrir fichier en ecriture
			System.out.println("Creation du fichier " + outputFile);
			BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));

			String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
			chaine = chaine + "<SAMPLE_SET>\n";
			for (String sampleCode : submission.sampleCodes){
				System.out.println("sampleCode = '" + sampleCode +"'");
				// Recuperer objet sample dans la base :
				Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.submit.common.instance.Sample.class, sampleCode);
				if (sample == null){
					throw new SraException("sample impossible à recuperer dans base :"+ sampleCode);
				}
				//output_buffer.write("//\n");
				System.out.println("Ecriture du sample " + sampleCode);

				chaine = chaine + "  <SAMPLE alias=\""+ sampleCode + "\"";
				
				if (StringUtils.isNotBlank(sample.accession)) {
					chaine = chaine + "accession=\"" + sample.accession + "\"";
				}
				chaine = chaine + ">\n";
				if (StringUtils.isNotBlank(sample.title)) {
					chaine = chaine + "    <TITLE>" + sample.title + "</TITLE>\n";
				}
				chaine = chaine + "    <SAMPLE_NAME>\n";
				chaine = chaine + "      <TAXON_ID>" + sample.taxonId + "</TAXON_ID>\n";
				if (StringUtils.isNotBlank(sample.scientificName)) {
					chaine = chaine + "      <SCIENTIFIC_NAME>" + sample.scientificName + "</SCIENTIFIC_NAME>\n";
				}
				if (StringUtils.isNotBlank(sample.commonName)) {
					chaine = chaine + "      <COMMON_NAME>" + sample.commonName + "</COMMON_NAME>\n";
				}
				if (StringUtils.isNotBlank(sample.anonymizedName)) {
					chaine = chaine + "      <ANONYMIZED_NAME>" + sample.anonymizedName + "</ANONYMIZED_NAME>\n";
				}
				chaine = chaine + "    </SAMPLE_NAME>\n";
				if (StringUtils.isNotBlank(sample.description)) {
					chaine = chaine + "      <DESCRIPTION>" + sample.description + "</DESCRIPTION>\n";
				}
				chaine = chaine + "  </SAMPLE>\n";
			}
			chaine = chaine + "</SAMPLE_SET>\n";
			output_buffer.write(chaine);
			output_buffer.close();
			submission.xmlSamples = outputFile.getName();
		}
	}
	public static void writeExperimentXml (Submission submission, File outputFile) throws IOException, SraException {
		if (submission == null) {
			return;
		}
		if (! submission.experimentCodes.isEmpty()) {	
			// ouvrir fichier en ecriture
			System.out.println("Creation du fichier " + outputFile);
			BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
			String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
			chaine = chaine + "<EXPERIMENT_SET>\n";
			for (String experimentCode : submission.experimentCodes){
				// Recuperer objet experiment dans la base :
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);
				//output_buffer.write("//\n");
				System.out.println("Ecriture de experiment " + experimentCode);
				if (experiment == null){
					throw new SraException("experiment impossible à recuperer dans base :"+ experimentCode);
				}
				chaine = chaine + "  <EXPERIMENT alias=\"" + experimentCode + "\" center_name=\"" + VariableSRA.centerName + "\"";
				if (StringUtils.isNotBlank(experiment.accession)) {
					chaine = chaine + "accession=\"" + experiment.accession + "\" ";	
				}
				chaine = chaine + ">\n";
				// Les champs title et libraryName sont considerés comme obligatoires
				chaine = chaine + "    <TITLE>" + experiment.title + "</TITLE>\n";
				chaine = chaine + "    <STUDY_REF ";
				if (StringUtils.isNotBlank(experiment.studyCode) && (experiment.studyCode.startsWith("external"))) { 
					chaine = chaine + "    <STUDY_REF refname=\"" + experiment.studyCode +"\"";
				}
				if (StringUtils.isNotBlank(experiment.studyAccession)){
					chaine = chaine + " accession=\""+experiment.studyAccession + "\"";
				}
				chaine = chaine + ">" +"</STUDY_REF>\n";
				
				chaine = chaine + "      <DESIGN>\n";
				chaine = chaine + "        <DESIGN_DESCRIPTION></DESIGN_DESCRIPTION>\n";
				chaine = chaine + "          <SAMPLE_DESCRIPTOR  ";
				if (StringUtils.isNotBlank(experiment.sampleCode) && (experiment.sampleCode.startsWith("external"))) { 
					chaine = chaine+  "refname=\"" + experiment.sampleCode + "\"";
				}
				if (StringUtils.isNotBlank(experiment.sampleAccession)){
					chaine = chaine + " accession=\""+experiment.sampleAccession + "\"";
				}
				chaine = chaine + "/>\n";
				
				chaine = chaine + "          <LIBRARY_DESCRIPTOR>\n";
				chaine = chaine + "            <LIBRARY_NAME>" + experiment.libraryName + "</LIBRARY_NAME>\n";
				chaine = chaine + "            <LIBRARY_STRATEGY>"+ VariableSRA.mapLibraryStrategy.get(experiment.libraryStrategy) + "</LIBRARY_STRATEGY>\n";
				chaine = chaine + "            <LIBRARY_SOURCE>" + VariableSRA.mapLibrarySource.get(experiment.librarySource) + "</LIBRARY_SOURCE>\n";
				chaine = chaine + "            <LIBRARY_SELECTION>" + VariableSRA.mapLibrarySelection.get(experiment.librarySelection) + "</LIBRARY_SELECTION>\n";
				chaine = chaine + "            <LIBRARY_LAYOUT>\n";
				
				chaine = chaine + "              <"+ experiment.libraryLayout;	
				if("PAIRED".equals(experiment.libraryLayout)) {
					chaine = chaine + " NOMINAL_LENGTH=\"" + experiment.libraryLayoutNominalLength + "\"";
				}
				chaine = chaine + " />\n";

				chaine = chaine + "            </LIBRARY_LAYOUT>\n";
				chaine = chaine + "            <LIBRARY_CONSTRUCTION_PROTOCOL>none provided</LIBRARY_CONSTRUCTION_PROTOCOL>\n";
				chaine = chaine + "          </LIBRARY_DESCRIPTOR>\n";
				if (! "OXFORD_NANOPORE".equalsIgnoreCase(experiment.typePlatform)) {
					chaine = chaine + "          <SPOT_DESCRIPTOR>\n";
					chaine = chaine + "            <SPOT_DECODE_SPEC>\n";
					chaine = chaine + "              <SPOT_LENGTH>"+experiment.spotLength+"</SPOT_LENGTH>\n";
					for (ReadSpec readSpec: experiment.readSpecs) {
						chaine = chaine + "              <READ_SPEC>\n";
						chaine = chaine + "                <READ_INDEX>"+readSpec.readIndex+"</READ_INDEX>\n";
						chaine = chaine + "                <READ_LABEL>"+readSpec.readLabel+"</READ_LABEL>\n";
						chaine = chaine + "                <READ_CLASS>"+readSpec.readClass+"</READ_CLASS>\n";
						chaine = chaine + "                <READ_TYPE>"+readSpec.readType+"</READ_TYPE>\n";
						chaine = chaine + "                <BASE_COORD>" + readSpec.baseCoord + "</BASE_COORD>\n";
						chaine = chaine + "              </READ_SPEC>\n";
					}
					chaine = chaine + "            </SPOT_DECODE_SPEC>\n";
					chaine = chaine + "          </SPOT_DESCRIPTOR>\n";
				}
				chaine = chaine + "      </DESIGN>\n";
				chaine = chaine + "      <PLATFORM>\n";
				chaine = chaine + "        <" + experiment.typePlatform + ">\n";
				chaine = chaine + "          <INSTRUMENT_MODEL>" + experiment.instrumentModel + "</INSTRUMENT_MODEL>\n";
				chaine = chaine + "        </" + experiment.typePlatform + ">\n";
				chaine = chaine + "      </PLATFORM>\n";
				chaine = chaine + "  </EXPERIMENT>\n";
			}
			chaine = chaine + "</EXPERIMENT_SET>\n";
			output_buffer.write(chaine);
			output_buffer.close();
			submission.xmlExperiments = outputFile.getName();
		}
	}
	public static void writeRunXml (Submission submission, File outputFile) throws IOException, SraException {
		if (submission == null) {
			return;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
		// On accede au run via l'experiment:
		if (! submission.experimentCodes.isEmpty()) {	
			// ouvrir fichier en ecriture
			System.out.println("Creation du fichier " + outputFile);
			BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
			String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
			chaine = chaine + "<RUN_SET>\n";
			for (String experimentCode : submission.experimentCodes){
				// Recuperer objet experiment dans la base :
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.submit.sra.instance.Experiment.class, experimentCode);
				if (experiment == null) {
					throw new SraException("experiment impossible à recuperer dans base :"+ experimentCode);
				}
				Run run = experiment.run;
				if (run == null){
					throw new SraException("run impossible à recuperer dans objet experiment:"+ experimentCode);
				}
				//output_buffer.write("//\n");
				String runCode = run.code;
				System.out.println("Ecriture du run " + runCode);
				chaine = chaine + "  <Run alias=\""+ runCode + "\" ";
				if (StringUtils.isNotBlank(run.accession)) {
					chaine = chaine + "accession=\"" + run.accession + "\" ";
				}
				
				//Format date
				chaine =  chaine + "run_date=\""+ formatter.format(run.runDate)+"\"  run_center=\""+run.runCenter+ "\" ";
				chaine = chaine + ">\n";
				chaine = chaine + "    <EXPERIMENT_REF refname=\"" + experimentCode +"\">" +"</EXPERIMENT_REF>\n";
				chaine = chaine + "    <DATA_BLOCK>\n";
				chaine = chaine + "      <FILES>\n";
				for (RawData rawData: run.listRawData) {
					String fileType = rawData.extention;
					if (fileType.equalsIgnoreCase("fastq.gz")){
						fileType = "fastq";
					} else {
						fileType.replace(".gz", "");
					}
					chaine = chaine + "        <FILE filename=\"" + rawData.relatifName + "\" "+"filetype=\"" + fileType + "\" checksum_method=\"MD5\" checksum=\"" + rawData.md5 + "\">\n";
					if ( run.listRawData.size() == 2 ) {
						chaine = chaine + "          <READ_LABEL>F</READ_LABEL>\n";
						chaine = chaine + "          <READ_LABEL>R</READ_LABEL>\n";
					}
					chaine = chaine +"</FILE>\n";
				}
				chaine = chaine + "      </FILES>\n";
				chaine = chaine + "    </DATA_BLOCK>\n";
				chaine = chaine + "  </RUN>\n";
				}
				chaine = chaine + "</RUN_SET>\n";
				output_buffer.write(chaine);
				output_buffer.close();
				submission.xmlRuns = outputFile.getName();
			}
		}
	

		
	public static void writeSubmissionXml (Submission submission, File outputFile) throws IOException {
		if (submission == null) {
			return;
		}
		
		
		// ouvrir fichier en ecriture
		System.out.println("Creation du fichier " + outputFile);
		BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<SUBMISSION_SET>\n";
		
		System.out.println("Ecriture du submission " + submission.code);
		chaine = chaine + "  <SUBMISSION alias=\""+ submission.code + "\" ";
		if (StringUtils.isNotBlank(submission.accession)) {
			chaine = chaine + "accession=\"" + submission.accession + "\" ";
		}
		
		chaine = chaine + ">\n";	
		chaine = chaine + "    <CONTACTS>\n";
		chaine = chaine + "      <CONTACT name=\"william\" inform_on_status=\"william@genoscope.cns.fr\" inform_on_error=\"william@genoscope.cns.fr\"/>\n";
		chaine = chaine + "    </CONTACTS>\n";
			
		chaine = chaine + "    <ACTIONS>\n";
		// soumission systematique en confidential meme si study deja public
		/*if (!submission.release)  {
			chaine = chaine + "      <ACTION>\n        <HOLD/>\n      </ACTION>\n";
		}
        */
		if (StringUtils.isNotBlank(submission.studyCode)) {
			chaine = chaine + "      <ACTION>\n        <ADD source=\"study.xml\" schema=\"study\"/>\n      </ACTION>\n";
		}
		if (!submission.sampleCodes.isEmpty()){
			chaine = chaine + "      <ACTION>\n        <ADD source=\"sample.xml\" schema=\"sample\"/>\n      </ACTION>\n";
		}
		if (!submission.experimentCodes.isEmpty()){
			chaine = chaine + "      <ACTION>\n        <ADD source=\"experiment.xml\" schema=\"experiment\"/>\n      </ACTION>\n";
			chaine = chaine + "      <ACTION>\n        <ADD source=\"run.xml\" schema=\"run\"/>\n      </ACTION>\n";
		}
		chaine = chaine + "    </ACTIONS>\n";
		
		
		
		chaine = chaine + "  </SUBMISSION>\n";
		chaine = chaine + "</SUBMISSION_SET>\n";
		
		output_buffer.write(chaine);
		output_buffer.close();	
		submission.xmlSubmission = outputFile.getName();
	}


	public static void writeSubmissionReleaseXml (Submission submission, File outputFile) throws IOException {
		if (submission == null) {
			return;
		}
		if(StringUtils.isBlank(submission.studyCode)){
			return;
		}
		
		// ouvrir fichier en ecriture
		System.out.println("Creation du fichier " + outputFile);
		BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<SUBMISSION_SET>\n";
		
		System.out.println("Ecriture du submission " + submission.code);
		chaine = chaine + "  <SUBMISSION alias=\""+ submission.code + "\" ";
		if (StringUtils.isNotBlank(submission.accession)) {
			chaine = chaine + "accession=\"" + submission.accession + "\" ";
		}
		
		chaine = chaine + ">\n";	
		chaine = chaine + "    <CONTACTS>\n";
		chaine = chaine + "      <CONTACT>  name=\"william\" inform_on_status=\"william@genoscope.cns.fr\" inform_on_error=\"william@genoscope.cns.fr\"/>\n";
		chaine = chaine + "    </CONTACTS>\n";
			
		chaine = chaine + "    <ACTIONS>\n";
		
		chaine = chaine + "      <ACTION>\n        <RELEASE target=\"" + submission.studyCode + "\"/>\n      </ACTION>\n";
		
		chaine = chaine + "    </ACTIONS>\n";
		
		
		
		chaine = chaine + "  </SUBMISSION>\n";
		chaine = chaine + "</SUBMISSION_SET>\n";
		
		output_buffer.write(chaine);
		output_buffer.close();	
		submission.xmlSubmission = outputFile.getName();
	}

}
