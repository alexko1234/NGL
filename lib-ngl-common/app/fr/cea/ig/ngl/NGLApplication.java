package fr.cea.ig.ngl;

import javax.inject.Inject;
import javax.inject.Singleton;

import fr.cea.ig.lfw.LFWApplication;
// import fr.cea.ig.ngl.dao.DAOs;
import fr.cea.ig.ngl.daoapi.APIs;

@Singleton
public class NGLApplication extends LFWApplication {
	
	private final APIs apis;
	private final NGLConfig config;
	
	@Inject
	public NGLApplication(LFWApplication lfwa ,
						  NGLConfig config,
			              APIs apis) {
		super(lfwa);
		this.apis = apis;
		this.config = config;
	}
	
	public APIs apis() { return apis; }
	public NGLConfig nglConfig() { return config; }
	
}
