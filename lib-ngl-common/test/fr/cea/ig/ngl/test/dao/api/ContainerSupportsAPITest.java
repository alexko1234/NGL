package fr.cea.ig.ngl.test.dao.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.containers.ContainerSupportsAPI;
import fr.cea.ig.ngl.dao.api.containers.ContainersAPI;
import fr.cea.ig.ngl.dao.projects.ProjectsAPI;
import fr.cea.ig.ngl.dao.samples.SamplesAPI;
import fr.cea.ig.ngl.test.AbstractAPITests;
import fr.cea.ig.ngl.test.TestAppWithDroolsFactory;
import fr.cea.ig.ngl.test.dao.api.factory.TestContainerFactory;
import fr.cea.ig.ngl.test.dao.api.factory.TestProjectFactory;
import fr.cea.ig.ngl.test.dao.api.factory.TestSampleFactory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.DescriptionHelper;
import play.Logger.ALogger;
import rules.services.test.TestRules6Component;
import utils.AbstractTests;

public class ContainerSupportsAPITest extends AbstractTests implements AbstractAPITests {

	private static final play.Logger.ALogger logger = play.Logger.of(ContainerSupportsAPITest.class);
	private static final TestAppWithDroolsFactory TEST_APP_FACTORY = new TestAppWithDroolsFactory("ngl-sq.test.conf").bindRulesComponent();
	private static final String USER = "ngsrg";
	private static final double QUANTITY = 1.0;
	private static final double VOL = 1.0;
	
	//Tested API
	private static ContainerSupportsAPI api;

	// required APIs
	private static ProjectsAPI projectApi;
	private static SamplesAPI sampleApi;
	private static ContainersAPI contApi;

	// Reference objects
	private static Container refContainer;
	private static ContainerSupport refContainerSupport;
	private static Sample refSample;
	private static Project refProject;
	
	private ContainerSupport data;
	private static boolean clean = true;

	@Override
	public ALogger logger() {
		return logger;
	}
	
	@BeforeClass
	public static void startTestApplication() {
		logger.info("Start an app (Test Mode) using NGL-SQ TU config");
		app = TEST_APP_FACTORY.createApplication();
		DescriptionHelper.initInstitute();
		logger.info("test app started");
	}

	@BeforeClass
	public static void setUpClass() {
		assertTrue(app.isDev());
		projectApi = app.injector().instanceOf(ProjectsAPI.class);
		assertNotNull(projectApi);
		sampleApi = app.injector().instanceOf(SamplesAPI.class);
		Assert.assertNotNull(sampleApi);
		app.injector().instanceOf(TestRules6Component.class);	
		contApi = app.injector().instanceOf(ContainersAPI.class);
		Assert.assertNotNull(contApi);

		api = app.injector().instanceOf(ContainerSupportsAPI.class);
		Assert.assertNotNull(api);
		
		logger.debug("define ref objects");
		refProject = TestProjectFactory.project(USER);
		refSample = TestSampleFactory.sample(USER, refProject);
		refContainerSupport = TestContainerFactory.containerSupport(USER, refProject, refSample);
		refContainer = TestContainerFactory.container(USER, VOL, QUANTITY, refProject, refSample, refContainerSupport);
	}

	@After
	public void tearDown() {
		if(! clean) {
			deleteData();
		} else {
			logger.trace("data already cleaned");
		}
	}

	@Override
	public void setUpData() {
		if(clean) {
			logger.debug("create dep objects and test data");
			try {
				projectApi.create(refProject, USER);
				sampleApi.create(refSample, USER);
				data = api.create(refContainerSupport, USER);
				contApi.create(refContainer, USER);
			} catch (APIValidationException e) {
				logger.error(e.getMessage());
				logger.error("invalid fields: " + e.getErrors().keySet().toString());
				logValidationErrors(e);
				exit(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				exit(e.getMessage());
			} finally {
				clean = false;
			}
		} else {
			deleteData();
			setUpData();
		}
	}

	@Override
	public void deleteData() {
		logger.debug("delete dep objects and test data");
		try {
			api.delete(refContainerSupport.code);
		} catch (Exception e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		} finally {
			logger.debug("remove dep objects");
			contApi.delete(data.code);
			sampleApi.delete(refSample.code);
			projectApi.delete(refProject.code);	
			clean = true;
		}
	}

	@Test
	public void createTest() {
		logger.debug("Creation test");
		setUpData();
		Assert.assertEquals(refContainerSupport.categoryCode, data.categoryCode);
		Assert.assertEquals(refContainerSupport.code, data.code);
		Assert.assertEquals(refContainerSupport.nbContainers, data.nbContainers);
		Assert.assertEquals(refContainerSupport.nbContents, data.nbContents);
		deleteData();
	}
	
	@Test
	public void updateTest() {
		logger.debug("Update test");
		setUpData();
		try {
			ContainerSupport supportToUpdate = data;
			supportToUpdate.nbContainers = 3;
			String storageCode = "Bt20_70_A1";
			supportToUpdate.storageCode = storageCode;
			api.update(supportToUpdate, USER);
			
			ContainerSupport support = api.get(refContainerSupport.code);
			Assert.assertEquals(refContainerSupport.categoryCode, support.categoryCode);
			Assert.assertNotEquals(refContainerSupport.nbContainers, support.nbContainers);
			Assert.assertEquals(supportToUpdate.nbContainers, support.nbContainers);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}
	
	@Test 
	public void updateFieldsTest() {
		logger.debug("Update only some fields test");
		setUpData();
		try {
			ContainerSupport supportToUpdate = new ContainerSupport();
			supportToUpdate.code = data.code;
			String storageCode = "Bt20_70_A1";
			supportToUpdate.storageCode = storageCode;
			api.update(supportToUpdate, USER, Arrays.asList("storageCode"));
			
			ContainerSupport support = api.get(refContainerSupport.code);
			Assert.assertNotEquals(refContainerSupport.storageCode, support.storageCode);
			Assert.assertEquals(storageCode, support.storageCode);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}
	
	@Test
	public void deleteTest() throws APIException {
		logger.debug("Delete test");
		setUpData();
		contApi.delete(refContainer.code);
		Assert.assertNull(contApi.get(refContainer.getCode()));
		deleteData();
	}

	@Test
	public void getTest() {
		logger.debug("Get test");
		setUpData();
		try {
			ContainerSupport support = api.get(refContainerSupport.code);
			Assert.assertNotNull(support);
			Assert.assertEquals(data.get_id(), support.get_id());
			Assert.assertEquals(data.getCode(), support.getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}
}
