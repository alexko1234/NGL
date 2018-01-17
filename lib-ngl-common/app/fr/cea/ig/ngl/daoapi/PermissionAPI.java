package fr.cea.ig.ngl.daoapi;

import java.util.List;

import fr.cea.ig.ngl.dao.PermissionDAO;
import models.administration.authorisation.Permission;
import models.utils.dao.DAOException;

public class PermissionAPI {

	private final PermissionDAO dao;
	
	public PermissionAPI(PermissionDAO dao) {
		this.dao = dao;
	}
	
	public List<Permission> byUserLogin(String login) throws DAOException, APIException {
		return dao.byUserLogin(login);
	}

}
