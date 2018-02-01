package ngl.sq.auth;

import org.junit.Test;

import fr.cea.ig.ngl.test.authentication.Identity;
import ngl.sq.Global;

public class TestProcessesRoutes {

	@Test
	public void testHome() {
		Global.af.authURL(Identity.Read,"/processes/searchContainers/home");
	}

	@Test
	public void testTplSearch() {
		Global.af.authNobody("/tpl/processes/searchContainers");
	}
	
	@Test
	public void testJsRoutes() {
		Global.af.authNobody("/tpl/processes/js-routes");
	}
	
}
