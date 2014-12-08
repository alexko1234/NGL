package containers;

import views.components.datatable.DatatableBatchResponseElement;

public class DatatableBatchResponseElementForTest<T> extends DatatableBatchResponseElement {
	
	
	public DatatableBatchResponseElementForTest(){
		
	}
	
	
	public DatatableBatchResponseElementForTest(Integer status, T data, Integer index) {
		super();
		this.status = status;
		this.data = data;
		this.index = index;
	}
	
	public DatatableBatchResponseElementForTest(Integer status, Integer index) {
		super();
		this.status = status;
		this.index = index;
	}
	

}
