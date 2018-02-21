package fr.cea.ig.ngl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.Provider;

import fr.cea.ig.authorization.IAuthorizator;
import fr.cea.ig.lfw.LFWApplication;
import fr.cea.ig.ngl.dao.api.APIs;

@Singleton
public class NGLApplication extends LFWApplication {
	
	private final Provider<APIs> apis;
	private final NGLConfig      config;
	private final IAuthorizator  authorizator; 
	@Inject
	public NGLApplication(LFWApplication lfwa ,
						  NGLConfig config,
			              Provider<APIs> apis,
			              IAuthorizator authorizator) {
		super(lfwa);
		this.apis         = apis;
		this.config       = config;
		this.authorizator = authorizator;
	}
	
	public APIs apis()           { return apis.get(); }
	public NGLConfig nglConfig() { return config;     }
	public IAuthorizator authorizator() { return authorizator; } 
	
}
