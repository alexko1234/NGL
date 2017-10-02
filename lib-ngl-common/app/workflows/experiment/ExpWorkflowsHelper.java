package workflows.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;



import models.laboratory.common.description.Level;
import models.laboratory.common.description.Level.CODE;
import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.container.instance.QualityControlResult;
import models.laboratory.container.instance.StorageHistory;
import models.laboratory.container.instance.tree.From;
import models.laboratory.container.instance.tree.ParentContainers;
import models.laboratory.container.instance.tree.TreeOfLifeNode;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.laboratory.project.instance.Project;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.sample.description.SampleType;
import models.laboratory.sample.instance.Sample;
import models.laboratory.sample.instance.tree.SampleLife;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.instance.ContainerHelper;
import models.utils.instance.ExperimentHelper;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.container.ContSupportWorkflows;
import workflows.container.ContWorkflows;
import workflows.process.ProcWorkflows;
import akka.actor.ActorRef;
import akka.actor.Props;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult.Sort;

@Service
public class ExpWorkflowsHelper {

	private final String NEW_PROCESS_CODES = "NEW_PROCESS_CODES";
	private final String NEW_SAMPLE_CODES = "NEW_SAMPLE_CODES";
	@Autowired
	private ContWorkflows containerWorkflows;
	@Autowired
	private ContSupportWorkflows containerSupportWorkflows;
	@Autowired
	private ProcWorkflows processWorkflows;

	public void updateXCodes(Experiment exp) {
		Set<String> sampleCodes = new HashSet<String>();
		Set<String> projectCodes  = new HashSet<String>();
		Set<String> inputContainerSupportCodes = new HashSet<String>();
		Set<String> inputContainerCodes = new HashSet<String>();
		Set<String> inputProcessCodes  = new HashSet<String>();
		Set<String> inputFromTransformationTypeCodes = new HashSet<String>();
		Set<String> inputProcessTypeCodes = new HashSet<String>();

		exp.atomicTransfertMethods.stream().map(atm -> atm.inputContainerUseds)
		.flatMap(List::stream)
		.forEach(inputContainer -> {
			inputContainerCodes.add(inputContainer.code);
			projectCodes.addAll(inputContainer.projectCodes);
			sampleCodes.addAll(inputContainer.sampleCodes);
			inputContainerSupportCodes.add(inputContainer.locationOnContainerSupport.code);
			inputProcessCodes.addAll(inputContainer.processCodes);
			inputFromTransformationTypeCodes.addAll(inputContainer.fromTransformationTypeCodes);
			inputProcessTypeCodes.addAll(inputContainer.processTypeCodes);
		});

		ExperimentType experimentType=ExperimentType.find.findByCode(exp.typeCode);
		if(experimentType.newSample){
			exp.atomicTransfertMethods.stream().map(atm -> atm.outputContainerUseds)
			.flatMap(List::stream)
			.forEach(ocu -> {
				Map<String,PropertyValue> experimentProperties = ocu.experimentProperties;
				if(experimentProperties.containsKey("projectCode")){
					projectCodes.add(experimentProperties.get("projectCode").value.toString());
				}
				
				if(experimentProperties.containsKey("sampleCode")){
					sampleCodes.add(experimentProperties.get("sampleCode").value.toString());
				}						
			});
		}
		
		
		exp.projectCodes = projectCodes;		
		exp.sampleCodes = sampleCodes;
		exp.inputContainerSupportCodes = inputContainerSupportCodes;		
		exp.inputContainerCodes = inputContainerCodes;
		exp.inputProcessCodes = inputProcessCodes;
		exp.inputProcessTypeCodes = inputProcessTypeCodes;
		exp.inputFromTransformationTypeCodes = inputFromTransformationTypeCodes;
		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code)
				,DBUpdate.set("projectCodes", exp.projectCodes)
				.set("sampleCodes", exp.sampleCodes)
				.set("inputContainerSupportCodes", exp.inputContainerSupportCodes)
				.set("inputContainerCodes", exp.inputContainerCodes)
				.set("inputProcessCodes", exp.inputProcessCodes)
				.set("inputProcessTypeCodes", exp.inputProcessTypeCodes)
				.set("inputFromTransformationTypeCodes", exp.inputFromTransformationTypeCodes)
				);
	}


	public void updateOutputContainerCodes(Experiment exp) {
		Set<String> outputContainerSupportCodes = new HashSet<String>();
		Set<String> outputContainerCodes = new HashSet<String>();

		exp.atomicTransfertMethods.stream()
		.filter(atm -> (atm.outputContainerUseds != null))
		.map(atm -> atm.outputContainerUseds)
		.flatMap(List::stream)
		.forEach(outputContainer ->{
			outputContainerCodes.add(outputContainer.code);
			outputContainerSupportCodes.add(outputContainer.locationOnContainerSupport.code);

		});
		if(CollectionUtils.isNotEmpty(outputContainerCodes)){
			exp.outputContainerCodes = outputContainerCodes;
			exp.outputContainerSupportCodes = outputContainerSupportCodes;

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code)
					,DBUpdate.set("outputContainerSupportCodes", exp.outputContainerSupportCodes)
					.set("outputContainerCodes", exp.outputContainerCodes));
		}
	}


	public void updateStateOfInputContainers(Experiment exp, State nextState, ContextValidation ctxVal) {
		ctxVal.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "workflow");
		MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", exp.inputContainerCodes))
		.cursor.forEach(c -> containerWorkflows.setState(ctxVal, c, nextState));		
		ctxVal.removeObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT);
	}

	public void updateStateOfInputContainerSupports(Experiment exp, ContextValidation ctxVal) {
		ctxVal.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "workflow");
		MongoDBDAO.find(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("code", exp.inputContainerSupportCodes))
		.cursor.forEach(c -> containerSupportWorkflows.setStateFromContainers(ctxVal, c));
		ctxVal.removeObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT);
	}

	public void updateStateOfProcesses(Experiment exp, State nextState, ContextValidation ctxVal) {
		MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.in("code", exp.inputProcessCodes))
		.cursor.forEach(c -> processWorkflows.setState(ctxVal, c, nextState));				
	}

	public void linkExperimentWithProcesses(Experiment exp, ContextValidation validation) {
		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,Process.class,
				DBQuery.in("code", exp.inputProcessCodes).notEquals("state.code", "F").notIn("experimentCodes", exp.code), 
				DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code),true);

	}

	public void updateAddContainersToExperiment(Experiment expFromUser, ContextValidation ctxVal, State nextState) {
		Experiment expFromDB = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, expFromUser.code);

		List<String> newContainerCodes = getNewContainerCodes(expFromDB, expFromUser);
		if(newContainerCodes.size() > 0){
			Set<String> newContainerSupportCodes = new TreeSet<String>();
			Set<String> newProcessCodes = new TreeSet<String>();
			ctxVal.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "workflow");
			MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("code", newContainerCodes))
			.cursor.forEach(c -> {				
				newContainerSupportCodes.add(c.support.code);
				newProcessCodes.addAll(c.processCodes);
				containerWorkflows.setState(ctxVal, c, nextState);
			});

			MongoDBDAO.find(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.in("code", newContainerSupportCodes))
			.cursor.forEach(c -> {				
				containerSupportWorkflows.setStateFromContainers(ctxVal, c);
			});

			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
					DBQuery.in("code", newProcessCodes).notEquals("state.code", "F"), 
					DBUpdate.set("currentExperimentTypeCode", expFromDB.typeCode).push("experimentCodes", expFromDB.code));			

			ctxVal.removeObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT);
		}
	}

	public void updateRemoveContainersFromExperiment(Experiment expFromUser,	ContextValidation ctxVal, State containerNextState) {
		Experiment expFromDB = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, expFromUser.code);

		Set<String> removeContainerCodes = getRemoveContainerCodes(expFromDB, expFromUser);
		if(removeContainerCodes.size() > 0){
			rollbackOnContainers(ctxVal, containerNextState, expFromDB.code,	removeContainerCodes);
		}
	}


	public void rollbackOnContainers(ContextValidation ctxVal,
			State containerNextState, String expCode, Set<String> removeContainerCodes) {
		Set<String> removeContainerSupportCodes = new TreeSet<String>();
		Set<String> removeProcessCodes = new TreeSet<String>();
		ctxVal.putObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT, "workflow");
		MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("code", removeContainerCodes)).cursor
		.forEach(c -> {
			removeContainerSupportCodes.add(c.support.code);
			removeProcessCodes.addAll(c.processCodes);
			containerWorkflows.setState(ctxVal, c, containerNextState);
		});

		MongoDBDAO.find(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.in("code", removeContainerSupportCodes)).cursor
		.forEach(c -> {
			containerSupportWorkflows
			.setStateFromContainers(ctxVal, c);
		});

		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
				DBQuery.in("code", removeProcessCodes).notEquals("state.code", "F"), 
				DBUpdate.unset("currentExperimentTypeCode").pull("experimentCodes", expCode));
		ctxVal.removeObject(CommonValidationHelper.FIELD_STATE_CONTAINER_CONTEXT);
	}

	private List<String> getNewContainerCodes(Experiment expFromDB, Experiment expFromUser) {
		List<String> containerCodesFromDB = ExperimentHelper.getAllInputContainers(expFromDB).stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		List<String> containerCodesFromUser = ExperimentHelper.getAllInputContainers(expFromUser).stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());

		List<String> newContainersCodes = new ArrayList<String>();
		for(String codeFromDB:containerCodesFromUser){
			if(!containerCodesFromDB.contains(codeFromDB)){
				newContainersCodes.add(codeFromDB);
			}
		}		
		return newContainersCodes;
	}





	private Set<String> getRemoveContainerCodes(Experiment expFromDB, Experiment expFromUser) {
		List<String> containerCodesFromDB = ExperimentHelper.getAllInputContainers(expFromDB).stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());
		List<String> containerCodesFromUser = ExperimentHelper.getAllInputContainers(expFromUser).stream().map((InputContainerUsed c) -> c.code).collect(Collectors.toList());

		Set<String> removeContainersCodes = new TreeSet<String>();
		for(String codeFromDB:containerCodesFromDB){
			if(!containerCodesFromUser.contains(codeFromDB)){
				removeContainersCodes.add(codeFromDB);
			}
		}

		return removeContainersCodes;
	}
	/**
	 * Update ouput container code but not generate if null
	 * Used when user change plate line or column
	 * @param exp
	 */
	public void updateOutputContainerCode(Experiment exp) {
		ContainerSupportCategory outputCsc = ContainerSupportCategory.find.findByCode(exp.instrument.outContainerSupportCategoryCode);
		exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> atm.updateOutputCodeIfNeeded(outputCsc, null));
	}
	
	/**
	 * Update only content
	 * @param exp
	 */
	public void updateOutputContainerContents(Experiment exp) {
		exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> updateOutputContainerUsedContents(exp, atm));
	}
	
	/**
	 * Update OutputContainerUsed :
	 * 		- generate ContainerSupportCode and ContainerCode if needed
	 * 		- populate content, projectCodes, sampleCodes, fromTransformationTypeCodes, processTypeCodes, inputProcessCodes
	 * 		- remove empty volume, quantity, concentration
	 * 
	 * !! missing populate properties on container !!		
	 * 
	 * @param exp
	 * @param validation
	 */
	public void updateATMs(Experiment exp, boolean justContainerCode) {
		ContainerSupportCategory outputCsc = ContainerSupportCategory.find.findByCode(exp.instrument.outContainerSupportCategoryCode);

		if(outputCsc.nbLine.equals(Integer.valueOf(1)) && outputCsc.nbColumn.equals(Integer.valueOf(1))){
			exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> updateOutputContainerUsed(exp, atm, outputCsc, CodeHelper.getInstance().generateContainerSupportCode(), justContainerCode));
		}else if(!outputCsc.nbLine.equals(Integer.valueOf(1)) || !outputCsc.nbColumn.equals(Integer.valueOf(1))){
			String supportCode = CodeHelper.getInstance().generateContainerSupportCode();
			exp.atomicTransfertMethods.forEach((AtomicTransfertMethod atm) -> updateOutputContainerUsed(exp, atm, outputCsc, supportCode, justContainerCode));
		}	

		MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code), DBUpdate.set("atomicTransfertMethods", exp.atomicTransfertMethods));
	}


	private void updateOutputContainerUsed(Experiment exp, AtomicTransfertMethod atm, ContainerSupportCategory outputCsc, String supportCode, boolean justContainerCode) {
		if(atm.outputContainerUseds != null){
			atm.updateOutputCodeIfNeeded(outputCsc, supportCode);
			if(!justContainerCode){
				updateOutputContainerUsedContents(exp, atm);
			}
		}		
	}


	private void updateOutputContainerUsedContents(Experiment exp,
			AtomicTransfertMethod atm) {
		if(atm.outputContainerUseds != null){
			atm.outputContainerUseds.forEach((OutputContainerUsed ocu) ->{
				ocu.contents = getContents(exp, atm, ocu);				
			});
		}
	}

	private Set<String> getFromTransformationTypeCodes(Experiment exp, AtomicTransfertMethod atm) {
		Set<String> _fromExperimentTypeCodes = new HashSet<String>(0);
		if(ExperimentCategory.CODE.transformation.equals(ExperimentCategory.CODE.valueOf(exp.categoryCode))){
			_fromExperimentTypeCodes.add(exp.typeCode);
		}else{
			_fromExperimentTypeCodes = atm.inputContainerUseds.stream().map((InputContainerUsed icu) -> icu.fromTransformationTypeCodes).flatMap(Set::stream).collect(Collectors.toSet());
		}
		return _fromExperimentTypeCodes;
	}

	private Set<String> getFromTransformationCodes(Experiment exp, AtomicTransfertMethod atm) {
		Set<String> _fromExperimentCodes = new HashSet<String>(0);
		if(ExperimentCategory.CODE.transformation.equals(ExperimentCategory.CODE.valueOf(exp.categoryCode))){
			_fromExperimentCodes.add(exp.code);
		}else{
			_fromExperimentCodes = atm.inputContainerUseds.stream()
					.filter((InputContainerUsed icu) -> icu.fromTransformationCodes != null)
					.map((InputContainerUsed icu) -> icu.fromTransformationCodes).flatMap(Set::stream).collect(Collectors.toSet());
		}
		return _fromExperimentCodes;
	}

	private String getFromSatTypeCode(Experiment exp, ExperimentCategory.CODE code) {
		if(code.equals(ExperimentCategory.CODE.valueOf(exp.categoryCode))){
			return exp.typeCode;
		}
		return null;
	}
	
	private String getFromSatCode(Experiment exp, ExperimentCategory.CODE code) {
		if(code.equals(ExperimentCategory.CODE.valueOf(exp.categoryCode))){
			return exp.code;
		}
		return null;
	}
	

	private List<Content> getContents(Experiment exp, AtomicTransfertMethod atm, OutputContainerUsed ocu) {
		List<Content> contents =  atm.inputContainerUseds.stream()
				.map((InputContainerUsed icu) -> {
					Map<String, PropertyValue> contentProperties = getInputPropertiesForALevel(exp, icu, CODE.Content);
					List<Content> newContents = ContainerHelper.calculPercentageContent(icu.contents, icu.percentage);
					newContents.forEach(c -> c.properties.putAll(contentProperties));
					return newContents;
					})
				.flatMap(List::stream).collect(Collectors.toCollection(ArrayList::new));
		contents = ContainerHelper.fusionContents(contents);
		Map<String, PropertyValue> newContentProperties = getCommonPropertiesForALevelWithATM(exp, null, CODE.Content);
		newContentProperties.putAll(getOutputPropertiesForALevel(exp, ocu, CODE.Content));
		contents.forEach((Content c) ->{
			c.properties.putAll(newContentProperties);
		});
		return contents;
	}

	/**
	 * Generate Support and container and save it in MongoDB
	 * Add the support code inside processes
	 * Il only one error during validation process all object are delete from MongoDB
	 * @param exp
	 * @param validation
	 */
	public void createOutputContainerSupports(Experiment exp, ContextValidation validation) {
		TraceInformation traceInformation = new TraceInformation(validation.getUser());
		long t0 = System.currentTimeMillis();
		validation.putObject(NEW_PROCESS_CODES, new HashSet<String>());

		/*
		before parallelStream
		Map<String, List<Container>> containersBySupportCode = exp.atomicTransfertMethods.stream()
				.map(atm -> createOutputContainers(exp, atm, validation))
				.flatMap(List::stream)
				.collect(Collectors.groupingBy(c -> c.support.code));

		 */

		Map<String, List<Container>> containersBySupportCode = exp.atomicTransfertMethods
				.parallelStream()
				.map(atm -> createOutputContainers(exp, atm, validation))
				.flatMap(List::stream)
				.collect(Collectors.groupingBy(c -> c.support.code));

		long t1 = System.currentTimeMillis();
		//soit 1 seul support pour tous les atm
		//soit autant de support que d'atm

		//Map<ContainerSupport, List<Container>> containersBySupport = new HashMap<ContainerSupport, List<Container>>(0);
		ContextValidation supportsValidation = new ContextValidation(validation.getUser());
		supportsValidation.setCreationMode();
		ExperimentType experimentType=ExperimentType.find.findByCode(exp.typeCode);
		List<String> processusWithNewProjectCode=new ArrayList<String>();
		
		containersBySupportCode.entrySet().forEach(entry -> {
			List<Container> containers = entry.getValue();
			ContainerSupport support = createContainerSupport(entry.getKey(), containers, validation);
			//TODO GA extract only properties from exp and inst not from atm => must be improve
			support.properties = getCommonPropertiesForALevelWithATM(exp, null, CODE.ContainerSupport); 
			support.validate(supportsValidation);

			if(!supportsValidation.hasErrors()){
				support.traceInformation = traceInformation;
				MongoDBDAO.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, support);
				containers.parallelStream()
				.forEach(container -> {
					long t1_0 = System.currentTimeMillis();
					ContextValidation containerValidation = new ContextValidation(validation.getUser());
					containerValidation.setCreationMode();
					container.validate(containerValidation);
					long t1_1 = System.currentTimeMillis();
					if(!containerValidation.hasErrors()){
						container.traceInformation = traceInformation;
						MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME, container);
						MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
								DBQuery.in("code", container.processCodes).notIn("outputContainerSupportCodes", container.support.code),
								DBUpdate.push("outputContainerSupportCodes",container.support.code));
						MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
								DBQuery.in("code", container.processCodes).notIn("outputContainerCodes", container.code),
								DBUpdate.push("outputContainerCodes",container.code));
						
						if(experimentType.newSample){
							boolean projectCodeNotExist = MongoDBDAO.checkObjectExist(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
									DBQuery.in("code", container.processCodes).notIn("projectCodes", container.projectCodes));
							
							if(projectCodeNotExist){
								processusWithNewProjectCode.add(container.code);
								MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
										DBQuery.in("code", container.processCodes).notIn("projectCodes", container.projectCodes),
										DBUpdate.push("projectCodes",container.projectCodes.iterator().next()));
							}
							MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
									DBQuery.in("code", container.processCodes),
									DBUpdate.push("sampleCodes",container.sampleCodes.iterator().next()));
						}
					}else{
						supportsValidation.addErrors(containerValidation.errors);
					}
					long t1_2 = System.currentTimeMillis();
					/*
						Logger.debug("createOutputContainerSupports \n "
								+"1-1 = "+(t1_1-t1_0)+" ms\n"
								+"1-2 = "+(t1_2-t1_1)+" ms\n"
								);
					 */
				});
			}	
		});
		
		long t2 = System.currentTimeMillis();
		//delete all supports and containers if only one error
		if(supportsValidation.hasErrors()){
			containersBySupportCode.entrySet().parallelStream()
			.forEach(entry -> {
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
						DBQuery.in("outputContainerSupportCodes", entry.getKey()),
						DBUpdate.pull("outputContainerSupportCodes",entry.getKey()));
				MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", entry.getKey()));
				MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", entry.getKey()));
				
				List<Container> containers = entry.getValue();
				containers.parallelStream()
				.forEach(container -> {
					//remove outputContainerCodes from Process
					MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
							DBQuery.in("code", container.processCodes).in("outputContainerCodes",container.code),
							DBUpdate.pull("outputContainerCodes",container.code));
					//Retract sampleCode/projectCode of new container from a new sample
					if(experimentType.newSample){
				 		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
						DBQuery.in("code", container.processCodes),
						DBUpdate.pull("sampleCodes",container.sampleCodes.iterator().next()));
						if(processusWithNewProjectCode.contains(container.code)){
							Logger.debug("Pull container "+container.code+", projectCodes "+container.projectCodes.iterator().next());
							MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, 
									DBQuery.in("code", container.processCodes),
									DBUpdate.pull("projectCodes",container.projectCodes.iterator().next()));
						}
					}
				});
				
			});
			Set<String> newProcessCodes = (Set<String>)validation.getObject(NEW_PROCESS_CODES);
			if(null != newProcessCodes){
				MongoDBDAO.delete(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code", newProcessCodes));
			}
			validation.addErrors(supportsValidation.errors);
		}			

		long t3 = System.currentTimeMillis();
		/*
		Logger.debug("createOutputContainerSupports \n "
				+"1 = "+(t1-t0)+" ms\n"
				+"2 = "+(t2-t1)+" ms\n"
				+"3 = "+(t3-t2)+" ms\n"


			);
		 */
	}

	public void deleteOutputContainerSupports(Experiment exp, ContextValidation validation) {
		if(null != exp.outputContainerSupportCodes){
			exp.outputContainerSupportCodes.forEach(code -> {
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
						DBQuery.in("outputContainerSupportCodes", code),
						DBUpdate.pull("outputContainerSupportCodes",code));
				MongoDBDAO.delete(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", code));
				MongoDBDAO.delete(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", code));
			});
			
			exp.outputContainerCodes.forEach(code -> {
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,
						DBQuery.in("outputContainerCodes", code),
						DBUpdate.pull("outputContainerCodes",code));				
			});
			
			Set<String> newProcessCodes = (Set<String>)validation.getObject(NEW_PROCESS_CODES);
			if(null != newProcessCodes  && newProcessCodes.size() > 0){
				MongoDBDAO.delete(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code", newProcessCodes));
			}
		}
	}
	
	public void deleteSamplesIfNeeded(Experiment exp, ContextValidation validation) {
		ExperimentType experimentType=ExperimentType.find.findByCode(exp.typeCode);
		if(experimentType.newSample){	
			Set<String> newSampleCodes = (Set<String>)validation.getObject(NEW_SAMPLE_CODES);
			if(null != newSampleCodes && newSampleCodes.size() > 0){
				Logger.debug("Nb newSampleCodes :"+newSampleCodes.size());
				MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.in("code", newSampleCodes));
			}			
			//TODO Analyse if need to update lastSampleCode on project with the real exist sample. 
		}
		
	}


	private ContainerSupport createContainerSupport(String code, List<Container> containers, ContextValidation validation) {
		validation.addKeyToRootKeyName("creation.outputSupport."+code);
		ContainerSupport support = new ContainerSupport();
		support.code = code;
		support.state = new State("N", validation.getUser());
		support.traceInformation  = new TraceInformation(validation.getUser());
		support.categoryCode = getSupportCategoryCode(containers, validation);
		support.storageCode = getSupportStorageCode(containers, validation);
		support.storages = getNewStorages(support, validation);
		support.projectCodes = containers.stream().map(c -> c.projectCodes).flatMap(Set::stream).collect(Collectors.toSet());
		support.sampleCodes = containers.stream().map(c -> c.sampleCodes).flatMap(Set::stream).collect(Collectors.toSet());
		support.fromTransformationTypeCodes = containers.stream().map(c -> c.fromTransformationTypeCodes).flatMap(Set::stream).collect(Collectors.toSet());

		support.nbContainers = containers.size();
		support.nbContents = containers.stream().mapToInt(c -> c.contents.size()).sum();

		validation.removeKeyFromRootKeyName("creation.outputSupport."+code);
		return support;
	}

	private static List<StorageHistory> getNewStorages(ContainerSupport support, ContextValidation validation) {
		List<StorageHistory> storages = null;
		if(null != support.storageCode){
			storages = new ArrayList<StorageHistory>();
			StorageHistory sh = getStorageHistory(support.storageCode, storages.size(),validation.getUser());
			storages.add(sh);
			
		}
		return storages;
		
	}
	
	private static List<StorageHistory> updateStorages(ContainerSupport support, ContextValidation validation) {
		if(null != support.storageCode){
			if(support.storages == null){
				support.storages= new ArrayList<StorageHistory>();
			}
			StorageHistory sh = getStorageHistory(support.storageCode, support.storages.size(),validation.getUser());
			support.storages.add(sh);			
		}	
		return support.storages;
	}

	private static StorageHistory getStorageHistory(String storageCode, Integer index, String user) {
		StorageHistory sh = new StorageHistory();
		sh.code = storageCode;
		sh.date = new Date();
		sh.user = user;
		sh.index = index;
		return sh;
	}

	private String getSupportCategoryCode(List<Container> containers, ContextValidation validation) {
		Set<String> categoryCodes = containers.stream().map(c -> c.support.categoryCode).collect(Collectors.toSet());
		if(categoryCodes.size() == 1)
			return categoryCodes.iterator().next();
		else{
			validation.addErrors("categoryCode","different for several containers");
			return null;
		}
	}
	private String getSupportStorageCode(List<Container> containers, ContextValidation validation) {
		Set<String> storageCodes = containers.stream().map(c -> c.support.storageCode).collect(Collectors.toSet());
		if(storageCodes.size() == 1)
			return storageCodes.iterator().next();
		else{
			validation.addErrors("storageCode","different for several containers");
			return null;
		}
	}

	private List<Container> createOutputContainers(Experiment exp, AtomicTransfertMethod atm, ContextValidation validation) {
		Set<String> fromTransformationTypeCodes = getFromTransformationTypeCodes(exp, atm);
		Set<String> fromTransformationCodes = getFromTransformationCodes(exp, atm);
		
		String fromPurificationTypeCode = getFromSatTypeCode(exp, ExperimentCategory.CODE.purification);
		String fromPurificationCode = getFromSatCode(exp, ExperimentCategory.CODE.purification);
		
		String fromTransfertTypeCode =  getFromSatTypeCode(exp, ExperimentCategory.CODE.transfert);
		String fromTransfertCode = getFromSatCode(exp, ExperimentCategory.CODE.transfert);
		
		Map<String, PropertyValue> containerProperties = getCommonPropertiesForALevelWithATM(exp, null, CODE.Container);
		TreeOfLifeNode tree = getTreeOfLifeNode(exp, atm);

		Set<String> processTypeCodes =new HashSet<String>();
		Set<String> inputProcessCodes =new HashSet<String>();

		atm.inputContainerUseds.forEach(icu -> {
			processTypeCodes.addAll(icu.processTypeCodes);
			inputProcessCodes.addAll(icu.processCodes);
		});

		State state = new State("N", validation.getUser());
		TraceInformation traceInformation = new TraceInformation(validation.getUser());
		List<Container> newContainers = new ArrayList<Container>();
		if(atm.outputContainerUseds != null && atm.outputContainerUseds.size() != 0){
			OutputContainerUsed ocu = atm.outputContainerUseds.get(0);
			
			Container c = new Container();
			c.code = ocu.code;
			c.categoryCode = ocu.categoryCode;
			c.contents = ocu.contents;
			c.support = ocu.locationOnContainerSupport;
			Map<String, PropertyValue> outputContainerProperties = getOutputPropertiesForALevel(exp, ocu, CODE.Container);
			outputContainerProperties.putAll(containerProperties);
			c.properties = outputContainerProperties;
			c.concentration = getNullIfNoValue(ocu.concentration);
			c.quantity = getNullIfNoValue(ocu.quantity);
			c.volume = getNullIfNoValue(ocu.volume);
			c.size = getNullIfNoValue(ocu.size);
			c.projectCodes = getProjectsFromContents(c.contents);
			c.sampleCodes = getSamplesFromContents(c.contents);
			c.fromTransformationTypeCodes = fromTransformationTypeCodes;
			c.fromTransformationCodes = fromTransformationCodes;
			c.fromPurificationCode = fromPurificationCode;
			c.fromPurificationTypeCode = fromPurificationTypeCode;
			c.fromTransfertCode = fromTransfertCode;
			c.fromTransfertTypeCode = fromTransfertTypeCode;
			c.processTypeCodes = processTypeCodes;
			c.processCodes = inputProcessCodes;
			c.state = state;
			c.traceInformation = traceInformation;
			c.treeOfLife=tree;
			if(null != ocu.comment){
				c.comments = Collections.singletonList(updateComment(ocu.comment, validation));
			}
			newContainers.add(c);
		}
		
		//if oneToMany you need to create new processes for each new additional container
		if(atm.outputContainerUseds != null && atm.outputContainerUseds.size() > 1){
			//Set<String> allNewInputProcessCodes = new HashSet<String>();
			List<OutputContainerUsed> outputContainerUseds = atm.outputContainerUseds.subList(1, atm.outputContainerUseds.size());
			outputContainerUseds.forEach((OutputContainerUsed ocu) ->{
				Set<String> newInputProcessCodes = duplicateProcesses(inputProcessCodes);
				((Set<String>)validation.getObject(NEW_PROCESS_CODES)).addAll(newInputProcessCodes);
				Container c = new Container();
				c.code = ocu.code;
				c.categoryCode = ocu.categoryCode;
				c.contents = ocu.contents;
				c.support = ocu.locationOnContainerSupport;
				Map<String, PropertyValue> outputContainerProperties = getOutputPropertiesForALevel(exp, ocu, CODE.Container);
				outputContainerProperties.putAll(containerProperties);
				c.properties = outputContainerProperties;
				c.concentration = getNullIfNoValue(ocu.concentration);
				c.quantity = getNullIfNoValue(ocu.quantity);
				c.volume = getNullIfNoValue(ocu.volume);
				c.size = getNullIfNoValue(ocu.size);
				c.projectCodes = getProjectsFromContents(c.contents);
				c.sampleCodes = getSamplesFromContents(c.contents);
				c.fromTransformationTypeCodes = fromTransformationTypeCodes;
				c.fromTransformationCodes = fromTransformationCodes;
				c.fromPurificationCode = fromPurificationCode;
				c.fromPurificationTypeCode = fromPurificationTypeCode;
				c.fromTransfertCode = fromTransfertCode;
				c.fromTransfertTypeCode = fromTransfertTypeCode;				
				c.processTypeCodes = processTypeCodes;
				c.processCodes = newInputProcessCodes;
				c.state = state;
				c.traceInformation = traceInformation;
				c.treeOfLife=tree;
				if(null != ocu.comment){
					c.comments = Collections.singletonList(ocu.comment);
				}
				newContainers.add(c);
			});			
		}

		return newContainers;
	}

	private PropertySingleValue getNullIfNoValue(PropertySingleValue psv) {
		if(null != psv && null == psv.value)return null;
		else return psv;
	}

	private Comment updateComment(Comment comment, ContextValidation validation) {
		comment.createUser = validation.getUser();
		comment.creationDate = new Date();
		comment.code = CodeHelper.getInstance().generateExperimentCommentCode(comment);
		return comment;
	}


	private Set<String> getSamplesFromContents(List<Content> contents) {
		return contents.stream().map(c-> c.sampleCode).collect(Collectors.toSet());
	}


	private Set<String> getProjectsFromContents(List<Content> contents) {
		return contents.stream().map(c -> c.projectCode).collect(Collectors.toSet());
	}


	private Set<String> duplicateProcesses(Set<String> inputProcessCodes) {
		List<Process> processes = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code", inputProcessCodes)).toList();
		Set<String> newInputProcessCodes = new HashSet<String>();
		processes.forEach(p -> {
			p._id = null;
			p.code = CodeHelper.getInstance().generateProcessCode(p); //TO-DO NGL-119 => Quel est le sample d'origine ??
			MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, p);
			newInputProcessCodes.add(p.code);			
		});		
		return newInputProcessCodes;
	}


	private TreeOfLifeNode getTreeOfLifeNode(Experiment exp,	AtomicTransfertMethod atm) {
		TreeOfLifeNode treeNode = new TreeOfLifeNode();

		treeNode.from = new From();
		treeNode.from.experimentCode = exp.code;
		treeNode.from.experimentTypeCode = exp.typeCode;
		treeNode.from.containers = atm.inputContainerUseds.stream().map(icu -> {
			ParentContainers pc = new ParentContainers();
			pc.code = icu.code;
			pc.supportCode = icu.locationOnContainerSupport.code;
			pc.fromTransformationTypeCodes = icu.fromTransformationTypeCodes;
			pc.fromTransformationCodes = icu.fromTransformationCodes;			
			pc.processCodes = icu.processCodes;
			pc.processTypeCodes = icu.processTypeCodes;
			return pc;
		}).collect(Collectors.toList());

		treeNode.paths = new ArrayList<String>();

		atm.inputContainerUseds.forEach(icu -> {
			Container c = MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("code", icu.code));
			if(null != c.treeOfLife && null != c.treeOfLife.paths){
				treeNode.paths.addAll(c.treeOfLife.paths.stream().map(s -> s+","+icu.code).collect(Collectors.toList()));
			}else{
				treeNode.paths.add(","+icu.code);
			}
		});

		return treeNode;
	}

	
	private Set<String> getPropertyDefinitionCodesByLevel(List<PropertyDefinition> propertyDefs, Level.CODE level){

		Level l = new Level(level);

		return propertyDefs.stream().filter(pd -> pd.levels.contains(l)).map(pd -> pd.code).collect(Collectors.toSet());
	}
	
	/**
	 * Filter key by object syntax
	 * @param propertyDefs
	 * @param level
	 * @return
	 */
	private Set<String> getPropertyDefinitionCodesByLevelFilterObject(List<PropertyDefinition> propertyDefs, Level.CODE level){

		Level l = new Level(level);

		return propertyDefs.stream().filter(pd -> pd.levels.contains(l)).map(pd -> pd.code).collect(Collectors.toSet()).stream().map(s->{
			return getKeyPropertiesInstance(s);
		}).collect(Collectors.toSet());
	}
	
	private String getKeyPropertiesInstance(String keyPropertyDefinition)
	{
		if(keyPropertyDefinition.contains("."))
			return keyPropertyDefinition.substring(0, keyPropertyDefinition.indexOf("."));
		else
			return keyPropertyDefinition;
	}
	

	private Map<String, PropertyValue> getOutputPropertiesForALevel(Experiment exp, OutputContainerUsed ocu, Level.CODE level) {
		Map<String, PropertyValue> propertiesForALevel = new HashMap<String, PropertyValue>();

		ExperimentType expType = ExperimentType.find.findByCode(exp.typeCode);
		Set<String> experimentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevelFilterObject(expType.propertiesDefinitions, level);

		if(null != ocu && ocu.experimentProperties != null && experimentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(ocu.experimentProperties.entrySet().stream()
						.filter(entry -> experimentPropertyDefinitionCodes.contains(entry.getKey()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));					
		}

		//extract instrument content properties
		InstrumentUsedType insType = InstrumentUsedType.find.findByCode(exp.instrument.typeCode);
		Set<String> instrumentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevelFilterObject(insType.propertiesDefinitions, level);

		if(null != ocu && ocu.instrumentProperties != null && instrumentPropertyDefinitionCodes.size() > 0){			
			propertiesForALevel.putAll(ocu.instrumentProperties.entrySet().stream()
						.filter(entry -> instrumentPropertyDefinitionCodes.contains(entry.getKey()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));								
		}
		
		return propertiesForALevel;
	}
	
	private Map<String, PropertyValue> getInputPropertiesForALevel(Experiment exp, InputContainerUsed icu, Level.CODE level) {
		Map<String, PropertyValue> propertiesForALevel = new HashMap<String, PropertyValue>();

		ExperimentType expType = ExperimentType.find.findByCode(exp.typeCode);
		Set<String> experimentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevelFilterObject(expType.propertiesDefinitions, level);

		if(null != icu && icu.experimentProperties != null && experimentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(icu.experimentProperties.entrySet().stream()
						.filter(entry -> experimentPropertyDefinitionCodes.contains(entry.getKey()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));					
		}
		
		//extract instrument content properties
		InstrumentUsedType insType = InstrumentUsedType.find.findByCode(exp.instrument.typeCode);
		Set<String> instrumentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevelFilterObject(insType.propertiesDefinitions, level);

		if(null != icu && icu.instrumentProperties != null && instrumentPropertyDefinitionCodes.size() > 0){			
			propertiesForALevel.putAll(icu.instrumentProperties.entrySet().stream()
						.filter(entry -> instrumentPropertyDefinitionCodes.contains(entry.getKey()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));								
		}
		
		//extract process content properties for only the inputContainer of the process
		Set<String> processesPropertyDefinitionCodes = getProcessesPropertyDefinitionCodes(icu, level)
				.stream()
				.collect(Collectors.toList()).stream().map(s->{
					return getKeyPropertiesInstance(s);
				}).collect(Collectors.toSet());
		
		if(processesPropertyDefinitionCodes.size() >0){
			propertiesForALevel.putAll(getProcessesProperties(icu)
					.stream()
					.filter(entry -> processesPropertyDefinitionCodes.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));				
		}
		
		
		return propertiesForALevel;
	}
	
	
	/**
	 * Get all property for a level in expererimentProperties, instrumentProperties and inpoutContainerProperties
	 * NOT INCLUDE OUTPUT CONTAINER PROPERTY USED getOutputPropertiesForALevel METHOD
	 * @param exp
	 * @param atm
	 * @param level
	 * @return
	 */
	private Map<String, PropertyValue> getCommonPropertiesForALevelWithICU(Experiment exp, InputContainerUsed icu, Level.CODE level) {
		Map<String, PropertyValue> propertiesForALevel = new HashMap<String, PropertyValue>();

		ExperimentType expType = ExperimentType.find.findByCode(exp.typeCode);
		Set<String> experimentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevel(expType.propertiesDefinitions, level);

		//extract experiment content properties
		if(null != exp.experimentProperties && experimentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(exp.experimentProperties.entrySet()
					.stream()
					.filter(entry -> experimentPropertyDefinitionCodes.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		}
		
		//extract protocol
		Protocol protocol = MongoDBDAO.findByCode(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, exp.protocolCode);
		//TODO Need to define protocol properties in description but in waiting we just copy all
		if(Level.CODE.Content.equals(level) && null != protocol && null != protocol.properties && protocol.properties.size() > 0){
			propertiesForALevel.putAll(protocol.properties);
		}

		if(null != icu.experimentProperties && icu.experimentProperties.size() > 0){
			propertiesForALevel.putAll(icu.experimentProperties.entrySet().stream()
					.filter(entry -> experimentPropertyDefinitionCodes.contains(entry.getKey()))					
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));
					
		}

		//extract instrument content properties
		InstrumentUsedType insType = InstrumentUsedType.find.findByCode(exp.instrument.typeCode);
		Set<String> instrumentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevel(insType.propertiesDefinitions, level);

		if(null != exp.instrumentProperties && instrumentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(exp.instrumentProperties.entrySet()
					.stream()
					.filter(entry -> instrumentPropertyDefinitionCodes.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		}
		if(null != icu.instrumentProperties && icu.instrumentProperties.size() > 0){
			propertiesForALevel.putAll(icu.instrumentProperties.entrySet().stream()
					.filter(entry -> instrumentPropertyDefinitionCodes.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));
							
		}
		/* Do not extract property from process because the risk to have the same property on several process is very big
		 * To put process property in container used rules*/
		if(null != icu){
			//extract process content properties for only the inputContainer of the process
			List<String> processesPropertyDefinitionCodes = getProcessesPropertyDefinitionCodes(icu, level);					
			if(processesPropertyDefinitionCodes.size() >0){
				propertiesForALevel.putAll(getProcessesProperties(icu)
						.stream()
						.filter(entry -> processesPropertyDefinitionCodes.contains(entry.getKey()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));				
			}
		}

		return propertiesForALevel;
	}
	
	
	/**
	 * Get all property for a level in expererimentProperties, instrumentProperties and inpoutContainerProperties
	 * NOT INCLUDE OUTPUT CONTAINER PROPERTY USED getOutputPropertiesForALevel METHOD
	 * @param exp
	 * @param atm
	 * @param level
	 * @return
	 */
	private Map<String, PropertyValue> getCommonPropertiesForALevelWithATM(Experiment exp, AtomicTransfertMethod atm, Level.CODE level) {
		Map<String, PropertyValue> propertiesForALevel = new HashMap<String, PropertyValue>();

		ExperimentType expType = ExperimentType.find.findByCode(exp.typeCode);
		
		Set<String> experimentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevelFilterObject(expType.propertiesDefinitions, level);

		//extract experiment content properties
		if(null != exp.experimentProperties && experimentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(exp.experimentProperties.entrySet()
					.stream()
					.filter(entry -> experimentPropertyDefinitionCodes.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		}

		//extract protocol
		Protocol protocol = MongoDBDAO.findByCode(InstanceConstants.PROTOCOL_COLL_NAME, Protocol.class, exp.protocolCode);
		//TODO Need to define protocol properties in description but in waiting we just copy all
		if(Level.CODE.Content.equals(level) && null != protocol && null != protocol.properties && protocol.properties.size() > 0){
			propertiesForALevel.putAll(protocol.properties);
		}
		
		/*
		if(null != atm && experimentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(atm.inputContainerUseds.stream()
					.filter((InputContainerUsed icu) -> icu.experimentProperties != null)
					.map((InputContainerUsed icu) -> icu.experimentProperties.entrySet())
					.flatMap(Set::stream)
					.filter(entry -> experimentPropertyDefinitionCodes.contains(entry.getKey()))					
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));
					
		}
		*/
		
		//extract instrument content properties
		InstrumentUsedType insType = InstrumentUsedType.find.findByCode(exp.instrument.typeCode);
		Set<String> instrumentPropertyDefinitionCodes = getPropertyDefinitionCodesByLevelFilterObject(insType.propertiesDefinitions, level);

		if(null != exp.instrumentProperties && instrumentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(exp.instrumentProperties.entrySet()
					.stream()
					.filter(entry -> instrumentPropertyDefinitionCodes.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		}
		
		/*
		if(null != atm && instrumentPropertyDefinitionCodes.size() > 0){
			propertiesForALevel.putAll(atm.inputContainerUseds.stream()
					.filter((InputContainerUsed icu) -> icu.instrumentProperties != null)
					.map((InputContainerUsed icu) -> icu.instrumentProperties.entrySet())
					.flatMap(Set::stream)
					.filter(entry -> instrumentPropertyDefinitionCodes.contains(entry.getKey()))
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));
							
		}
		*/
		
		/* Do not extract property from process because the risk to have the same property on several process is very big
		 * To put process property in container used rules*/
		/*
		if(null != atm){
			//extract process content properties for only the inputContainer of the process
			Set<String> processesPropertyDefinitionCodes = atm.inputContainerUseds.stream()
					.map((InputContainerUsed icu) -> getProcessesPropertyDefinitionCodes(icu, level))
					.flatMap(List::stream)
					.collect(Collectors.toList()).stream().map(s->{
						return getKeyPropertiesInstance(s);
					}).collect(Collectors.toSet());
			
			if(processesPropertyDefinitionCodes.size() >0){
				propertiesForALevel.putAll(atm.inputContainerUseds.stream()
						.map((InputContainerUsed icu) -> getProcessesProperties(icu))
						.flatMap(List::stream)
						.filter(entry -> processesPropertyDefinitionCodes.contains(entry.getKey()))
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (u,v) -> PropertiesMerger(u, v))));				
			}
		}
		*/
		return propertiesForALevel;
	}

	
	private PropertyValue PropertiesMerger(PropertyValue u, PropertyValue v) {
		if(u.value.equals(v.value)){
			return u;
		}else{
			throw new IllegalStateException(String.format("Duplicate key %s with different values", u)); 
		}
    }
	
	private List<String> getProcessesPropertyDefinitionCodes(InputContainerUsed icu, Level.CODE level) {		
		return icu.processTypeCodes.stream()
				.map((String code) ->ProcessType.find.findByCode(code))
				.map((ProcessType p) -> p.getPropertyDefinitionByLevel(level))
				.flatMap(List::stream)
				.map(pd -> pd.code)
				.collect(Collectors.toList());		
	}

	/**
	 * Extract process property for only the first experiment of the process
	 * 
	 */
	private List<Map.Entry<String,PropertyValue>> getProcessesProperties(InputContainerUsed icu) {
		List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code", icu.processCodes).is("inputContainerCode", icu.code)).toList();
		if(null != processes && processes.size() > 0){
			return processes.stream()
					.filter(p -> p.properties != null)
					.map((Process p) -> p.properties.entrySet())
					.flatMap(Set::stream)
					.collect(Collectors.toList());
		}else{
			return new ArrayList<Map.Entry<String, PropertyValue>>();
		}

	}


	public void updateComments(Experiment exp, ContextValidation validation) {
		if(null != exp.comments && exp.comments.size() > 0){
			exp.comments.forEach(comment -> {
				if(comment.createUser == null){
					comment.createUser = validation.getUser();
					comment.creationDate = new Date();
				}else if(comment.creationDate == null){
					comment.creationDate = new Date();
				}

				if(comment.code == null){
					comment.code = CodeHelper.getInstance().generateExperimentCommentCode(comment);	
				}
			});
		}		
	}


	public void updateStatus(Experiment exp, ContextValidation validation) {
		if(!TBoolean.UNSET.equals(exp.status.valid)){
			exp.status.date = new Date();
			exp.status.user = validation.getUser();
		}

	}
	private ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));

	public void callWorkflowRules(ContextValidation validation, Experiment exp) {
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(exp);
		facts.add(validation);
		for(int i=0;i<exp.atomicTransfertMethods.size();i++){
			AtomicTransfertMethod atomic = exp.atomicTransfertMethods.get(i);
			if(atomic.viewIndex == null)atomic.viewIndex = i+1; //used to have the position in the list
			facts.add(atomic);
		}
		
		rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), "workflow", facts),null);
	}

	/**
	 * Update only the qc result and not the container attribut
	 * used in admin case
	 * @param exp
	 * @param validation
	 */
	public void updateQCResultInInputContainers(Experiment exp, ContextValidation validation) {
		exp.atomicTransfertMethods
				.parallelStream()
				.map(atm -> atm.inputContainerUseds)
				.flatMap(List::stream)
				.forEach(icu -> {
					Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,icu.code);
					
					QualityControlResult qcr = container.qualityControlResults.stream()
						.filter(rr -> rr.code.equals(exp.code))
						.findFirst().get();
					
					qcr.properties = icu.experimentProperties;
					qcr.valuation.valid = icu.valuation.valid;
					qcr.valuation.comment = icu.valuation.comment;
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,Container.class, DBQuery.is("code", container.code), DBUpdate.set("qualityControlResults",container.qualityControlResults));
				});

	}
	/**
	 * update volume, concentration, quantity and size only if present
	 * @param exp
	 * @param validation
	 */
	public void updateInputContainers(Experiment exp, ContextValidation validation) {
		Map<String, List<Container>> containersBySupportCode = exp.atomicTransfertMethods
		.parallelStream()
		.map(atm -> atm.inputContainerUseds)
		.flatMap(List::stream)
		.map(icu -> {
			Container c = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class,icu.code);

			/*
			 * TODO
			 * Here to keep the value in entry of experiment replace icu attribute values by input attribute values before update
			 * 
			 */
			
			if(null != icu.newConcentration && null != icu.newConcentration.value)c.concentration = icu.newConcentration;
			if(null != icu.newQuantity && null != icu.newQuantity.value)c.quantity = icu.newQuantity;
			if(null != icu.newVolume && null != icu.newVolume.value)c.volume = icu.newVolume;
			if(null != icu.newSize && null != icu.newSize.value)c.size = icu.newSize;
			if(null != icu.valuation && TBoolean.TRUE.equals(icu.copyValuationToInput)){
				c.valuation = icu.valuation;
				c.valuation.user = validation.getUser();
				c.valuation.date = new Date();
			}

			c.traceInformation.modifyDate = new Date();
			c.traceInformation.modifyUser = validation.getUser();
			
			if(null != icu.locationOnContainerSupport.storageCode){
				c.support.storageCode = icu.locationOnContainerSupport.storageCode;				
			}
			
			if(null == c.qualityControlResults)c.qualityControlResults = new ArrayList<QualityControlResult>(0);

			c.qualityControlResults.add(new QualityControlResult(exp.code, exp.typeCode, c.qualityControlResults.size(), icu.experimentProperties, c.valuation));

			Map<String, PropertyValue> newContentProperties = getCommonPropertiesForALevelWithICU(exp, icu, CODE.Content);
			c.contents.forEach(content -> {
				content.properties.putAll(newContentProperties);
			});
			//TODO Validate Container
			return c;

		}).collect(Collectors.groupingBy(c -> c.support.code));

		//update support storage if needed
		containersBySupportCode.entrySet().forEach(entry -> {
			ContainerSupport support = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,entry.getKey());
			List<Container> containers = entry.getValue();
			
			String oldStorageCode = support.storageCode;
			String newStorageCode =  getSupportStorageCode(containers, validation);
			if(newStorageCode != null && !newStorageCode.equals(oldStorageCode)){
				support.storageCode = newStorageCode;
				support.storages = updateStorages(support, validation);
				
				support.traceInformation.modifyDate = new Date();
				support.traceInformation.modifyUser = validation.getUser();
				
				MongoDBDAO.save(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, support);				
			}
			//update containers
			containers
				.parallelStream()
				.forEach(container -> {
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,container);
				});
			
		});
		
	}
	
	public void updateWithNewSampleCodesIfNeeded(Experiment exp){
		
		ExperimentType experimentType=ExperimentType.find.findByCode(exp.typeCode);
		if(experimentType.newSample){	
			exp.atomicTransfertMethods
				.stream()
				.map(atm -> atm.outputContainerUseds)
				.flatMap(List::stream)				
				.forEach(ocu -> {					
					updateContents(ocu);
				});				
		}
	};
	
	/**
	 * Create new sample code for the output containers in case we want to create another sample
	 * @param exp
	 * @param validation
	 */
	public void createNewSampleCodesIfNeeded(Experiment exp, ContextValidation validation){
		
		ExperimentType experimentType=ExperimentType.find.findByCode(exp.typeCode);
		if(experimentType.newSample){	
			Set<String> newProjectCodes = new TreeSet<String>();
			Set<String> newSampleCodes = new TreeSet<String>();
			
			exp.atomicTransfertMethods
				.stream()
				.map(atm -> atm.outputContainerUseds)
				.flatMap(List::stream)
				.sorted(new Comparator<OutputContainerUsed>() {
					@Override
					public int compare(OutputContainerUsed ocu1,	OutputContainerUsed ocu2) {						
						Content content1 = ocu1.contents.get(0); //in theory only one content;
						Content content2 = ocu2.contents.get(0); //in theory only one content;						
						//TODO Add other field to compare in case of same sample. This other field need to be used in exp.js
						return content1.sampleCode.compareTo(content2.sampleCode);						
					}
					
				})
				.forEach(ocu -> {
					Map<String,PropertyValue> experimentProperties = ocu.experimentProperties;
					
					if(experimentProperties.containsKey("sampleTypeCode") 
							&& experimentProperties.containsKey("projectCode")
							&& !experimentProperties.containsKey("sampleCode")){
						String nextProjectCode = (String)experimentProperties.get("projectCode").value;
						
						String newSampleCode=CodeHelper.getInstance().generateSampleCode(nextProjectCode, true);
						ocu.experimentProperties.put("sampleCode", new PropertySingleValue(newSampleCode));
						
						newProjectCodes.add(nextProjectCode);
						newSampleCodes.add(newSampleCode);
						
					}
					updateContents(ocu);
				});
			
			if(newProjectCodes.size() > 0){
				exp.projectCodes.addAll(newProjectCodes);
			}
			
			if(newSampleCodes.size() > 0){
				exp.sampleCodes.addAll(newSampleCodes);
			}
			
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code", exp.code), 
					DBUpdate.set("atomicTransfertMethods", exp.atomicTransfertMethods).set("projectCodes", exp.projectCodes).set("sampleCodes",exp.sampleCodes));			
		}
	};
	
	
	public void createNewSamplesIfNeeded(Experiment exp, ContextValidation validation){
		ExperimentType experimentType=ExperimentType.find.findByCode(exp.typeCode);
		if(experimentType.newSample){
			validation.putObject(NEW_SAMPLE_CODES, new HashSet<String>());
			
			exp.atomicTransfertMethods
			.stream()
			.forEach(atm -> createNewSamples(exp, atm, validation));			
		}
	}
	
	
	private void createNewSamples(Experiment exp, AtomicTransfertMethod  atm, ContextValidation validation) {
		if(atm.inputContainerUseds.size() == 1 && atm.inputContainerUseds.get(0).contents.size() == 1){
			InputContainerUsed icu = atm.inputContainerUseds.get(0);
			String inputContentSampleCode = atm.inputContainerUseds.get(0).contents.get(0).sampleCode;
			String inputContentProjectCode = atm.inputContainerUseds.get(0).contents.get(0).projectCode;
			
			Sample sampleIn=MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, inputContentSampleCode);

			atm.outputContainerUseds
				.stream()
				.forEach(ocu -> createNewSamplesFromSampleIn(exp, ocu, icu, sampleIn, inputContentProjectCode, validation))
				;
			
		}else{
			throw new RuntimeException("To create a new sample we need to have only one InputContainerUsed with only one Content");
		}		
	}

	private void updateContents(OutputContainerUsed ocu) {
		String sampleTypeCode=ocu.experimentProperties.get("sampleTypeCode").value.toString();
		String projectCode=ocu.experimentProperties.get("projectCode").value.toString();
		String sampleCode=ocu.experimentProperties.get("sampleCode").value.toString();
		String sampleCategoryCode = SampleType.find.findByCode(sampleTypeCode).category.code;
		
		ocu.contents
			.forEach(c -> {
				//Add the fromSampleTypeCode in properties to keep the link with parent type
				PropertySingleValue fromSampleTypeCode = new PropertySingleValue(c.sampleTypeCode);
				c.properties.put("fromSampleTypeCode", fromSampleTypeCode);
				PropertySingleValue fromSampleCode = new PropertySingleValue(c.sampleCode);
				c.properties.put("fromSampleCode", fromSampleCode);
				PropertySingleValue fromProjectCode = new PropertySingleValue(c.projectCode);
				c.properties.put("fromProjectCode", fromProjectCode);
				
				c.projectCode = projectCode;
				c.sampleCode = sampleCode;
				c.sampleTypeCode = sampleTypeCode;
				c.sampleCategoryCode = sampleCategoryCode;
				
			});
	}


	private OutputContainerUsed createNewSamplesFromSampleIn(Experiment exp, OutputContainerUsed ocu,
			InputContainerUsed icu, Sample sampleIn, String inputContentProjectCode, ContextValidation validation) {
		
		String sampleTypeCode=ocu.experimentProperties.get("sampleTypeCode").value.toString();
		String projectCode=ocu.experimentProperties.get("projectCode").value.toString();
		String sampleCode=ocu.experimentProperties.get("sampleCode").value.toString();
		
		if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,sampleCode)){
				
			Sample newSample = new Sample();
			newSample.code=sampleCode;
			newSample.typeCode=sampleTypeCode;
			newSample.categoryCode=SampleType.find.findByCode(sampleTypeCode).category.code;			
			newSample.projectCodes=new HashSet<String>();
			newSample.projectCodes.add(projectCode);
	
			newSample.taxonCode=sampleIn.taxonCode;
			newSample.properties=sampleIn.properties;
			newSample.importTypeCode=sampleIn.importTypeCode;
			newSample.ncbiLineage=sampleIn.ncbiLineage;
			newSample.ncbiScientificName=sampleIn.ncbiScientificName;
			newSample.referenceCollab=sampleIn.referenceCollab;
			newSample.life = getSampleLife(exp, sampleIn, icu, inputContentProjectCode);
			
			newSample.traceInformation=new TraceInformation(validation.getUser());
			
			ContextValidation sampleValidation = new ContextValidation(validation.getUser());
			sampleValidation.setCreationMode();
			newSample.validate(sampleValidation);
			if(!sampleValidation.hasErrors()){
				((Set<String>)validation.getObject(NEW_SAMPLE_CODES)).add(newSample.code);
				MongoDBDAO.save(InstanceConstants.SAMPLE_COLL_NAME,newSample);
			}else{
				validation.addErrors(sampleValidation.errors);
			}
		}
		return ocu;
	}

	
	private SampleLife getSampleLife(Experiment exp, Sample sampleIn,
			InputContainerUsed icu, String projectCode) {
		SampleLife lifeSample = new SampleLife();
		
		lifeSample.from = new models.laboratory.sample.instance.tree.From();
		lifeSample.from.experimentCode = exp.code;
		lifeSample.from.experimentTypeCode = exp.typeCode;
		
		lifeSample.from.containerCode=icu.code;
		lifeSample.from.supportCode=icu.locationOnContainerSupport.code;
		lifeSample.from.processCodes=icu.processCodes;
		lifeSample.from.processTypeCodes=icu.processTypeCodes;
		
		lifeSample.from.projectCode =  projectCode;
		lifeSample.from.sampleCode=sampleIn.code;
		lifeSample.from.sampleTypeCode=sampleIn.typeCode;
		
		if(null != sampleIn.life && null != sampleIn.life.path){
			lifeSample.path=sampleIn.life.path+","+sampleIn.code;
		}else{
			lifeSample.path=","+sampleIn.code;
		}
		
		return lifeSample;
	}


	/**
	 * Delete created sample and reset the last sampleCode on project
	 * @param contextValidation
	 * @param exp
	 */
	public void deleteNewSampleAndRollbackProject(ContextValidation contextValidation, Experiment exp) {
		// TODO Auto-generated method stub
		ExperimentType experimentType=ExperimentType.find.findByCode(exp.typeCode);
		if(experimentType.newSample){
			Set<String> updateProjectCodes = exp.atomicTransfertMethods
					.stream()
					.map(atm -> atm.outputContainerUseds).flatMap(List::stream)
					.filter(ocu -> ocu.experimentProperties.containsKey("projectCode"))
					.map(ocu -> ocu.experimentProperties.get("projectCode").value.toString())
					.collect(Collectors.toSet());
			
			Set<String> deleteSampleCodes = exp.atomicTransfertMethods
				.stream()
				.map(atm -> atm.outputContainerUseds).flatMap(List::stream)
				.filter(ocu -> ocu.experimentProperties.containsKey("sampleCode"))
				.map(ocu -> ocu.experimentProperties.get("sampleCode").value.toString())
				.collect(Collectors.toSet());
			
			if(deleteSampleCodes != null && deleteSampleCodes.size() > 0){
				MongoDBDAO.delete(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.in("code", deleteSampleCodes));
			}
			
			updateProjectCodes.parallelStream().forEach(projectCode -> {
				Sample sample = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.in("projectCodes", projectCode))
					.sort("code", Sort.DESC).limit(1).toList().get(0);
				
				MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, DBQuery.is("code", projectCode),
						DBUpdate.set("lastSampleCode",sample.code));
				
			});
			
		}
	}


	public void removeOutputContainerCode(Experiment exp, ContextValidation errorValidation) {
		exp.atomicTransfertMethods.stream().forEach(atm -> atm.removeOutputContainerCode());
		
	}



	

}


