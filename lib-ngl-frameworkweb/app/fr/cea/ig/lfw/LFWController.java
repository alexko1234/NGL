package fr.cea.ig.lfw;

import javax.inject.Inject;

import play.mvc.Controller;

public class LFWController extends Controller  {
	
	private final LFWApplication app;
	
	@Inject
	public LFWController(LFWApplication app) {
		this.app = app;
	}
	
	public LFWApplication getLFWApplication() { return app; }
	
}
