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
import fr.cea.ig.ngl.dao.projects.ProjectsAPI;
import fr.cea.ig.ngl.test.dao.api.factory.TestProjectFactory;
import models.laboratory.project.instance.Project;
import play.data.validation.ValidationError;
import utils.AbstractTests;

@Singleton  
public class ProjectsAPITest extends AbstractTests {

	private static final play.Logger.ALogger logger = play.Logger.of(ProjectsAPITest.class);

	private static ProjectsAPI api;

	private static final String USER = "ngsrg";

	private static Project refProject;

	private Project createdProject;


	@BeforeClass
	public static void setUpClass() {
		assertTrue(app.isDev());
		api = app.injector().instanceOf(ProjectsAPI.class);
		assertNotNull(api);
		refProject = TestProjectFactory.project(USER);
	}


	/**
	 * Create required Data for test
	 */
	public void setUpData() {
		try {
			createdProject = api.create(refProject, USER);
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
			createdProject = api.create(refProject, USER);
			assertNotNull(createdProject);
			logger.debug("Project ID: " + createdProject._id);
			assertEquals(refProject.code, createdProject.code);
			assertEquals(refProject.name, createdProject.name);
			assertEquals(refProject.typeCode, createdProject.typeCode);
			assertEquals(refProject.categoryCode, createdProject.categoryCode);
			assertEquals(refProject.description, createdProject.description);
			assertEquals(refProject.umbrellaProjectCode, createdProject.umbrellaProjectCode);
			assertEquals(refProject.lastSampleCode, createdProject.lastSampleCode);
			assertEquals(refProject.nbCharactersInSampleCode, createdProject.nbCharactersInSampleCode);
			assertEquals(refProject.archive, createdProject.archive);
			assertEquals(refProject.state.code, createdProject.state.code);
			assertEquals(refProject.state.user, createdProject.state.user);
			assertEquals(refProject.authorizedUsers, createdProject.authorizedUsers);
			assertEquals(refProject.comments.size(), createdProject.comments.size());

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
			api.delete(refProject.code);
			Project proj = api.get(refProject.getCode());
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
			Project proj = api.get(refProject.code);
			assertNotNull(proj);
			assertEquals(createdProject.get_id(), proj.get_id());
			assertEquals(refProject.getCode(), proj.getCode());
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
			Query query = DBQuery.is("code", refProject.code);
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
			Project updatedProj = TestProjectFactory.projectArchived(USER);
			updatedProj._id = createdProject._id;
			updatedProj.traceInformation.modifyUser = USER;
			updatedProj.traceInformation.modifyDate = new Date();

			api.update(refProject.code, updatedProj, USER);
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
