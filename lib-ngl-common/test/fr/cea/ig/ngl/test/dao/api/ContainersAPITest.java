package fr.cea.ig.ngl.test.dao.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.api.containers.ContainersAPI;
import fr.cea.ig.ngl.dao.projects.ProjectsAPI;
import fr.cea.ig.ngl.dao.samples.SamplesAPI;
import fr.cea.ig.ngl.test.AbstractAPITests;
import fr.cea.ig.ngl.test.dao.api.factory.TestContainerFactory;
import fr.cea.ig.ngl.test.dao.api.factory.TestProjectFactory;
import fr.cea.ig.ngl.test.dao.api.factory.TestSampleFactory;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import play.Logger.ALogger;
import utils.AbstractTests;

public class ContainersAPITest extends AbstractTests implements AbstractAPITests {

	private static final play.Logger.ALogger logger = play.Logger.of(ContainersAPITest.class);

	private static final String USER = "ngsrg";

	//Tested API
	private static ContainersAPI api;

	// required APIs
	private static ProjectsAPI projectApi;
	private static SamplesAPI sampleApi;


	// Reference objects
	private static Container refContainer;
	private static Sample refSample;
	private static Project refProject;
	
	private Container data;
	private static boolean clean = true;

	@Override
	public ALogger logger() {
		return logger;
	}

	@BeforeClass
	public static void setUpClass() {
		assertTrue(app.isDev());
		projectApi = app.injector().instanceOf(ProjectsAPI.class);
		assertNotNull(projectApi);
		sampleApi = app.injector().instanceOf(SamplesAPI.class);
		Assert.assertNotNull(sampleApi);
		api = app.injector().instanceOf(ContainersAPI.class);
		Assert.assertNotNull(api);

		logger.debug("define ref objects");
		refProject = TestProjectFactory.project(USER);
		refSample = TestSampleFactory.sample(USER, refProject);
		refContainer = TestContainerFactory.container(USER, refProject, refSample);
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
				//data = api.create(refContainer, USER);
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
			api.delete(data.code);
		} catch (Exception e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		} finally {
			logger.debug("remove dep objects");
			sampleApi.delete(refSample.code);
			projectApi.delete(refProject.code);	
			clean = true;
		}
	}

	@Test
	public void createTest() {
		setUpData();
		logger.debug("Creation test");
		try {
			data = api.create(refContainer, USER);
		} catch (APIException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		Assert.assertEquals(refContainer.categoryCode, data.categoryCode);
		Assert.assertEquals(refContainer.code, data.code);
		Assert.assertEquals(refContainer.concentration, data.concentration);
		Assert.assertEquals(refContainer.volume, data.volume);
		Assert.assertEquals(refContainer.quantity, data.quantity);
		deleteData();
	}
}
