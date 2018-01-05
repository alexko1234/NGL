package fr.cea.ig.authorization;

/**
 * Simple authorization check that validates that a given login 
 * has a set of permissions.
 *  
 * @author vrd
 *
 */
public interface IAuthorizator {

	/**
	 * Has the login enough permissions to be authorized ?
	 * @param  login login to check
	 * @param  perms required set of permissions
	 * @return true if the login has enough permissions
	 */
	public boolean authorize(String login, String[] perms);
	
}
