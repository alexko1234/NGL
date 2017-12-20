package fr.cea.ig.authorization.authorizators;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import fr.cea.ig.authorization.IAuthorizator;
import fr.cea.ig.play.IGConfig;
import play.api.inject.Injector;

public class ConfiguredAuthorizator implements IAuthorizator {

	private static final play.Logger.ALogger logger = play.Logger.of(ConfiguredAuthorizator.class);
	
	public static final String AUTHZ_MODE_KEY = "authorization.mode";
	public static final String AUTHZ_CLASS_KEY = "authorization.class";
	public static final Map<String,Class<?>> modeMap;
	
	private final IAuthorizator delegate;
	
	static {
		modeMap = new HashMap<>();
		modeMap.put("always", AlwaysAuthorizator.class);
		modeMap.put("never",  NeverAuthorizator.class);
		modeMap.put("fixed",  FixedAuthorizator.class);
		modeMap.put("dao",    DAOAuthorizator.class);
	}
	
	@Inject
	public ConfiguredAuthorizator(IGConfig config, Injector injector) {
		Class<?> delegateClass = null;
		String mode = config.getString(AUTHZ_MODE_KEY, null);
		if (mode != null) {
			delegateClass = modeMap.get(mode);
			if (delegateClass == null)
				logger.error("unkown mode {}",mode);
		}
		String delegateClassName = config.getString(AUTHZ_CLASS_KEY, null);
		if (delegateClassName != null) {
			if (delegateClass != null)
				logger.warn("definition of {} and {}, will keep {}",AUTHZ_MODE_KEY,AUTHZ_CLASS_KEY,AUTHZ_CLASS_KEY);
			try {
				delegateClass = Class.forName(delegateClassName);
			} catch (ClassNotFoundException e) {
				
			}
		}
		if (delegateClass == null) {
			logger.error("no authorization mode configured at {} or {}",AUTHZ_MODE_KEY,AUTHZ_CLASS_KEY);
			delegate = new NeverAuthorizator(); 
		} else {
			Object delegateAuthorizator = injector.instanceOf(delegateClass);
			if (delegateAuthorizator instanceof IAuthorizator) {
				delegate = (IAuthorizator)delegateAuthorizator;
			} else { 
				logger.error("{} does not implement {}",delegateClass.getName(),IAuthorizator.class.getName());
				delegate = new NeverAuthorizator(); 
			}
		}
	}

	@Override
	public boolean authorize(String login, String[] perms) {
		return delegate.authorize(login, perms);
	}

}
