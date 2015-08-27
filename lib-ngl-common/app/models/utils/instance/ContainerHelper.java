package models.utils.instance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.sample.description.ImportType;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;

import validation.ContextValidation;
import validation.utils.BusinessValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class ContainerHelper {


	public static void addContent(Container container, Sample sample) throws DAOException{
		addContent(container,sample,null);
	}

	public static void addContent(Container container,Sample sample, Content content) throws DAOException{

		Content finalContent =new Content(sample.code, sample.typeCode, sample.categoryCode);
		finalContent.projectCode = content.projectCode;
		finalContent.percentage=content.percentage;
		finalContent.referenceCollab=content.referenceCollab;
		
		SampleType sampleType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.typeCode, "typeCode", SampleType.find,true);
		ImportType importType =BusinessValidationHelper.validateExistDescriptionCode(null, sample.importTypeCode, "importTypeCode", ImportType.find,true);

		if(importType !=null){

			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(importType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,finalContent.properties);
		}
		if(sampleType !=null){
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(sampleType.getPropertyDefinitionByLevel(Level.CODE.Content), sample.properties,finalContent.properties);
		}

		if(content.properties!=null)
			finalContent.properties.putAll(content.properties);

		container.contents.add(finalContent);

		container.projectCodes.addAll(sample.projectCodes);

		container.sampleCodes.add(sample.code);

	}

	public static void addContent(Container outputContainer, List<ContainerUsed> inputContainerUseds , Experiment experiment, Map<String,PropertyValue> properties) throws DAOException {
		
		List<String> inputContainerCodes=new ArrayList<String>();
		
		for(ContainerUsed inputContainerUsed:inputContainerUseds){

			inputContainerCodes.add(inputContainerUsed.code);
			
			Container inputContainer=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.code);

			Set<Content> contents = new HashSet<Content>(inputContainer.contents);
			
			if(inputContainerUsed.percentage==null){
				inputContainerUsed.percentage=100.0/inputContainerUseds.size();
			}
			calculPercentageContent(contents,inputContainerUsed.percentage);
			outputContainer.contents.addAll(contents);
			if(outputContainer.projectCodes == null){
				outputContainer.projectCodes = new HashSet<String>();
			}
			outputContainer.projectCodes.addAll(inputContainer.projectCodes);
			if(outputContainer.sampleCodes == null){
				outputContainer.sampleCodes = new HashSet<String>();
			}
			outputContainer.sampleCodes.addAll(inputContainer.sampleCodes);
			outputContainer.categoryCode = ContainerSupportCategory.find.findByCode(experiment.instrument.outContainerSupportCategoryCode).containerCategory.code;
			
			if(CollectionUtils.isNotEmpty(inputContainer.inputProcessCodes)){
				if(outputContainer.inputProcessCodes == null){
					outputContainer.inputProcessCodes = new HashSet<String>();
				}
				outputContainer.inputProcessCodes.addAll(inputContainer.inputProcessCodes); 
			}
			outputContainer.processTypeCode=inputContainer.processTypeCode;

			if(experiment.categoryCode.equals("transformation")){
				if(outputContainer.fromExperimentTypeCodes == null){
					outputContainer.fromExperimentTypeCodes = new HashSet<String>();
				}
				outputContainer.fromExperimentTypeCodes.add(experiment.typeCode);
			}else{
				if(CollectionUtils.isNotEmpty(inputContainer.fromExperimentTypeCodes)){				
					if(outputContainer.fromExperimentTypeCodes == null){
						outputContainer.fromExperimentTypeCodes = new HashSet<String>();
					}
					outputContainer.fromExperimentTypeCodes.addAll(inputContainer.fromExperimentTypeCodes);
				}
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
		
		
		List<models.laboratory.processes.instance.Process> process=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME
				, models.laboratory.processes.instance.Process.class, DBQuery.in("experimentCodes", experiment.code).in("containerInputCode", inputContainerCodes)).toList();
		
		Set<String > processTypes=new HashSet<String>();
		 Map<String,PropertyValue> propertiesProcess=new HashMap<String, PropertyValue>();
		for (models.laboratory.processes.instance.Process p:process){
			propertiesProcess.putAll(p.properties);
			processTypes.add(p.typeCode);
		}
		
		for(String processTypeCode:processTypes)
		{
			ProcessType processType=BusinessValidationHelper.validateExistDescriptionCode(null, processTypeCode, "typeCode", ProcessType.find,true);
			if(processType !=null){
				InstanceHelpers.copyPropertyValueFromPropertiesDefinition(processType.getPropertyDefinitionByLevel(Level.CODE.Container), propertiesProcess,outputContainer.properties);
			}
			
			for(Content content :outputContainer.contents){
				if(processType !=null){
					InstanceHelpers.copyPropertyValueFromPropertiesDefinition(processType.getPropertyDefinitionByLevel(Level.CODE.Content), propertiesProcess,content.properties);
				}
			}
		}
		
		
	}
	
	public static void calculPercentageContent(Set<Content> contents, Double percentage){
		if(percentage!=null){
			for(Content cc:contents){
				BigDecimal bd=null;				
				if(cc.percentage != null){
					bd = (new BigDecimal((cc.percentage*percentage)/100.00)).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				cc.percentage= cc.percentage == null ? percentage : bd.doubleValue();
					
					
			}
		}
	}


	private static SimpleDateFormat getSimpleDateFormat(){
		return new SimpleDateFormat("yyyyMMdd_HHmmss");
	}

	public static String generateContainerCode(String categoryCode){
		Random randomGenerator = new Random();
		return (categoryCode+"-"+getSimpleDateFormat().format(new Date())+randomGenerator.nextInt(100)).toUpperCase();
	}


	public static void createSupportFromContainers(List<Container> containers, Map<String, PropertyValue<String>> mapSupportsCodeSeq, ContextValidation contextValidation){

		HashMap<String,ContainerSupport> mapSupports = new HashMap<String,ContainerSupport>();

		for (Container container : containers) {
			if (container.support != null) {
				ContainerSupport newSupport = null;
				if (mapSupportsCodeSeq != null) {
					newSupport = ContainerSupportHelper.createContainerSupport(container.support.code, mapSupportsCodeSeq.get(container.support.code), container.support.categoryCode,"ngl");
				}
				else {
					newSupport = ContainerSupportHelper.createContainerSupport(container.support.code, null, container.support.categoryCode,"ngl");
				}
				newSupport.projectCodes = new  HashSet<String>(container.projectCodes);
				newSupport.sampleCodes = new  HashSet<String>(container.sampleCodes);
				newSupport.state=container.state;
				
				if(null != container.fromExperimentTypeCodes){//TODO Must be manage for CNG
					newSupport.fromExperimentTypeCodes = new  HashSet<String>(container.fromExperimentTypeCodes);
				}
				if (!mapSupports.containsKey(newSupport.code)) {
					mapSupports.put(newSupport.code, newSupport);
				}
				else {
					ContainerSupport oldSupport = (ContainerSupport) mapSupports.get(newSupport.code);
					oldSupport.projectCodes.addAll(newSupport.projectCodes); 
					oldSupport.sampleCodes.addAll(newSupport.sampleCodes);
					if(null != newSupport.fromExperimentTypeCodes && null != oldSupport.fromExperimentTypeCodes){//TODO Must be manage for CNG
						oldSupport.fromExperimentTypeCodes.addAll(newSupport.fromExperimentTypeCodes);
					}
				}

			}
		}

		InstanceHelpers.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, new ArrayList<ContainerSupport>(mapSupports.values()), contextValidation, true);

	}


	public static void updateSupportFromUpdatedContainers(List<Container> updatedContainers, Map<String, PropertyValue<String>> mapSupportsCodeSeq, ContextValidation contextValidation){

		HashMap<String,ContainerSupport> mapSupports = new HashMap<String,ContainerSupport>();

		for (Container container : updatedContainers) {
			if (container.support != null) {
				//FDS note 22/06/2015: mapSupportsCodeSeq n'est defini que pour les container de type lane!!
				//FDS bug 22/06/2015: il manquait le test sur mapSupportsCodeSeq
				//ContainerSupport newSupport = ContainerSupportHelper.createContainerSupport(container.support.code, mapSupportsCodeSeq.get(container.support.code), container.support.categoryCode,"ngl");
				
				ContainerSupport newSupport = null;
				if (mapSupportsCodeSeq != null) {
					newSupport = ContainerSupportHelper.createContainerSupport(container.support.code, mapSupportsCodeSeq.get(container.support.code), container.support.categoryCode,"ngl");
				}
				else {
					newSupport = ContainerSupportHelper.createContainerSupport(container.support.code, null, container.support.categoryCode,"ngl");
				}
					
				newSupport.projectCodes = new  HashSet<String>(container.projectCodes);
				newSupport.sampleCodes = new  HashSet<String>(container.sampleCodes);							
				if (!mapSupports.containsKey(newSupport.code)) {
					mapSupports.put(newSupport.code, newSupport);
				}
				else {
					ContainerSupport oldSupport = (ContainerSupport) mapSupports.get(newSupport.code);
					oldSupport.projectCodes.addAll(newSupport.projectCodes); 
					oldSupport.sampleCodes.addAll(newSupport.sampleCodes);
				}

			}
		}

		for (Map.Entry<String,ContainerSupport> e : mapSupports.entrySet()) {
			ContainerSupport dbCs = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, e.getKey());

			ContainerSupport updatedCs = e.getValue();

			updatedCs.traceInformation = dbCs.traceInformation;
			updatedCs.traceInformation.modifyDate = new Date();
			updatedCs.traceInformation.modifyUser = "ngl";

			if (!( dbCs.projectCodes.containsAll(updatedCs.projectCodes) && dbCs.sampleCodes.containsAll(updatedCs.sampleCodes) 
					&& updatedCs.projectCodes.containsAll(dbCs.projectCodes) && updatedCs.sampleCodes.containsAll(dbCs.sampleCodes)) ) {

				MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, e.getKey());

				InstanceHelpers.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, updatedCs, contextValidation, true);
			}
		}


	}

	public static Set<Content> contentFromSampleCode(Set<Content> contents,
			String sampleCode) {
		Set<Content> contentsFind=new HashSet<Content>();
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
	
	public static Double getEquiPercentValue(int size){
		BigDecimal p = (new BigDecimal(100.00/size)).setScale(2, RoundingMode.HALF_UP);						
		return p.doubleValue();
	}

}
