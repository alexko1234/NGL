package ngl.sq;

import org.junit.Test;

import fr.cea.ig.ngl.test.authentication.Identity;

public class TestSamplesRoutes {

	@Test
	public void testHome() {
		Global.af.authURL(Identity.Read,"/samples/home");
	}
	
	@Test
	public void testGet() {
		Global.af.authURL(Identity.Read,"/samples/AAAA-A120_ST147_T0_A");
	}
	
}
