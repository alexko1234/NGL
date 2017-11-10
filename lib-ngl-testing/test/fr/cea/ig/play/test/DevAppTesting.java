package fr.cea.ig.play.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
//import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Test support for NGL on DEV server.
 * The application is a singleton as the shutdown is not properly managed. All the
 * tests then share the same application instance and this could go wrong.
 * 
 * Configuration files are located using the class loader. The "ngl.test.dir" environment
 * variable content is added to the classpath in the build file.
 * 
 * The configuration files are loaded from the classpath and this does not differ from loading
 * the directory taht is added to the classpath.
 * 
 * @author vrd
 *
 */
public class DevAppTesting {
	
	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(DevAppTesting.class);
	
	// static GuiceApplicationBuilder applicationBuilder;
	/**
	 * Application singleton instance.
	 */
	private static Application application;
	
	/**
	 * Get the full name of the file that mathces the given resource. 
	 * @param name name of the resource to find
	 * @return     full path to the found file
	 */
	public static String resourceFileName(String name) {
		URL resource = DevAppTesting.class.getClassLoader().getResource(name);
		if (resource == null)
			throw new RuntimeException("could not locate resource '" + name + "' using classloader");
		try {
			File confFile = new File(resource.toURI());
			return confFile.toString();
		} catch (Exception e) {
			throw new RuntimeException("resource " + resource + " cannot be converted to File",e);
		}
	}
	
	/**
	 * DEV application singleton instance. This does not sets the play global application
	 * instance. Actual configuration should be done using an application specifc tag that
	 * is used to look for the application configuration that is in "ngl-{tag}-test.conf" 
	 * file. Logger configuration comes from the "logger.xml" file.
	 * 
	 * @return dev application instance
	 */
	// TODO: provide support for other project by supporting a project name (e.g. "sq").
	public static Application devapp(String appConfFile, String logConfFile) {
		if (application == null) {
			System.setProperty("config.file", resourceFileName(appConfFile)); // resourceFileName("conf/ngl-sq-test.conf"));
			System.setProperty("logger.file", resourceFileName(logConfFile)); // resourceFileName("conf/logger.xml"));
			System.setProperty("play.server.netty.maxInitialLineLength", "16384");
			Environment env = new Environment(/*new File("path/to/app"),*//* classLoader,*/ play.Mode.DEV);
			GuiceApplicationBuilder applicationBuilder = new GuiceApplicationBuilder().in(env);
		    application = applicationBuilder.build();
		}
		return application;
	}
		
	/**
	 * Default port for tests http server.
	 */
	public static final int TESTS_PORT = 3333;
	
	/**
	 * Run the given test using the app and the default port for the http server.
	 * @param app   application to test
	 * @param toRun test to run
	 */
	public static void testInServer(Application app, Consumer<WSClient> toRun) {
		testInServer(app,TESTS_PORT,toRun);
	}
	
	/**
	 * Run the given test using the app and the http server at the given port.
	 * @param app
	 * @param port
	 * @param toRun
	 */
	public static void testInServer(Application app, int port, Consumer<WSClient> toRun) {
		TestServer server = testServer(port,app);
	    running(server, () -> {
	        try (WSClient ws = WSTestClient.newClient(port)) {
	        	toRun.accept(ws);
	        } catch (Exception e) {
	        	throw new RuntimeException("failed",e);
	        }
	    });
	}
	
	/**
	 * Read, modify data, update, read again and compare read data to modified data.
	 * @param ws       web client
	 * @param url      URL to use for the get and put
	 * @param modify   JSON modification to run
	 * @param preCheck JSON before check modification
	 */
	// TODO: use logger
	public static void rur(WSClient ws, String url, Consumer<JsonNode> modify, Consumer<JsonNode> preCheck) {
		// Read
		logger.debug("GET - " + url);
		WSResponse r0 = get(ws,url,Status.OK);
		JsonNode js0 = Json.parse(r0.getBody());
		modify.accept(js0);
		// Update
		logger.debug("PUT - " + url);		
		WSResponse r1 = put(ws,url,js0.toString());
		assertEquals(Status.OK, r1.getStatus());
		// Read updated
		logger.debug("GET - " + url);
		WSResponse r2 = get(ws,url);
		assertEquals(Status.OK, r2.getStatus());
		JsonNode js1 = Json.parse(r2.getBody());
		// apply precheck to js0 and js1
		preCheck.accept(js0);
		preCheck.accept(js1);
		// assertEquals(js0,js1);
		cmp("",js0,js1);
	}
	
	// RUR could be made a class with some configuration and run methods
	// Could check that we get come error code instead of asserting equality 
	public static void rur(WSClient ws, String url, Consumer<JsonNode> modify) {
		rur(ws,url,modify,js -> { remove(js,"traceInformation"); });
	}
	
	public static void rur(WSClient ws, String url) {
		rur(ws,url,js -> {});
	}
	
	public static final void cmp(JsonNode n0, JsonNode n1) {
		cmp("",n0,n1);
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
	
	static class Routes {
		private boolean loadRedirects = false;
		static class Entry {
			public String method,url,target;
			public Entry(String method, String url, String target) {
				this.method = method;
				this.url = url;
				this.target = target;
			}
		}
		public List<Entry> entries = new ArrayList<Entry>();
		public void load(String name) throws IOException,java.net.URISyntaxException {
			URL resource = DevAppTesting.class.getClassLoader().getResource(name);
			if (resource == null) {
				System.out.println("skipping route file " + name);
				return;
			}
			File file = new File(resource.toURI());
			BufferedReader r = new BufferedReader(new FileReader(file));
			String l;
			Pattern pat = Pattern.compile("(\\S+)\\s+(\\S+)\\s+\\S+(\\([^\\)]*\\))\\s*");
			Pattern blanks = Pattern.compile("\\s*");
			Pattern com    = Pattern.compile("#.*");
			Pattern redirect = Pattern.compile("->\\s+(\\S+)\\s+(\\S+)\\s*"); 
			while ((l = r.readLine()) != null) {
				Matcher m = pat.matcher(l);
				if (m.matches()) {
					Entry e = new Entry(m.group(1),m.group(2),m.group(3));
					entries.add(e);
					//System.out.println("matched entry    " + l);
				} else if ((m = blanks.matcher(l)).matches()) {
					//System.out.println("matched blanks   " + l);
				} else if ((m = com.matcher(l)).matches()) {
					//System.out.println("matched comments " + l);
				} else if ((m = redirect.matcher(l)).matches()) {
					//System.out.println("matched redirect " + l);
					if (loadRedirects)
						load(m.group(2));
				} else {
					throw new RuntimeException("unmacthed line in " + name + " " + l);
				}
			}
		}
	}
	
	public static Routes loadRoutes() {
		try {
		Routes routes = new Routes();
		routes.load("routes");
		return routes;
		} catch (Exception e) {
			throw new RuntimeException("route loading failed",e); 
		}
	}
		
	public static void checkRoutes(WSClient ws) {
		Routes routes = loadRoutes();
		for (Routes.Entry e : routes.entries) {
			String url = e.url;
			if (e.method.equals("GET")) {
				if (!(url.contains(":") || url.contains("*"))) {
					get(ws,url,Status.OK);
				} else if (url.contains(":homecode")) {
					get(ws,url.replace(":homecode","search"),Status.OK);
				}
			}
		}
	}
	
	// ------------------------------------------------------------
	// Http shortcuts
	/**
	 * Shorcut for http get. Exceptions are converted to runtime
	 * exceptions.
	 * @param ws  web client to use
	 * @param url url to get 
	 * @return    web response for the given url
	 */
	public static WSResponse get(WSClient ws, String url) { // throws InterruptedException,ExecutionException {
		try {
			CompletionStage<WSResponse> completionStage = ws.url(url).get();
			WSResponse response = completionStage.toCompletableFuture().get();	
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Execute a get request and assert the response status. 
	 * @param ws     web client
	 * @param url    url to get
	 * @param status status to assert
	 * @return       request response
	 */
	public static WSResponse get(WSClient ws, String url, int status) {
		WSResponse r = get(ws,url);
		assertEquals(url, status, r.getStatus());
		return r;
	}

	/**
	 * Short for http put with some payload.
	 * @param ws      web client to use
	 * @param url     url to put to
	 * @param payload payload to send along the put request
	 * @return        web response
	 */
	public static WSResponse put(WSClient ws, String url, String payload) { // throws InterruptedException,ExecutionException {
		try {
			CompletionStage<WSResponse> completionStage = ws.url(url).setContentType("application/json;charset=UTF-8").put(payload);
			WSResponse response = completionStage.toCompletableFuture().get();	
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static WSResponse put(WSClient ws, String url, String payload, int status) {
		WSResponse r = put(ws,url,payload);
		assertEquals(url, status, r.getStatus());
		return r;
	}

	// public static void rcrud()
	
	// Could provide a / separator to split the path.
	public static void remove(JsonNode n, String... path) {
		String[] parts = path; //path.split("/");
		for (int i=0; i<parts.length-1; i++) {
			JsonNode m = n.get(parts[i]);
			if (m != null)
				n = m;
			else
				throw new RuntimeException("could not find " + parts[i] + " in node for path " + path);
		}
		if (n instanceof ObjectNode)
			((ObjectNode)n).remove(parts[parts.length-1]);
		else
			throw new RuntimeException(String.join(".",path) + " does not lead to an object");
	}
	// provide static methods to alter JSON with ease
	public static void set(JsonNode node, String path, String value) { throw new RuntimeException("not implemented"); }
	public static void set(JsonNode node, String path, int value) { throw new RuntimeException("not implemented"); }
	public static JsonNode get(JsonNode node, String path) { throw new RuntimeException("not implemented"); }

	
}
