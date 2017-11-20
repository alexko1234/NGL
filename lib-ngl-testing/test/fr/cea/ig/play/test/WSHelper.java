package fr.cea.ig.play.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CompletionStage;

import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

/**
 * WSClient shortcuts.
 * 
 * @author vrd
 *
 */
public class WSHelper {
	
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
	
	/**
	 * Short for http put with some payload.
	 * @param ws      web client to use
	 * @param url     url to put to
	 * @param payload payload to send along the put request
	 * @param status  expected http status
	 * @return        web response
	 */
	public static WSResponse put(WSClient ws, String url, String payload, int status) {
		WSResponse r = put(ws,url,payload);
		assertEquals(url, status, r.getStatus());
		return r;
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
		WSResponse r = post(ws,url,payload);
		assertEquals(url, status, r.getStatus());
		return r;
	}

}
