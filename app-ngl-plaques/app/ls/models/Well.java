package ls.models;

import play.data.validation.Constraints.Required;

public class Well {
	
	@Required
	public Integer code;
	@Required
	public String name;
	@Required
	public String x;
	@Required
	public String y;
	@Override
	public String toString() {
		return "Well [code=" + code + ", name=" + name + ", x=" + x + ", y="
				+ y + "]";
	}

}
