package models.utils.instance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ContainerHelper {


	public static void addContent(Container container, Sample sample) throws DAOException{
		addContent(container,sample,null);
	}

	public static void addContent(Container container,Sample sample,Map<String,PropertyValue> properties) throws DAOException{

		Content sampleUsed =new Content(sample.code, sample.typeCode, sample.categoryCode);

		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);

		if(importType !=null){

			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,sampleUsed.properties);
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,sampleUsed.properties);
		}

		if(properties!=null)
			sampleUsed.properties.putAll(properties);

		container.contents.add(sampleUsed);

		container.projectCodes=InstanceHelpers.addCodesList(sample.projectCodes,container.projectCodes);

		container.sampleCodes=InstanceHelpers.addCode(sample.code,container.sampleCodes);

	}

	public static void addContent(Container outputContainer, List<ContainerUsed> inputContainerUseds , Experiment experiment, Map<String,PropertyValue> properties) throws DAOException {
		
		for(ContainerUsed inputContainerUsed:inputContainerUseds){

			Container inputContainer=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.code);

			List<Content> contents = new ArrayList<Content>(inputContainer.contents);
			calculPercentageContent(contents,inputContainerUsed.percentage);
			outputContainer.contents.addAll(contents);

			outputContainer.projectCodes=InstanceHelpers.addCodesList(inputContainer.projectCodes,outputContainer.projectCodes);
			outputContainer.sampleCodes=InstanceHelpers.addCodesList(inputContainer.sampleCodes,outputContainer.sampleCodes);
			outputContainer.categoryCode = ContainerSupportCategory.find.findByCode(experiment.instrument.outContainerSupportCategoryCode).containerCategory.code;
			outputContainer.inputProcessCodes=InstanceHelpers.addCodesList(inputContainer.inputProcessCodes,outputContainer.inputProcessCodes);
			outputContainer.processTypeCode=inputContainer.processTypeCode;

			if(experiment.categoryCode.equals("transformation")){
				outputContainer.fromExperimentTypeCodes=InstanceHelpers.addCode(experiment.typeCode ,outputContainer.fromExperimentTypeCodes);
			}else{
				outputContainer.fromExperimentTypeCodes=InstanceHelpers.addCodesList(inputContainer.fromExperimentTypeCodes,outputContainer.fromExperimentTypeCodes);
			}

		}		
		
		//Add properties in Container
		ExperimentType experimentType =BusinessValidationHelper.validateExistDescriptionCode(null, experiment.typeCode, "typeCode", ExperimentType.find,true);
		if(experimentType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentType.getPropertyDefinitionByLevel(Level.CODE.Container), properties,outputContainer.properties);
		}

		InstrumentUsedType instrumentUsedType=BusinessValidationHelper.validateExistDescriptionCode(null, experiment.instrument.typeCode, "typeCode", InstrumentUsedType.find,true);
		if(instrumentUsedType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(instrumentUsedType.getPropertyDefinitionByLevel(Level.CODE.Container), properties,outputContainer.properties);
		}
		
		//Add properties in Content List of Container
		for(Content content :outputContainer.contents){
			if(experimentType !=null){
				InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentType.getPropertyDefinitionByLevel(Level.CODE.Content), properties,content.properties);
			}

			if(instrumentUsedType !=null){
				InstanceHelpers.copyPropertyValueFromPropertiesDefinition(instrumentUsedType.getPropertyDefinitionByLevel(Level.CODE.Content), properties,content.properties);
			}
		}
		
	}

	public static void calculPercentageContent(List<Content> contents, Float percentage){
		if(percentage!=null){
			for(Content cc:contents){

				cc.percentage= cc.percentage == null ? percentage : cc.percentage*percentage/100;
			}
		}
	}




	private static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyyMMddHHmmssSS");
	}

	public static String generateContainerCode(String categoryCode){
		return (categoryCode+"-"+getSimpleDateFormat().format(new Date())).toUpperCase();
	}


	public static void createSupportFromContainers(List<Container> containers, Map<String, PropertyValue<String>> mapSupportsCodeSeq, ContextValidation contextValidation){

		HashMap<String,ContainerSupport> mapSupports = new HashMap<String,ContainerSupport>();

		for (Container container : containers) {
			if (container.support != null) {
				ContainerSupport newSupport = null;
				if (mapSupportsCodeSeq != null) {
					newSupport = ContainerSupportHelper.createSupport(container.support.code, mapSupportsCodeSeq.get(container.support.code), container.support.categoryCode,"ngl");
				}
				else {
					newSupport = ContainerSupportHelper.createSupport(container.support.code, null, container.support.categoryCode,"ngl");
				}
				newSupport.projectCodes = new  ArrayList<String>(container.projectCodes);
				newSupport.sampleCodes = new  ArrayList<String>(container.sampleCodes);
				if(null != container.fromExperimentTypeCodes){//TODO Must be manage for CNG
					newSupport.fromExperimentTypeCodes = new  ArrayList<String>(container.fromExperimentTypeCodes);
				}
				if (!mapSupports.containsKey(newSupport.code)) {
					mapSupports.put(newSupport.code, newSupport);
				}
				else {
					ContainerSupport oldSupport = (ContainerSupport) mapSupports.get(newSupport.code);
					InstanceHelpers.addCodesList(newSupport.projectCodes, oldSupport.projectCodes); 
					InstanceHelpers.addCodesList(newSupport.sampleCodes, oldSupport.sampleCodes);
					if(null != newSupport.fromExperimentTypeCodes && null != oldSupport.fromExperimentTypeCodes){//TODO Must be manage for CNG
						InstanceHelpers.addCodesList(newSupport.fromExperimentTypeCodes, oldSupport.fromExperimentTypeCodes);
					}
				}

			}
		}

		InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME, new ArrayList<ContainerSupport>(mapSupports.values()), contextValidation, true);

	}


	public static void updateSupportFromUpdatedContainers(List<Container> updatedContainers, Map<String, PropertyValue<String>> mapSupportsCodeSeq, ContextValidation contextValidation){

		HashMap<String,ContainerSupport> mapSupports = new HashMap<String,ContainerSupport>();

		for (Container container : updatedContainers) {
			if (container.support != null) {
				ContainerSupport newSupport = ContainerSupportHelper.createSupport(container.support.code, mapSupportsCodeSeq.get(container.support.code), container.support.categoryCode,"ngl");
				newSupport.projectCodes = new  ArrayList<String>(container.projectCodes);
				newSupport.sampleCodes = new  ArrayList<String>(container.sampleCodes);							
				if (!mapSupports.containsKey(newSupport.code)) {
					mapSupports.put(newSupport.code, newSupport);
				}
				else {
					ContainerSupport oldSupport = (ContainerSupport) mapSupports.get(newSupport.code);
					InstanceHelpers.addCodesList(newSupport.projectCodes, oldSupport.projectCodes); 
					InstanceHelpers.addCodesList(newSupport.sampleCodes, oldSupport.sampleCodes);
				}

			}
		}

		for (Map.Entry<String,ContainerSupport> e : mapSupports.entrySet()) {
			ContainerSupport dbCs = MongoDBDAO.findByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, e.getKey());

			ContainerSupport updatedCs = e.getValue();

			updatedCs.traceInformation = dbCs.traceInformation;
			updatedCs.traceInformation.modifyDate = new Date();
			updatedCs.traceInformation.modifyUser = "ngl";

			if (!( dbCs.projectCodes.containsAll(updatedCs.projectCodes) && dbCs.sampleCodes.containsAll(updatedCs.sampleCodes) 
					&& updatedCs.projectCodes.containsAll(dbCs.projectCodes) && updatedCs.sampleCodes.containsAll(dbCs.sampleCodes)) ) {

				MongoDBDAO.deleteByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, e.getKey());

				InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME, updatedCs, contextValidation, true);
			}
		}


	}

	public static List<Content> contentFromSampleCode(List<Content> contents,
			String sampleCode) {
		List<Content> contentsFind=new ArrayList<Content>();
		for(Content content:contents){
			if(content.sampleCode.equals(sampleCode)){
				contentsFind.add(content);
			}
		}
		return contentsFind;
	}

	public static void save(Container outputContainer,
			ContextValidation contextValidation) {
		contextValidation.addKeyToRootKeyName("container["+outputContainer.code+"]");
		contextValidation.setCreationMode();
		InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME,outputContainer, contextValidation);
		contextValidation.removeKeyFromRootKeyName("container["+outputContainer.code+"]");
	}

}
