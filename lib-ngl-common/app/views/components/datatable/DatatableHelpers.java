package views.components.datatable;

import java.util.Date;

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

}
