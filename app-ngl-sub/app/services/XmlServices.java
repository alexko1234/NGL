package services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Date;

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

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;

public class XmlServices {

	

	public static Submission writeAllXml(String submissionCode) throws IOException, SraException {
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submission.instance.Submission.class, submissionCode);
		String resultDirectory = submission.submissionDirectory;
		return writeAllXml(submissionCode, resultDirectory);
	}
	
	public static void writeAllXml(String submissionCode, String resultDirectory, Boolean release) throws IOException, SraException {
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submission.instance.Submission.class, submissionCode);
		writeAllXml(submissionCode, resultDirectory);
	}
		
	public static Submission writeAllXml(String submissionCode, String resultDirectory) throws IOException, SraException {
		System.out.println("creation des fichiers xml pour l'ensemble de la soumission "+ submissionCode);
		// Recuperer l'objet submission:
		Submission submission = MongoDBDAO.findByCode(InstanceConstants.SRA_SUBMISSION_COLL_NAME, models.sra.submission.instance.Submission.class, submissionCode);
		if (submission.studyCode != null){
			File studyFile = new File(resultDirectory + File.separator + VariableSRA.xmlStudys);
			writeStudyXml(submission, studyFile);
		}
		if (submission.sampleCodes.size() != 0){
			File sampleFile = new File(resultDirectory + File.separator + VariableSRA.xmlSamples);
			writeSampleXml(submission, sampleFile); 
		}
		if (submission.sampleCodes.size() != 0){
			File experimentFile = new File(resultDirectory + File.separator + VariableSRA.xmlExperiments);
			writeExperimentXml(submission, experimentFile); 
		}
		if (submission.runCodes.size() != 0){
			File runFile = new File(resultDirectory + File.separator + VariableSRA.xmlRuns);
			writeRunXml(submission, runFile); 
		}
		File submissionFile = new File(resultDirectory + File.separator + VariableSRA.xmlSubmission);
		writeSubmissionXml(submission, submissionFile);
		// mettre à jour dans la base l'objet submission pour les champs xml...
		MongoDBDAO.update(InstanceConstants.SRA_SUBMISSION_COLL_NAME, Submission.class, 
				DBQuery.is("code", submissionCode),
				DBUpdate.set("xmlSubmission", submission.xmlSubmission).set("xmlStudys", submission.xmlStudys).set("xmlSamples", submission.xmlSamples).set("xmlExperiments", submission.xmlExperiments).set("xmlRun", submission.xmlRuns).set("traceInformation.modifyUser", VariableSRA.admin).set("traceInformation.modifyDate", new Date()));
		return submission;
	}

	public static void writeStudyXml (Submission submission, File outputFile) throws IOException, SraException {	
		if (submission == null) {
			return;
		}
		if ( submission.studyCode != null) {
			System.out.println("Creation du fichier " + outputFile);
			// ouvrir fichier en ecriture
			BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
			String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
			chaine = chaine + "<STUDY_SET>\n";
			String studyCode = submission.studyCode;
			// Recuperer objet study dans la base :
			Study study = MongoDBDAO.findByCode(InstanceConstants.SRA_STUDY_COLL_NAME, models.sra.study.instance.Study.class, studyCode);
			//output_buffer.write("//\n");
			if (study == null){
				throw new SraException("study impossible à recuperer dans base :"+ studyCode);
			}
			System.out.println("Ecriture du study " + studyCode);

			chaine = chaine + "  <STUDY alias=\""+ studyCode + "\"";
			if (study.accession != null){
				if (!study.accession.equals("")){
					chaine = chaine + "accession=\"" + study.accession + "\"";
				}
			}
				
			chaine = chaine + ">\n";
			chaine = chaine + "    <DESCRIPTOR>\n";
			chaine = chaine + "      <STUDY_TITLE>" + study.title + "</STUDY_TITLE>\n";
			chaine = chaine + "      <STUDY_TYPE existing_study_type="+ study.existingStudyType +" />\n";
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
		//System.out.println("sample = "  + submission.sampleCodes.get(0));
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
				Sample sample = MongoDBDAO.findByCode(InstanceConstants.SRA_SAMPLE_COLL_NAME, models.sra.sample.instance.Sample.class, sampleCode);
				if (sample == null){
					throw new SraException("sample impossible à recuperer dans base :"+ sampleCode);
				}
				//output_buffer.write("//\n");
				System.out.println("Ecriture du sample " + sampleCode);

				chaine = chaine + "  <SAMPLE alias=\""+ sampleCode + "\"";
				
				if (sample.accession != null) {
					if (! sample.accession.equals("") ) {
						chaine = chaine + "accession=\"" + sample.accession + "\"";
					}
				}
			
				chaine = chaine + ">\n";
				chaine = chaine + "    <TITLE>" + sample.title + "</TITLE>\n";
				chaine = chaine + "    <SAMPLE_NAME>\n";
				chaine = chaine + "      <TAXON_ID>" + sample.taxonId + "</TAXON_ID>\n";
				chaine = chaine + "      <SCIENTIFIC_NAME>" + sample.scientificName + "</SCIENTIFIC_NAME>\n";
				chaine = chaine + "      <COMMON_NAME>" + sample.commonName + "</COMMON_NAME>\n";
				chaine = chaine + "    </SAMPLE_NAME>\n";
				chaine = chaine + "      <DESCRIPTION>" + sample.commonName + "</DESCRIPTION>\n";
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
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.experiment.instance.Experiment.class, experimentCode);
				//output_buffer.write("//\n");
				System.out.println("Ecriture du experiment " + experimentCode);
				if (experiment == null){
					throw new SraException("study impossible à recuperer dans base :"+ experimentCode);
				}
				chaine = chaine + "  <EXPERIMENT alias=\""+ experimentCode + "\"";
				
				if (experiment.accession != null) {
					if (! experiment.accession.equals(""))  {
						chaine = chaine + "accession=\"" + experiment.accession + "\"";
					}
				}
				chaine = chaine + ">\n";
				chaine = chaine + "    <TITLE>" + experiment.title + "</TITLE>\n";
				chaine = chaine + "    <STUDY_REF refname=\"" + experiment.studyCode +"\">" +"</STUDY_REF>\n";
				chaine = chaine + "      <DESIGN>\n";
				chaine = chaine + "        <DESIGN_DESCRIPTION></DESIGN_DESCRIPTION>\n";
				chaine = chaine + "          <SAMPLE_DESCRIPTOR  refname=\"" + experiment.sampleCode + "\"/>\n";
				chaine = chaine + "          <LIBRARY_DESCRIPTOR>\n";
				chaine = chaine + "            <LIBRARY_NAME>" + experiment.libraryName + "</LIBRARY_NAME>\n";
				chaine = chaine + "            <LIBRARY_STRATEGY>"+ experiment.libraryStrategy + "</LIBRARY_STRATEGY>\n";
				chaine = chaine + "            <LIBRARY_SOURCE>" + experiment.librarySource + "</LIBRARY_SOURCE>\n";
				chaine = chaine + "            <LIBRARY_SELECTION>" + experiment.librarySelection + "</LIBRARY_SELECTION>\n";
				chaine = chaine + "            <LIBRARY_LAYOUT>\n";
				chaine = chaine + "              <" + experiment.libraryLayout + "/>\n";
				chaine = chaine + "            </LIBRARY_LAYOUT>\n";
				chaine = chaine + "            <LIBRARY_CONSTRUCTION_PROTOCOL>none provided</LIBRARY_CONSTRUCTION_PROTOCOL>\n";
				chaine = chaine + "          </LIBRARY_DESCRIPTOR>\n";
				chaine = chaine + "          <SPOT_DESCRIPTOR>\n";
				chaine = chaine + "            <SPOT_DECODE_SPEC>\n";
				chaine = chaine + "              <SPOT_LENGTH>"+experiment.spotLength+"</SPOT_LENGTH>\n";
				for (ReadSpec readSpec: experiment.readSpecs) {
					chaine = chaine + "              <READ_SPEC>\n";
					chaine = chaine + "                <READ_INDEX>"+readSpec.readIndex+"</READ_INDEX>\n";
					chaine = chaine + "                <READ_LABEL>"+readSpec.readLabel+"</READ_LABEL>\n";
					chaine = chaine + "                <READ_CLASS>"+readSpec.readClass+"</READ_CLASS>\n";
					chaine = chaine + "                <READ_TYPE>"+readSpec.readType+"</READ_TYPE>\n";
					chaine = chaine + "                <BASE_COORD>" + readSpec.lastBaseCoord + "</BASE_COORD>\n";
					chaine = chaine + "              </READ_SPEC>\n";
				}
				chaine = chaine + "            </SPOT_DECODE_SPEC>\n";
				chaine = chaine + "          </SPOT_DESCRIPTOR>\n";
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
		// On accede au run via l'experiment:
		if (! submission.experimentCodes.isEmpty()) {	
			// ouvrir fichier en ecriture
			System.out.println("Creation du fichier " + outputFile);
			BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
			String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
			chaine = chaine + "<RUN_SET>\n";
			for (String experimentCode : submission.experimentCodes){
				// Recuperer objet experiment dans la base :
				Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.SRA_EXPERIMENT_COLL_NAME, models.sra.experiment.instance.Experiment.class, experimentCode);
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
				chaine = chaine + "  <Run alias=\""+ experimentCode + "\"";
				if ( (experiment.accession != null) && (! experiment.accession.equals("")) ) {
					chaine = chaine + "accession=\"" + experiment.accession + "\" ";
				}
				chaine =  chaine + "run_date=\""+ run.runDate+"\"  run_center=\""+run.runCenter+ "\" ";
				chaine = chaine + ">\n";
				chaine = chaine + "    <EXPERIMENT_REF refname=\"" + experimentCode +"\">" +"</EXPERIMENT_REF>\n";
				chaine = chaine + "    <DATABLOCK>\n";
				chaine = chaine + "      <FILES>\n";
				for (RawData rawData: run.listRawData) {
					chaine = chaine + "        <FILE>filename=\"" + rawData.relatifName + "\" "+"filetype=\"" + rawData.extention + "\" checksum_method=\"MD5\" checksum=\"" + rawData.md5 + "\"</FILE>\n";
					if ( run.listRawData.size() == 2 ) {
						chaine = chaine + "          <READ_LABEL>F</READ_LABEL>\n";
						chaine = chaine + "          <READ_LABEL>R</READ_LABEL>\n";
					}
				}
				chaine = chaine + "      </FILES>\n";
				chaine = chaine + "    </DATABLOCK>\n";
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
		chaine = chaine + "  <SUBMISSION alias=\""+ submission.code + "\"";
		
		if (submission.accession != null) {
			if (! submission.accession.equals("")) {
				chaine = chaine + "accession=\"" + submission.accession + "\"";
			}
		}
		chaine = chaine + ">\n";	
		chaine = chaine + "    <CONTACTS>\n";
		chaine = chaine + "      <CONTACT>  name=\"william\" inform_on_status=\"william@genoscope.cns.fr\" inform_on_error=\"william@genoscope.cns.fr\"/>\n";
		chaine = chaine + "    </CONTACTS>\n";
			
		chaine = chaine + "    <ACTIONS>\n";
		if (!submission.release)  {
			chaine = chaine + "      <ACTION>\n        <HOLD/> Verifier ou placer la contrainte dans objet ????\n      </ACTION>\n";
		}
		if (submission.studyCode != null){
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


	/*    public void testProcessBuilder() throws IOException {
	        String[] command = {"ls", "-al"}; // commande et option ou argument
	        ProcessBuilder processBuilder = new ProcessBuilder(command);

	        //You can set up your work directory
	        //processBuilder.directory(new File("test"));
	        Process process = processBuilder.start();
	        //Read out dir output
	        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line;

	        while ((line = br.readLine()) != null) {
	            System.out.println(line);
	        }

	        //Wait to get exit value
	        try {
	            int exitValue = process.waitFor();
	            System.out.println("\n\nExit Value is " + exitValue);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    */ 
}
