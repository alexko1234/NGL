package services.io.reception.mapping;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import fr.cea.ig.DBObject;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import services.io.reception.Mapping;
import validation.ContextValidation;



public class ContainerMapping extends Mapping<Container> {

	public ContainerMapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action, ContextValidation contextValidation) {
		super(objects, configuration, action, InstanceConstants.CONTAINER_COLL_NAME, Container.class, contextValidation);
	}

	protected void update(Container container) {
		//TODO update categoryCode if not a code but a label.
		if(Action.update.equals(action)){
			container.traceInformation.setTraceInformation(contextValidation.getUser());
		}else{
			container.traceInformation = new TraceInformation(contextValidation.getUser());
		}
		//TODO better management for state with a fieldConfiguration
		if(null == container.state){
			container.state = new State("IS", contextValidation.getUser());
		}		
	}

	@Override
	public void consolidate(Container c) {
		ContainerSupport support = getContainerSupport(c.support.code);
		if(c.categoryCode == null){
			c.categoryCode = ContainerCategory.find.findByContainerSupportCategoryCode(support.code).code;
		}
		
		c.projectCodes = new TreeSet<String>();
		c.sampleCodes = new TreeSet<String>();
		
		c.support.categoryCode = support.categoryCode;
		c.support.storageCode = support.storageCode;
		double percentage = (new BigDecimal(100.00/c.contents.size()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		c.contents.forEach(content -> {
			Sample sample = getSample(content.sampleCode);
			content.referenceCollab = sample.referenceCollab;
			content.sampleCategoryCode = sample.categoryCode;
			content.sampleTypeCode = sample.typeCode;
			content.percentage = percentage;
			content.properties = computeProperties(content.properties, sample);
			
			c.projectCodes.add(content.projectCode);
			c.sampleCodes.add(content.sampleCode);
		});				
	}

	private Map<String, PropertyValue> computeProperties(
			Map<String, PropertyValue> properties, Sample sample) {
		SampleType sampleType = SampleType.find.findByCode(sample.typeCode);
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,properties);
		}
		
		ImportType importType = ImportType.find.findByCode(sample.importTypeCode);
		if(importType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,properties);
		}
		
		return properties;
	}

	
}
