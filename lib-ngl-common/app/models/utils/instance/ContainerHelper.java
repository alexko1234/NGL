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
import java.util.stream.Collectors;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
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

import play.Logger;

import com.google.common.collect.Multiset.Entry;

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
	@Deprecated
	public static void addContent(Container outputContainer, List<InputContainerUsed> inputContainerUseds , Experiment experiment, Map<String,PropertyValue> properties) throws DAOException {
		
		List<String> inputContainerCodes=new ArrayList<String>();
		
		for(InputContainerUsed inputContainerUsed:inputContainerUseds){

			inputContainerCodes.add(inputContainerUsed.code);
			
			Container inputContainer=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, inputContainerUsed.code);

			List<Content> contents = inputContainer.contents;
			
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
		
		// fusion content with same projectCode, sampleCode et tag if present
		outputContainer.contents = fusionContents(outputContainer.contents);
		
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
	/**
	 * fusion content if same projectCode, sampleCode and tag if exist.
	 * 
	 * the fusion : 
	 * 	- sum the percentage of content 
	 *  - keep properties with same key and same value
	 *  - remove properties  with same key and different value
	 *  - add properties that exists only in one content
	 *  
	 * @param contents
	 * @return
	 */
	public static List<Content> fusionContents(List<Content> contents) {
		
		//groupb by a key
		Map<String, List<Content>> contentsByKey = contents.stream().collect(Collectors.groupingBy((Content c ) -> getContentKey(c)));
		
		//extract values with only one content
		Map<String, Content> contentsByKeyWithOneValues = contentsByKey.entrySet().stream()
				.filter((Map.Entry<String, List<Content>> e) -> e.getValue().size() == 1)
				.collect(Collectors.toMap((Map.Entry<String, List<Content>> e) -> e.getKey(), (Map.Entry<String, List<Content>> e) -> e.getValue().get(0)));
		
		//extract values with several contents and fusion the contents
		Map<String, Content> contentsByKeyWithSeveralValues = contentsByKey.entrySet().stream()
				.filter((Map.Entry<String, List<Content>> e) -> e.getValue().size() > 1)
				.collect(Collectors.toMap((Map.Entry<String, List<Content>> e) -> e.getKey(), (Map.Entry<String, List<Content>> e) -> fusionSameContents(e.getValue())));
		
		
		contentsByKeyWithOneValues.putAll(contentsByKeyWithSeveralValues);
		
		return new ArrayList(contentsByKeyWithOneValues.values());
	}

	private static Content fusionSameContents(List<Content> contents) {
		Content finalContent = new Content();
		
		finalContent.projectCode = contents.get(0).projectCode;
		finalContent.sampleCode = contents.get(0).sampleCode;
		finalContent.sampleCategoryCode = contents.get(0).sampleCategoryCode;
		finalContent.sampleTypeCode = contents.get(0).sampleTypeCode;
		finalContent.referenceCollab = contents.get(0).referenceCollab;
		finalContent.percentage = new BigDecimal(contents.stream().mapToDouble((Content c) -> c.percentage).sum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		
		for(Content c : contents){
			for(String key : c.properties.keySet()){
				PropertyValue<?> pv = c.properties.get(key);
				finalContent.properties.computeIfAbsent(key, k -> pv);
				finalContent.properties.computeIfPresent(key, (k,v) -> fusionSameProperty(v, pv));
			}
		}
		
		return finalContent;
	}

	private static  PropertyValue<?> fusionSameProperty(PropertyValue<?> currentPv, PropertyValue<?> newPv) {
		if(currentPv.value.equals(newPv.value)){
			return currentPv;
		}else{
			return null;
		}		
	}

	private static String getContentKey(Content content) {
		if(content.properties.containsKey("tag")){
			return content.projectCode+"_"+content.sampleCode+"_"+content.properties.get("tag").value;
		}else{
			return content.projectCode+"_"+content.sampleCode;
		}		
	}

	public static List<Content> calculPercentageContent(List<Content> contents, Double percentage){
		if(percentage!=null){
			for(Content cc:contents){
				if(cc.percentage != null){
					cc.percentage = (new BigDecimal((cc.percentage*percentage)/100.00)).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
				}else{
					cc.percentage = percentage;
				}						
			}
		}
		return contents;
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
				
				//FDS 14/10/2015 ajout storage code
				newSupport.storageCode=container.support.storageCode;
				Logger.debug("2) createSupportFromContainers; support "+ newSupport.code + ": storageCode ="+newSupport.storageCode);
				
				if(null != container.fromExperimentTypeCodes){
					newSupport.fromExperimentTypeCodes = new  HashSet<String>(container.fromExperimentTypeCodes);
				}
				
				if (!mapSupports.containsKey(newSupport.code)) {
					mapSupports.put(newSupport.code, newSupport);
				}
				else {
					ContainerSupport oldSupport = (ContainerSupport) mapSupports.get(newSupport.code);
					oldSupport.projectCodes.addAll(newSupport.projectCodes); 
					oldSupport.sampleCodes.addAll(newSupport.sampleCodes);
					if(null != newSupport.fromExperimentTypeCodes && null != oldSupport.fromExperimentTypeCodes){
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
				
				ContainerSupport newSupport = null;
				if (mapSupportsCodeSeq != null) {
					newSupport = ContainerSupportHelper.createContainerSupport(container.support.code, mapSupportsCodeSeq.get(container.support.code), container.support.categoryCode,"ngl");
				}
				else {
					newSupport = ContainerSupportHelper.createContainerSupport(container.support.code, null, container.support.categoryCode,"ngl");
				}
					
				newSupport.projectCodes = new  HashSet<String>(container.projectCodes);
				newSupport.sampleCodes = new  HashSet<String>(container.sampleCodes);		
				
				//FDS 14/10/2015 ajout storage code 
				if ( container.support.storageCode != null ){
					newSupport.storageCode=container.support.storageCode;
					Logger.debug("updating support...storage Code="+ newSupport.storageCode);
				}else {
					Logger.warn("not updating support...storage Code is null");
				}
				
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

		// GA 02/11/2015 prise en compte de la modification du storageCode... pourrait etre null ???
		for (Map.Entry<String,ContainerSupport> support : mapSupports.entrySet()) {
			ContainerSupport dbCs = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, support.getKey());

			ContainerSupport updatedCs = support.getValue();

			updatedCs.traceInformation = dbCs.traceInformation;
			updatedCs.traceInformation.modifyDate = new Date();
			updatedCs.traceInformation.modifyUser = "ngl";

			// FDS NOTE: projectCodes et sampleCodes sont des listes ils faut faire les tests d'inclusions dans les 2 sens !
			if (!dbCs.projectCodes.containsAll(updatedCs.projectCodes) 
				|| !updatedCs.projectCodes.containsAll(dbCs.projectCodes) 
				|| !dbCs.sampleCodes.containsAll(updatedCs.sampleCodes) 
				|| !updatedCs.sampleCodes.containsAll(dbCs.sampleCodes) 
				|| (null != updatedCs.storageCode && !updatedCs.storageCode.equals(dbCs.storageCode))
				|| (null != dbCs.storageCode && !dbCs.storageCode.equals(updatedCs.storageCode))) {

				MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, support.getKey());

				InstanceHelpers.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, updatedCs, contextValidation, true);
			}
		}
	}

	public static Set<Content> contentFromSampleCode(List<Content> contents,
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
