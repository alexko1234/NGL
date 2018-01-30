package ngl.sq;

import static ngl.sq.Global.devapp;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import play.test.TestServer;

public class TestBrowser {
	
	// @Test
	public void runInBrowser() throws IOException {
		// CompleteSQTestServer ts = new CompleteSQTestServer();
		// ts.start();
		TestServer server = new TestServer(3333,devapp());
		play.test.Helpers.running(server, play.test.Helpers.HTMLUNIT, browser -> {
		// play.test.Helpers.running(server, play.test.Helpers.FIREFOX, browser -> {
	        browser.goTo("/");
	        // assertEquals("Welcome to Play!", browser.$("#title").text());
	        // browser.$("a").click();
	        assertEquals("/", browser.url());
	        // ts.stop();
	    });
	}

}
