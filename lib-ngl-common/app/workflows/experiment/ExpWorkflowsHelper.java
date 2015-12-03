package workflows.experiment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import models.laboratory.common.description.Level;
import models.laboratory.common.description.Level.CODE;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ExperimentHelper;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.experiments.api.ExperimentCategories;
import validation.ContextValidation;
import workflows.container.ContainerWorkflows;
import workflows.process.ProcessWorkflows;
import fr.cea.ig.MongoDBDAO;

public class ExpWorkflowsHelper {
	
	
	public static void updateXCodes(Experiment exp) {
		Set<String> sampleCodes = new HashSet<String>();
		Set<String> projectCodes  = new HashSet<String>();
		Set<String> inputContainerSupportCodes  = new HashSet<String>();
		
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes)).toList();
		for(Container container:containers){			
			if(CollectionUtils.isNotEmpty(container.projectCodes)){
				projectCodes.addAll(container.projectCodes);
			}	
			if(CollectionUtils.isNotEmpty(container.sampleCodes)){
				sampleCodes.addAll(container.sampleCodes);
			}
			inputContainerSupportCodes.add(container.support.code);
		}	
		
		exp.projectCodes = projectCodes;		
		exp.sampleCodes = sampleCodes;
		exp.inputContainerSupportCodes = inputContainerSupportCodes;		
		
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code)
				,DBUpdate.set("projectCodes", exp.projectCodes).set("sampleCodes", exp.sampleCodes).set("inputContainerSupportCodes", exp.inputContainerSupportCodes));
	}


	public static void updateOutputContainerCodes(Experiment exp) {
		Set<String> outputContainerSupportCodes = exp.getAllOutputContainers().stream().map((OutputContainerUsed c) -> c.locationOnContainerSupport.code).collect(Collectors.toSet());
		exp.outputContainerSupportCodes = outputContainerSupportCodes; 
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code)
				,DBUpdate.set("outputContainerSupportCodes", exp.outputContainerSupportCodes));
	}

	

	
	
	public static void updateInputContainersAndProcesses(Experiment exp, ContextValidation ctxVal) {
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		if(inputContainerCodes.size() > 0){
			List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes)).toList();
			ContainerWorkflows.setContainerState(inputContainers,"IW-E", ctxVal);
			//TODO GA Revoir Gestion workflow process			
			Set<String> processCodes = inputContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,Process.class,
					DBQuery.in("code", processCodes).notEquals("state.code", "F"), 
					DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code),true);		
		}
	}
	
	public static void updateInputContainersAndProcessesState(Experiment exp, ContextValidation ctxVal, String containerStateCode, String processStateCode) {
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		if(inputContainerCodes.size() > 0){
			List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes).notEquals("state.code", containerStateCode)).toList();
			ContainerWorkflows.setContainerState(inputContainers,containerStateCode, ctxVal);
			
			//TODO GA Revoir Gestion workflow process
			Set<String> processCodes = inputContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.in("code", processCodes).notEquals("state.code", processStateCode)).toList();
			ProcessWorkflows.setProcessState(processes, processStateCode, null, ctxVal);
		}
	}
	
	
	public static void updateAddContainersToExperiment(Experiment expFromUser, ContextValidation ctxVal) {
		Experiment expFromDB = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, expFromUser.code);
		
		List<String> newContainerCodes = getNewContainerCodes(expFromDB, expFromUser);
		if(newContainerCodes.size() > 0){
			List<Container> newContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", newContainerCodes)).toList();
			ContainerWorkflows.setContainerState(newContainers,"IW-E", ctxVal);
			
			Set<String> processCodes = newContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
					DBQuery.in("code", processCodes).notEquals("state.code", "F"), 
					DBUpdate.set("currentExperimentTypeCode", expFromDB.typeCode).push("experimentCodes", expFromDB.code));			
		}
	}
	
	public static void updateRemoveContainersFromExperiment(Experiment expFromUser,
			ContextValidation ctxVal) {
		Experiment expFromDB = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, expFromUser.code);
		
		List<String> removeContainerCodes = getRemoveContainerCodes(expFromDB, expFromUser);
		if(removeContainerCodes.size() > 0){
			List<Container> removeContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", removeContainerCodes)).toList();			
			String nextContainerState=ContainerWorkflows.getAvailableContainerStateFromExperimentCategory(expFromDB.categoryCode);			
			ContainerWorkflows.setContainerState(removeContainers, nextContainerState, ctxVal);
			
			Set<String> processCodes = removeContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
					DBQuery.in("code", processCodes).notEquals("state.code", "F"), 
					DBUpdate.unset("currentExperimentTypeCode").pull("experimentCodes", expFromDB.code));
			
		}
	}

	




	private static List<String> getNewContainerCodes(Experiment expFromDB, Experiment expFromUser) {
		List<String> containerCodesFromDB = expFromDB.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		List<String> containerCodesFromUser = expFromUser.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		
		List<String> newContainersCodes = new ArrayList<String>();
		for(String codeFromDB:containerCodesFromUser){
			if(!containerCodesFromDB.contains(codeFromDB)){
				newContainersCodes.add(codeFromDB);
			}
		}		
		return newContainersCodes;
	}




	private static List<String> getRemoveContainerCodes(Experiment expFromDB, Experiment expFromUser) {
		List<String> containerCodesFromDB = expFromDB.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		List<String> containerCodesFromUser = expFromUser.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		
		List<String> removeContainersCodes = new ArrayList<String>();
		for(String codeFromDB:containerCodesFromDB){
			if(!containerCodesFromUser.contains(codeFromDB)){
				removeContainersCodes.add(codeFromDB);
			}
		}
		
		return removeContainersCodes;
	}
	
	/**
	 * Update OutputContainerUsed :
	 * 		- generate ContainerSupportCode and ContainerCode if needed
	 * 		- populate content, projectCodes, sampleCodes, fromExperimentTypeCodes, processTypeCodes, inputProcessCodes
	 * 		- remove empty volume, quantity, concentration
	 * 
	 * !! missing populate properties on container !!		
	 * 
	 * @param exp
	 * @param validation
	 */
	public static void updateATMs(Experiment exp, boolean justContainerCode) {
		ContainerSupportCategory outputCsc = ContainerSupportCategory.find.findByCode(exp.instrument.outContainerSupportCategoryCode);
		
		if(outputCsc.nbLine.equals(Integer.valueOf(1)) && outputCsc.nbColumn.equals(Integer.valueOf(1))){
			exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> updateOutputContainerUsed(exp, atm, outputCsc, CodeHelper.getInstance().generateContainerSupportCode(), justContainerCode));
		}else if(!outputCsc.nbLine.equals(Integer.valueOf(1))){
			String supportCode = CodeHelper.getInstance().generateContainerSupportCode();
			exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> updateOutputContainerUsed(exp, atm, outputCsc, supportCode, justContainerCode));
		}	
		
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code), DBUpdate.set("atomicTransfertMethods", exp.atomicTransfertMethods));
	}


	private static void updateOutputContainerUsed(Experiment exp, AtomicTransfertMethod atm, ContainerSupportCategory outputCsc, String supportCode, boolean justContainerCode) {
		atm.updateOutputCodeIfNeeded(outputCsc, supportCode);
		if(!justContainerCode){
			List<Content> contents = getContents(exp, atm);
						
			atm.outputContainerUseds.forEach((OutputContainerUsed ocu) ->{
				ocu.contents = contents;
				if(ocu.volume != null && ocu.volume.value == null)ocu.volume=null;
				if(ocu.concentration != null &&ocu.concentration.value == null)ocu.concentration=null;
				if(ocu.quantity != null &&ocu.quantity.value == null)ocu.quantity=null;			
			});
		}
	}


	private static Set<String> getFromExperimentTypeCodes(Experiment exp, AtomicTransfertMethod atm) {
		Set<String> _fromExperimentTypeCodes = new HashSet(0);
		if(!ExperimentCategory.CODE.transformation.equals(ExperimentCategory.CODE.valueOf(exp.categoryCode))){
			_fromExperimentTypeCodes.add(exp.categoryCode);
		}else{
			_fromExperimentTypeCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.fromExperimentTypeCodes).flatMap(Set::stream).collect(Collectors.toSet());
		}
		return _fromExperimentTypeCodes;
	}


	private static List<Content> getContents(Experiment exp, AtomicTransfertMethod atm) {
		List<Content> contents =  atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> ContainerHelper.calculPercentageContent(icu.contents, icu.percentage)).flatMap(List::stream).collect(Collectors.toList());
		contents = ContainerHelper.fusionContents(contents);
		Map<String, PropertyValue> newContentProperties = getPropertiesForALevel(exp, atm, CODE.Content);
		contents.forEach((Content c) ->{
			c.properties.putAll(newContentProperties);
		});
		return contents;
	}

	/**
	 * Generate Support and container and save it in MongoDB
	 * Il only one error during validation process all object are delete from MongoDB
	 * @param exp
	 * @param validation
	 */
	public static void createOutputContainerSupports(Experiment exp, ContextValidation validation) {
		Map<String, List<Container>> containersBySupportCode = exp.atomicTransfertMethods.stream()
				.map((AtomicTransfertMethod atm) ->createOutputContainers(exp, atm, validation))
				.flatMap(List::stream)
				.collect(Collectors.groupingBy(c -> c.support.code));
		
		//soit 1 seul support pour tous les atm
		//soit autant de support que d'atm
		
		//Map<ContainerSupport, List<Container>> containersBySupport = new HashMap<ContainerSupport, List<Container>>(0);
		ContextValidation supportsValidation = new ContextValidation(validation.getUser());
		supportsValidation.setCreationMode();
		
		containersBySupportCode.entrySet().forEach(entry -> {
			List<Container> containers = entry.getValue();
			ContainerSupport support = createContainerSupport(entry.getKey(), containers, validation);
			support.properties = getPropertiesForALevel(exp, CODE.ContainerSupport); //TODO GA extract only properties for exp and inst not for atm so must be improve
			
			support.validate(supportsValidation);
			if(!supportsValidation.hasErrors()){
				MongoDBDAO.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, support);
				containers.forEach(container -> {
					container.validate(supportsValidation);
					if(!supportsValidation.hasErrors()){
						MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, container);
					}
				});
			}				
		});
		
		//delete all supports and containers if only one error
		if(supportsValidation.hasErrors()){
			containersBySupportCode.entrySet().forEach(entry -> {
				MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", entry.getKey()));
				MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", entry.getKey()));
			});
		}			
		validation.addErrors(supportsValidation.errors);
	}

	public static void deleteOutputContainerSupports(Experiment exp, ContextValidation validation) {
		exp.outputContainerSupportCodes.forEach(code -> {
			MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", code));
			MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", code));
		});
	}

	
	private static ContainerSupport createContainerSupport(String code, List<Container> containers, ContextValidation validation) {
		validation.addKeyToRootKeyName("creation.outputSupport."+code);
		ContainerSupport support = new ContainerSupport();
		support.code = code;
		support.state = new State("N", validation.getUser());
		support.traceInformation  = new TraceInformation(validation.getUser());
		support.categoryCode = getSupportCategoryCode(containers, validation);
		support.storageCode = getSupportStorageCode(containers, validation);
		support.projectCodes = containers.stream().map(c -> c.projectCodes).flatMap(Set::stream).collect(Collectors.toSet());
		support.sampleCodes = containers.stream().map(c -> c.sampleCodes).flatMap(Set::stream).collect(Collectors.toSet());
		support.fromExperimentTypeCodes = containers.stream().map(c -> c.fromExperimentTypeCodes).flatMap(Set::stream).collect(Collectors.toSet());
		validation.removeKeyFromRootKeyName("creation.outputSupport."+code);
		return support;
	}


	private static String getSupportCategoryCode(List<Container> containers, ContextValidation validation) {
		Set<String> categoryCodes = containers.stream().map(c -> c.support.categoryCode).collect(Collectors.toSet());
		if(categoryCodes.size() == 1)
			return categoryCodes.iterator().next();
		else{
			validation.addErrors("categoryCode","different for several containers");
			return null;
		}
	}
	private static String getSupportStorageCode(List<Container> containers, ContextValidation validation) {
		Set<String> storageCodes = containers.stream().map(c -> c.support.storageCode).collect(Collectors.toSet());
		if(storageCodes.size() == 1)
			return storageCodes.iterator().next();
		else{
			validation.addErrors("storageCode","different for several containers");
			return null;
		}
	}

	private static List<Container> createOutputContainers(Experiment exp, AtomicTransfertMethod atm, ContextValidation validation) {
		Set<String> projectCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.projectCodes).flatMap(Set::stream).collect(Collectors.toSet());
		Set<String> sampleCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.sampleCodes).flatMap(Set::stream).collect(Collectors.toSet());
		Set<String> fromExperimentTypeCodes = getFromExperimentTypeCodes(exp, atm);
		Set<String> processTypeCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.processTypeCodes).flatMap(Set::stream).collect(Collectors.toSet());
		Set<String> inputProcessCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());
		Map<String, PropertyValue> containerProperties = getPropertiesForALevel(exp, atm, CODE.Container);
		State state = new State("N", validation.getUser());
		TraceInformation traceInformation = new TraceInformation(validation.getUser());
		
		List<Container> newContainers = new ArrayList<Container>();
		atm.outputContainerUseds.forEach((OutputContainerUsed ocu) ->{
			Container c = new Container();
			c.code = ocu.code;
			c.categoryCode = ocu.categoryCode;
			c.contents = ocu.contents;
			c.support = ocu.locationOnContainerSupport;
			c.properties = containerProperties;
			c.mesuredConcentration = ocu.concentration;
			c.mesuredQuantity = ocu.quantity;
			c.mesuredVolume = ocu.volume;
			c.projectCodes = projectCodes;
			c.sampleCodes = sampleCodes;
			c.fromExperimentTypeCodes = fromExperimentTypeCodes;
			c.processTypeCodes = processTypeCodes;
			c.inputProcessCodes = inputProcessCodes;
			c.state = state;
			c.traceInformation = traceInformation;
			newContainers.add(c);
		});
		
		return newContainers;
	}

	private static Map<String, PropertyValue> getPropertiesForALevel(Experiment exp, CODE code) {
		return getPropertiesForALevel(exp, null, code);
	}
	
	private static Map<String, PropertyValue> getPropertiesForALevel(Experiment exp, AtomicTransfertMethod atm, Level.CODE level) {
		Map<String, PropertyValue> propertiesForALevel = new HashMap<String, PropertyValue>();
		
		ExperimentType expType = ExperimentType.find.findByCode(exp.typeCode);
		List<PropertyDefinition> experimentPropertyDefinitions = expType.getPropertyDefinitionByLevel(level);
		
		//extract experiment content properties
		Map<String,PropertyValue> allExperimentProperties=new HashMap<String, PropertyValue>(0);
		if(null != exp.experimentProperties)allExperimentProperties.putAll(exp.experimentProperties);
		if(null != atm){
			allExperimentProperties.putAll(atm.inputContainerUseds.stream()
					.filter((InputContainerUsed icu) -> icu.experimentProperties != null)
					.map((InputContainerUsed icu) -> icu.experimentProperties.entrySet())
					.flatMap(Set::stream)
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
			allExperimentProperties.putAll(atm.outputContainerUseds.stream()
					.filter((OutputContainerUsed ocu) -> ocu.experimentProperties != null)
					.map((OutputContainerUsed ocu) -> ocu.experimentProperties.entrySet())
					.flatMap(Set::stream)
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		}
		InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentPropertyDefinitions, allExperimentProperties, propertiesForALevel);
		
		//extract instrument content properties
		InstrumentUsedType insType = InstrumentUsedType.find.findByCode(exp.instrument.typeCode);
		List<PropertyDefinition> instrumentPropertyDefinitions = insType.getPropertyDefinitionByLevel(level);
		Map<String,PropertyValue> allInstrumentProperties=new HashMap<String, PropertyValue>();
		if(null != exp.instrumentProperties)allInstrumentProperties.putAll(exp.instrumentProperties);
		if(null != atm){
			allInstrumentProperties.putAll(atm.inputContainerUseds.stream()
					.filter((InputContainerUsed icu) -> icu.instrumentProperties != null)
					.map((InputContainerUsed icu) -> icu.instrumentProperties.entrySet())
					.flatMap(Set::stream)
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
			allInstrumentProperties.putAll(atm.outputContainerUseds.stream()
					.filter((OutputContainerUsed ocu) -> ocu.instrumentProperties != null)
					.map((OutputContainerUsed ocu) -> ocu.instrumentProperties.entrySet())
					.flatMap(Set::stream)
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));			
		
		}
		InstanceHelpers.copyPropertyValueFromPropertiesDefinition(instrumentPropertyDefinitions, allInstrumentProperties, propertiesForALevel);
		
		if(null != atm){
			//extract process content properties for only the inputContainer of the process
			List<PropertyDefinition> processesPropertyDefinitions = atm.inputContainerUseds.stream()
					.map((InputContainerUsed icu) -> getProcessesPropertyDefinitions(icu, level))
					.flatMap(List::stream)
					.collect(Collectors.toList()); 
			Map<String,PropertyValue> allProcessesProperties=atm.inputContainerUseds.stream()
					.map((InputContainerUsed icu) -> getProcessesProperties(icu))
					.flatMap(List::stream)
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(processesPropertyDefinitions, allProcessesProperties, propertiesForALevel);			
		}
		
		return propertiesForALevel;
	}
	
	private static List<PropertyDefinition> getProcessesPropertyDefinitions(InputContainerUsed icu, Level.CODE level) {		
		return icu.processTypeCodes.stream()
				.map((String code) ->ProcessType.find.findByCode(code))
				.map((ProcessType p) -> p.getPropertyDefinitionByLevel(level))
				.flatMap(List::stream)
				.collect(Collectors.toList());		
	}


	private static List<Map.Entry<String,PropertyValue>> getProcessesProperties(InputContainerUsed icu) {
		List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code", icu.inputProcessCodes).is("containerInputCode", icu.code)).toList();
		if(null != processes && processes.size() > 0){
			return processes.stream().map((Process p) -> p.properties.entrySet())
				.flatMap(Set::stream)
				.collect(Collectors.toList());
		}else{
			return new ArrayList<Map.Entry<String, PropertyValue>>();
		}
		
	}


}
