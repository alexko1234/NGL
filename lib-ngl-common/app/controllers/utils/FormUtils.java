package controllers.utils;

import play.data.Form;
import views.components.datatable.DatatableHelpers;
import fr.cea.ig.MongoDBResult.Sort;
@Deprecated
public class FormUtils {
	/**
	 * Return the order sense in mongo db 
	 * @param filledForm
	 * @return
	 */
	@Deprecated
	public static Sort getMongoDBOrderSense(Form filledForm) {
		if(Integer.valueOf(-1).equals(DatatableHelpers.getOrderSense(filledForm))){
			return Sort.DESC;
		}else{
			return Sort.ASC;
		}
	}
	
}
