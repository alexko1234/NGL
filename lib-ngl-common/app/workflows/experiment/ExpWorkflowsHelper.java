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
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import models.laboratory.instrument.description.InstrumentUsedType;
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

	

	
	
	public static void updateContainersAndProcesses(Experiment exp, ContextValidation ctxVal) {
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		if(inputContainerCodes.size() > 0){
			List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes)).toList();
			ContainerWorkflows.setContainerState(inputContainers,"IW-E", ctxVal);
			
			Set<String> processCodes = inputContainers.stream().map((Container c)-> c.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());			
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,Process.class,
					DBQuery.in("code", processCodes).notEquals("state.code", "F"), 
					DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code),true);		
		}
	}
	
	public static void updateContainersAndProcessesState(Experiment exp, ContextValidation ctxVal, String containerStateCode, String processStateCode) {
		List<String> inputContainerCodes = exp.getAllInputContainers().stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		if(inputContainerCodes.size() > 0){
			List<Container> inputContainers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", inputContainerCodes).notEquals("state.code", containerStateCode)).toList();
			ContainerWorkflows.setContainerState(inputContainers,containerStateCode, ctxVal);
			
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
	public static void updateATMs(Experiment exp) {
		ContainerSupportCategory outputCsc = ContainerSupportCategory.find.findByCode(exp.instrument.outContainerSupportCategoryCode);
		
		if(outputCsc.nbLine.equals(Integer.valueOf(1)) && outputCsc.nbColumn.equals(Integer.valueOf(1))){
			exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> updateOutputContainerUsed(exp, atm, outputCsc, CodeHelper.getInstance().generateContainerSupportCode()));
		}else if(!outputCsc.nbLine.equals(Integer.valueOf(1))){
			String supportCode = CodeHelper.getInstance().generateContainerSupportCode();
			exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> updateOutputContainerUsed(exp, atm, outputCsc, supportCode));
		}	
		
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code), DBUpdate.set("atomicTransfertMethods", exp.atomicTransfertMethods));
	}


	private static void updateOutputContainerUsed(Experiment exp, AtomicTransfertMethod atm, ContainerSupportCategory outputCsc, String supportCode) {
		atm.updateOutputCodeIfNeeded(outputCsc, supportCode);
		
		Set<String> projectCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.projectCodes).flatMap(Set::stream).collect(Collectors.toSet());
		Set<String> sampleCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.sampleCodes).flatMap(Set::stream).collect(Collectors.toSet());
		Set<String> fromExperimentTypeCodes = getFromExperimentTypeCodes(exp, atm);
		Set<String> processTypeCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.processTypeCodes).flatMap(Set::stream).collect(Collectors.toSet());
		Set<String> inputProcessCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.inputProcessCodes).flatMap(Set::stream).collect(Collectors.toSet());
		List<Content> contents = getContents(exp, atm);
		
		
		atm.outputContainerUseds.forEach((OutputContainerUsed ocu) ->{
			ocu.projectCodes = projectCodes;
			ocu.sampleCodes = sampleCodes;
			ocu.fromExperimentTypeCodes = fromExperimentTypeCodes;
			ocu.processTypeCodes = processTypeCodes;
			ocu.inputProcessCodes = inputProcessCodes;
			ocu.contents = contents;
			
			if(ocu.volume != null && ocu.volume.value == null)ocu.volume=null;
			if(ocu.concentration != null &&ocu.concentration.value == null)ocu.concentration=null;
			if(ocu.quantity != null &&ocu.quantity.value == null)ocu.quantity=null;			
		});
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
		
		ExperimentType expType = ExperimentType.find.findByCode(exp.typeCode);
		List<PropertyDefinition> experimentPropertyDefinitions = expType.getPropertyDefinitionByLevel(Level.CODE.Content);
		
		Map<String,PropertyValue> allExperimentProperties=new HashMap<String, PropertyValue>(0);
		if(null != exp.experimentProperties)allExperimentProperties.putAll(exp.experimentProperties);
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
		
		
		InstrumentUsedType insType = InstrumentUsedType.find.findByCode(exp.instrument.typeCode);
		List<PropertyDefinition> instrumentPropertyDefinitions = insType.getPropertyDefinitionByLevel(Level.CODE.Content);
		Map<String,PropertyValue> allInstrumentProperties=new HashMap<String, PropertyValue>();
		if(null != exp.instrumentProperties)allInstrumentProperties.putAll(exp.instrumentProperties);
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
		
		
		contents.forEach((Content c) ->{
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(experimentPropertyDefinitions, allExperimentProperties, c.properties);
			InstanceHelpers.copyPropertyValueFromPropertiesDefinition(instrumentPropertyDefinitions, allInstrumentProperties, c.properties);
		});
		
		
		return contents;
	}



}
