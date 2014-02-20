package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import controllers.CommonController;

import validation.ContextValidation;


import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;
import net.vz.mongodb.jackson.DBQuery;
import fr.cea.ig.MongoDBDAO;

public class ManytoOneContainer extends AtomicTransfertMethod{

	public int inputNumber;

	public List<ContainerUsed> inputContainerUseds;
	public ContainerUsed outputContainerUsed;

	public ManytoOneContainer(){
		super();
	}
	
	@Override
	public List<Container> createOutputContainerUsed(Experiment experiment) {


		List<String> containerCodes= new ArrayList<String>();
		for(int i=0;i<inputContainerUseds.size();i++){
			containerCodes.add(inputContainerUseds.get(i).containerCode);
		}

		List<Container> inputContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("code", containerCodes)).toList();

		List<Container> containers = new ArrayList<Container>();
		Container container = new Container();
		
		container.traceInformation.creationDate = new Date();
		container.traceInformation.createUser = CommonController.getCurrentUser();
		
		//container.support.categoryCode
		container.stateCode="N";
		

		for(int i=0;i<inputContainers.size();i++){
			ContainerHelper.addContent(inputContainers.get(i), container,experiment);
		}
		
		ContainerHelper.generateCode(container);
		//TODO copy properties
		ContainerHelper.addContainerSupport(container, experiment);
		
		outputContainerUsed=new ContainerUsed(container);
		containers.add(container);
		return containers;
	}

	@Override
	public void validate(ContextValidation contextValidation) {
		outputContainerUsed.validate(contextValidation);
		for(ContainerUsed containerUsed:inputContainerUseds){
			containerUsed.validate(contextValidation);
		}
	}
	
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){
		return inputContainerUseds;
	}
	
	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(outputContainerUsed);
		return cu;
	}

}
