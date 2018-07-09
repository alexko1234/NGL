package ngl.bi.auth;

import org.junit.Test;

import fr.cea.ig.ngl.test.authentication.Identity;
import ngl.bi.Global;

public class TestUsersRoutes {
	
	@Test
	public void testHome() {
		Global.af.authURL(Identity.Read,"/users/spongebob/home");
	}	
	
	@Test
	public void testTplSearch() {
		Global.af.authNobody("/tpl/users/search");
	}

	@Test
	public void testJsRoutes() {
		Global.af.authNobody("/tpl/users/js-routes");
	}

}
