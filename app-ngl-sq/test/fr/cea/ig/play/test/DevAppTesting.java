package fr.cea.ig.play.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Application;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.test.TestServer;
import play.test.WSTestClient;

import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import static play.mvc.Http.Status;

public class DevAppTesting {
	
	static GuiceApplicationBuilder applicationBuilder;
	
	static Application application;
	
	public static Application devapp() {
		if (applicationBuilder == null) {
			// This should fetch the config files from the resources directory
			// or from a preset classpath.
			System.setProperty("config.file", "c:\\projets\\config\\ngl-sq-dev.2.6.conf");
			System.setProperty("logger.file", "c:\\projets\\config\\logger.xml");
			System.setProperty("play.server.netty.maxInitialLineLength", "16384");
			Environment env = new Environment(/*new File("path/to/app"),*//* classLoader,*/ play.Mode.DEV);
		    applicationBuilder = new GuiceApplicationBuilder().in(env);
		    application = applicationBuilder.build();
		    //System.out.println("** injector " + application.injector());
		}
		// return applicationBuilder.build();
		// This does not properly sets the Play.application() instance.
		return application;
	}

	public static WSResponse get(WSClient ws, String url) { // throws InterruptedException,ExecutionException {
		try {
			CompletionStage<WSResponse> completionStage = ws.url(url).get();
			WSResponse response = completionStage.toCompletableFuture().get();	
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static WSResponse put(WSClient ws, String url, String payload) { // throws InterruptedException,ExecutionException {
		try {
			CompletionStage<WSResponse> completionStage = ws.url(url).setContentType("application/json;charset=UTF-8").put(payload);
			WSResponse response = completionStage.toCompletableFuture().get();	
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	public static final int TESTS_PORT = 3333;
	
	public static void testInServer(Consumer<WSClient> toRun) {
		testInServer(TESTS_PORT,toRun);
	}
	
	public static void testInServer(int port, Consumer<WSClient> toRun) {
		TestServer server = testServer(port,devapp());
	    running(server, () -> {
	        try (WSClient ws = WSTestClient.newClient(port)) {
	        	toRun.accept(ws);
	        } catch (Exception e) {
	        	throw new RuntimeException("failed",e);
	        }
	    });
	}
	
	// provide static methods to alter JSON with ease
	public static void set(JsonNode node, String path, String value) { throw new RuntimeException("not implemented"); }
	public static void set(JsonNode node, String path, int value) { throw new RuntimeException("not implemented"); }
	public static JsonNode get(JsonNode node, String path) { throw new RuntimeException("not implemented"); }
	
	/**
	 * Read, modify data, update, read again and compare read data to modified data.
	 * @param ws       web client
	 * @param url      URL to use for the get and put
	 * @param modify   JSON modification to run
	 * @param preCheck JSON before check modification
	 */
	public static void rur(WSClient ws, String url, Consumer<JsonNode> modify, Consumer<JsonNode> preCheck) {
		// Read
		System.out.println("GET - " + url);
		WSResponse r0 = get(ws,url);
		assertEquals(Status.OK, r0.getStatus());
		JsonNode js0 = Json.parse(r0.getBody());
		modify.accept(js0);
		// Update
		System.out.println("PUT - " + url);		
		WSResponse r1 = put(ws,url,js0.toString());
		assertEquals(Status.OK, r1.getStatus());
		// Read updated
		System.out.println("GET - " + url);
		WSResponse r2 = get(ws,url);
		assertEquals(Status.OK, r2.getStatus());
		JsonNode js1 = Json.parse(r2.getBody());
		// apply precheck to js0 and js1
		preCheck.accept(js0);
		preCheck.accept(js1);
		// assertEquals(js0,js1);
		cmp("",js0,js1);
	}
	
	// Could check that we get come error code instead of asserting equality 
	public static void rur(WSClient ws, String url, Consumer<JsonNode> modify) {
		rur(ws,url,modify,js -> { ((ObjectNode)js).remove("traceInformation"); });
	}
	
	// Assert equals
	public static final void cmp(String path, JsonNode n0, JsonNode n1) {
		//System.out.println("cmp " + path);
		assertEquals(path,n0.getNodeType(),n1.getNodeType());
		assertEquals(path,n0.size(),n1.size());
		switch (n0.getNodeType()) {
		case ARRAY:
			cmpArray(path,n0,n1);
			break;
		case BINARY:
			throw new RuntimeException("unexpected BINARY at " + path);
		case BOOLEAN:
			assertEquals(path,n0,n1);
			break;
		case MISSING:
			throw new RuntimeException("unexpected MISSING at " + path);
		case NULL:
			break;
		case NUMBER:
			assertEquals(path,n0,n1);
		case OBJECT:
			Iterator<String> iter = n0.fieldNames();
			while(iter.hasNext()) {
				String s = iter.next();
				JsonNode c0 = n0.get(s);
				JsonNode c1 = n1.get(s);
				cmp(path+"/"+s,c0,c1);
			}
			break;
		case POJO:
			throw new RuntimeException("unexpected POJO at " + path);
		case STRING:
			assertEquals(path,n0,n1);
		}
	}

	// Assert equals array / indexed.
	public static void cmpArray(String path, JsonNode n0, JsonNode n1) {
		if (n0.size() > 0) {
			if (n0.get(0).get("index") != null) {
				List<JsonNode> l0 = new ArrayList<JsonNode>();
				List<JsonNode> l1 = new ArrayList<JsonNode>();
				for (int i=0; i<n0.size(); i++) {
					l0.add(n0.get(i));
					l1.add(n1.get(i));
				}
				// Sort lists by "index"
				Comparator<JsonNode> indexCmp = new Comparator<JsonNode>() {
					@Override
					public int compare(JsonNode arg0, JsonNode arg1) {
						return arg0.get("index").toString().compareTo(arg1.get("index").toString());
					}
				};
				Collections.sort(l0,indexCmp);
				Collections.sort(l1,indexCmp);
			} else {
				for (int i=0; i<n0.size(); i++)
					cmp(path + "[" + i + "]", n0.get(i), n1.get(i));				
			}
		}
	}
	
}
