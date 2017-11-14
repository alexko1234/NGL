package fr.cea.ig.play.test;

import static fr.cea.ig.play.test.WSHelper.get;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.mvc.Http.Status;
import play.libs.ws.WSClient;



public class RoutesTest {
	
	private List<Routes> routesList;
	private Set<String> ignore;
	
	public RoutesTest() {
		routesList = new ArrayList<Routes>();
		ignore = new HashSet<String>();
	}
	
	// Lol ?
	public RoutesTest autoRoutes() {
		routesList.add(loadRoutes());
		return this;
	}
	
	public RoutesTest ignore(String... urls) {
		for (String url : urls)
			ignore.add(url);
		return this;
	}
	
	public void run(WSClient ws) {
		for (Routes routes : routesList) {
			for (Routes.Entry e : routes.entries) {
				String url = e.url;
				String rUrl = null;
				if (e.method.equals("GET")) {
					if (!(url.contains(":") || url.contains("*"))) {
						// get(ws,url,Status.OK);
						rUrl = url; 
					} else if (url.contains(":homecode")) {
						// get(ws,url.replace(":homecode","search"),Status.OK);
						rUrl = url.replace(":homecode","search");
					}
				}
				if (rUrl != null) 
					if (!ignore.contains(rUrl))
						get(ws,url,Status.OK);
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
		/*
		Routes routes = loadRoutes();
		for (Routes.Entry e : routes.entries) {
			String url = e.url;
			if (e.method.equals("GET")) {
				String rUrl = null;
				if (!(url.contains(":") || url.contains("*"))) {
					// get(ws,url,Status.OK);
					rUrl = url; 
				} else if (url.contains(":homecode")) {
					// get(ws,url.replace(":homecode","search"),Status.OK);
					rUrl = url.replace(":homecode","search");
				}
				if (rUrl != null) 
					if (!ignore.contains(rUrl))
						get(ws,url,Status.OK);
			}
		}
		*/
		new RoutesTest()
		.autoRoutes()
		.run(ws);
	}

}