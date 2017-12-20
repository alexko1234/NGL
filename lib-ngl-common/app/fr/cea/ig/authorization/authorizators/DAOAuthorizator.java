package fr.cea.ig.authorization.authorizators;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;

import controllers.authorisation.PermissionHelper;
import fr.cea.ig.authorization.IAuthorizator;
import fr.cea.ig.play.IGConfig;
import models.administration.authorisation.User;
import models.administration.authorisation.description.dao.UserDAO;
import play.Logger;
import play.api.modules.spring.Spring;
// import play.Play;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import static play.mvc.Results.unauthorized;

public class DAOAuthorizator implements IAuthorizator {

	private static final play.Logger.ALogger logger = play.Logger.of(DAOAuthorizator.class);
	
	public static final String APPLICATION_NAME_KEY = "auth.application"; //, "authorization.dao.application" };
	public static final String USER_CREATION_KEY    = "authorization.dao.userCreation";
	public static final String USER_ROLE_KEY        = "authorization.dao.roleAtCreation";
	
	// More a relaxed access policy than user creation
	private final boolean userCreation;
	private final String roleAtCreation;
	private final String applicationName;
	private final UserDAO userDAO;
	
	@Inject
	public DAOAuthorizator(IGConfig config) {
		// userDAO         = Spring.getBeanOfType(UserDAO.class);
		userDAO = (UserDAO)new User.UserFinder().getInstance();
		userCreation    = config.getBoolean(USER_CREATION_KEY, false);
		roleAtCreation  = config.getString(USER_ROLE_KEY,null);
		applicationName = config.getString(APPLICATION_NAME_KEY,null); 
	}
	
	@Override
	public boolean authorize(String login, String[] perms) {
		if (applicationName == null) {
			logger.error("application name is not defined");
			return false;
		}
		if (isBlank(login)) {
			Logger.warn("blank login {}",login);
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
		boolean authorized = true;
		for (String perm : perms) {
			if (perm == null)
				throw new RuntimeException("badly configured null permission");
			authorized = authorized && hasPermission(login,perm); 
		}
		return authorized;
	}

	public boolean isDeclaredUser(String login) {
		return userDAO.isDeclaredUser(login);
	}
	
	public boolean isActiveUser(String login) {
		return userDAO.isUserActive(login);		
	}
	
	public boolean canAccessApplication(String login, String app) {
		return userDAO.canAccessApplication(login, app);
	}
	
	public boolean hasPermission(String login, String permission) {
		return PermissionHelper.checkPermission(login, permission);
	}
	
	public void declareUser(String login, String role) {
		userDAO.declareUser(login, role);
	}
	
	public void grantApplicationAccess(String login, String application) {
		userDAO.grantApplicationAccess(login, application);
	}
	
}
