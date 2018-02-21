package fr.cea.ig.ngl.test.dao.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import controllers.projects.api.ProjectsSearchForm;
import fr.cea.ig.ngl.dao.api.APIException;
import fr.cea.ig.ngl.dao.projects.ProjectAPI;
import models.laboratory.project.instance.Project;
import models.utils.dao.DAOException;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import utils.AbstractTests;

@Singleton
public class ProjectAPITest extends AbstractTests {
	
	private static final play.Logger.ALogger logger = play.Logger.of(ProjectAPITest.class);
	
	private ProjectAPI api;
	private FormFactory formFactory;
	
	private static final String currentUser = "ngsrg";
	
	@Before
	public void setUp() {
		assertTrue(app.isDev());
		api = app.injector().instanceOf(ProjectAPI.class);
		assertNotNull(api);
		formFactory = app.injector().instanceOf(FormFactory.class);
	}
	
	//TODO procedure to create a project
	@Test
	public void creationTest() {
		Form<Project> form = formFactory.form(Project.class);
		Project proj = new Project();
		proj.setCode("TEST");
		logger.info("test project: " + Json.toJson(proj));
		form = form.fill(proj);
		logger.info(form.get().toString());
		try {
			Project project = this.api.create(form, currentUser);
			assertNotNull(project);
		} catch (APIException e) {
			logger.error(e.getMessage());
			fail(e.getMessage());
		} 
	}
	
	@Test
	public void getProjectTest() {
		try {
			Project proj = this.api.get("A");
			assertNotNull(proj);
			assertEquals("A", proj.code);
		} catch (APIException e) {
			logger.error(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	@Test
	public void listProjectTest() {
		fail("Not implemented");
	}
	
	@Test
	public void updateTest() {
		fail("Not implemented");
	}
}
