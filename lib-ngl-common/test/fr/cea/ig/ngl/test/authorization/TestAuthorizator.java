package fr.cea.ig.ngl.test.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.cea.ig.authorization.IAuthorizator;
import fr.cea.ig.authorization.Permission;
import fr.cea.ig.ngl.test.authentication.Identity;

/**
 * Fixed authorization set that matches the identities as defined in 
 * {@link fr.cea.ig.ngl.test.authentication.Identity}.
 *  
 * @author vrd
 *
 */
public class TestAuthorizator implements IAuthorizator {

	private static final play.Logger.ALogger logger = play.Logger.of(TestAuthorizator.class);
	
	private static final Map<Identity,Set<String>> rights;
	
	private static Set<String> set(Permission... rs) {
		HashSet<String> s = new HashSet<>();
		for (Permission r : rs)
			s.add(r.getAlias());
		return s;
	}
	
	static {
		rights = new HashMap<>();
		rights.put(Identity.Nobody,    set());
		rights.put(Identity.Read,      set(Permission.Read));
		rights.put(Identity.Write,     set(Permission.Write));
		rights.put(Identity.ReadWrite, set(Permission.Read,Permission.Write));
		rights.put(Identity.Admin,     set(Permission.Read,Permission.Write,Permission.Admin));
	}
	
	@Override
	public boolean authorize(String login, String[] perms) {
		if (perms.length == 0)
			throw new RuntimeException("invalid permission request with no permission");
		Identity id = Identity.valueOf(login);
		if (id == null)
			throw new RuntimeException("invalid login '" + login + "'");
		Set<String> idRights = rights.get(id);
		if (idRights == null)
			throw new RuntimeException("no rights defined for " + id);
		for (String p : perms) {
			logger.debug("checking auth {} for {}", p, id );
			if (!idRights.contains(p))
				return false;
		}
		return true;
	}

}
