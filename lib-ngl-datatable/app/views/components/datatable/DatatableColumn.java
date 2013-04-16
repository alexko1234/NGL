package views.components.datatable;

import java.util.Date;

public class DatatableColumn {
	
	public String header; //header of the column
	public String property; //property name in object
	public String id;   //id of the column
	public Boolean edit = Boolean.FALSE;
	public Boolean hide = Boolean.FALSE;
	public Boolean order = Boolean.FALSE;
	public Class<?> type = String.class;
	
	public Boolean isDate(){
		return type.equals(Date.class);
	}
}
