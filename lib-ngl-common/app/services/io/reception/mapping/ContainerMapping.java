package services.io.reception.mapping;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.mongojack.DBQuery;

import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.reception.instance.AbstractFieldConfiguration;
import models.laboratory.reception.instance.ReceptionConfiguration.Action;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import play.Logger;
import services.io.reception.Mapping;
import validation.ContextValidation;



public class ContainerMapping extends Mapping<Container> {

	public ContainerMapping(Map<String, Map<String, DBObject>> objects, Map<String, ? extends AbstractFieldConfiguration> configuration, Action action, ContextValidation contextValidation) {
		super(objects, configuration, action, InstanceConstants.CONTAINER_COLL_NAME, Container.class, Mapping.Keys.container, contextValidation);
	}

	/**
	 * Try to find container with code obtain from support (best practice) but if not found try default method with code field.
	 */
	protected Container get(Container object, Map<Integer, String> rowMap, boolean errorIsNotFound) {
		try {
			AbstractFieldConfiguration supportConfig = configuration.get("support");
			if(null != supportConfig){
				supportConfig.populateField(object.getClass().getField("support"), object, rowMap, contextValidation, action);
				if(null != object.support){
					String code = computeCode(object);
					if(null != code){
						object.code = code;
						object = MongoDBDAO.findByCode(collectionName, type, object.code);	
						if(errorIsNotFound && null == object){
							contextValidation.addErrors("Error", "not found "+type.getSimpleName()+" for code "+code);
						}
					}else{
						object = super.get(object, rowMap, errorIsNotFound);
					}
				}else if(supportConfig.required){
					contextValidation.addErrors("Error", "not found "+type.getSimpleName()+" support !!!");
				}else{
					object = super.get(object, rowMap, errorIsNotFound);
				}
			}else{
				object = super.get(object, rowMap, errorIsNotFound);
			}
		} catch (Exception e) {
			Logger.error("Error", e.getMessage(), e);
			contextValidation.addErrors("Error", e.getMessage());
			throw new RuntimeException(e);
		}		
		return object;
	}
	
	protected void update(Container container) {
		//TODO update categoryCode if not a code but a label.
		if(Action.update.equals(action)){
			container.traceInformation.setTraceInformation(contextValidation.getUser());
		}else{
			container.code = computeCode(container);
			container.traceInformation = new TraceInformation(contextValidation.getUser());
		}
			
	}
	/**
	 * Compute the container code if possible
	 * @param container
	 * @return
	 */
	private String computeCode(Container container) {
		String code = null;
		if(container.support != null && 
				container.support.code != null && container.support.line != null && container.support.column != null){
			ContainerSupportCategory csc = ContainerSupportCategory.find.findByCode(container.support.categoryCode);
			if(csc.nbLine == 1 && csc.nbColumn == 1){
				code= container.support.code;
			}else if(csc.nbLine > 1 && csc.nbColumn == 1){
				container.support.line = container.support.line.toUpperCase();
				code=container.support.code+"_"+container.support.line;
	
			}else if(csc.nbLine > 1 && csc.nbColumn > 1){
				container.support.line = container.support.line.toUpperCase();
				container.support.column = container.support.column.toUpperCase();
				
				code=container.support.code+"_"+container.support.line+container.support.column;
			}
		}
		return code;
	}
	
	@Override
	public void consolidate(Container c) {
		
		if(null == c.state || null == c.state.code){
			c.state = new State("IS", contextValidation.getUser());
		} else if(c.state.user == null){
			c.state.user = contextValidation.getUser();
		}
		
		if(c.categoryCode == null && c.support.categoryCode != null){
			c.categoryCode = ContainerCategory.find.findByContainerSupportCategoryCode(c.support.categoryCode).code;
		}
		
		c.projectCodes = new TreeSet<String>();
		c.sampleCodes = new TreeSet<String>();
		
		double percentage = (new BigDecimal(100.00/c.contents.size()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		c.contents.forEach(content -> {
			Sample sample = getSample(content.sampleCode);
			content.referenceCollab = sample.referenceCollab;
			content.taxonCode = sample.taxonCode;
			content.ncbiScientificName = sample.ncbiScientificName;
			content.sampleCategoryCode = sample.categoryCode;
			content.sampleTypeCode = sample.typeCode;
			if(null == content.percentage)content.percentage = percentage;
			content.properties = computeProperties(content.properties, sample, c.code);
			if(content.projectCode == null && sample.projectCodes.size() == 1){
				content.projectCode = sample.projectCodes.iterator().next();
			}
			c.projectCodes.add(content.projectCode);
			c.sampleCodes.add(content.sampleCode);
		});				
	}

	
	private Map<String, PropertyValue> computeProperties(
			Map<String, PropertyValue> properties, Sample sample, String containerCode) {
		setPropertiesFromSample(properties, sample);
		if(sample.life != null && sample.life.from != null && sample.life.from.sampleCode != null && sample.life.from.projectCode != null){
			PropertySingleValue fromSampleTypeCode = new PropertySingleValue(sample.life.from.sampleTypeCode);
			properties.put("fromSampleTypeCode", fromSampleTypeCode);
			PropertySingleValue fromSampleCode = new PropertySingleValue(sample.life.from.sampleCode);
			properties.put("fromSampleCode", fromSampleCode);
			PropertySingleValue fromProjectCode = new PropertySingleValue(sample.life.from.projectCode);
			properties.put("fromProjectCode", fromProjectCode);
			
			Sample parentSample = MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, 
					DBQuery.is("code",sample.life.from.sampleCode).in("projectCodes", sample.life.from.projectCode));
			setPropertiesFromSample(properties, parentSample);
			
		}
		
		//HACK to have the original container on the readset
		if(Action.save.equals(action) && !properties.containsKey("sampleAliquoteCode")){
			PropertySingleValue psv = new PropertySingleValue(containerCode);
			properties.put("sampleAliquoteCode", psv);
		}
		
		return properties;
	}

	private void setPropertiesFromSample(Map<String, PropertyValue> properties, Sample sample) {
		SampleType sampleType = SampleType.find.findByCode(sample.typeCode);
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,properties);
		}
		
		ImportType importType = ImportType.find.findByCode(sample.importTypeCode);
		if(importType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,properties);
		}
	}

	
}
