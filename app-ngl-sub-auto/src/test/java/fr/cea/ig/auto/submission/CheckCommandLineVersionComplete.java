package fr.cea.ig.auto.submission;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.genoscope.lis.devsi.birds.api.entity.Job;
import fr.genoscope.lis.devsi.birds.api.entity.TreatmentSpecification;
import fr.genoscope.lis.devsi.birds.api.exception.BirdsException;
import fr.genoscope.lis.devsi.birds.api.exception.FatalException;
import fr.genoscope.lis.devsi.birds.api.exception.PersistenceException;
import fr.genoscope.lis.devsi.birds.api.persistence.EntityManagerHelper;
import fr.genoscope.lis.devsi.birds.api.persistence.PersistenceServiceFactory;
import fr.genoscope.lis.devsi.birds.api.test.GenericTest;
import fr.genoscope.lis.devsi.birds.impl.factory.JobServiceFactory;
import fr.genoscope.lis.devsi.birds.impl.factory.TreatmentSpecificationServiceFactory;

public class CheckCommandLineVersionComplete extends GenericTest{

	/**
	 * Check command Line for old version : complete version with zip and md5
	 */
	private static Logger log = Logger.getLogger(CheckCommandLineVersionComplete.class);

	private final String urlProd = "http://appprod.genoscope.cns.fr:90??/";
	private final String urlDev = "http://appdev.genoscope.cns.fr:90??/";
	private final String workspace = "/env/cns/submit_traces/SRA/SNTS_output_xml/autoFtpTest/log";

	@Before
	public void addDeclaration() throws PersistenceException, BirdsException, FatalException
	{
		addConfig("changesets/changesets_versionComplete.xml", "declarations/admin_versionComplete.xml", "declarations/specification_versionComplete.xml", "SRA");
		//Changer le project directory pour ne pas mettre les output dans le repertoire de prod
		replaceProjectDirectory("SRA", workspace);
	}

	/**
	 * 
	 * @throws FatalException
	 * @throws BirdsException
	 */
	@Test
	public void shouldCreateZipCommandLine() throws FatalException, BirdsException
	{
		//replace referentialDevice by abstractDevice
		replaceDevice("internalRefSubmission", "rawDataDevice", "fr.cea.ig.auto.submission.test.referential.RawDataDevice");

		//Building job simulation
		jobsBuildingSimulation("zip", "SRA");

		//Get jobs
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Set<Job> jobs = JobServiceFactory.getInstance().getAllJobs(em.getEm());
		for(Job job : jobs){
			log.debug(job);
			log.debug("Command line "+job.getUnixCommand());
			//Check command line 
			if(job.getUniqueJobResource("rawDataFile").getProperty("relatifName").equals("file1.fastq")){
				String fileName = job.getUniqueJobResource("rawDataFile").getProperty("path")+File.separator+job.getUniqueJobResource("rawDataFile").getProperty("relatifName");
				String unixCommandExpected = "gzip -c "+fileName+" > "+fileName+".gz";
				Assert.assertTrue(job.getUnixCommand().equals(unixCommandExpected));
			}
		}
		em.endTransaction();


	}

	@Test
	public void shouldCreateMd5CommandLine() throws FatalException, BirdsException
	{
		//replace device by abstractDevice
		replaceDevice("internalRefRawData", "rawDataDevice", "fr.cea.ig.auto.submission.test.referential.RawDataDevice");

		//Building job simulation
		jobsBuildingSimulation("md5", "SRA");

		//Get jobs
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Set<Job> jobs = JobServiceFactory.getInstance().getAllJobs(em.getEm());
		for(Job job : jobs){
			log.debug(job);
			log.debug("Command line "+job.getUnixCommand());
			if(job.getUniqueJobResource("zipDataFile").getProperty("fileZiped").equals("/env/cns/submit_traces/SRA/SNTS_output_xml/autoFtpTest/testU/dataTest/file1.fastq.gz")){
				String unixCommandExpected = "md5sum "+job.getUniqueJobResource("zipDataFile").getProperty("fileZiped")+" >> "+job.getUniqueJobResource("zipDataFile").getProperty("md5File");
				Assert.assertTrue(job.getUnixCommand().equals(unixCommandExpected));
			}
		}
		em.endTransaction();
	}

	@Test
	public void shouldCreateFtpCommandLine() throws FatalException, BirdsException
	{
		//replace device by abstractDevice
		replaceDevice("internalRefSubmission", "submissionDevice", "fr.cea.ig.auto.submission.test.referential.SubmissionDevice");

		//Building job simulation
		jobsBuildingSimulation("ftp", "SRA");

		//Get jobs
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		//Get one job 
		Job job = JobServiceFactory.getInstance().getAllJobs(em.getEm()).iterator().next();
		log.debug(job);
		log.debug("Command line "+job.getUnixCommand());
		String[] extensions = new String[] { "fastq.gz", "sff" , "srf" };
		String commandLineExpected = "ncftpput -u era-drop-9 -p Axqw16nI -R -t 60 -V ftp.sra.ebi.ac.uk / "+SRAFilesUtil.getLocalDirectoryParameter(job.getUniqueJobResource("rawDataDir").getProperty("directory"), extensions);
		Assert.assertTrue(job.getUnixCommand().equals(commandLineExpected));
		em.endTransaction();
	}


	@Test
	public void shouldCreateXMLCommandLine() throws FatalException, BirdsException
	{
		//replace device by abstractDevice
		replaceDevice("internalRefSubmission", "submissionDevice", "fr.cea.ig.auto.submission.test.referential.SubmissionDevice");

		//Replace API REST Service NGL Sub by dev services
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		TreatmentSpecification treatSpec = TreatmentSpecificationServiceFactory.getInstance().getTreatmentSpecification("createXML", "SRA",em.getEm());
		treatSpec.getExecutableSpecification().getExecutable().setExecutablePath(treatSpec.getExecutableSpecification().getExecutable().getExecutablePath().replace(urlProd, urlDev));
		em.endTransaction();

		//Building job simulation
		jobsBuildingSimulation("createXML", "SRA");

		//Get job
		em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Job job = JobServiceFactory.getInstance().getAllJobs(em.getEm()).iterator().next();
		log.debug(job);
		log.debug("Command line "+job.getUnixCommand());
		Assert.assertTrue(job.getUnixCommand().equals("curl "+urlDev+"api/submissions/"+job.getUniqueJobResource("subToXML").getProperty("code")));
		em.endTransaction();

	}

	@Test
	public void shouldCreateSendXMLCommandLine() throws FatalException, BirdsException
	{
		//replace device by abstractDevice
		replaceDevice("internalRefSubmission", "submissionDevice", "fr.cea.ig.auto.submission.test.referential.SubmissionDevice");

		//Building job simulation
		jobsBuildingSimulation("sendXML", "SRA");

		//Get job
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Job job = JobServiceFactory.getInstance().getAllJobs(em.getEm()).iterator().next();
		log.debug(job);
		log.debug("Command line "+job.getUnixCommand());
		String startCommandLineExpected = "curl https://www.ebi.ac.uk/ena/submit/drop-box/submit/?auth=ERA%20era-drop-9%20N7mo%2B8F4aHH%2BrCjLTuMo59xwfFo%3D -k ";
		Assert.assertTrue(job.getUnixCommand().startsWith(startCommandLineExpected));
		Assert.assertTrue(job.getUnixCommand().contains("-F \"SUBMISSION=@"+job.getUniqueJobResource("subToSend").getProperty("submissionXML")+"\""));
		Assert.assertTrue(job.getUnixCommand().contains("-F \"SAMPLE=@"+job.getUniqueJobResource("subToSend").getProperty("sampleXML")+"\""));
		Assert.assertTrue(job.getUnixCommand().contains("-F \"RUN=@"+job.getUniqueJobResource("subToSend").getProperty("runXML")+"\""));
		Assert.assertTrue(job.getUnixCommand().contains("-F \"STUDY=@"+job.getUniqueJobResource("subToSend").getProperty("studyXML")+"\""));
		Assert.assertTrue(job.getUnixCommand().contains("-F \"EXPERIMENT=@"+job.getUniqueJobResource("subToSend").getProperty("experimentXML")+"\""));
		em.endTransaction();
	}

	@Test
	public void shouldCreateSendXMLCommandLineWithNoStudy() throws FatalException, BirdsException
	{
		//replace device by abstractDevice
		replaceDevice("internalRefSubmission", "submissionDevice", "fr.cea.ig.auto.submission.test.referential.SubmissionDeviceWithNullValue");

		//Building job simulation
		jobsBuildingSimulation("sendXML", "SRA");

		//Get job
		EntityManagerHelper em = PersistenceServiceFactory.getInstance().createEntityManagerHelper();
		em.beginTransaction();
		Job job = JobServiceFactory.getInstance().getAllJobs(em.getEm()).iterator().next();
		log.debug(job);
		log.debug("Command line "+job.getUnixCommand());
		Assert.assertFalse(job.getUnixCommand().contains("-F \"STUDY=@"));
	}
}
