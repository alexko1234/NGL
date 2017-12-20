package fr.cea.ig.authentication.validators;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.security.crypto.bcrypt.BCrypt;

import fr.cea.ig.authentication.ILoginPasswordValidator;
import models.administration.authorisation.User;
import models.administration.authorisation.description.dao.AuthenticateDAO;
import models.administration.authorisation.description.dao.UserDAO;
import play.api.modules.spring.Spring;

// TODO: fix name, it is backed by the UserDAO but still
public class DAOLoginPasswordValidator implements ILoginPasswordValidator {

	private static final play.Logger.ALogger logger = play.Logger.of(DAOLoginPasswordValidator.class);
	
	@Override
	public void validate(String login, String password) throws ValidationFailedException {
		if (isBlank(login))
			throw new ValidationFailedException("empty login name '" + login + "'");
		if (isBlank(password))
			throw new ValidationFailedException("empty password");
		User.find.findByLogin(login);
		// Cannot get the instance through Spring
		// AuthenticateDAO auth = Spring.getBeanOfType(AuthenticateDAO.class);
		UserDAO auth = (UserDAO)new User.UserFinder().getInstance();
		if (!auth.isUserActive(login)) {
			logger.debug("inactive user {}, authentication failed",login);
			throw new ValidationFailedException("inactive user '" + login + "'"); 
		}
		String passwordInDB = auth.getUserPassword(login);
		if (isBlank(passwordInDB))
			throw new ValidationFailedException("no password or blank in db for user '" + login + "'");
		System.out.println(" *** " + password + " *** " + passwordInDB + " ***");
		if (!BCrypt.checkpw(password,passwordInDB))
			throw new ValidationFailedException("bad password");
	}

}
