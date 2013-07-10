package models.laboratory.experiment.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import play.data.validation.ValidationError;
import fr.cea.ig.MongoDBDAO;

public class OneToOneContainer extends AtomicTransfertMethod{

	public ContainerUsed inputContainerUsed;
	public ContainerUsed outputContainerUsed;

	
	@Override
	public void createOutputContainerUsed(Map<String,PropertyDefinition> propertyDefinitions, Map<String,PropertyValue> propertyValues) {
		
		Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.containerCode);
		Container outputContainer = new Container();
		if(outputContainer.contents==null){
			container.contents=new ArrayList<Content>();
		}
		
		outputContainer.contents.addAll(container.contents);
		//copy properties where level match with content in all content
		for(Content content:outputContainer.contents){
			InstanceHelpers.copyPropertyValueFromLevel(propertyDefinitions, "content", propertyValues, content.properties);
		}
		
		//copy value in properties container
		//TODO

	}
	
	@Override
	public void validate(Map<String, List<ValidationError>> errors) {
		inputContainerUsed.validate(errors);
		outputContainerUsed.validate(errors);
	}
	
	public List<ContainerUsed> getInputContainers(){
		List<ContainerUsed> cu = new ArrayList<ContainerUsed>();
		cu.add(inputContainerUsed);
		return cu;
	}
}
