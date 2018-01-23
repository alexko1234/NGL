package fr.cea.ig.ngl.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

@Singleton
public class ProjectDAO {

	// Should have some injected generic db acccess
	
	// Not needed, placeholder
	@Inject
	public ProjectDAO() {
	}
	
	public Iterable<Project> all() throws DAOException {
		return MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class).cursor;
	}
	
	
}
