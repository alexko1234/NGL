package fr.cea.ig.play.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import play.libs.Json;

public class JsonHelper {
	
	private static final play.Logger.ALogger logger = play.Logger.of(JsonHelper.class);
	// -- JSON shortcuts
	
	// public static void rcrud()
	
	// Could provide a / separator to split the path.
	public static void remove(JsonNode n, String... path) {
		n = getParent(n,path);
		if (n instanceof ObjectNode)
			((ObjectNode)n).remove(path[path.length-1]);
		else
			throw new RuntimeException(String.join(".",path) + " does not lead to an object");
	}
	
	
	public static void set(JsonNode node, String value, String... path) { 
		node = getParent(node,path);
		if (node instanceof ObjectNode)
			((ObjectNode)node).set(path[path.length-1], new TextNode(value));
		else
			throw new RuntimeException(String.join(".",path) + " does not lead to an object");		
	}
	
	public static void set(JsonNode node, int value, String... path) { 
		node = getParent(node,path);
		if (node instanceof ObjectNode)
			((ObjectNode)node).set(path[path.length-1], new IntNode(value));
		else
			throw new RuntimeException(String.join(".",path) + " does not lead to an object");				
	}
	
	public static JsonNode get(JsonNode node, String... path) {
		for (int i=0; i<path.length; i++) {
			JsonNode m = node.get(path[i]);
			if (m != null)
				node = m;
			else
				throw new RuntimeException("could not find " + path[i] + " in node for path " + String.join("/", path));
		}
		return node;
	}
	
	public static JsonNode getParent(JsonNode node, String... path) {
		for (int i=0; i<path.length-1; i++) {
			JsonNode m = node.get(path[i]);
			if (m != null)
				node = m;
			else
				throw new RuntimeException("could not find " + path[i] + " in node for path " + String.join("/", path));
		}
		return node;
	}

	/*public static JsonNode get(JsonNode n, String... path) {
		for (int i=0; i<path.length-1; i++) {
			JsonNode m = n.get(path[i]);
			if (m != null)
				n = m;
			else
				throw new RuntimeException("could not find " + path[i] + " in node for path " + String.join("/", path));
		}
		return n;
	}*/

	public static JsonNode getJson(String name) {
		String rname = name + ".json";
		logger.debug("loading json from " + DevAppTesting.class.getClassLoader().getResource(rname));
		return Json.parse(DevAppTesting.class.getClassLoader().getResourceAsStream(rname));
	}

	
}