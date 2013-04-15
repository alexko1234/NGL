package views.components.datatable;

import java.util.List;

public class DatatableResponse {
	
	public List<? extends Object> data;
	public Integer recordsNumber;
	
	
	
	public DatatableResponse(List<? extends Object> data) {
		super();
		this.data = data;
		this.recordsNumber = data.size();
	}

	public DatatableResponse(List<? extends Object> data, Integer recordsNumber) {
		super();
		this.data = data;
		this.recordsNumber = recordsNumber;
	}

	
	

}
