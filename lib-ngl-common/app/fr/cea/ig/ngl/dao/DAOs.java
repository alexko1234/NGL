package fr.cea.ig.ngl.dao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DAOs {

	private final PermissionDAO permissionDAO;
	private final ProjectDAO    projectDAO;
	private final UserDAO       userDAO;
	
	@Inject
	public DAOs(PermissionDAO permissionDAO,
			    ProjectDAO    projectDAO,
			    UserDAO       userDAO) {
		this.permissionDAO = permissionDAO;
		this.projectDAO    = projectDAO;
		this.userDAO       = userDAO;
	}
	
	public ProjectDAO project() { return projectDAO; }
	public UserDAO user() { return userDAO; }
	public PermissionDAO permission() { return permissionDAO; }
	
}
