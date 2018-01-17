package fr.cea.ig.ngl.dao;

import java.util.List;

import models.administration.authorisation.Permission;
import models.utils.dao.DAOException;

public class PermissionDAO {

	public List<Permission> byUserLogin(String login) throws DAOException {
		return Permission.find.findByUserLogin(login);
	}
	
}
