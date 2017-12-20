package fr.cea.ig.authorization.authorizators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import fr.cea.ig.authorization.IAuthorizator;
import fr.cea.ig.play.IGConfig;

public class FixedAuthorizator implements IAuthorizator {

	private static final play.Logger.ALogger logger = play.Logger.of(FixedAuthorizator.class);
	
	public static final String AUTHZ_FIXED_KEY = "authorization.fixed";
	
	// Read "authorization.fixed" from config
	private final Set<String> authorizations;
	
	@Inject
	public FixedAuthorizator(IGConfig config) {
		List<String> perms = config.getStringList(AUTHZ_FIXED_KEY);
		authorizations = new HashSet<>();
		if (perms == null) {
			logger.error("no/bad premissions configured at {}",AUTHZ_FIXED_KEY);
		} else {
			for (String perm : perms) {
				logger.debug("adding perm '{}'",perm);
				authorizations.add(perm);
			}
		}
	}
	
	@Override
	public boolean authorize(String login, String[] perms) {
		boolean authorized = true; 
		for (String perm : perms)
			authorized = authorized && authorizations.contains(perm);
		return authorized;
	}

}
