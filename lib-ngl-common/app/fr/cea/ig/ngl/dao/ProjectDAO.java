package fr.cea.ig.ngl.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

@Singleton
public class ProjectDAO {

	private final GenericMongoDAO<Project> gdao;
	
	// Not needed, placeholder
	@Inject
	public ProjectDAO() {
		gdao = new GenericMongoDAO<>(InstanceConstants.PROJECT_COLL_NAME, Project.class);
	}
	
	public Iterable<Project> all() throws DAOException {
		return gdao.all();
	}
	
	
}
