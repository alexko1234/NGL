package fr.cea.ig.ngl.test.dao.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.DescriptionHelper;
import play.Logger.ALogger;
import rules.services.test.TestRules6Component;
import utils.AbstractTests;

public class ContainersAPITest extends AbstractTests implements AbstractAPITests {

	private static final play.Logger.ALogger logger = play.Logger.of(ContainersAPITest.class);
	private static final TestAppWithDroolsFactory TEST_APP_FACTORY = new TestAppWithDroolsFactory("ngl-sq.test.conf").bindRulesComponent();
	private static final String USER = "ngsrg";
	private static final double QUANTITY = 1.0;
	private static final double VOL = 1.0;
	
	//Tested API
	private static ContainersAPI api;

	// required APIs
	private static ProjectsAPI projectApi;
	private static SamplesAPI sampleApi;
	private static ContainerSupportsAPI csApi;

	// Reference objects
	private static Container refContainer;
	private static ContainerSupport refContainerSupport;
	private static Sample refSample;
	private static Project refProject;
	
	private Container data;
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
		csApi = app.injector().instanceOf(ContainerSupportsAPI.class);
		Assert.assertNotNull(csApi);
		
		api = app.injector().instanceOf(ContainersAPI.class);
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
	@Before
	public void setUpData() {
		if(clean) {
			logger.debug("create dep objects and test data");
			try {
				projectApi.create(refProject, USER);
				sampleApi.create(refSample, USER);
				csApi.create(refContainerSupport, USER);
				data = api.create(refContainer, USER);
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
	@After
	public void deleteData() {
		logger.debug("delete dep objects and test data");
		try {
			api.delete(data.code);
		} catch (Exception e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		} finally {
			logger.debug("remove dep objects");
			csApi.delete(refContainerSupport.code);
			sampleApi.delete(refSample.code);
			projectApi.delete(refProject.code);	
			clean = true;
		}
	}

	@Test
	public void createTest() {
		logger.debug("Creation test");
		Assert.assertEquals(refContainer.categoryCode, data.categoryCode);
		Assert.assertEquals(refContainer.code, data.code);
		Assert.assertEquals(refContainer.concentration, data.concentration);
		Assert.assertEquals(refContainer.volume, data.volume);
		Assert.assertEquals(refContainer.quantity, data.quantity);
		Assert.assertEquals(refContainer.contents.size(), data.contents.size());
	}
	
	@Test
	public void updateTest() {
		logger.debug("Update test");
		try {
			Container contToUpdate = data;
			final double newVol = VOL * 2;
			final double newConcentration = QUANTITY / newVol;
			contToUpdate.volume.value = newVol;
			contToUpdate.concentration.value = newConcentration;
			contToUpdate.traceInformation.modifyUser = USER;
			contToUpdate.traceInformation.modifyDate = new Date();
			api.update(contToUpdate, USER);
			
			Container cont = api.get(refContainer.code);
			Assert.assertEquals(refContainer.categoryCode, cont.categoryCode);
			Assert.assertNotEquals(refContainer.volume, cont.volume);
			Assert.assertEquals(contToUpdate.volume, cont.volume);
			Assert.assertNotEquals(refContainer.concentration, cont.concentration);
			Assert.assertEquals(contToUpdate.concentration, cont.concentration);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}
	
	@Test
	public void updateFieldsTest() {
		logger.debug("Update only some fields test");
		try {
			Container contToUpdate = new Container();
			contToUpdate.code = data.code;
			final double newVol = VOL * 2;
			final double newConcentration = QUANTITY / newVol;
			contToUpdate.volume = new PropertySingleValue(newVol, "µl");
			contToUpdate.concentration = new PropertySingleValue(newConcentration, "µl");
			api.update(contToUpdate, USER, Arrays.asList("volume", "concentration"));
			
			Container cont = api.get(refContainer.code);
			Assert.assertEquals(refContainer.categoryCode, cont.categoryCode);
			Assert.assertNotEquals(refContainer.volume, cont.volume);
			Assert.assertEquals(contToUpdate.volume, cont.volume);
			Assert.assertNotEquals(refContainer.concentration, cont.concentration);
			Assert.assertEquals(contToUpdate.concentration, cont.concentration);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}
	
	@Test
	public void deleteTest() throws APIException {
		logger.debug("Delete test");
		api.delete(refContainer.code);
		Assert.assertNull(api.get(refContainer.getCode()));
	}

	@Test
	public void getTest() {
		logger.debug("Get test");
		try {
			Container cont = api.get(refContainer.code);
			Assert.assertNotNull(cont);
			Assert.assertEquals(data.get_id(), cont.get_id());
			Assert.assertEquals(refContainer.getCode(), cont.getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}
	
	@Test
	public void updateStateTest() {
		logger.debug("update state test");
		try {
			State state = new State("IS", USER);
			api.updateState(refContainer.code, state, USER);
			Container cont = api.get(refContainer.code);
			Assert.assertEquals(state.code, cont.state.code);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}
}
