package supports;

import controllers.supports.api.SupportsSearchForm;
import models.laboratory.container.instance.ContainerSupport;

public class SupportTestHelper {

	public static ContainerSupport getFakeSupport(){
		ContainerSupport cs = new ContainerSupport();
		return cs;
	}
	
	public static ContainerSupport getFakeSupportWithCode(String code){
		ContainerSupport cs = new ContainerSupport();
		cs.code = code;
		return cs;
	}
	
	public static SupportsSearchForm getFakeSupportsSearchForm(){
		SupportsSearchForm ssf = new SupportsSearchForm();
		return ssf;
		
	}
	
	
}
