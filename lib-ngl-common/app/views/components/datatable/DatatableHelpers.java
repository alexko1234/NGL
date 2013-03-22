package views.components.datatable;

import java.util.Date;

import play.data.Form;
import play.data.Form.Field;
import static views.components.datatable.DatatableConfig.*;
public class DatatableHelpers {
	
	
	public static DatatableColumn getColumn(String property, String header) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header = header;
		return column1;
	}
	
	public static DatatableColumn getDateColumn(String property, String header) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header = header;
		column1.type = Date.class;
		return column1;
	}
	
	public static Integer getNumberRecordsPerPage(Form form){
		Field field = form.field("numberRecordsPerPage");
		String value = field.valueOr(DEFAULT_NB_ELEMENT);
		return Integer.valueOf(value);
	}
	
	public static Integer getPageNumber(Form form){
		Field field = form.field("pageNumber");
		String value = field.valueOr(DEFAULT_PAGE_NUMBER);
		return Integer.valueOf(value);
	}
	
	public static String getOrderBy(Form form){
		Field field = form.field("orderBy");
		String value = field.valueOr("");
		return value;
	}

	public static Integer getOrderSense(Form form){
		Field field = form.field("orderSense");
		String value = field.valueOr(DEFAULT_ORDER_SENSE);
		return Integer.valueOf(value);
	}
}
