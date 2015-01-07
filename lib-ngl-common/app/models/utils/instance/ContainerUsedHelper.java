package models.utils.instance;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.experiment.instance.ContainerUsed;

public class ContainerUsedHelper {

	public static Object getContainerCodes(List<ContainerUsed> inputContainerUseds) {
	
		List<String> containerCodes=new ArrayList<String>();
		
		for(ContainerUsed containerUsed:inputContainerUseds){
			containerCodes.add(containerUsed.code);
		}
		return containerCodes;
	}

	public static Object getContainerSupportCodes(List<ContainerUsed> inputContainerUseds) {
		List<String> containerSupportCodes=new ArrayList<String>();
		for(ContainerUsed containerUsed:inputContainerUseds){
			containerSupportCodes.add(containerUsed.locationOnContainerSupport.code);
		}
		return containerSupportCodes;
	}

}
