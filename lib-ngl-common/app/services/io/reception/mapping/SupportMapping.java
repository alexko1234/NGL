package services.io.reception.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.StorageHistory;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.utils.InstanceConstants;
import services.io.reception.Mapping;
import validation.ContextValidation;
import fr.cea.ig.DBObject;



public class SupportMapping extends Mapping<ContainerSupport> {

	public SupportMapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action, ContextValidation contextValidation) {
		super(objects, configuration, action, InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, contextValidation);
	}

	
	protected void update(ContainerSupport support) {
		//TODO update categoryCode if not a code but a label.
		if(Action.update.equals(action)){
			support.traceInformation.setTraceInformation(contextValidation.getUser());
		}else{
			support.traceInformation = new TraceInformation(contextValidation.getUser());
		}
		//TODO better management for state with a fieldConfiguration
		if(null == support.state){
			support.state = new State("IS", contextValidation.getUser());
		}		
		
		//historisation of storageCode
		if(configuration.containsKey("storageCode")){
			if(support.storages == null){
				support.storages = new ArrayList<StorageHistory>();
			}
			StorageHistory sh = new StorageHistory();
			sh.code = support.storageCode;
			sh.date = new Date();
			sh.user = contextValidation.getUser();
			sh.index = support.storages.size();
			support.storages.add(sh);
		}
	}


	@Override
	public void consolidate(ContainerSupport support) {
		List<Container> containers = getContainersForASupport(support);
		support.nbContainers = containers.size();
		support.nbContents = containers.stream().mapToInt(c -> c.contents.size()).sum();
		support.projectCodes = containers.stream().map(c -> c.projectCodes).flatMap(Set::stream).collect(Collectors.toSet());
		support.sampleCodes = containers.stream().map(c -> c.sampleCodes).flatMap(Set::stream).collect(Collectors.toSet());
		
	}


	private List<Container> getContainersForASupport(ContainerSupport containerSupport) {
		Map<String, DBObject> allContainers = objects.get("container");
		
		List<Container> selectedContainers = allContainers.values().stream()
			.map(c -> (Container)c)
			.filter(c -> containerSupport.code.equals(c.support.code))
			.collect(Collectors.toList());
		
		return selectedContainers;
	}

	
}
