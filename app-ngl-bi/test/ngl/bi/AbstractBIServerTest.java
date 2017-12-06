package ngl.bi;

import org.junit.BeforeClass;

import fr.cea.ig.play.test.AbstractServerTest;
import fr.cea.ig.play.test.CompleteTestServer;
import play.Application;

class CompleteBITestServer extends CompleteTestServer {
	public Application createApplication() { 
		return Global.devapp();
	}
}

public class AbstractBIServerTest extends AbstractServerTest {
	
	/**
	 * Initialize test application.
	 */
	@BeforeClass
	public static void startTestApplication() {
		initFrom(new CompleteBITestServer());
	}

}
