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
import models.laboratory.instrument.description.InstrumentUsedType;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;

import play.Logger;
import validation.ContextValidation;
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

}
