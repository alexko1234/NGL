package models.utils.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;
import rules.services.RulesException;
import rules.services.RulesServices;
import rules.services.RulesServices6;
import validation.ContextValidation;
import workflows.Workflows;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.cea.ig.MongoDBDAO;

public class ExperimentHelper extends InstanceHelpers {


	public static void generateOutputContainerUsed(Experiment exp, ContextValidation contextValidation) throws DAOException{

		if (!contextValidation.hasErrors()) {
			for(int i=0;i<exp.atomicTransfertMethods.size();i++){
				contextValidation.addKeyToRootKeyName("atomicTransfertMethods["+i+"]");
				exp.atomicTransfertMethods.get(i).createOutputContainerUsed(exp,contextValidation);
				contextValidation.removeKeyFromRootKeyName("atomicTransfertMethods["+i+"]");
			}
		}
	}




	public static void saveOutputContainerUsed(Experiment exp, ContextValidation contextValidation) throws DAOException{

		if (!contextValidation.hasErrors()) {
			for(int i=0;i<exp.atomicTransfertMethods.size();i++){
				contextValidation.errors.putAll(exp.atomicTransfertMethods.get(i).saveOutputContainers(exp, contextValidation).errors);
			}

			List<String> containerSupportCodes=ExperimentHelper.getOutputContainerSupportCodes(exp);
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,DBQuery.is("code", exp.code)
					,DBUpdate.set("outputContainerSupportCodes", containerSupportCodes));

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

	public static Experiment updateData(Experiment exp) {
		exp.sampleCodes = new ArrayList<String>();
		exp.projectCodes  = new ArrayList<String>();

		for(int i=0;i<exp.atomicTransfertMethods.size();i++)
			for(ContainerUsed c:exp.atomicTransfertMethods.get(i).getInputContainers()){
				Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, c.code);
				exp.sampleCodes = InstanceHelpers.addCodesList(container.sampleCodes,exp.sampleCodes);
				exp.projectCodes = InstanceHelpers.addCodesList(container.projectCodes,exp.projectCodes);
				exp.inputContainerSupportCodes=ExperimentHelper.getInputContainerSupportCodes(exp);
			}	
		return exp;
	}


	public static Map<String,PropertyValue> getAllPropertiesFromAtomicTransfertMethod(AtomicTransfertMethod atomicTransfertMethod,Experiment experiment){
		List<ContainerUsed> inputContainerUseds=atomicTransfertMethod.getInputContainers();

		Map<String,PropertyValue> properties=new HashMap<String, PropertyValue>();
		if(experiment.experimentProperties!=null){
			properties.putAll(experiment.experimentProperties);
		}	
		if(experiment.instrumentProperties!=null){
			properties.putAll(experiment.instrumentProperties);
		}
		for(ContainerUsed inputContainerUsed:inputContainerUseds){

			if(inputContainerUsed.experimentProperties!=null)
				properties.putAll(inputContainerUsed.experimentProperties);
			if(inputContainerUsed.instrumentProperties!=null)
				properties.putAll(inputContainerUsed.instrumentProperties);
		}		

		List<ContainerUsed> outputContainerUseds=atomicTransfertMethod.getOutputContainers();
		for(ContainerUsed outputContainerUsed:outputContainerUseds){
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
		for(ContainerUsed containerUsed:exp.getAllInPutContainer()){
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
	public static List<String> getOutputContainerSupportCodes(Experiment exp){
		List<String> codes = new ArrayList<String>();
		List<ContainerUsed> containersUSed=new ArrayList<ContainerUsed>();
		if(exp.atomicTransfertMethods!=null){
			for(int i = 0; i < exp.atomicTransfertMethods.size() ; i++){
				if(exp.atomicTransfertMethods.get(i).getInputContainers().size()!=0){
					containersUSed.addAll(exp.atomicTransfertMethods.get(i).getOutputContainers());
				}
			}
			for(int i = 0; i < containersUSed.size(); i++)
			{
				if(containersUSed.get(i).locationOnContainerSupport==null){
					InstanceHelpers.addCode(containersUSed.get(i).code, codes);
				}else {
					InstanceHelpers.addCode(containersUSed.get(i).locationOnContainerSupport.code, codes);
				}
			}
		}
		return codes;
	}




	@JsonIgnore
	public static List<String> getInputContainerSupportCodes(Experiment exp){
		List<String> codes = new ArrayList<String>();
		List<ContainerUsed> containersUSed=new ArrayList<ContainerUsed>();
		if(exp.atomicTransfertMethods!=null){
			for(int i = 0; i < exp.atomicTransfertMethods.size() ; i++){
				if(exp.atomicTransfertMethods.get(i).getInputContainers().size()!=0){
					containersUSed.addAll(exp.atomicTransfertMethods.get(i).getInputContainers());
				}
			}
			for(int i = 0; i < containersUSed.size(); i++)
			{
				String code;
				if(containersUSed.get(i).locationOnContainerSupport!=null){
					code=containersUSed.get(i).locationOnContainerSupport.code; 
					InstanceHelpers.addCode(code, codes);
				}
				else { Logger.error("No locationOnContainerSupport in ContainerUSed "+ containersUSed.get(i).code);}	
			}
		}
		return codes;
	}


	public static void doCalculations(Experiment exp,String rulesName){
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(exp);
		for(int i=0;i<exp.atomicTransfertMethods.size();i++){
			if(ManytoOneContainer.class.isInstance(exp.atomicTransfertMethods.get(i))){
				ManytoOneContainer atomic = (ManytoOneContainer) exp.atomicTransfertMethods.get(i);
				facts.add(atomic);
			}
		}

		List<Object> factsAfterRules = RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), rulesName, facts);
		
		for(Object obj:factsAfterRules){
			if(ManytoOneContainer.class.isInstance(obj)){
				exp.atomicTransfertMethods.remove(((ManytoOneContainer)obj).position-1);
				exp.atomicTransfertMethods.put(((ManytoOneContainer)obj).position-1,(ManytoOneContainer) obj);
			}
		}

	}

	public static void cleanContainers(Experiment experiment, ContextValidation contextValidation){
		//load experiment from mongoDB
		Experiment exp = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, experiment.code);
		//extract containerIn From DB
		List<ContainerUsed> containersInFromDB = exp.getAllInPutContainer();
		List<ContainerUsed> containersIn = experiment.getAllInPutContainer();
		
		List<ContainerUsed> addedContainers = getDiff(containersIn,containersInFromDB);
		if(addedContainers.size() > 0){
			Workflows.nextInputContainerState(experiment, addedContainers, contextValidation, false, false, null);
			
			for(ContainerUsed addedContainer:addedContainers){
				//add the current experiment in the process and the experiment in the list of experiment
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("containerInputCode", addedContainer.code), DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code));
			}
		}
		
		List<ContainerUsed> deletedContainers = getDiff(containersInFromDB,containersIn);
		if(deletedContainers.size() > 0){
			Workflows.previousContainerState(deletedContainers,exp.code, exp.typeCode, contextValidation);
		}
	}
	
	private static List<ContainerUsed> getDiff(List<ContainerUsed> containersFrom, List<ContainerUsed> containersTo){
		List<ContainerUsed> containerDiff = new ArrayList<ContainerUsed>();
		boolean found = false;
		for(ContainerUsed cf:containersFrom){
			String code = cf.code;
			found = false;
			for(ContainerUsed c:containersTo){
				if(code.equals(c.code)){
					found = true;
					break;
				}
			}
			if(!found){
				containerDiff.add(cf);
			}
		}
		
		return containerDiff;
	}
}
