package fr.cea.ig.ngl.test.dao.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.inject.Singleton;
import javax.validation.constraints.AssertTrue;

import org.drools.compiler.lang.dsl.DSLMapParser.statement_return;
import org.drools.core.command.assertion.AssertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import com.google.inject.matcher.Matchers;

import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.projects.ProjectsAPI;
import fr.cea.ig.ngl.dao.samples.SamplesAPI;
import fr.cea.ig.ngl.test.AbstractAPITests;
import fr.cea.ig.ngl.test.TestAppWithDroolsFactory;
import fr.cea.ig.ngl.test.dao.api.factory.TestProjectFactory;
import fr.cea.ig.ngl.test.dao.api.factory.TestSampleFactory;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.DescriptionHelper;
import models.utils.dao.DAOException;
import play.Logger.ALogger;
import rules.services.RulesServices6;
import rules.services.test.TestRules6Component;
import utils.AbstractTests;

/**
 * @author ajosso
 *
 */
@Singleton  
public class SamplesAPITest extends AbstractTests implements AbstractAPITests {

	private static final play.Logger.ALogger logger = play.Logger.of(SamplesAPITest.class);

	//Tested API
	private static SamplesAPI api;
	
	// required APIs
	private static ProjectsAPI projectApi;

	private static final String USER = "ngsrg";
	
	// Reference objects
	private static Sample refSample;
	private static Project refProject;
	
	private static final TestAppWithDroolsFactory TEST_APP_FACTORY = new TestAppWithDroolsFactory("ngl-sq.test.conf");
	
	private Sample data;
	
	@Rule
    public ExpectedException exceptions = ExpectedException.none();

	private static boolean clean = true;

	/**
	 * Override default method
	 * Initialize test application.
	 */
	@BeforeClass
	public static void startTestApplication() {
		logger.info("Start an app (Test Mode) using NGL-SQ TU config");
		app = TEST_APP_FACTORY.bindRulesComponent().createApplication();
		DescriptionHelper.initInstitute();
		logger.info("test app started");
	}
	
	@BeforeClass
	public static void setUpClass() {
		assertTrue(app.isDev());
		projectApi = app.injector().instanceOf(ProjectsAPI.class);
		assertNotNull(projectApi);
		api = app.injector().instanceOf(SamplesAPI.class);
		Assert.assertNotNull(api);
		app.injector().instanceOf(TestRules6Component.class);
		
		logger.debug("define ref objects");
		refProject = TestProjectFactory.project(USER);
		refSample = TestSampleFactory.sample(USER, refProject);
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
				data = api.create(refSample, USER);
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
			projectApi.delete(refProject.code);	
			clean = true;
		}
		
	}

	@Test
	public void createTest() {
		logger.debug("Creation test");
		setUpData();
		Assert.assertEquals(refSample.categoryCode, data.categoryCode);
		Assert.assertEquals(refSample.code, data.code);
		Assert.assertEquals(refSample.importTypeCode, data.importTypeCode);
		Assert.assertEquals(refSample.name, data. name);
		Assert.assertEquals(refSample.typeCode, data.typeCode);
		deleteData();
	}

	@Test
	public void deleteTest() throws APIException {
		logger.debug("Delete test");
		setUpData();

		//Assert the exception thrown confirms the deletion of sample
		exceptions.expect(DAOException.class);
		exceptions.expectMessage("no instance found");

		api.delete(refSample.code);
		api.get(refSample.getCode()); // if sample not exists then an APIException is thrown

		// We can't delete dep objects here (due to Exception) so it will delete into tearDown() (@After method)
	}

	@Test
	public void getTest() {
		logger.debug("Get test");
		setUpData();
		try {
			Sample sample = api.get(refSample.code);
			Assert.assertNotNull(sample);
			Assert.assertEquals(data.get_id(), sample.get_id());
			Assert.assertEquals(refSample.getCode(), sample.getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}

	@Test
	public void listTest() {
		logger.debug("List test");
		setUpData();
		try {
			Query query = DBQuery.is("code", refSample.code);
			List<Sample> samples = api.list(query, "code", Sort.valueOf(0));
			Assert.assertEquals(1, samples.size());
			Assert.assertEquals(refSample.code, samples.get(0).code);
			Assert.assertEquals(refSample.categoryCode, samples.get(0).categoryCode);
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}
	
	@Test
	public void isObjectExistsTest() {
		setUpData();
		Assert.assertTrue(api.isObjectExist(refSample.code));
		deleteData();
	}
	
	@Test
	public void isObjectNotExistsTest() {
		Assert.assertFalse(api.isObjectExist(refSample.code));
	}

	@Test
	public void updateTest() {
		logger.debug("Update test");
		setUpData();
		try {
			Sample sampleToUpdate = data;
			String newName = "New name after update";
			sampleToUpdate.name = newName;
			sampleToUpdate.traceInformation.modifyUser = USER;
			sampleToUpdate.traceInformation.modifyDate = new Date();
			api.update(sampleToUpdate, USER);
			Sample sample = api.get(refSample.code);
			Assert.assertEquals(refSample.categoryCode, sample.categoryCode);
			Assert.assertNotEquals(refSample.name, sample.name);
			Assert.assertEquals(newName, sample.name);
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

	@Override
	public ALogger logger() {
		return logger;
	}

}
