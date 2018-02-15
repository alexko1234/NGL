package fr.cea.ig.authorization.authorizators;

//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionStage;
//import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.Provider;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashSet;
import java.util.Set;

import models.administration.authorisation.Permission;
import controllers.authorisation.PermissionHelper;
import fr.cea.ig.authorization.IAuthorizator;
import fr.cea.ig.ngl.daoapi.APIException;
import fr.cea.ig.ngl.daoapi.PermissionAPI;
import fr.cea.ig.play.IGConfig;
import models.administration.authorisation.User;
import models.administration.authorisation.description.dao.UserDAO;
// import play.Logger;
// import play.api.modules.spring.Spring;
// import play.Play;
//  import play.mvc.Action;
// import play.mvc.Http.Context;
// import play.mvc.Result;
// import static play.mvc.Results.unauthorized;
import models.utils.dao.DAOException;

// TODO: use a UserAPI object

/**
 * IAuhtorization implementation on top of UserDAO.
 * <p>
 * Application name that is checked against the database is set using:
 * <br>
 * <code>
 * authorization.user_dao.application = "ngl-sq"
 * </code>
 * <p>
 * Automatic user creation is specified using a boolean flag, it defaults
 * to false:
 * <br>
 * <code>
 * authorization.user_dao.user_creation
 * </code>
 * <p>
 * Created user role name is not needed if the user_creation is set to false and
 * is set using:
 * <br>
 * <code>
 * authorization.user_dao.role_at_creation = "reader"
 * </code>
 * 
 * @author vrd
 *
 */
@Singleton
public class UserDAOAuthorizator implements IAuthorizator {

    /**
     * Logger.
     */
    private static final play.Logger.ALogger logger = play.Logger.of(UserDAOAuthorizator.class);

    /**
     * Root configuration path.
     */
    public static final String CONF_ROOT_PATH = "authorization.user_dao";

    /**
     * Path in the configuration for the application name as defined in the UserDAO.
     */
    public static final String CONF_APPLICATION_NAME_PATH = CONF_ROOT_PATH + ".application";

    /**
     * Path in the configuration for the user creation flag.
     */
    public static final String CONF_USER_CREATION_PATH    = CONF_ROOT_PATH + ".user_creation";

    /**
     * Path in the configuration for the user role at creation.
     */
    public static final String CONF_USER_ROLE_PATH        = CONF_ROOT_PATH + ".role_at_creation";

    /**
     * Should the user be created if it does not exist ?
     */
    private final boolean userCreation;

    /**
     * Role the user is granted when automatically created. 
     */
    private final String roleAtCreation;

    /**
     * Application name in the UserDAO.
     */
    private final String applicationName;

    /**
     * User DAO.
     */
    //private final UserDAO userDAO;

    /**
     * Permission API.
     */
    private final Provider<PermissionAPI> permissionAPI;

    // At this point the system may not support application
    // checks. Implementation in the UserDOA is incorrect.
    private static final boolean enableApplicationCheck = false; 

    /**
     * DI constructor.
     * @param config configuration to use
     */
    @Inject
    public UserDAOAuthorizator(IGConfig config, Provider<PermissionAPI> permissionAPI) {
        logger.error("init UserDAOAuthorizator");
        // userDAO         = Spring.getBeanOfType(UserDAO.class);
        //userDAO      = ((UserDAO)new User.UserFinder().getInstance());
        userCreation = config.getBoolean(CONF_USER_CREATION_PATH, false);
        if (userCreation)
            roleAtCreation = config.getString(CONF_USER_ROLE_PATH,null);
        else
            roleAtCreation = null;
        applicationName = config.getString(CONF_APPLICATION_NAME_PATH,null);
        this.permissionAPI = permissionAPI;
    }

    /**
     * Authorize user based on the UserDAO information, may create 
     * the user if configured to do so.
     */
    @Override
    public boolean authorize(String login, String[] perms) {
        if (applicationName == null) {
            logger.error("application name is not defined");
            return false;
        }
        if (isBlank(login)) {
            logger.warn("blank login {}",login);
            return false;
        }
        if (!isDeclaredUser(login)) {
            if (userCreation) {
                declareUser(login,roleAtCreation);
            } else {
                return false;
            }
        }
        if (!isActiveUser(login))
            return false;
        if (!canAccessApplication(applicationName,login)) {
            if (userCreation) {
                grantApplicationAccess(login,applicationName);
            } else {
                return false;
            }
        }
        /*boolean authorized = true;
		for (String perm : perms) {
			if (perm == null)
				throw new RuntimeException("badly configured null permission");
			authorized = authorized && hasPermission(login,perm); 
		}
		return authorized;*/
        for (String perm : perms) {
            if (perm == null)
                throw new RuntimeException("badly configured null permission");
            if (!hasPermission(login,perm))
                return false; 
        }
        return true;
    }

    @Override
    public Set<String> getPermissions(String login) {
        Set<String> permissions = new HashSet<>();
        if (isBlank(login))                               return permissions;
        if (!isDeclaredUser(login))                       return permissions;
        if (!isActiveUser(login))                         return permissions;
        if (!canAccessApplication(applicationName,login)) return permissions;
        try {
            for (Permission p : permissionAPI.get().byUserLogin(login)) 
                permissions.add(p.code);
        } catch (DAOException | APIException e) {
            logger.error("permission access failed for " + login,e);
        } 
        return permissions;
    }

    // ---------------- UserDAO indirection -------------------------------

    /**
     * Does the user exist ?
     * @param login user login
     * @return      true is the user exists
     */
    public boolean isDeclaredUser(String login) {
        return getUserDAO().isDeclaredUser(login);
    }

    private UserDAO getUserDAO() {
        return (UserDAO)new User.UserFinder().getInstance();
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
        if (enableApplicationCheck)
            return getUserDAO().canAccessApplication(login, app);
        return true;
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
        if (enableApplicationCheck)
            getUserDAO().grantApplicationAccess(login, application);
    }

}
