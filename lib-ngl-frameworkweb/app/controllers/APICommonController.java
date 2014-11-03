package controllers;

import static play.data.Form.form;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;

import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.With;
import controllers.history.UserHistory;


@With({fr.cea.ig.authentication.Authenticate.class, UserHistory.class})
public abstract class APICommonController<T> extends Controller{

	protected final DynamicForm listForm = new DynamicForm();
	protected Class<T> type;
	protected final Form<T> mainForm = form(type);
	

	public APICommonController(Class<T> type) {
		super();
		this.type = type;
	}

	/**
	 * Filled the main form
	 * @return
	 */
	protected Form<T> getMainFilledForm(){
		return getFilledForm(mainForm, type); 
	}
	
	/**
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected <P> Form<P> getFilledForm(Form<P> form, Class<P> clazz) {		
		JsonNode json = request().body().asJson();
		P input = Json.fromJson(json, clazz);
		Form<P> filledForm = form.fill(input); 
		return filledForm;
	}
	
	protected <P> List<Form<P>> getFilledFormList(Form<P> form, Class<P> clazz) {		
		JsonNode json = request().body().asJson();
		List<Form<P>> results = new ArrayList<Form<P>>();
		Iterator<JsonNode> iterator = json.elements();
		
		while(iterator.hasNext()){
			JsonNode jsonChild = iterator.next();
			P input = Json.fromJson(jsonChild, clazz);
			Form<P> filledForm = form.fill(input);
			results.add(filledForm);
		}
		
		return results;
	}

	/**
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected <T> Form<T> filledFormQueryString(Form<T> form, Class<T> clazz) {		
		Map<String, String[]> queryString =request().queryString();
		Map<String, Object> transformMap = new HashMap<String, Object>();
		for(String key :queryString.keySet()){			
			try {
				if(isNotEmpty(queryString.get(key))){				
					Field field = clazz.getField(key);
					Class type = field.getType();
					if(type.isArray() || Collection.class.isAssignableFrom(type)){
						transformMap.put(key, queryString.get(key));						
					}else{
						transformMap.put(key, queryString.get(key)[0]);						
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 

		}

		JsonNode json = Json.toJson(transformMap);
		T input = Json.fromJson(json, clazz);
		Form<T> filledForm = form.fill(input); 
		return filledForm;
	}


	private boolean isNotEmpty(String[] strings) {
		if(null == strings)return false;
		if(strings.length == 0)return false;
		if(strings.length == 1 && StringUtils.isBlank(strings[0]))return false;
		return true;
	}

	protected String getCurrentUser(){
		return Context.current().request().username();
	}
	
	
}
