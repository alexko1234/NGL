package fr.cea.ig.auto.submission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import fr.genoscope.lis.devsi.birds.api.device.JSONDevice;
import fr.genoscope.lis.devsi.birds.api.entity.Job;
import fr.genoscope.lis.devsi.birds.api.entity.ResourceProperties;
import fr.genoscope.lis.devsi.birds.api.exception.BirdsException;
import fr.genoscope.lis.devsi.birds.api.exception.FatalException;
import fr.genoscope.lis.devsi.birds.api.exception.PersistenceException;
import fr.genoscope.lis.devsi.birds.api.persistence.EntityManagerHelper;
import fr.genoscope.lis.devsi.birds.api.persistence.PersistenceServiceFactory;
import fr.genoscope.lis.devsi.birds.api.test.GenericTest;
import fr.genoscope.lis.devsi.birds.impl.factory.CoreJobServiceFactory;
import fr.genoscope.lis.devsi.birds.impl.factory.CoreTreatmentSpecificationServiceFactory;
import fr.genoscope.lis.devsi.birds.impl.factory.JobServiceFactory;
import fr.genoscope.lis.devsi.birds.impl.properties.ProjectProperties;

public class CheckWorkflow extends GenericTest{

	private static Logger log = Logger.getLogger(CheckWorkflow.class);
	private final String scriptEcho = "/env/cns/home/ejacoby/testU/app-NGL-sub-auto/bin/scriptEcho.sh";
	private final String scriptCreateXML = "/env/cns/home/ejacoby/testU/app-NGL-sub-auto/bin/scriptCreateXML";
	private final String workspace = "/env/cns/home/ejacoby/testU/app-NGL-sub-auto/log";

	/**
	 * Call initData of NGL-SUB unit test (builder.data)
	 * @throws PersistenceException
	 * @throws BirdsException
	 * @throws FatalException
	 */
	@Before
	public void addDeclaration() throws PersistenceException, BirdsException, FatalException
	{
		addConfig("changesets/changesets.xml", "declarations/admin.xml", "declarations/specification.xml", "SRA");
		//Changer le project directory pour ne pas mettre les output dans le repertoire de prod
		replaceProjectDirectory("SRA", workspace);
	}


	@Test
	public void shouldExecuteWorkflowWithNGLSUB() throws PersistenceException, BirdsException, FatalException, IOException
	{
		JSONDevice jsonDevice = new JSONDevice();

		//Replace executable of createXML
		replaceExecutable("createXML", "SRA", CoreTreatmentSpecificationServiceFactory.getInstance().getTreatmentSpecification("createXML", "SRA").getExecutableSpecification().getExecutable().getExecutablePath().replace("appprod", "appdev"));

		//Replace executable of transfertRawData 
		//TODO voir si existe url de test au NCBI
		replaceExecutable("transfertRawData", "SRA", scriptEcho);

		//Replace executable of sendXML 
		//TODO voir si existe url de test au NCBI
		replaceExecutable("sendXML", "SRA", scriptEcho);

		log.debug("FIRST ROUND");
		executeBirdsCycle("SRA", "WF_Submission");

		//Check group
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Set<Job> jobs = CoreJobServiceFactory.getInstance().getAllJobs(em.getEm());
		Assert.assertEquals(jobs.size(), 1);
		Job groupJob = jobs.iterator().next();
		ResourceProperties rp = groupJob.getUniqueJobResource("subData").getResourceProperties();
		String codeSubmission = rp.get("code");
		Assert.assertNotNull(codeSubmission);
		Assert.assertEquals(rp.get("state.code"),"inWaiting");
		Assert.assertNotNull(rp.get("submissionDirectory"));
		Assert.assertNotNull(rp.get("submissionDate"));
		for(String key : rp.keysSet()){
			log.debug("key:"+key+"="+rp.get(key));
		}
		em.endTransaction();

		log.debug("SECOND ROUND");
		executeBirdsCycle("SRA", "WF_Submission");

		//Check group update state in transfert ressource
		//Get submission in waiting ==0
		Set<ResourceProperties> setRP = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/submissions?state=inWaiting");
		Assert.assertEquals(setRP.size(),0);


		//Check CreateXML
		//Check transfertRawData
		em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Job jobCreateXML = CoreJobServiceFactory.getInstance().getJobBySpecification("createXML", em.getEm()).iterator().next();
		Assert.assertNotNull(jobCreateXML);
		//Check output ressources
		ResourceProperties rpOut = jobCreateXML.getOutputUniqueJobResource("outputSubXML").getResourceProperties();
		log.debug("Resource properties out createXML : "+rpOut);
		Assert.assertNotNull(rpOut.get("xmlSamples"));
		Assert.assertEquals(rpOut.get("xmlSamples"), "sample.xml");
		Assert.assertNotNull(rpOut.get("xmlRuns"));
		Assert.assertEquals(rpOut.get("xmlRuns"), "run.xml");
		Assert.assertNotNull(rpOut.get("xmlSubmission"));
		Assert.assertEquals(rpOut.get("xmlSubmission"), "submission.xml");
		Assert.assertNotNull(rpOut.get("xmlStudys"));
		Assert.assertEquals(rpOut.get("xmlStudys"),"null");
		
		Job jobTransfertRawData = CoreJobServiceFactory.getInstance().getJobBySpecification("transfertRawData",em.getEm()).iterator().next();
		Assert.assertNotNull(jobTransfertRawData);
		log.debug("Command aspera "+jobTransfertRawData.getUnixCommand());
		Assert.assertTrue(jobTransfertRawData.getUnixCommand().contains("-i ~/.ssh/ebi.sra -T -l 300M"));
		Assert.assertTrue(jobTransfertRawData.getUnixCommand().endsWith(" --mode=send --host=webin.ebi.ac.uk --user=Webin-9 ."));
		Assert.assertTrue(jobTransfertRawData.getUnixCommand().contains(jobTransfertRawData.getUniqueJobResource("rawDataDir").getProperty("submissionDirectory")+"/list_aspera_WGS"));

		//Check file WGS
		File file = new File(jobTransfertRawData.getParameterValue("fileList").getValue());
		Assert.assertTrue(file.exists());
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		int nbLine=0;
		while((line=br.readLine())!=null){
			log.debug("Line "+line);
			nbLine++;
		}
		br.close();
		Assert.assertEquals(nbLine, 4);
		Assert.assertTrue(file.delete());
		ResourceProperties rpOutRawData = jobTransfertRawData.getOutputUniqueJobResource("outputRawData").getResourceProperties();
		Assert.assertNotNull(rpOutRawData.get("fileList"));

		em.endTransaction();


		log.debug("THIRD ROUND");
		executeBirdsCycle("SRA", "WF_Submission");
		//Check send XML
		em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Job jobSendXML = CoreJobServiceFactory.getInstance().getJobBySpecification("sendXML",em.getEm()).iterator().next();
		log.debug("job send XML "+jobSendXML);
		Assert.assertNotNull(jobSendXML);

		Assert.assertTrue(jobSendXML.getUnixCommand().contains("-F \"SUBMISSION=@"+jobSendXML.getUniqueJobResource("subToSend").getProperty("xmlSubmission")+"\""));
		Assert.assertTrue(jobSendXML.getUnixCommand().contains("-F \"SAMPLE=@"+jobSendXML.getUniqueJobResource("subToSend").getProperty("xmlSamples")+"\""));
		Assert.assertTrue(jobSendXML.getUnixCommand().contains("-F \"RUN=@"+jobSendXML.getUniqueJobResource("subToSend").getProperty("xmlRuns")+"\""));
		Assert.assertTrue(jobSendXML.getUnixCommand().contains("-F \"STUDY=@"+jobSendXML.getUniqueJobResource("subToSend").getProperty("xmlStudys")+"\""));
		Assert.assertTrue(jobSendXML.getUnixCommand().contains("-F \"EXPERIMENT=@"+jobSendXML.getUniqueJobResource("subToSend").getProperty("xmlExperiments")+"\""));

		//End command line redirect to AC files
		Assert.assertTrue(jobSendXML.getUnixCommand().endsWith("> listAC_"+jobSendXML.getUniqueJobResource("subToSend").getProperty("submissionDate")+".txt"));

		//Check input
		Assert.assertEquals(jobSendXML.getInputValue("subToSend").getInputJobResourceValues().size(),1);
		Assert.assertEquals(jobSendXML.getInputValue("rawDataSend").getInputJobResourceValues().size(),1);
		em.endTransaction();

		//Get Submission from database
		
		Set<ResourceProperties> setRPSub = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/api/submissions?state=submitted");
		log.debug("Set RPub "+setRPSub);
		Assert.assertTrue(setRPSub.size()==1);
		ResourceProperties RPSub = setRPSub.iterator().next();
		Assert.assertTrue(RPSub.get("state.code").equals("submitted"));
		Assert.assertNotNull(RPSub.get("accession"));
		//Update state submission from IN_PROGRESS to IN_WAITING at the end of test
	  	//Get submission
	  	String JSONSubmission = jsonDevice.httpGet(ProjectProperties.getProperty("server")+"/api/submissions/"+codeSubmission);
	  	//Modify submission
	  	String newJSONSubmission = jsonDevice.modifyJSON(JSONSubmission, "state.code", "inWaiting");
	  	jsonDevice.httpPut(ProjectProperties.getProperty("server")+"/submissions/"+codeSubmission, newJSONSubmission);

	}

	
}
