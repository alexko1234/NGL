package fr.cea.ig.auto.submission;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import fr.genoscope.lis.devsi.birds.api.device.JSONDevice;
import fr.genoscope.lis.devsi.birds.api.entity.ResourceProperties;
import fr.genoscope.lis.devsi.birds.api.exception.BirdsException;
import fr.genoscope.lis.devsi.birds.api.exception.FatalException;
import fr.genoscope.lis.devsi.birds.api.exception.JSONDeviceException;
import fr.genoscope.lis.devsi.birds.impl.properties.ProjectProperties;

public class XMLServices implements IXMLServices{

	private static Logger log = Logger.getLogger(XMLServices.class);

	@Override
	public void writeStudyXml(File outputFile, String code) throws IOException, JSONDeviceException, FatalException
	{
		//Get study from DB
		JSONDevice jsonDevice = new JSONDevice();
		ResourceProperties rpsStudy = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/studies/"+code,"bot").iterator().next();


		log.debug("Creation du fichier " + outputFile);
		// ouvrir fichier en ecriture
		BufferedWriter output_buffer = new BufferedWriter(new FileWriter(outputFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<STUDY_SET>\n";
		log.debug("Ecriture du study " + code);

		chaine = chaine + "  <STUDY alias=\""+ code + "\" ";
		String accession = rpsStudy.getProperty("accession");
		String existingStudyType = rpsStudy.getProperty("existingStudyType");
		ResourceProperties rpsExistingStudy = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/variables/existingStudyType/"+existingStudyType.toLowerCase(),"bot").iterator().next();
		if (accession!=null && !accession.equals("")) {	
			chaine = chaine + " accession=\"" + accession + "\" ";
		}

		chaine = chaine + ">\n";
		chaine = chaine + "    <DESCRIPTOR>\n";
		chaine = chaine + "      <STUDY_TITLE>" + rpsStudy.getProperty("title") + "</STUDY_TITLE>\n";
		chaine = chaine + "      <STUDY_TYPE existing_study_type=\""+ rpsExistingStudy.get("value") +"\"/>\n";
		chaine = chaine + "      <STUDY_ABSTRACT>" + rpsStudy.getProperty("studyAbstract") + "</STUDY_ABSTRACT>\n";
		chaine = chaine + "      <CENTER_PROJECT_NAME>" + rpsStudy.getProperty("centerProjectName")+"</CENTER_PROJECT_NAME>\n"; 
		//if (study.bioProjectId != 0) {
		chaine = chaine + "      <RELATED_STUDIES>\n";
		chaine = chaine + "        <RELATED_STUDY>\n";
		chaine = chaine + "          <RELATED_LINK>\n";
		chaine = chaine + "            <DB>ENA</DB>\n";
		chaine = chaine + "            <ID>" + rpsStudy.getProperty("bioProjectId") + "</ID>\n";
		chaine = chaine + "          </RELATED_LINK>\n";
		chaine = chaine + "          <IS_PRIMARY>false</IS_PRIMARY>\n";
		chaine = chaine + "        </RELATED_STUDY>\n";
		chaine = chaine + "      </RELATED_STUDIES>\n";
		//}

		chaine = chaine + "      <STUDY_DESCRIPTION>"+rpsStudy.getProperty("description")+"</STUDY_DESCRIPTION>\n";
		chaine = chaine + "    </DESCRIPTOR>\n";
		chaine = chaine + "  </STUDY>\n";
		chaine = chaine + "</STUDY_SET>\n";
		output_buffer.write(chaine);
		output_buffer.close();

	}

	@Override
	public void writeSampleXml(File outputFile, String codes) throws IOException, JSONDeviceException, FatalException
	{

		String[] sampleCodes = codes.split(",");
		// ouvrir fichier en ecriture
		log.debug("Creation du fichier " + outputFile);
		BufferedWriter output_buffer = new BufferedWriter(new FileWriter(outputFile));

		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<SAMPLE_SET>\n";

		JSONDevice jsonDevice = new JSONDevice();
		for (int i=0; i<sampleCodes.length; i++){
			String sampleCode = sampleCodes[i];
			log.debug("sampleCode = '" + sampleCode +"'");
			ResourceProperties rpsSample = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/samples/"+sampleCode,"bot").iterator().next();
			String accession = rpsSample.get("accession");
			String title = rpsSample.get("title");
			String scientificName = rpsSample.get("scientificName");
			String commonName = rpsSample.get("commonName");
			String anonymizedName = rpsSample.get("anonymizedName");
			String description = rpsSample.get("description");
			// Recuperer objet sample dans la base :
			chaine = chaine + "  <SAMPLE alias=\""+ sampleCode + "\"";

			if (accession!=null && !accession.equals("")) {
				chaine = chaine + " accession=\"" + accession + "\"";
			}
			chaine = chaine + ">\n";
			if (title!=null && !title.equals("")) {
				chaine = chaine + "    <TITLE>" + title + "</TITLE>\n";
			}
			chaine = chaine + "    <SAMPLE_NAME>\n";
			chaine = chaine + "      <TAXON_ID>" + rpsSample.get("taxonId") + "</TAXON_ID>\n";
			if (scientificName!=null && !scientificName.equals("")) {
				chaine = chaine + "      <SCIENTIFIC_NAME>" + scientificName + "</SCIENTIFIC_NAME>\n";
			}
			if (commonName!=null && !commonName.equals("")) {
				chaine = chaine + "      <COMMON_NAME>" + commonName + "</COMMON_NAME>\n";
			}
			if (anonymizedName!=null && !anonymizedName.equals("")) {
				chaine = chaine + "      <ANONYMIZED_NAME>" + anonymizedName + "</ANONYMIZED_NAME>\n";
			}
			chaine = chaine + "    </SAMPLE_NAME>\n";
			if (description!=null && !description.equals("")) {
				chaine = chaine + "      <DESCRIPTION>" + description + "</DESCRIPTION>\n";
			}
			chaine = chaine + "  </SAMPLE>\n";
		}
		chaine = chaine + "</SAMPLE_SET>\n";
		output_buffer.write(chaine);
		output_buffer.close();

	}

	@Override
	public void writeExperimentXml(File outputFile, String codes) throws IOException, JSONDeviceException, FatalException
	{
		String[] experimentCodes = codes.split(",");
		// ouvrir fichier en ecriture
		log.debug("Creation du fichier " + outputFile);
		BufferedWriter output_buffer = new BufferedWriter(new FileWriter(outputFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<EXPERIMENT_SET>\n";

		JSONDevice jsonDevice = new JSONDevice();
		for (int i=0; i<experimentCodes.length;i++){
			String experimentCode = experimentCodes[i];
			ResourceProperties rpsExp = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/experiments/"+experimentCode,"bot").iterator().next();

			log.debug("ReadSpec "+rpsExp.get("readSpecs"));

			String accession = rpsExp.get("accession");
			String studyCode = rpsExp.get("studyCode");
			String studyAccession = rpsExp.get("studyAccession");
			String sampleCode = rpsExp.get("sampleCode");
			String sampleAccession = rpsExp.get("sampleAccession");

			String libraryStrategy = rpsExp.get("libraryStrategy");
			ResourceProperties rpsLibraryStrategy = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/variables/libraryStrategy/"+libraryStrategy.toLowerCase(),"bot").iterator().next();
			String librarySource = rpsExp.get("librarySource");
			ResourceProperties rpsLibrarySource = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/variables/librarySource/"+librarySource.toLowerCase(),"bot").iterator().next();
			String librarySelection = rpsExp.get("librarySelection");
			ResourceProperties rpsLibrarySelection = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/variables/librarySelection/"+librarySelection.toLowerCase(),"bot").iterator().next();
			String libraryLayout = rpsExp.get("libraryLayout");
			ResourceProperties rpsLibraryLayout = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/variables/libraryLayout/"+libraryLayout.toLowerCase(),"bot").iterator().next();
			String libraryConstructionProtocol = rpsExp.get("libraryConstructionProtocol");
			String typePlatform = rpsExp.get("typePlatform");
			ResourceProperties rpsTypePlatform = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/variables/typePlatform/"+typePlatform.toLowerCase(),"bot").iterator().next();
			String instrumentModel = rpsExp.get("instrumentModel");
			ResourceProperties rpsInstrumentModel = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/variables/instrumentModel/"+instrumentModel.toLowerCase(),"bot").iterator().next();

			chaine = chaine + "  <EXPERIMENT alias=\"" + experimentCode + "\" center_name=\"" + ProjectProperties.getProperty("centerName") + "\"";
			if (accession!=null && !accession.equals("")) {
				chaine = chaine + " accession=\"" + accession + "\" ";	
			}
			chaine = chaine + ">\n";
			// Les champs title et libraryName sont considerés comme obligatoires
			chaine = chaine + "    <TITLE>" + rpsExp.get("title") + "</TITLE>\n";
			chaine = chaine + "    <STUDY_REF ";
			//if (StringUtils.isNotBlank(experiment.studyCode) && (experiment.studyCode.startsWith("external"))) { 
			if (studyCode!=null && !studyCode.equals("")) { 
				chaine = chaine + " refname=\"" + studyCode +"\"";
			}
			if (studyAccession!=null && !studyAccession.equals("")){
				chaine = chaine + " accession=\"" + studyAccession + "\"";
			}
			chaine = chaine + "/>\n"; 

			chaine = chaine + "      <DESIGN>\n";
			chaine = chaine + "        <DESIGN_DESCRIPTION></DESIGN_DESCRIPTION>\n";
			chaine = chaine + "          <SAMPLE_DESCRIPTOR  ";
			//if (StringUtils.isNotBlank(experiment.sampleCode) && (experiment.sampleCode.startsWith("external"))) {
			if (sampleCode!=null && !sampleCode.equals("")){
				chaine = chaine+  "refname=\"" + sampleCode + "\"";
			}
			if (sampleAccession!=null && !sampleAccession.equals("")){
				chaine = chaine + " accession=\""+sampleAccession + "\"";
			}
			chaine = chaine + "/>\n";

			chaine = chaine + "          <LIBRARY_DESCRIPTOR>\n";
			chaine = chaine + "            <LIBRARY_NAME>" + rpsExp.get("libraryName") + "</LIBRARY_NAME>\n";
			chaine = chaine + "            <LIBRARY_STRATEGY>"+ rpsLibraryStrategy.get("value") + "</LIBRARY_STRATEGY>\n";
			chaine = chaine + "            <LIBRARY_SOURCE>" + rpsLibrarySource.get("value") + "</LIBRARY_SOURCE>\n";
			chaine = chaine + "            <LIBRARY_SELECTION>" + rpsLibrarySelection.get("value") + "</LIBRARY_SELECTION>\n";
			chaine = chaine + "            <LIBRARY_LAYOUT>\n";

			chaine = chaine + "              <"+ rpsLibraryLayout.get("value");	
			if("PAIRED".equalsIgnoreCase(libraryLayout)) {
				chaine = chaine + " NOMINAL_LENGTH=\"" + rpsExp.get("libraryLayoutNominalLength") + "\"";
			}
			chaine = chaine + " />\n";

			chaine = chaine + "            </LIBRARY_LAYOUT>\n";
			if (libraryConstructionProtocol!=null && !libraryConstructionProtocol.equals("")){
				chaine = chaine + "            <LIBRARY_CONSTRUCTION_PROTOCOL>"+libraryConstructionProtocol+"</LIBRARY_CONSTRUCTION_PROTOCOL>\n";
			} else {
				chaine = chaine + "            <LIBRARY_CONSTRUCTION_PROTOCOL>none provided</LIBRARY_CONSTRUCTION_PROTOCOL>\n";
			}
			chaine = chaine + "          </LIBRARY_DESCRIPTOR>\n";
			if (! "OXFORD_NANOPORE".equalsIgnoreCase(rpsExp.get("typePlatform"))) {
				chaine = chaine + "          <SPOT_DESCRIPTOR>\n";
				chaine = chaine + "            <SPOT_DECODE_SPEC>\n";
				chaine = chaine + "              <SPOT_LENGTH>"+rpsExp.get("spotLength")+"</SPOT_LENGTH>\n";
				/*for (ReadSpec readSpec: experiment.readSpecs) {
					chaine = chaine + "              <READ_SPEC>\n";
					chaine = chaine + "                <READ_INDEX>"+readSpec.readIndex+"</READ_INDEX>\n";
					chaine = chaine + "                <READ_LABEL>"+readSpec.readLabel+"</READ_LABEL>\n";
					chaine = chaine + "                <READ_CLASS>"+readSpec.readClass+"</READ_CLASS>\n";
					chaine = chaine + "                <READ_TYPE>"+readSpec.readType+"</READ_TYPE>\n";
					chaine = chaine + "                <BASE_COORD>" + readSpec.baseCoord + "</BASE_COORD>\n";
					chaine = chaine + "              </READ_SPEC>\n";
				}*/
				chaine = chaine + "            </SPOT_DECODE_SPEC>\n";
				chaine = chaine + "          </SPOT_DESCRIPTOR>\n";
			}
			chaine = chaine + "      </DESIGN>\n";
			chaine = chaine + "      <PLATFORM>\n";
			chaine = chaine + "        <" + rpsTypePlatform.get("value") + ">\n";
			chaine = chaine + "          <INSTRUMENT_MODEL>" + rpsInstrumentModel.get("value") + "</INSTRUMENT_MODEL>\n";
			chaine = chaine + "        </" + rpsTypePlatform.get("value") + ">\n";
			chaine = chaine + "      </PLATFORM>\n";
			chaine = chaine + "  </EXPERIMENT>\n";
		}
		chaine = chaine + "</EXPERIMENT_SET>\n";
		output_buffer.write(chaine);
		output_buffer.close();

	}

	@Override
	public void writeRunXml(File outputFile, String codes) throws IOException, JSONDeviceException, FatalException
	{
		String[] runCodes = codes.split(",");
		JSONDevice jsonDevice = new JSONDevice();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		// On accede au run via l'experiment:
		// ouvrir fichier en ecriture
		System.out.println("Creation du fichier " + outputFile);
		BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<RUN_SET>\n";
		for (int i=0; i<runCodes.length; i++){
			String runCode = runCodes[i];
			ResourceProperties rpsRun = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/sra/experiments/runs/"+runCode,"bot").iterator().next();
			String accession = rpsRun.get("accession");
			
			log.debug("RawData "+rpsRun.get("listRawData"));
			
			System.out.println("Ecriture du run " + runCode);
			chaine = chaine + "  <RUN alias=\""+ runCode + "\" ";
			if (accession!=null && !accession.equals("")) {
				chaine = chaine + " accession=\"" + accession + "\" ";
			}

			//Format date
			chaine =  chaine + "run_date=\""+ formatter.format(rpsRun.get("runDate"))+"\"  run_center=\""+rpsRun.get("runCenter")+ "\" ";
			chaine = chaine + ">\n";
			chaine = chaine + "    <EXPERIMENT_REF refname=\"" + rpsRun.get("expCode") + "\"/>\n";
			chaine = chaine + "    <DATA_BLOCK>\n";
			chaine = chaine + "      <FILES>\n";

			/*for (RawData rawData: run.listRawData) {
				String fileType = rawData.extention;
				String relatifName = rawData.relatifName;
				fileType = fileType.replace(".gz", "");
				chaine = chaine + "        <FILE filename=\"" + relatifName + "\" "+"filetype=\"" + fileType + "\" checksum_method=\"MD5\" checksum=\"" + rawData.md5 + "\">\n";
				if ( run.listRawData.size() == 2 ) {
					chaine = chaine + "          <READ_LABEL>F</READ_LABEL>\n";
					chaine = chaine + "          <READ_LABEL>R</READ_LABEL>\n";
				}
				chaine = chaine + "        </FILE>\n";
			}*/
			chaine = chaine + "      </FILES>\n";
			chaine = chaine + "    </DATA_BLOCK>\n";
			chaine = chaine + "  </RUN>\n";
		}
		chaine = chaine + "</RUN_SET>\n";
		output_buffer.write(chaine);
		output_buffer.close();
	}

	@Override
	public void writeSubmissionXml(File outputFile, String code, String studyCode, String sampleCodes, String experimentCodes) throws IOException
	{
		// ouvrir fichier en ecriture
		log.debug("Creation du fichier " + outputFile);
		BufferedWriter output_buffer = new BufferedWriter(new java.io.FileWriter(outputFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<SUBMISSION_SET>\n";

		log.debug("Ecriture du submission " + code);
		chaine = chaine + "  <SUBMISSION alias=\""+ code + "\" ";
		chaine = chaine + ">\n";	
		chaine = chaine + "    <CONTACTS>\n";
		chaine = chaine + "      <CONTACT name=\"william\" inform_on_status=\"william@genoscope.cns.fr\" inform_on_error=\"william@genoscope.cns.fr\"/>\n";
		chaine = chaine + "    </CONTACTS>\n";

		chaine = chaine + "    <ACTIONS>\n";
		// soumission systematique en confidential meme si study deja public
		chaine = chaine + "      <ACTION>\n        <HOLD/>\n      </ACTION>\n";
		if (studyCode!=null && !studyCode.equals("")) {
			chaine = chaine + "      <ACTION>\n        <ADD source=\"study.xml\" schema=\"study\"/>\n      </ACTION>\n";
		}
		if (sampleCodes!=null &&!sampleCodes.equals("")){
			chaine = chaine + "      <ACTION>\n        <ADD source=\"sample.xml\" schema=\"sample\"/>\n      </ACTION>\n";
		}
		if (experimentCodes!=null && !experimentCodes.equals("")){
			chaine = chaine + "      <ACTION>\n        <ADD source=\"experiment.xml\" schema=\"experiment\"/>\n      </ACTION>\n";
			chaine = chaine + "      <ACTION>\n        <ADD source=\"run.xml\" schema=\"run\"/>\n      </ACTION>\n";
		}
		chaine = chaine + "    </ACTIONS>\n";

		chaine = chaine + "  </SUBMISSION>\n";
		chaine = chaine + "</SUBMISSION_SET>\n";

		output_buffer.write(chaine);
		output_buffer.close();	
	}

	@Override
	public void createXMLRelease(File outputFile, String submissionCode, String studyCode) throws BirdsException, IOException
	{

		// ouvrir fichier en ecriture
		BufferedWriter output_buffer = new BufferedWriter(new FileWriter(outputFile));
		String chaine = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
		chaine = chaine + "<SUBMISSION_SET>\n";

		log.debug("Ecriture du submission " + submissionCode);
		chaine = chaine + "  <SUBMISSION alias=\""+ submissionCode + "\" ";
		chaine = chaine + ">\n";	
		chaine = chaine + "    <CONTACTS>\n";
		chaine = chaine + "      <CONTACT  name=\"william\" inform_on_status=\"william@genoscope.cns.fr\" inform_on_error=\"william@genoscope.cns.fr\"/>\n";
		chaine = chaine + "    </CONTACTS>\n";

		chaine = chaine + "    <ACTIONS>\n";

		chaine = chaine + "      <ACTION>\n        <RELEASE target=\"" + studyCode + "\"/>\n      </ACTION>\n";

		chaine = chaine + "    </ACTIONS>\n";



		chaine = chaine + "  </SUBMISSION>\n";
		chaine = chaine + "</SUBMISSION_SET>\n";

		output_buffer.write(chaine);
		output_buffer.close();	
	}


}
