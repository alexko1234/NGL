package fr.cea.ig.ngl.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;

@Singleton
public class ProjectDAO {

	// Should have some injected generic db acccess
	
	@Inject
	public ProjectDAO() {
	}
	
	public Iterable<Project> all() {
		return MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class).cursor;
	}
	
	
}
