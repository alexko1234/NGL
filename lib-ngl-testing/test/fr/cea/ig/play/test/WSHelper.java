package fr.cea.ig.play.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http.Status;

/**
 * WSClient shortcuts.
 * 
 * @author vrd
 *
 */
public class WSHelper {
	
	private static final play.Logger.ALogger logger = play.Logger.of(WSHelper.class);
	
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
		return assertResponseStatus("GET " + url, get(ws,url), status);
	}

	// assumes HTTP OK 
	public static <T> T getObject(WSClient ws, String url, Class<T> clazz) {
		return Json.fromJson(Json.parse(get(ws,url,Status.OK).getBody()),clazz);
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
	
	public static WSResponse put(WSClient ws, String url, JsonNode payload) {
		return put(ws,url,payload.toString());
	}
	public static WSResponse putObject(WSClient ws, String url, Object payload) {
		return put(ws,url,Json.toJson(payload));
	}
	
	
	/**
	 * Short for http put with some payload.
	 * @param ws      web client to use
	 * @param url     url to put to
	 * @param payload payload to send along the put request
	 * @param status  expected http status
	 * @return        web response
	 */
	public static WSResponse put(WSClient ws, String url, String payload, int status) {
		return assertResponseStatus("PUT " + url, put(ws,url,payload), status);
	}

	public static WSResponse put(WSClient ws, String url, JsonNode payload, int status) {
		return put(ws,url,payload.toString(),status);
	}
	public static WSResponse putObject(WSClient ws, String url, Object payload, int status) {
		return put(ws,url,Json.toJson(payload),status);
	}
	
	/**
	 * Short for http post with some payload.
	 * @param ws      web client to use
	 * @param url     url to post to
	 * @param payload payload to send along the post request
	 * @return        web response
	 */
	public static WSResponse post(WSClient ws, String url, String payload) { // throws InterruptedException,ExecutionException {
		try {
			CompletionStage<WSResponse> completionStage = ws.url(url).setContentType("application/json;charset=UTF-8").post(payload);
			WSResponse response = completionStage.toCompletableFuture().get();	
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Short for http post with some payload.
	 * @param ws      web client to use
	 * @param url     url to post to
	 * @param payload payload to send along the post request
	 * @param status  expected http status
	 * @return        web response
	 */
	public static WSResponse post(WSClient ws, String url, String payload, int status) {
		return assertResponseStatus("POST " + url, post(ws,url,payload), status);
	}

	public static WSResponse post(WSClient ws, String url, JsonNode payload) {
		return post(ws,url,payload.toString());
	}

	public static WSResponse post(WSClient ws, String url, JsonNode payload, int status) {
		return assertResponseStatus("POST " + url, post(ws,url,payload), status);
	}
	
	public static WSResponse postObject(WSClient ws, String url, Object object) {
		return post(ws,url,Json.toJson(object));
	}
	
	public static WSResponse postObject(WSClient ws, String url, Object object, int status) {
		return assertResponseStatus("POST " + url, postObject(ws,url,object), status);
	}
	public static WSResponse assertResponseStatus(String message, WSResponse response, int status) {
		assertEquals(message + " " + response.getBody(), status, response.getStatus());
		return response;
	}
	/**
	 * Short for http delete with some payload.
	 * @param ws      web client to use
	 * @param url     url to put to
	 * @return        web response
	 */
	public static WSResponse delete(WSClient ws, String url) { 
		try {
			CompletionStage<WSResponse> completionStage = ws.url(url).delete();
			WSResponse response = completionStage.toCompletableFuture().get();	
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Short for http delete 
	 * @param ws      web client to use
	 * @param url     url to put to
	 * @param status  expected http status
	 * @return        web response
	 */
	public static WSResponse delete(WSClient ws, String url, int status) {
		WSResponse r = delete(ws,url);
		assertEquals("DELETE " + url + " " + r.getBody(), status, r.getStatus());
		return r;
	}
	

	/**
	 * Execute a head request and assert the response status. 
	 * @param ws     web client
	 * @param url    url to get
	 * @param status status to assert
	 * @return       request response
	 */
	public static WSResponse head(WSClient ws, String url, int status) {
		WSResponse r = head(ws,url);
		assertEquals("GET " + url + " " + r.getBody(), status, r.getStatus());
		return r; 
	}
	
	/**
	 * Shorcut for http head. Exceptions are converted to runtime
	 * exceptions.
	 * @param ws  web client to use
	 * @param url url to get 
	 * @return    web response for the given url
	 */
	public static WSResponse head(WSClient ws, String url) { // throws InterruptedException,ExecutionException {
		try {
			CompletionStage<WSResponse> completionStage = ws.url(url).head();
			WSResponse response = completionStage.toCompletableFuture().get();	
			return response;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
