package models.utils.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.ManyToOneContainer;
import models.laboratory.experiment.instance.OneToOneContainer;
import models.laboratory.experiment.instance.OutputContainerUsed;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;
import rules.services.RulesServices6;
import validation.ContextValidation;
import workflows.container.ContainerWorkflows;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.MongoDBDAO;

public class ExperimentHelper extends InstanceHelpers {

	@Deprecated
	public static void generateOutputContainerUsed(Experiment exp, ContextValidation contextValidation) throws DAOException{

		if (!contextValidation.hasErrors()) {
			for(int i=0;i<exp.atomicTransfertMethods.size();i++){
				contextValidation.addKeyToRootKeyName("atomicTransfertMethods["+i+"]");
				exp.atomicTransfertMethods.get(i).createOutputContainerUsed(exp,contextValidation);
				contextValidation.removeKeyFromRootKeyName("atomicTransfertMethods["+i+"]");
			}
		}
	}



	@Deprecated
	public static void saveOutputContainerUsed(Experiment exp, ContextValidation contextValidation) throws DAOException{

		if (!contextValidation.hasErrors()) {
			for(int i=0;i<exp.atomicTransfertMethods.size();i++){
				contextValidation.errors.putAll(exp.atomicTransfertMethods.get(i).saveOutputContainers(exp, contextValidation).errors);
			}

			exp.outputContainerSupportCodes=ExperimentHelper.getOutputContainerSupportCodes(exp);
			
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,DBQuery.is("code", exp.code)
					,DBUpdate.set("outputContainerSupportCodes", exp.outputContainerSupportCodes));

		}

	}

	public static Experiment updateInstrumentCategory(Experiment exp) throws DAOException{
		Logger.debug("Test categoryCode :"+exp.instrument.categoryCode+" .");
		if((exp.instrument.categoryCode == null ||exp.instrument.categoryCode.equals("") ) && exp.instrument.typeCode!=null){
			InstrumentUsedType instrumentUsedType=InstrumentUsedType.find.findByCode(exp.instrument.typeCode);
			Logger.debug("Result categoryCode"+instrumentUsedType.category.code);
			exp.instrument.categoryCode=instrumentUsedType.category.code;
		}
		return exp;	
	}
	@Deprecated
	public static Experiment updateXCodes(Experiment exp) {
		exp.sampleCodes = new HashSet<String>();
		exp.projectCodes  = new HashSet<String>();
		exp.inputContainerSupportCodes  = new HashSet<String>();
		for(int i=0;i<exp.atomicTransfertMethods.size();i++){
			if(exp.atomicTransfertMethods.get(i)!=null && exp.atomicTransfertMethods.get(i).inputContainerUseds.size()>0){
				for(InputContainerUsed c:exp.atomicTransfertMethods.get(i).inputContainerUseds){
					Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, c.code);
					
					if(container!=null){
						if(CollectionUtils.isNotEmpty(container.sampleCodes)){
							exp.sampleCodes.addAll(container.sampleCodes);
						}
						if(CollectionUtils.isNotEmpty(container.projectCodes)){
							exp.projectCodes.addAll(container.projectCodes);
						}						
					}
					exp.inputContainerSupportCodes.add(container.support.code);
					//exp.inputContainerSupportCodes=getInputContainerSupportCodes(exp);
				}	
			}
		}
			
		return exp;
	}


	public static Map<String,PropertyValue> getAllPropertiesFromAtomicTransfertMethod(AtomicTransfertMethod atomicTransfertMethod,Experiment experiment){
		List<InputContainerUsed> inputContainerUseds=atomicTransfertMethod.inputContainerUseds;

		Map<String,PropertyValue> properties=new HashMap<String, PropertyValue>();
		if(experiment.experimentProperties!=null){
			properties.putAll(experiment.experimentProperties);
		}	
		if(experiment.instrumentProperties!=null){
			properties.putAll(experiment.instrumentProperties);
		}
		for(InputContainerUsed inputContainerUsed:inputContainerUseds){

			if(inputContainerUsed.experimentProperties!=null)
				properties.putAll(inputContainerUsed.experimentProperties);
			if(inputContainerUsed.instrumentProperties!=null)
				properties.putAll(inputContainerUsed.instrumentProperties);
		}		

		List<OutputContainerUsed> outputContainerUseds=atomicTransfertMethod.outputContainerUseds;
		for(OutputContainerUsed outputContainerUsed:outputContainerUseds){
			if(outputContainerUsed.experimentProperties!=null)
				properties.putAll(outputContainerUsed.experimentProperties);
			if(outputContainerUsed.instrumentProperties!=null)
				properties.putAll(outputContainerUsed.instrumentProperties);
		}

		return properties;
	}

	public static List<String> getAllProcessCodesFromExperiment(Experiment exp){

		List<String> containerCodes=new ArrayList<String>();
		List<String> processCodes=new ArrayList<String>();
		for(InputContainerUsed containerUsed:exp.getAllInputContainers()){
			containerCodes.add(containerUsed.code);
		}

		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.in("code",containerCodes)).toList();

		if(CollectionUtils.isNotEmpty(containers))
		{
			for(Container container:containers){
				if(CollectionUtils.isNotEmpty(container.inputProcessCodes))
					processCodes.addAll(container.inputProcessCodes);
			}
		}
		return processCodes;

	}




	@JsonIgnore
	public static Set<String> getOutputContainerSupportCodes(Experiment exp){
		Set<String> codes = new HashSet<String>();
		List<OutputContainerUsed> containersUsed=new ArrayList<OutputContainerUsed>();
		if(exp.atomicTransfertMethods!=null){
			for(int i = 0; i < exp.atomicTransfertMethods.size() ; i++){
				if(exp.atomicTransfertMethods.get(i).outputContainerUseds != null && exp.atomicTransfertMethods.get(i).outputContainerUseds.size()!=0){
					containersUsed.addAll(exp.atomicTransfertMethods.get(i).outputContainerUseds);
				}
			}
			for(int i = 0; i < containersUsed.size(); i++)
			{
				if(containersUsed.get(i).locationOnContainerSupport==null){
					codes.add(containersUsed.get(i).code);
				//	InstanceHelpers.addCode(containersUSed.get(i).code, codes);
				}else {
					codes.add(containersUsed.get(i).locationOnContainerSupport.code);
				//	InstanceHelpers.addCode(containersUSed.get(i).locationOnContainerSupport.code, codes);
				}
			}
		}
		return codes;
	}




	@JsonIgnore
	public static Set<String> getInputContainerSupportCodes(Experiment exp){
		Set<String> codes = new HashSet<String>();
		List<InputContainerUsed> containersUsed=new ArrayList<InputContainerUsed>();
		if(exp.atomicTransfertMethods!=null){
			for(int i = 0; i < exp.atomicTransfertMethods.size() ; i++){
				if(exp.atomicTransfertMethods.get(i)!=null && exp.atomicTransfertMethods.get(i).inputContainerUseds.size()!=0){
					containersUsed.addAll(exp.atomicTransfertMethods.get(i).inputContainerUseds);
				}
			}
			for(int i = 0; i < containersUsed.size(); i++)
			{
				String code;
				if(containersUsed.get(i).locationOnContainerSupport!=null){
					code=containersUsed.get(i).locationOnContainerSupport.code;
					codes.add(code);
					//InstanceHelpers.addCode(code, codes);
				}
				else { Logger.error("No locationOnContainerSupport in ContainerUSed "+ containersUsed.get(i).code);}	
			}
		}
		return codes;
	}


	public static void doCalculations(Experiment exp,String rulesName){
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(exp);
		for(int i=0;i<exp.atomicTransfertMethods.size();i++){
			if(ManyToOneContainer.class.isInstance(exp.atomicTransfertMethods.get(i))){
				ManyToOneContainer atomic = (ManyToOneContainer) exp.atomicTransfertMethods.get(i);
				facts.add(atomic);
			}
			if(OneToOneContainer.class.isInstance(exp.atomicTransfertMethods.get(i))){
				OneToOneContainer atomic = (OneToOneContainer) exp.atomicTransfertMethods.get(i);
				facts.add(atomic);
			}
		}
		
		List<Object> factsAfterRules = RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), rulesName, facts);
		
		for(Object obj:factsAfterRules){
			if(ManyToOneContainer.class.isInstance(obj)){
				exp.atomicTransfertMethods.remove((ManyToOneContainer)obj);
				exp.atomicTransfertMethods.add((ManyToOneContainer) obj);
			}
		}
		
	}

	@Deprecated
	public static void cleanContainers(Experiment experiment, ContextValidation contextValidation){
		//load experiment from mongoDB
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, experiment.code);
		//extract containerIn From DB
		List<InputContainerUsed> containersInFromDB = exp.getAllInputContainers();
		List<InputContainerUsed> containersIn = experiment.getAllInputContainers();
		
		List<Container> addedContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
				DBQuery.in("code",getDiff(containersIn,containersInFromDB))).toList();
		if(addedContainers.size() > 0){
			ContainerWorkflows.setContainerState(addedContainers, "IW-E", contextValidation);
			
			for(Container addedContainer:addedContainers){
				//add the current experiment in the process and the experiment in the list of experiment
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.and(DBQuery.or(DBQuery.in("containerInputCode", addedContainer.code),DBQuery.in("newContainerSupportCodes", addedContainer.code)),DBQuery.notEquals("state.code", "F")), DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code));
			}
		}
		
		List<Container> deletedContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class
				,DBQuery.in("code", getDiff(containersInFromDB,containersIn))).toList();
		if(deletedContainers.size() > 0){
			
			String nextContainerState=ContainerWorkflows.getAvailableContainerStateFromExperimentCategory(exp.categoryCode);			
			ContainerWorkflows.setContainerState(deletedContainers, nextContainerState, contextValidation);
			for(Container deletedContainer:deletedContainers){
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.and(DBQuery.or(DBQuery.in("containerInputCode", deletedContainer.code),DBQuery.in("newContainerSupportCodes", deletedContainer.code)),DBQuery.notEquals("state.code", "F")), DBUpdate.unset("currentExperimentTypeCode").pull("experimentCodes", exp.code));
			}
		}
	}
	@Deprecated
	public static List<String> getDiff(List<InputContainerUsed> containersFrom, List<InputContainerUsed> containersTo){
		
		containersFrom=flattenContainerUsed(containersFrom);
		containersTo=flattenContainerUsed(containersTo);
		List<String> containerDiff = new ArrayList<String>();
		boolean found = false;
		for(InputContainerUsed cf:containersFrom){
			String code = cf.code;
			found = false;
			for(InputContainerUsed c:containersTo){
				if(StringUtils.isNotBlank(code) && code.equals(c.code)){
					found = true;
					break;
				}
			}
			if(!found){
				containerDiff.add(cf.code);
			}
		}
		
		return containerDiff;
	}
	
	@Deprecated
	private static List<InputContainerUsed> flattenContainerUsed(List<InputContainerUsed> containerUseds){
		List<InputContainerUsed> results=new ArrayList<InputContainerUsed>(containerUseds);
		for(int i=0;i<containerUseds.size();i++){
			String code=containerUseds.get(i).code;
			boolean delete=false;
			for(InputContainerUsed containerUsed:containerUseds){
				if(StringUtils.isNotBlank(code) && code.equals(containerUsed.code)){
					if(!delete){
						delete=true;
					}
					else {results.remove(containerUsed);}
				}
			}
		}
		return results;
	}





}
