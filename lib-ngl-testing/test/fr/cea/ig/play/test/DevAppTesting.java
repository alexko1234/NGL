package fr.cea.ig.play.test;

import static org.junit.Assert.assertEquals;
// import static fr.cea.ig.play.test.ReadUpdateReadTest.*;
import static fr.cea.ig.play.test.WSHelper.get;
import static fr.cea.ig.play.test.ReadUpdateReadTest.notEqualsPath;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
//import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.IntNode;

import play.Application;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.ApplicationLifecycle;
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

import javax.inject.Inject;

import com.google.common.io.Resources;

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
	
	
	private static String testTimeKey = null;
	
	public static String testTimeKey() {
		if (testTimeKey == null) {
			testTimeKey = Long.toHexString(System.currentTimeMillis());
			testTimeKey = testTimeKey.substring(testTimeKey.length() - 6);
			// Could map 0-F -> A-. for non numeric stuff
			// testTimeKey = org.apache.commons.lang.StringUtils.replaceChars(testTimeKey,"0123456789ABCDEF","ABCDEFGHIJKLMONP");			                                                                                
		}
		return testTimeKey;
	}

	// Generate a key from some components
	public static String code(String head) {
		String testRunner = System.getProperty("user.name");
		String datePart   = testTimeKey();
		return head + testRunner + datePart;
	}
	

	/**
	 * Get the full name of the file that mathces the given resource. 
	 * @param name name of the resource to find
	 * @return     full path to the found file
	 */
	// TODO: fix resource lookup
	public static String resourceFileName(String name) {
		try {
		List<URL> resources = Collections.list(DevAppTesting.class.getClassLoader().getResources(name));
		if (resources.size() == 0)
			throw new RuntimeException("could not locate resource '" + name + "' using classloader");
		for (URL url : resources) {
			try {
				logger.info("trying to load " + name + " from " + url);
				File file = new File(url.toURI());
				return file.toString();
			} catch (Exception e) {
				// throw new RuntimeException("resource " + resources.get(0) + " cannot be converted to File",e);
			}
		}
		} catch (IOException e) {
			throw new RuntimeException("classloader get resource failed",e);
		}
		throw new RuntimeException("resource could not be loaded '" + name + "'");
	}
	
	/*
	 * Application builder instance, should either be destroyed  
	 */
	// private static GuiceApplicationBuilder applicationBuilder;
	
	/*
	 * Application singleton instance.
	 */
	private static Application application;
	
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
		if (application != null) {
			logger.warn("returning already application");
			return application;
		}
		
		if (application == null) {
			try {
			File unfragedConf = FragmentedConfiguration.file(appConfFile + ".frag");
			// System.setProperty("config.file", resourceFileName(appConfFile)); // resourceFileName("conf/ngl-sq-test.conf"));
			System.setProperty("config.file", unfragedConf.toString());
			System.setProperty("logger.file", resourceFileName(logConfFile)); // resourceFileName("conf/logger.xml"));
			System.setProperty("play.server.netty.maxInitialLineLength", "16384");
			if (false) {
			logger.debug("conn " + resourceFileName("jconn4.jar"));
			try {
			logger.debug("sybase driver " + DevAppTesting.class.getClassLoader().loadClass("com.sybase.jdbc4.jdbc.SybDriver").getName());
			} catch (Exception e) {
				throw new RuntimeException("sybdriver",e);
			}
			}
			// TODO: use play.Mode.TEST
			Environment env = new Environment(/*new File("path/to/app"),*//* classLoader,*/ play.Mode.DEV);
			GuiceApplicationBuilder applicationBuilder = new GuiceApplicationBuilder().in(env);
			application = applicationBuilder.build();
			} catch (IOException e) {
				throw new RuntimeException("application build init failed",e);
			}
			// GuiceApplicationBuilder applicationBuilder = new GuiceApplicationBuilder().in(env);
			// Application builder runs the db connection pool manager.
			//throw new RuntimeException("abort application creation");
		    //application = applicationBuilder.build();
		}
		// Register an aplication lifecycle cleaner.
		application.injector().instanceOf(Cleaner.class);
		return application; //Builder.build();
		// return applicationBuilder.build();		
	}
		
	static class Cleaner {
		@Inject
		public Cleaner(ApplicationLifecycle c) {
			// c.addStopHook(new Callable() {
			//	public CompletionStage call() throws Exception {		
			//	}
			//});
			c.addStopHook(() -> {
				logger.debug("clearing application reference");
				application = null;
				return  CompletableFuture.completedFuture(null);
			});
		}
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
	/*
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
	*/
	
	/**
	 * Standard RUR test that checks that the traceInformation has chnaged after the udate.
	 * @param url url to check
	 * @param ws  web client to use
	 */
	public static void rurNeqTraceInfo(String url, WSClient ws) {
		new ReadUpdateReadTest(url)
			.assertion(notEqualsPath("traceInformation"))
			.run(ws);
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
	
	public static void checkRoutes(WSClient ws) {
		RoutesTest.checkRoutes(ws);
	}
	
}

/**
 * Fragmeneted configuration loading so that configuration changes are done in a fragment
 * and then propragated to all the including configuration files.
 * 
 * @author vrd
 *
 */ 
class FragmentedConfiguration {

	private static final play.Logger.ALogger logger = play.Logger.of(FragmentedConfiguration.class);
	
	private static Pattern includePat = Pattern .compile("@include\\s+(\\S+)\\s*");
	
	public static File file(String name) throws IOException {
			// Load the main fragment, should check that the name ends with ".frag"
			File main = new File(DevAppTesting.resourceFileName(name));
			// Load the file and build the target
			// FileReader r = new FileReader(main);
			// StringBuilder out = new StringBuilder();
			FragmentedConfiguration fc = new FragmentedConfiguration();
			fc.include(main);
			String text = fc.text();
			// System.out.println(out.toString());
			// Generate tmp file with givne content
			String cleanName = main.getName().replace(".frag", "");
			File generated = new File(System.getProperty("java.io.tmpdir"),cleanName);
			logger.debug("generating file '" + generated + "'");
			FileWriter f = new FileWriter(generated);
			f.append(text);
			f.close();
			return generated;
	}

	private StringBuilder out;
	
	public FragmentedConfiguration() {
		out = new StringBuilder();
	}
	
	public void include( File f) throws IOException {
		StringBuilder b = out;
		Pattern p = includePat; // Pattern .compile("@include\s+(\S+)\s*");
		logger.debug("loading fragment '" + f + "'");
		b.append("# including --------- ");
		b.append(f);
		b.append('\n');
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line;
		while ((line = r.readLine()) != null) {
			Matcher m = p.matcher(line);
			if (m.matches()) {
				String include = m.group(1);
				File includedFile = new File(f.getParent(),include);
				include(new File(f.getParent(),include));
			} else {
				b.append(line);
				b.append('\n');
			}
		}
		r.close();
		b.append("# end --------------- ");
		b.append(f);
		b.append('\n');
		logger.debug("loaded fragment '" + f + "'");
	}
	public String text() {
		return out.toString();
	}
}



