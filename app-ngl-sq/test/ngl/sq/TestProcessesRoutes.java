package ngl.sq;

import org.junit.Test;

import fr.cea.ig.ngl.test.authentication.Identity;

public class TestProcessesRoutes {

	@Test
	public void testHome() {
		Global.af.authURL(Identity.Read,"/processes/searchContainers/home");
	}

	
}
