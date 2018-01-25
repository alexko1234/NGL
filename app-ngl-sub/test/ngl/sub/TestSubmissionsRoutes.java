package ngl.sub;

// import static ngl.sub.Global.devapp;
import static fr.cea.ig.play.test.DevAppTesting.testInServer;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.FORBIDDEN;

import org.junit.Test;

import fr.cea.ig.ngl.test.TestAuthConfig;
import fr.cea.ig.ngl.test.authentication.Identity;
import fr.cea.ig.play.test.WSHelper;
import play.Application;

// routes targeting : controllers.sra.submissions.tpl.Submissions
// GET		/sra/submissions/:homeCode/home					controllers.sra.submissions.tpl.Submissions.home(homeCode : java.lang.String)
// GET		/sra/submissions/:code							controllers.sra.submissions.tpl.Submissions.get(code : java.lang.String)
// GET		/tpl/sra/submissions/create						controllers.sra.submissions.tpl.Submissions.create()
// GET		/tpl/sra/submissions/activate					controllers.sra.submissions.tpl.Submissions.activate()
// GET		/tpl/sra/submissions/consultation				controllers.sra.submissions.tpl.Submissions.consultation()
// GET		/tpl/sra/submissions/validation				    controllers.sra.submissions.tpl.Submissions.validation()
// GET		/tpl/sra/submissions/details					controllers.sra.submissions.tpl.Submissions.details()
// GET		/tpl/sra/submissions/js-routes					controllers.sra.submissions.tpl.Submissions.javascriptRoutes()

public class TestSubmissionsRoutes {

	// Test that the url is acessible for at least the given
	// permission.
	
	// Could define maps for constants.
	
	private static Identity worsen(Identity i) {
		switch (i) {
		case Nobody:    return Identity.Nobody;
		case Read:      return Identity.Nobody;
		case ReadWrite: return Identity.Read;
		case Write:     return Identity.Read;
		case Admin:     return Identity.ReadWrite;
		default:		throw new RuntimeException("no worsening defined for " + i);
		}
	}
	
	private static Application devapp(Identity i) {
		switch (i) {
		case Nobody:    return ngl.sub.Global.devapp(TestAuthConfig.asNobody);
		case Read:      return ngl.sub.Global.devapp(TestAuthConfig.asRead);
		case Write:     return ngl.sub.Global.devapp(TestAuthConfig.asWrite);
		case ReadWrite: return ngl.sub.Global.devapp(TestAuthConfig.asReadWrite);
		case Admin:     return ngl.sub.Global.devapp(TestAuthConfig.asAdmin);
		default:        throw new RuntimeException("no auth config defined for " + i);
		}
	}
	
	private static void checkURL(Identity i, String url, int status) {
	    testInServer(devapp(i),
	    		ws -> {	    	
	    			WSHelper.get(ws,url,status);
	    		});
	}
	
	private static void authURL(Identity i, String url) {
		Identity j = worsen(i);
	    checkURL(i,url,OK);
	    checkURL(j,url,FORBIDDEN);
	}
	
	// Authorization tests
	// GET		/tpl/sra/submissions/activate					controllers.sra.submissions.tpl.Submissions.activate()
	// GET		/tpl/sra/submissions/consultation				controllers.sra.submissions.tpl.Submissions.consultation()
	// GET		/tpl/sra/submissions/create						controllers.sra.submissions.tpl.Submissions.create()
	// GET		/tpl/sra/submissions/details					controllers.sra.submissions.tpl.Submissions.details()
	// GET		/tpl/sra/submissions/validation				    controllers.sra.submissions.tpl.Submissions.validation()

	@Test public void testAuth_home_consultation() { authURL(Identity.Read,"/sra/submissions/consultation/home"); }
	@Test public void testAuth_tpl_activate     () { authURL(Identity.Read,"/tpl/sra/submissions/activate");      }
	@Test public void testAuth_tpl_consultation () { authURL(Identity.Read,"/tpl/sra/submissions/consultation");  }
	@Test public void testAuth_tpl_create       () { authURL(Identity.Read,"/tpl/sra/submissions/create");        }
	@Test public void testAuth_tpl_details      () { authURL(Identity.Read,"/tpl/sra/submissions/details");       }
	@Test public void testAuth_tpl_validation   () { authURL(Identity.Read,"/tpl/sra/submissions/validation");    }
	
}
