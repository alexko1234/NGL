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
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;
import fr.cea.ig.MongoDBDAO;

public class OneToManyContainer extends AtomicTransfertMethod {

	public int outputNumber;
	public ContainerUsed inputContainerUsed;
	public List<ContainerUsed> outputContainerUseds;
	
	public OneToManyContainer(){
		super();
	}
	
	@Override
	public List<Container> createOutputContainerUsed(Experiment experiment) {
		
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.containerCode);
		
		List<Container> outputContainers  = new ArrayList<Container>();
		outputContainerUseds=new ArrayList<ContainerUsed>();
	
		for(int i=0;i<outputNumber;i++){
			
			Container outputContainer=new Container();

			
			ContainerHelper.addContent(container, outputContainer, experiment);
			
			ContainerHelper.generateCode(outputContainer);
			
			outputContainer.state = new State(); 
			outputContainer.state.code="N";
			outputContainer.state.user = CommonController.getCurrentUser();
			outputContainer.state.date = new Date();

			
			//TODO copy properties in ContainerOut and Container

			ContainerHelper.addContainerSupport(outputContainer, experiment);
				
			outputContainers.add(outputContainer);
			outputContainerUseds.add(new ContainerUsed(outputContainer));
		}
		
		return outputContainers;
	}


	@Override
	public void validate(ContextValidation contextValidation) {
		inputContainerUsed.validate(contextValidation);
		for(ContainerUsed containerUsed:outputContainerUseds){
			containerUsed.validate(contextValidation);
		}
	}
	@JsonIgnore
	public List<ContainerUsed> getInputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}
	@JsonIgnore
	public List<ContainerUsed> getOutputContainers(){
		return outputContainerUseds;
	}
}
