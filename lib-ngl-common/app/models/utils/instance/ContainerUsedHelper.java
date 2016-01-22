package models.utils.instance;

import java.util.ArrayList;
import java.util.List;




import models.laboratory.experiment.instance.AbstractContainerUsed;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.InputContainerUsed;

public class ContainerUsedHelper {

	public static List<String> getContainerCodes(List<InputContainerUsed> inputContainerUseds) {
	
		List<String> containerCodes=new ArrayList<String>();
		
		for(InputContainerUsed containerUsed:inputContainerUseds){
			containerCodes.add(containerUsed.code);
		}
		return containerCodes;
	}

	public static List<String> getContainerSupportCodes(List<? extends AbstractContainerUsed> inputContainerUseds) {
		List<String> containerSupportCodes=new ArrayList<String>();
		for(AbstractContainerUsed containerUsed:inputContainerUseds){
			containerSupportCodes.add(containerUsed.locationOnContainerSupport.code);
		}
		return containerSupportCodes;
	}
	
	public static List<String> getContainerSupportCodesOld(List<ContainerUsed> inputContainerUseds) {
		List<String> containerSupportCodes=new ArrayList<String>();
		for(ContainerUsed containerUsed:inputContainerUseds){
			containerSupportCodes.add(containerUsed.locationOnContainerSupport.code);
		}
		return containerSupportCodes;
	}

}
