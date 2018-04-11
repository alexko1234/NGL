package fr.cea.ig.ngl.test.dao.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.projects.ProjectsAPI;
import fr.cea.ig.ngl.test.AbstractAPITests;
import fr.cea.ig.ngl.test.dao.api.factory.TestProjectFactory;
import models.laboratory.project.instance.Project;
import play.Logger.ALogger;
import utils.AbstractTests;

/**
 * Test {@link ProjectsAPI} methods
 * 
 * @author ajosso
 *
 */
@Singleton  
public class ProjectsAPITest extends AbstractTests implements AbstractAPITests {

	private static final play.Logger.ALogger logger = play.Logger.of(ProjectsAPITest.class);

	private static ProjectsAPI api;

	private static boolean clean = true;

	private static final String USER = "ngsrg";

	private static Project refProject;

	private Project data;


	@BeforeClass
	public static void setUpClass() {
		assertTrue(app.isDev());
		api = app.injector().instanceOf(ProjectsAPI.class);
		assertNotNull(api);
		refProject = TestProjectFactory.project(USER);
	}

	@Override
	@Before
	public void setUpData() {
		try {
			data = api.create(refProject, USER);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logger.error("invalid fields: " + e.getErrors().keySet().toString());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}

	@Override
	@After
	public void deleteData() {
		try {
			api.delete(data.code);
		} catch (Exception e) {
			logger.error(e.getMessage());
			if(logger.isDebugEnabled()) {
				e.printStackTrace();
			}
		} finally {
			clean = true;
		}

	}

	@Test
	public void createTest() {
		assertNotNull(data);
		logger.debug("Project ID: " + data._id);
		assertEquals(refProject.code, data.code);
		assertEquals(refProject.name, data.name);
		assertEquals(refProject.typeCode, data.typeCode);
		assertEquals(refProject.categoryCode, data.categoryCode);
		assertEquals(refProject.description, data.description);
		assertEquals(refProject.umbrellaProjectCode, data.umbrellaProjectCode);
		assertEquals(refProject.lastSampleCode, data.lastSampleCode);
		assertEquals(refProject.nbCharactersInSampleCode, data.nbCharactersInSampleCode);
		assertEquals(refProject.archive, data.archive);
		assertEquals(refProject.state.code, data.state.code);
		assertEquals(refProject.state.user, data.state.user);
		assertEquals(refProject.authorizedUsers, data.authorizedUsers);
		assertEquals(refProject.comments.size(), data.comments.size());
	}

	@Test
	public void deleteTest() {
		try {
			api.delete(refProject.code);
			Project proj = api.get(refProject.getCode());
			assertNull(proj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}

	@Test
	public void getTest() {
		try {
			Project proj = api.get(refProject.code);
			assertNotNull(proj);
			assertEquals(data.get_id(), proj.get_id());
			assertEquals(refProject.getCode(), proj.getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}

	@Test
	public void listTest() {
		try {
			Query query = DBQuery.is("code", refProject.code);
			List<Project> projs = api.list(query, "code", Sort.valueOf(0));
			assertEquals(1, projs.size());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}

	@Test
	public void updateTest() {
		try {
			Project updatedProj = TestProjectFactory.projectArchived(USER);
			updatedProj._id = data._id;
			updatedProj.traceInformation.modifyUser = USER;
			updatedProj.traceInformation.modifyDate = new Date();

			api.update(updatedProj, USER);
			Project proj = api.get(TestProjectFactory.projectArchived(USER).code);
			assertNotEquals(refProject.archive, proj.archive);
			assertEquals(TestProjectFactory.projectArchived(USER).archive, proj.archive);
		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}


	@Override
	public ALogger logger() {
		return logger;
	}
}
