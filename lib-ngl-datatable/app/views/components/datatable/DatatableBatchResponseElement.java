package views.components.datatable;

//import play.Logger;

public class DatatableBatchResponseElement {
	
	public Integer status;
	public Object data;
	public Integer index;	
	
	public DatatableBatchResponseElement(Integer status, Object data, Integer index) {
		super();
		this.status = status;
		this.data = data;
		this.index = index;
//		Logger.debug("DatatableBatchResponseElement 1 : " + this.status + ", " + this.index + ", " + this.data.toString());
	}
	
	public DatatableBatchResponseElement(Integer status, Integer index) {
		super();
		this.status = status;
		this.index = index;
//		Logger.debug("DatatableBatchResponseElement 2");
	}

}
