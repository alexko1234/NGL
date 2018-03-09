package fr.cea.ig.ngl.dao.permissions;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import controllers.authorisation.PermissionHelper;
import fr.cea.ig.authorization.authorizators.UserDAOAuthorizator;
import fr.cea.ig.ngl.dao.api.APIException;
import models.administration.authorisation.Permission;
import models.administration.authorisation.User;
import models.administration.authorisation.description.dao.UserDAO;
import models.utils.dao.DAOException;
import play.api.modules.spring.Spring;
import play.api.modules.spring.SpringPlugin;

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

	
	 /**
     * Does the user exist ?
     * @param login user login
     * @return      true is the user exists
     */
    public boolean isDeclaredUser(String login) {
        return getUserDAO().isDeclaredUser(login);
    }

    private UserDAO getUserDAO() {
        return (UserDAO) Spring.getBeanOfType(UserDAO.class);
    }

    /**
     * Is the user active ?
     * @param login user login
     * @return      true if the user is active 
     */
    public boolean isActiveUser(String login) {
        return getUserDAO().isUserActive(login);		
    }

    /**
     * Can the user access the given application by name .
     * @param login user login
     * @param app   application key
     * @return      tue if the user can access the application
     */
    public boolean canAccessApplication(String login, String app) {
        return getUserDAO().canAccessApplication(login, app);        
    }

    /**
     * Does the user have a given permission ?
     * @param login      user login
     * @param permission permission to check
     * @return           true if the user has the requested permission
     */
    public boolean hasPermission(String login, String permission) {
        return PermissionHelper.checkPermission(login, permission);
    }

    /**
     * Declare user in database.
     * @param login user login
     * @param role  user role
     */
    public void declareUser(String login, String role) {
        synchronized (UserDAOAuthorizator.class) {
            if (!isDeclaredUser(login))
                getUserDAO().declareUser(login, role);
        }
    }

    /**
     * Grant application access to a user.
     * @param login       user login
     * @param application application key
     */
    public void grantApplicationAccess(String login, String application) {
        getUserDAO().grantApplicationAccess(login, application);
    }

}
