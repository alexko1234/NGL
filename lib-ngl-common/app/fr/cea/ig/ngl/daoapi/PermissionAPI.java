package fr.cea.ig.ngl.daoapi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.ngl.dao.PermissionDAO;
import models.administration.authorisation.Permission;
import models.utils.dao.DAOException;

@Singleton
public class PermissionAPI {
	
	private final PermissionDAO dao;
	
	@Inject
	public PermissionAPI(PermissionDAO dao) {
		this.dao = dao;
	}
	
	public List<Permission> byUserLogin(String login) throws DAOException, APIException {
		return dao.byUserLogin(login);
	}

}
