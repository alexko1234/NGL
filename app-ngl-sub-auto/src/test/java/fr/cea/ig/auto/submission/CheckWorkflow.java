package fr.cea.ig.auto.submission;

import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import fr.genoscope.lis.devsi.birds.api.entity.Job;
import fr.genoscope.lis.devsi.birds.api.exception.BirdsException;
import fr.genoscope.lis.devsi.birds.api.exception.FatalException;
import fr.genoscope.lis.devsi.birds.api.exception.PersistenceException;
import fr.genoscope.lis.devsi.birds.api.persistence.EntityManagerHelper;
import fr.genoscope.lis.devsi.birds.api.persistence.PersistenceServiceFactory;
import fr.genoscope.lis.devsi.birds.api.test.GenericTest;
import fr.genoscope.lis.devsi.birds.impl.factory.CoreJobServiceFactory;
import fr.genoscope.lis.devsi.birds.impl.factory.JobServiceFactory;

public class CheckWorkflow extends GenericTest{

	private static Logger log = Logger.getLogger(CheckWorkflow.class);
	private final String scriptEcho = "/env/cns/submit_traces/SRA/SNTS_output_xml/autoFtpTest/testU/bin/scriptEcho.sh";
	private final String scriptCreateXML = "/env/cns/submit_traces/SRA/SNTS_output_xml/autoFtpTest/testU/bin/scriptCreateXML";
	private final String workspace = "/env/cns/submit_traces/SRA/SNTS_output_xml/autoFtpTest/log";

	@Before
	public void addDeclaration() throws PersistenceException, BirdsException, FatalException
	{
		addConfig("changesets/changesets.xml", "declarations/admin.xml", "declarations/specification.xml", "SRA");
		//Changer le project directory pour ne pas mettre les output dans le repertoire de prod
		replaceProjectDirectory("SRA", workspace);
	}

	@Test
	public void shouldExecuteWorkflow() throws FatalException, BirdsException
	{
		//replace referentialDevice by abstractDevice
		replaceDevice("nglSubRef", "submissionDevice", "fr.cea.ig.auto.submission.test.referential.SubmissionDevice");

		//Replace executable of transfertRawData set to /env/cns/submit_traces/SRA/SNTS_output_xml/autoFtpTest/testU/bin/scriptEcho.sh
		replaceExecutable("transfertRawData", "SRA", scriptEcho);
		//Replace executable of createXML
		replaceExecutable("createXML", "SRA", scriptCreateXML);
		//Replace executable of sendXML
		replaceExecutable("sendXML", "SRA", scriptEcho);

		//Simulate a BIRDS cycle
		executeBirdsCycle("SRA", "WF_Submission");

		log.debug("FIRST ROUND");
		//Group job created with NEW status
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		//Create one group job
		Set<Job> jobs = JobServiceFactory.getInstance().getAllJobs(em.getEm());
		Assert.assertTrue(jobs.size()==1);
		Job groupJob = jobs.iterator().next();
		Assert.assertEquals(groupJob.getExecutionState(), Job.NEW_STATUS);
		em.endTransaction();

		//Simulate a BIRDS cycle
		executeBirdsCycle("SRA", "WF_Submission");
		log.debug("SECOND ROUND");
		//Transfert RawData and createXML step of workflow
		em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		groupJob = em.getEm().find(Job.class, groupJob.getId());
		//1 group in progress
		Assert.assertEquals(groupJob.getExecutionState(),Job.INPROGRESS_STATUS);
		Assert.assertEquals(groupJob.getSubJobs().size(), 2);

		//2 jobs DONE
		for(Job job : groupJob.getSubJobs()){
			log.debug(job);
			log.debug("Command line "+job.getUnixCommand());
			log.debug("status "+job.getExecutionState());
			log.debug("job stdout "+job.getProperty(Job.STDOUT));
			Assert.assertEquals(job.getExecutionState(), Job.DONE_STATUS);
		}
		em.endTransaction();

		//Simulate a BIRDS cycle
		executeBirdsCycle("SRA", "WF_Submission");
		log.debug("THIRD ROUND");
		//SendXML step of workflow
		em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		jobs = JobServiceFactory.getInstance().getAllJobs(em.getEm());
		//1 group in progress
		groupJob = em.getEm().find(Job.class, groupJob.getId());
		Assert.assertEquals(groupJob.getExecutionState(),Job.INPROGRESS_STATUS);
		//3 jobs DONE
		Assert.assertEquals(groupJob.getSubJobs().size(), 3);
		
		//Check transfertRawData job
		Job jobtransfertRawData = CoreJobServiceFactory.getInstance().getUniqueSubJobFromGroupJob(groupJob.getId(), "transfertRawData");
		Assert.assertNotNull(jobtransfertRawData);
		Assert.assertEquals(jobtransfertRawData.getExecutionState(),Job.DONE_STATUS);
		
		//Check createXML job
		Job jobcreateXML = CoreJobServiceFactory.getInstance().getUniqueSubJobFromGroupJob(groupJob.getId(), "createXML");
		Assert.assertNotNull(jobcreateXML);
		Assert.assertEquals(jobcreateXML.getExecutionState(),Job.DONE_STATUS);
		
		//Check sendXML
		Job jobsubToXML = CoreJobServiceFactory.getInstance().getUniqueSubJobFromGroupJob(groupJob.getId(), "sendXML");
		Assert.assertNotNull(jobsubToXML);
		Assert.assertEquals(jobsubToXML.getExecutionState(),Job.DONE_STATUS);
		
		em.endTransaction();
	}

	
}
