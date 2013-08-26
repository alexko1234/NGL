package controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.*;

import fr.cea.ig.MongoDBResult.Sort;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableHelpers;

public abstract class CommonController extends Controller{
	/**
	 * Return the order sense in mongo db 
	 * @param filledForm
	 * @return
	 */
	protected static Sort getMongoDBOrderSense(Form filledForm) {
		if(Integer.valueOf(-1).equals(DatatableHelpers.getOrderSense(filledForm))){
			return Sort.DESC;
		}else{
			return Sort.ASC;
		}
	}
	/**
	 * Fill a form in json mode
	 * @param form
	 * @param clazz
	 * @return
	 */
	protected static <T> Form<T> getFilledForm(Form<T> form, Class<T> clazz) {
		Logger.info("Request: "+request().body().toString());
		JsonNode json = request().body().asJson();
		T input = Json.fromJson(json, clazz);
		Form<T> filledForm = form.fill(input); 
		return filledForm;
	}
}
