package views.common.datatable;

import java.util.Date;

public class DatatableHelpers {
	
	public static DatatableColumn getColumn(String id, String label) {
		DatatableColumn column1 = new DatatableColumn();
		column1.id = id;		
		column1.label = label;
		return column1;
	}
	
	public static DatatableColumn getDateColumn(String id, String label) {
		DatatableColumn column1 = new DatatableColumn();
		column1.id = id;		
		column1.label = label;
		column1.type = Date.class;
		return column1;
	}

}
