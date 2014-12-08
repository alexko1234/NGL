package containers;

import java.util.List;

import views.components.datatable.DatatableResponse;

public class DatatableResponseForTest<T> extends DatatableResponse<T> {

	public DatatableResponseForTest(){
		
	}
	
	public DatatableResponseForTest(List<T> data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	public DatatableResponseForTest(List<T> data, Integer recordsNumber) {
		super(data, recordsNumber);
		this.data = data;
		this.recordsNumber = recordsNumber;
	}

}
