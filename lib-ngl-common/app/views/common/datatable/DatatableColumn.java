package views.common.datatable;

import java.util.Date;

public class DatatableColumn {
	
	public String label;
	public String id;
	public Boolean editable = Boolean.FALSE;
	public Boolean hidding = Boolean.FALSE;
	public Boolean ordering = Boolean.FALSE;
	public Class<?> type = String.class;
	
	public Boolean isDate(){
		return type.equals(Date.class);
	}
}
