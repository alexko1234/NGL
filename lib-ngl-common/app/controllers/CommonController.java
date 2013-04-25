package controllers;

import org.codehaus.jackson.JsonNode;

import fr.cea.ig.MongoDBResult.Sort;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import views.components.datatable.DatatableHelpers;

public class CommonController extends Controller{
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
		JsonNode json = request().body().asJson();
		//T input = Json.fromJson(json, clazz);
		Form<T> filledForm = form.bind(json); 
		return filledForm;
	}
}
