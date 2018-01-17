package fr.cea.ig.ngl;

import javax.inject.Inject;

import fr.cea.ig.lfw.LFWController;

public class NGLController extends LFWController {

	private final NGLApplication app;
	
	@Inject
	public NGLController(NGLApplication app) {
		super(app);
		this.app = app;
	}
	
	public NGLApplication getNGLApplication() { return app; }
	
}
