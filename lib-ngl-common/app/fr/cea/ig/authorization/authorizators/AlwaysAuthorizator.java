package fr.cea.ig.authorization.authorizators;

import fr.cea.ig.authorization.IAuthorizator;

/**
 * Always authorize.
 * 
 * @author vrd
 *
 */
public class AlwaysAuthorizator implements IAuthorizator {
	
	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(AlwaysAuthorizator.class);

	// Implicit no arg DI constructor
	
	/**
	 * Always authorized.
	 */
	@Override
	public boolean authorize(String login, String[] perms) {
		logger.debug("authorizing");
		return true;
	}

}
