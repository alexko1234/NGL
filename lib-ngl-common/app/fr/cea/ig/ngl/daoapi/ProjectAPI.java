package fr.cea.ig.ngl.daoapi;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.ngl.dao.ProjectDAO;
import models.laboratory.project.instance.Project;
import models.utils.dao.DAOException;

@Singleton
public class ProjectAPI {

	private final ProjectDAO dao; 
	
	@Inject
	public ProjectAPI(ProjectDAO dao) {
		this.dao = dao;
	}
	
	public Iterable<Project> all() throws DAOException, APIException {
		return dao.all();
	}

	
	
}
