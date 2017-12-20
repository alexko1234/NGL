package fr.cea.ig.authorization.authorizators;

import javax.inject.Inject;

import fr.cea.ig.authorization.IAuthorizator;

public class NeverAuthorizator implements IAuthorizator {

	// Not needed but clearer
	@Inject
	public NeverAuthorizator() {
	}
	
	@Override
	public boolean authorize(String login, String[] perms) {
		return false;
	}

}
