package views.components.datatable;

import java.util.Date;

import play.i18n.Messages;
import play.data.Form;
import play.data.Form.Field;
import static views.components.datatable.DatatableConfig.*;
public class DatatableHelpers {
	public static DatatableColumn getColumn(String property, String header) {
		return getColumn(property, header, false, false, false);
	}
	
	public static DatatableColumn getDateColumn(String property, String header) {
		return getDateColumn(property, header, false, false, false);
	}
	
	public static DatatableColumn getColumn(String property, String header, Boolean order, Boolean edit, Boolean hide) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header = Messages.get(header);
		column1.order=order;
		column1.edit=edit;
		column1.hide=hide;
		
		return column1;
	}
	
	public static DatatableColumn getDateColumn(String property, String header, Boolean order, Boolean edit, Boolean hide) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header =  Messages.get(header);
		column1.type = Date.class;
		column1.order=order;
		column1.edit=edit;
		column1.hide=hide;
		
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
