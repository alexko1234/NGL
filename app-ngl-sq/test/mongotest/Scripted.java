package mongotest;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import static play.mvc.Http.Status;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

import play.Application;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;

// Infrastructure for ngl testing through routes
//
// One of the first stage would be to clear data that is related to testing
// Using the mongo client we would delete all the data whose name contains TEST or 
// something along those lines. The other way around is to have the mongo client
// use a mocked db.
// 
public class Scripted {

	public static Application devapp() {
		return fr.cea.ig.play.test.DevAppTesting.devapp();
	}

	
	public void testBadRoute() {
		Application app = devapp();
	    RequestBuilder request = Helpers.fakeRequest()
	            .method(GET)
	            .uri("/xx/Kiwi");

	    Result result = route(app, request);
	    assertEquals(Status.NOT_FOUND, result.status());
	}
	
	@Test
	public void testGoodRoute() {
		Application app = devapp();
	    RequestBuilder request = Helpers.fakeRequest()
	            .method(GET)
	            .uri("/experiments/CHIP-MIGRATION-20170915_144939CDA");

	    Result result = route(app, request);
	    assertEquals(Status.OK, result.status());
	}
	
}
