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

import org.junit.BeforeClass;
import org.junit.Test;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import fr.cea.ig.MongoDBResult.Sort;
import fr.cea.ig.ngl.dao.api.APIValidationException;
import fr.cea.ig.ngl.dao.projects.ProjectAPI;
import fr.cea.ig.ngl.test.dao.api.factory.TestProjectFactory;
import models.laboratory.project.instance.Project;
import play.data.validation.ValidationError;
import utils.AbstractTests;

@Singleton  
public class ProjectAPITest extends AbstractTests {

	private static final play.Logger.ALogger logger = play.Logger.of(ProjectAPITest.class);

	private static ProjectAPI api;

	private static final String currentUser = "ngsrg";

	private static final Project PROJECT_REF = TestProjectFactory.project(currentUser);

	private Project createdProject;


	@BeforeClass
	public static void setUpClass() {
		assertTrue(app.isDev());
		api = app.injector().instanceOf(ProjectAPI.class);
		assertNotNull(api);
	}


	/**
	 * Create required Data for test
	 */
	public void setUpData() {
		try {
			createdProject = api.create(PROJECT_REF, currentUser, new TreeMap<String, List<ValidationError>>());
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

	/**
	 * Delete Data used in test
	 */
	public void deleteData() {
		api.delete(createdProject.code);
	}

	@Test
	public void createTest() {
		try {
			createdProject = api.create(PROJECT_REF, currentUser, new TreeMap<String, List<ValidationError>>());
			assertNotNull(createdProject);
			logger.debug("Project ID: " + createdProject._id);
			assertEquals(PROJECT_REF.code, createdProject.code);
			assertEquals(PROJECT_REF.name, createdProject.name);
			assertEquals(PROJECT_REF.typeCode, createdProject.typeCode);
			assertEquals(PROJECT_REF.categoryCode, createdProject.categoryCode);
			assertEquals(PROJECT_REF.description, createdProject.description);
			assertEquals(PROJECT_REF.umbrellaProjectCode, createdProject.umbrellaProjectCode);
			assertEquals(PROJECT_REF.lastSampleCode, createdProject.lastSampleCode);
			assertEquals(PROJECT_REF.nbCharactersInSampleCode, createdProject.nbCharactersInSampleCode);
			assertEquals(PROJECT_REF.archive, createdProject.archive);
			assertEquals(PROJECT_REF.state.code, createdProject.state.code);
			assertEquals(PROJECT_REF.state.user, createdProject.state.user);
			assertEquals(PROJECT_REF.authorizedUsers, createdProject.authorizedUsers);
			assertEquals(PROJECT_REF.comments.size(), createdProject.comments.size());

		} catch (APIValidationException e) {
			logger.error(e.getMessage());
			logger.error("invalid fields: " + e.getErrors().keySet().toString());
			logValidationErrors(e);
			exit(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}

	@Test
	public void deleteTest() {
		setUpData();
		try {
			api.delete(PROJECT_REF.code);
			Project proj = api.get(PROJECT_REF.getCode());
			assertNull(proj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
	}

	@Test
	public void getProjectTest() {
		setUpData();
		try {
			Project proj = api.get(PROJECT_REF.code);
			assertNotNull(proj);
			assertEquals(createdProject.get_id(), proj.get_id());
			assertEquals(PROJECT_REF.getCode(), proj.getCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}

	@Test
	public void listProjectTest() {
		setUpData();
		try {
			Query query = DBQuery.is("code", PROJECT_REF.code);
			List<Project> projs = api.list(query, "code", Sort.valueOf(0));
			assertEquals(1, projs.size());
		} catch (Exception e) {
			logger.error(e.getMessage());
			exit(e.getMessage());
		}
		deleteData();
	}

	@Test
	public void updateTest() {
		setUpData();
		try {
			Project updatedProj = TestProjectFactory.projectArchived(currentUser);
			updatedProj._id = createdProject._id;
			updatedProj.traceInformation.modifyUser = currentUser;
			updatedProj.traceInformation.modifyDate = new Date();

			api.update(PROJECT_REF.code, updatedProj, currentUser, new TreeMap<String, List<ValidationError>>());
			Project proj = api.get(TestProjectFactory.projectArchived(currentUser).code);
			assertNotEquals(PROJECT_REF.archive, proj.archive);
			assertEquals(TestProjectFactory.projectArchived(currentUser).archive, proj.archive);
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


	/**
	 * 
	 * @param message
	 */
	private void exit(String message) {
		deleteData();
		fail(message);
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
