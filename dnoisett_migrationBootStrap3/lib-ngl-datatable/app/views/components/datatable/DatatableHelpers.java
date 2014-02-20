package views.components.datatable;

import static views.components.datatable.DatatableConfig.DEFAULT_NB_ELEMENT;
import static views.components.datatable.DatatableConfig.DEFAULT_ORDER_SENSE;
import static views.components.datatable.DatatableConfig.DEFAULT_PAGE_NUMBER;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.data.Form;
import play.data.Form.Field;
import play.i18n.Messages;
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
	
	public static DatatableColumn getColumn(String property, String header, Boolean order, Boolean edit, Boolean hide, Boolean choiceInList) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header = Messages.get(header);
		column1.order=order;
		column1.edit=edit;
		column1.hide=hide;
		column1.choiceInList = choiceInList;
		
		return column1;
	}
	
	public static DatatableColumn getColumn(String property, String header, Boolean order, Boolean edit, Boolean hide, Boolean choiceInList,String type,Map<Integer,String> headers) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header = Messages.get(header);
		column1.order=order;
		column1.edit=edit;
		column1.hide=hide;
		column1.choiceInList = choiceInList;
		column1.extraHeaders = headers;
		return column1;
	}
	
	public static DatatableColumn getColumn(String property, String header, Boolean order, Boolean edit, Boolean hide, Boolean choiceInList,Map<Integer,String> headers) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header = Messages.get(header);
		column1.order=order;
		column1.edit=edit;
		column1.hide=hide;
		column1.choiceInList = choiceInList;
		column1.extraHeaders = headers;
		return column1;
	}
	
	public static DatatableColumn getDateColumn(String property, String header, Boolean order, Boolean edit, Boolean hide) {
		DatatableColumn column1 = new DatatableColumn();
		column1.property = property;		
		column1.header =  Messages.get(header);
		column1.type = "Date";
		column1.order=order;
		column1.edit=edit;
		column1.hide=hide;
		
		return column1;
	}
	
	@Deprecated()
	public static Integer getNumberRecordsPerPage(Form form){
		Field field = form.field("numberRecordsPerPage");
		String value = field.valueOr(DEFAULT_NB_ELEMENT.toString());
		return Integer.valueOf(value);
	}
	@Deprecated()
	public static Integer getPageNumber(Form form){
		Field field = form.field("pageNumber");
		String value = field.valueOr(DEFAULT_PAGE_NUMBER.toString());
		return Integer.valueOf(value);
	}
	@Deprecated()
	public static String getOrderBy(Form form){
		Field field = form.field("orderBy");
		String value = field.valueOr("");
		return value;
	}
	@Deprecated()
	public static Integer getOrderSense(Form form){
		Field field = form.field("orderSense");
		String value = field.valueOr(DEFAULT_ORDER_SENSE.toString());
		return Integer.valueOf(value);
	}
}
