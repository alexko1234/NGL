package containers;

public class DatatableBatchResponseElementForTest<T> {
	
	public Integer status;
	public T data;
	public Integer index;
	
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
