package models.utils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ch.qos.logback.core.util.TimeUtil;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.libs.ws.WS;
import play.libs.ws.WSResponse;

// TODO: cleanup
public class HttpReference<T> implements IFetch<T> {

	@JsonIgnore
	private Class<T> className;

	//String url 
	public String code;

	public HttpReference(Class<T> className) {
		this.className = className;
	}

	public HttpReference(Class<T> className, String code) {
		this.className = className;
		this.code = code;
	}

	@Override
	public T getObject() {
		try {
		// execute GET external URL
		// WSResponse reponse = WS.url(code).get().get(10, TimeUnit.SECONDS);
		WSResponse reponse = WS.url(code).get().toCompletableFuture().get(10, TimeUnit.SECONDS);
		//if (reponse.getStatus()!=play.mvc.Http.Status.OK) throw new Exception  
				
			// TODO: Should probably be better to use the play object mapper
			return new  ObjectMapper().readValue(reponse.getBody(),className);
			// play.libs.Json.mapper().readValue(reponse.getBody(),className);
		} catch (JsonParseException e) {
			//TODO
		} catch (JsonMappingException e) {
			//TODO
		} catch (IOException e) {
			//TODO
		} catch (TimeoutException e) {
			// TODO
		} catch (ExecutionException e) {
			// TODO
		} catch (InterruptedException e) {
			// TODO
		} 
		return null;
	}


}
