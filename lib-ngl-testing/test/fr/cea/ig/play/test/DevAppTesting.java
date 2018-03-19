package fr.cea.ig.play.test;

import static org.junit.Assert.assertEquals;
// import static fr.cea.ig.play.test.ReadUpdateReadTest.*;
// import static fr.cea.ig.play.test.WSHelper.get;
import static fr.cea.ig.play.test.ReadUpdateReadTest.notEqualsPath;

// import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
// import java.util.concurrent.CompletionStage;
// import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
//import java.util.regex.Pattern;
import java.util.function.Function;
// import java.io.BufferedReader;
import java.io.File;
// import java.io.FileReader;
// import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
// import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
// import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.node.ObjectNode;
// import com.fasterxml.jackson.databind.node.TextNode;
// import com.fasterxml.jackson.databind.node.IntNode;

import play.Application;
// import play.Configuration;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.ApplicationLifecycle;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http.Status;
import play.test.TestServer;
import play.test.WSTestClient;

import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

import javax.inject.Inject;

// import com.google.common.io.Resources;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.DBObject;
//import models.laboratory.common.instance.TraceInformation;
//import models.utils.instance.SampleHelper;
//import models.utils.InstanceConstants;
//import validation.ContextValidation;

/**
 * Test support for NGL on DEV server.
 * The application life cycle is managed properly and only one application is active at
 * any given time when the {@link #devapp} method is used. 
 * 
 * @author vrd
 *
 */
public class DevAppTesting {
	
	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(DevAppTesting.class);
	
	public static final String PROP_NAME_LOGGER_FILE = "ngl.test.logger.file";
	public static final String PROP_NAME_LOGGER_RESOURCE = "ngl.test.logger.resource"; 
	
	/**
	 * Somewhat unique identifier per test set execution that can be used to create unique identifiers.
	 */
	private static String testTimeKey = null;
	
	/**
	 * Map hexadecimal chars to letters.
	 * @param s string to apply substitution to
	 * @return  string with applied substitution
	 */
	private static String hexToLetters(String s) {
		return org.apache.commons.lang3.StringUtils.replaceChars(s,"0123456789abcdef","ABCDEFGHIJKLMONP");	
	}
	
	/**
	 * Somewhat unique identifier that can be used to create unique identifiers.
	 * @return 
	 */
	public static String testTimeKey() {
		if (testTimeKey == null) {
			testTimeKey = Long.toHexString(System.currentTimeMillis());
			testTimeKey = testTimeKey.substring(testTimeKey.length() - 6);
			testTimeKey = hexToLetters(testTimeKey); 			                                                                                
		}
		return testTimeKey;
	}

	private static int codeId = 0;
	
	/**
	 * Generate a code with the given prefix.
	 * @param head prefix to prepend
	 * @return     generated code
	 */
	public static String newCode(String head) {
		String testRunner = System.getProperty("user.name").toUpperCase();
		String datePart   = testTimeKey();
		String iid        = hexToLetters(String.format("%04d", codeId ++)); 
		return head + testRunner + datePart + iid;
	}
	
	/**
	 * Generate a new code with "TEST" as prefix.
	 * @return generated code
	 */
	public static String newCode() {
		return newCode("TEST");
	}

	/*
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
					logger.warn(" {} cannot be converted to a File",url);
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

	private static void propsDump() {
		Properties p = System.getProperties();
		Enumeration<Object> keys = p.keys();
		while (keys.hasMoreElements()) {
		    String key = (String)keys.nextElement();
		    String value = (String)p.get(key);
		    if (key.startsWith("ngl") || key.startsWith("logger") || key.startsWith("NGL"))
		    	// System.out.println("------------ " + key + ": " + value);
		    	logger.info("test VM option {}={}", key, value);
		    else 
		    	; // System.out.println(key + ": " + value);
		}		
	}
	
	/*private static void loggerSetup() {
		if (System.getProperty(PROP_NAME_LOGGER_FILE) != null)
			System.setProperty("logger.file", System.getProperty(PROP_NAME_LOGGER_FILE));
		else if (System.getProperty(PROP_NAME_LOGGER_RESOURCE) != null)
			System.setProperty("logger.resource", System.getProperty(PROP_NAME_LOGGER_RESOURCE));
		else if (System.getProperty("logger.file") != null)
			;
		else if (System.getProperty("logger.file") != null)
			;
		else 
			throw new RuntimeException(" set either '" + PROP_NAME_LOGGER_FILE + "' or '" + PROP_NAME_LOGGER_RESOURCE 
					                   + "' by setting an environment variable or running sbt \"-D" + PROP_NAME_LOGGER_FILE 
					                   + "=<absolutefilename>\" or \"-D" + PROP_NAME_LOGGER_RESOURCE + "=<name>\" that is"
					                   + " looked for in the classpath");
	}*/

	/*
	 * DEV application singleton instance. This does not sets the play global application
	 * instance. Actual configuration should be done using an application specifc tag that
	 * is used to look for the application configuration that is in "ngl-{tag}-test.conf" 
	 * file. Logger configuration comes from the "logger.xml" file.
	 * 
	 * @return dev application instance
	 */
	// TODO: provide support for other project by supporting a project name (e.g. "sq").
	// TODO: change to use the devappF version that use file configuration and not resource.
	@SafeVarargs
	public static Application devapp(String appConfFile, Function<GuiceApplicationBuilder,GuiceApplicationBuilder>... mods) { //, String logConfFile) {
		return devapp(appConfFile,Arrays.asList(mods));
	}
	
	public static Application devapp(String appConfFile, List<Function<GuiceApplicationBuilder,GuiceApplicationBuilder>> mods) {
		if (application != null) {
			logger.warn("returning already application");
			return application;
		}
		try {
			propsDump();
			// File unfragedConf = FragmentedConfiguration.file(appConfFile + ".frag");
			String confFileName = resourceFileName(appConfFile);
			// String confFileName = appConfFile;
			// String confFileName = "conf.play.frag";
			logger .debug("using config file '" + confFileName + "'");
			//logger.debug("config file name : " + confFileName + " " + resourceFileName(confFileName));
			// System.setProperty("config.resource", confFileName); // + ".frag")); // resourceFileName("conf/ngl-sq-test.conf"));
			System.setProperty("config.file", confFileName);
			//System.setProperty("config.file", resourceFileName(confFileName));
			//System.setProperty("config.file", unfragedConf.toString());
			// System.setProperty("logger.file", resourceFileName(logConfFile)); // resourceFileName("conf/logger.xml"));
			// System.setProperty("logger.resource", logConfFile);
			// loggerSetup();
			System.setProperty("play.server.netty.maxInitialLineLength", "16384");
			// TODO: use play.Mode.TEST
			Environment env = new Environment(/*new File("path/to/app"),*//* classLoader,*/ play.Mode.DEV);
			// Map<String,Object> config = new HashMap<String,Object>();
			// config.put("config.file", confFileName);
			// config.put("play.server.netty.maxInitialLineLength", "16384");
			// Configuration conf = new Configuration(config);
			
			GuiceApplicationBuilder applicationBuilder = 
					new GuiceApplicationBuilder()
					// .configure("config.file", confFileName)
					.in(env); //.configure(config);
			for (Function<GuiceApplicationBuilder,GuiceApplicationBuilder> mod : mods)
				applicationBuilder = mod.apply(applicationBuilder);
			// applicationBuilder = applicationBuilder.configure("config.file", confFileName);
			// applicationBuilder = applicationBuilder.configure("play.server.netty.maxInitialLineLength", "16384");
			
			application = applicationBuilder.build();
			// System.out.println(application.config().toString());
			// Register an aplication lifecycle cleaner.
			application.injector().instanceOf(Cleaner.class);
			return application; //Builder.build();
		} catch (Exception e) {
			throw new RuntimeException("application build init failed",e);
		}
	}
	
	// Locate the configuration through the resources but use it with 'config.file'
	// so the configuration file includes are consistent with the usual -Dconfig.file
	/*public static Application devappF(String appConfFile, String logConfFile) {
		if (application != null) {
			logger.warn("returning already application");
			return application;
		}
		try {
			String confFileName = resourceFileName(appConfFile);
			logger .debug("using config file '" + confFileName + "'");
			System.setProperty("config.file",     confFileName);
			System.setProperty("logger.resource", logConfFile);
			System.setProperty("play.server.netty.maxInitialLineLength", "16384");
			Environment env = new Environment(play.Mode.TEST);
			GuiceApplicationBuilder applicationBuilder = new GuiceApplicationBuilder().in(env);
			application = applicationBuilder.build();
			// Register an aplication lifecycle cleaner.
			application.injector().instanceOf(Cleaner.class);
			return application;
		} catch (Exception e) {
			throw new RuntimeException("application build init failed",e);
		}
	}*/

	static class Cleaner {
		@Inject
		public Cleaner(ApplicationLifecycle c) {
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
	public static void rurNeqTraceInfo(WSClient ws, String url) {
		new ReadUpdateReadTest(url)
			.assertion(notEqualsPath("traceInformation"))
			.run(ws);
	}
	
	// This requires that the input object has a code field (DBObject most certainly).
	public static void rurNeqTraceInfo(WSClient ws, String url, JsonNode n) {
		String code = new JsonFacade(n).getString("code");
		new ReadUpdateReadTest(url + "/" + code)
			.assertion(notEqualsPath("traceInformation"))
			.run(ws);
	}
	
	public static void rurNeqTraceInfo(WSClient ws, String url, Object o) {
		rurNeqTraceInfo(ws,url,Json.toJson(o));
	}
	
	
	public static void cr(WSClient ws, String url, JsonNode data) {
		// This must post data to fill a form server side (Form<Sample>)
		// The provided json data has to be converted to form data.
		// With some luck we can map the provided json fields to the
		// corresponding sample attribute.
		// @see models.laboratory.sample.instance.Sample
		WSResponse r0 = WSHelper.post(ws,url,data.toString(),Status.OK);
		// logger.debug("post " + url + " : " + r0.getBody());
		// assertEquals(Status.OK,r0.getStatus());
		JsonNode js0 = Json.parse(r0.getBody());
		WSResponse r1 = WSHelper.get(ws,url + "/" + JsonHelper.get(js0,"code").textValue(),Status.OK);
		JsonNode js1 = Json.parse(r1.getBody());
		cmp(js0,js1);
	}
	
	public static void cr(WSClient ws, String url, Object data) {
		cr(ws,url,Json.toJson(data));
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
	
	// Run through DAO stuff.
	public static <T extends DBObject> void savage(JsonNode n, Class<T> t, String collectionName) {
		T o = Json.fromJson(n, t);
		MongoDBDAO.save(collectionName, o);
	}
	
	public static <T extends DBObject> void savage(T o, Class<T> t, String collectionName) {
		MongoDBDAO.save(collectionName, o);
	}
	
}

/*
 * Fragmented configuration loading so that configuration changes are done in a fragment
 * and then propragated to all the including configuration files.
 * 
 * @author vrd
 *
 * 
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
*/



