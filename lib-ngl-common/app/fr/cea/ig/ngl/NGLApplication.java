package fr.cea.ig.ngl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.Provider;

import fr.cea.ig.lfw.LFWApplication;
// import fr.cea.ig.ngl.dao.DAOs;
import fr.cea.ig.ngl.daoapi.APIs;

@Singleton
public class NGLApplication extends LFWApplication {
	
	private final Provider<APIs> apis;
	private final NGLConfig      config;
	
	@Inject
	public NGLApplication(LFWApplication lfwa ,
						  NGLConfig config,
			              Provider<APIs> apis) {
		super(lfwa);
		this.apis   = apis;
		this.config = config;
	}
	
	public APIs apis()           { return apis.get(); }
	public NGLConfig nglConfig() { return config;     }
	
}
