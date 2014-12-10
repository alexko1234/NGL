package models.utils.instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.ManytoOneContainer;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;
import rules.services.RulesException;
import rules.services.RulesServices;
import validation.ContextValidation;

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

			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,DBQuery.is("code", exp.code)
					,DBUpdate.set("outputContainerSupportCodes", ExperimentHelper.getOutputContainerSupportCodes(exp)));
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
		
		for(Container container:containers){
			processCodes.addAll(container.inputProcessCodes);
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
				codes.add(containersUSed.get(i).locationOnContainerSupport.code);
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
				if(containersUSed.get(i).locationOnContainerSupport==null){
					code=containersUSed.get(i).code;
				}else { code=containersUSed.get(i).locationOnContainerSupport.code;}
				codes.add(code);
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
	
		RulesServices rulesServices = new RulesServices();
		List<Object> factsAfterRules = null;
		try {
			factsAfterRules = rulesServices.callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), rulesName, facts);
		} catch (RulesException e) {
			throw new RuntimeException();
		}
	
		for(Object obj:factsAfterRules){
			if(ManytoOneContainer.class.isInstance(obj)){
				exp.atomicTransfertMethods.remove(((ManytoOneContainer)obj).position-1);
				exp.atomicTransfertMethods.put(((ManytoOneContainer)obj).position-1,(ManytoOneContainer) obj);
			}
		}
	
	}

}
