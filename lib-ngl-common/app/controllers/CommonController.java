package controllers;

import fr.cea.ig.MongoDBResult.Sort;
import play.data.Form;
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
}
