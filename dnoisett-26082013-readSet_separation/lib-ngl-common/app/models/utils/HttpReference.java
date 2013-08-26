package models.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.libs.WS;
import play.libs.WS.Response;


// TODO
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
		// execute GET external URL
		Response reponse = WS.url(code).get().get();

		//if (reponse.getStatus()!=play.mvc.Http.Status.OK) throw new Exception  
				
		try {
			return new  ObjectMapper().readValue(reponse.getBody(),className);
		} catch (JsonParseException e) {
			//TODO
		} catch (JsonMappingException e) {
			//TODO
		} catch (IOException e) {
			//TODO
		}
		return null;
	}


}
