package fr.cea.ig.ngl.test.dao.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Singleton;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.projects.ProjectAPI;
import fr.cea.ig.ngl.test.dao.api.factory.TestProjectFactory;
import models.laboratory.project.instance.Project;
import play.data.validation.ValidationError;
import utils.AbstractTests;

@Singleton
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // NB: Optional because creation and deletion are done by @BeforeClass and @AfterClass methods  
public class ProjectAPITest extends AbstractTests {
	
	private static final play.Logger.ALogger logger = play.Logger.of(ProjectAPITest.class);
	
	private static ProjectAPI api;
	private static TestProjectFactory factory;
	
	private static final String currentUser = "ngsrg";
	
	private static Project createdProject;
		
	
	@BeforeClass
	public static void setUpClass() {
		assertTrue(app.isDev());
		api = app.injector().instanceOf(ProjectAPI.class);
		assertNotNull(api);
		factory = app.injector().instanceOf(TestProjectFactory.class);
		assertNotNull(factory);
		
		// Create Data and check creation process
		try {
			createdProject = api.create(factory.project, currentUser, new TreeMap<String, List<ValidationError>>());
			assertNotNull(createdProject);
			logger.debug("Project ID: " + createdProject._id);
			assertEquals(factory.project.code, createdProject.code);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logger.error("invalid fields: " + e.getErrors().keySet().toString());
			logValidationErrors(e);
			fail(e.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		assertNotNull(api);
		api.delete(factory.project.code);
		try {
			Project proj = api.get(factory.project.getCode());
			assertNull(proj);
		} catch (APIException e) {
			logger.error(e.getMessage());
			fail(e.getMessage());
		}
	}

	@Test
	public void getProjectTest() {
		try {
			Project proj = api.get(factory.project.code);
			assertNotNull(proj);
			assertEquals(createdProject.get_id(), proj.get_id());
			assertEquals(factory.project.getCode(), proj.getCode());
		} catch (APIException e) {
			logger.error(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	@Test
	public void listProjectTest() {
		Query query = DBQuery.is("code", factory.project.code);
		List<Project> projs = api.list(query, "code", Sort.valueOf(0));
		assertEquals(1, projs.size());
	}

	@Test
	public void updateTest() {
		try {
			Project updatedProj = factory.projectArchived;
			updatedProj._id = createdProject._id;
			updatedProj.traceInformation.modifyUser = currentUser;
			updatedProj.traceInformation.modifyDate = new Date();
			
			api.update(factory.project.code, updatedProj, currentUser, new TreeMap<String, List<ValidationError>>());
			Project proj = api.get(factory.projectArchived.code);
			assertNotEquals(factory.project.archive, proj.archive);
			assertEquals(factory.projectArchived.archive, proj.archive);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logValidationErrors(e);
			fail(e.getMessage());
		} catch (APIException e) {
			logger.error(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	/**
	 * Log Validation Errors only if logger level is DEBUG or less
	 * @param e
	 */
	private static void logValidationErrors(APIValidationException e) {
		if(logger.isDebugEnabled()) {
			e.getErrors().keySet().forEach(key -> {
				e.getErrors().get(key).forEach(err -> {
					logger.error(key + " - "+ err.message());						
				});

			});
		}
	}
}
