package fr.cea.ig.auto.submission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
import fr.genoscope.lis.devsi.birds.impl.factory.JobServiceFactory;
import fr.genoscope.lis.devsi.birds.impl.properties.ProjectProperties;

public class CheckWorkflow extends GenericTest{

	private static Logger log = Logger.getLogger(CheckWorkflow.class);
	private final String scriptEcho = "/env/cns/home/ejacoby/testU/app-NGL-sub-auto/bin/scriptEcho.sh";
	private final String scriptCreateXML = "/env/cns/home/ejacoby/testU/app-NGL-sub-auto/bin/scriptCreateXML";
	private final String workspace = "/env/cns/home/ejacoby/testU/app-NGL-sub-auto/log";

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
		//TODO replace with dev API REST
		replaceExecutable("createXML", "SRA", scriptCreateXML);
		
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
		Assert.assertEquals(rp.get("state.code"),"IN_WAITING");
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
		Set<ResourceProperties> setRP = jsonDevice.httpGetJSON(ProjectProperties.getProperty("server")+"/submissions/search/IN_WAITING");
		Assert.assertEquals(setRP.size(),0);


		//Check CreateXML
		//Check transfertRawData
		em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		jobs = CoreJobServiceFactory.getInstance().getAllJobs(em.getEm());
		for(Job job : jobs){
			log.debug("Job "+job);
			log.debug("Job command "+job.getUnixCommand());
			if(job.getTreatmentSpecification().getName().equals("createXML")){
				//Check output ressources
				ResourceProperties rpOut = job.getOutputUniqueJobResource("outputSubXML").getResourceProperties();
				Assert.assertNotNull(rpOut.get("sampleXML"));
				Assert.assertNotNull(rpOut.get("runXML"));
				Assert.assertNotNull(rpOut.get("submissionXML"));
				Assert.assertNotNull(rpOut.get("studyXML"));
			}else if(job.getTreatmentSpecification().getName().equals("transfertRawData")){
				log.debug("Command transfert "+job.getUnixCommand());
				Assert.assertTrue(job.getUnixCommand().contains("-T -k2 -Q -m 300M -v"));
				Assert.assertTrue(job.getUnixCommand().endsWith(" --mode send --host fasp.ega.ebi.ac.uk --user era-drop-9 / | tee result"));
				Assert.assertTrue(job.getUnixCommand().contains(job.getUniqueJobResource("rawDataDir").getProperty("submissionDirectory")+"/list_aspera_WGS"));
				
				//Check file WGS
				File file = new File(job.getParameterValue("fileList").getValue());
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
			}
		}
		em.endTransaction();

		
		log.debug("THIRD ROUND");
		executeBirdsCycle("SRA", "WF_Submission");
		//Check send XML
		em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		jobs = CoreJobServiceFactory.getInstance().getAllJobs(em.getEm());
		for(Job job : jobs){
			log.debug("Job "+job);
			log.debug("Job command "+job.getUnixCommand());
			if(job.getTreatmentSpecification().getName().equals("sendXML")){
				Assert.assertTrue(job.getUnixCommand().contains("-F \"SUBMISSION=@"+job.getUniqueJobResource("subToSend").getProperty("submissionXML")+"\""));
				Assert.assertTrue(job.getUnixCommand().contains("-F \"SAMPLE=@"+job.getUniqueJobResource("subToSend").getProperty("sampleXML")+"\""));
				Assert.assertTrue(job.getUnixCommand().contains("-F \"RUN=@"+job.getUniqueJobResource("subToSend").getProperty("runXML")+"\""));
				Assert.assertTrue(job.getUnixCommand().contains("-F \"STUDY=@"+job.getUniqueJobResource("subToSend").getProperty("studyXML")+"\""));
				Assert.assertTrue(job.getUnixCommand().contains("-F \"EXPERIMENT=@"+job.getUniqueJobResource("subToSend").getProperty("experimentXML")+"\""));
				
				//End command line redirect to AC files
				Assert.assertTrue(job.getUnixCommand().endsWith("> listAC_"+job.getUniqueJobResource("subToSend").getProperty("submissionDate")+".txt"));
			}
		}
		em.endTransaction();

		//Update state submission from IN_PROGRESS to IN_WAITING at the end of test
		jsonDevice.httpPut(ProjectProperties.getProperty("server")+"/submissions/"+codeSubmission+"/state/IN_WAITING", null);

	}

}
