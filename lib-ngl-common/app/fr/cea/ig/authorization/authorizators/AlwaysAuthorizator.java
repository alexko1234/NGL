package fr.cea.ig.authorization.authorizators;

import javax.inject.Inject;

import fr.cea.ig.authorization.IAuthorizator;

public class AlwaysAuthorizator implements IAuthorizator {
	
	private static final play.Logger.ALogger logger = play.Logger.of(AlwaysAuthorizator.class);

	// Not needed but this is clearer
	@Inject
	public AlwaysAuthorizator() {
	}
	
	@Override
	public boolean authorize(String login, String[] perms) {
		logger.debug("authorizing");
		return true;
	}

}
