package fr.cea.ig.ngl.support;

import fr.cea.ig.ngl.daoapi.APIException;
import models.utils.dao.DAOException;
import play.mvc.Result;

@FunctionalInterface
public interface APIExecution {

	Result run() throws DAOException, APIException, Exception;
	
}
