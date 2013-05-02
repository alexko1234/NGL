package ls.models;


public class Well {
	
	public Integer code;
	
	public String name;
	
	public String x;
	
	public String y;
	
	public Integer typeCode;
	
	public String typeName;

	@Override
	public String toString() {
		return "Well [code=" + code + ", name=" + name + ", x=" + x + ", y="
				+ y + ", typeCode=" + typeCode + ", typeName=" + typeName + "]";
	}
	
	

}
