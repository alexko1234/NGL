package workflows;

import java.util.Date;
import java.util.List;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.processes.instance.Process;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.StateHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.CommonController;
import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.container.instance.ContainerValidationHelper;
import validation.experiment.instance.ExperimentValidationHelper;
import validation.processes.instance.ProcessValidationHelper;
import fr.cea.ig.MongoDBDAO;

public class Workflows {


	/**
	 * Set a state of an experiment 
	 * @param experiment: the experiment, errors: the filledForm errors
	 */
	public static void setExperimentState(Experiment experiment, State nextState, ContextValidation ctxValidation){

		ctxValidation.getContextObjects().put("stateCode", nextState.code);
		ExperimentValidationHelper.validateState(experiment.typeCode, nextState, ctxValidation);

		//il fau peut etre valider tout l'experiment quand elle passe à "F"
		ExperimentValidationHelper.validateNewState(experiment, ctxValidation);

		if(!ctxValidation.hasErrors() && !nextState.code.equals(experiment.state)){

			experiment.traceInformation=StateHelper.getUpdateTraceInformation(experiment.traceInformation, ctxValidation.getUser());  
			experiment.state = StateHelper.updateHistoricalNextState(experiment.state, nextState);
			experiment.state=nextState;


			if(experiment.state.code.equals("IP")){
				try {
					ExperimentHelper.generateOutputContainerUsed(experiment, ctxValidation);
					if(!ctxValidation.hasErrors()){
						MongoDBDAO.save(InstanceConstants.EXPERIMENT_COLL_NAME, experiment);
					}
				} catch (DAOException e) {
					throw new RuntimeException();
				}
			}else if(experiment.state.code.equals("F")){
				try {
					ExperimentHelper.saveOutputContainerUsed(experiment, ctxValidation);
				} catch (DAOException e) {
					throw new RuntimeException();
				}
				Logger.debug("Apres saveOutputContainerUsed");
				if(!ctxValidation.hasErrors()){
					nextOutputContainerState(experiment, ctxValidation);
				}

			}

			if(!ctxValidation.hasErrors()){
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME,  Experiment.class, 
						DBQuery.is("code", experiment.code),
						DBUpdate.set("state", experiment.state).set("traceInformation",experiment.traceInformation));
			}

			if(!ctxValidation.hasErrors()){
				nextInputContainerState(experiment, ctxValidation);
			}
		}
	}

	public static void nextExperimentState(Experiment experiment,ContextValidation contextValidation){
		State state = StateHelper.cloneState(experiment.state);

		if(experiment.state == null || experiment.state.code.equals("")){
			state.code = "N";
		}else if(experiment.state.code.equals("N")){
			state.code = "IP";
		}else if(experiment.state.code.equals("IP")){
			state.code = "F";
		}

		setExperimentState(experiment, state, contextValidation);
	}

	public static void nextInputContainerState(Experiment experiment,ContextValidation contextValidation){
		State state=new State();
		state.date=new Date();
		state.user=contextValidation.getUser();

		if(experiment.state.code.equals("N")){
			state.code= "IW-E"; 
		}else if(experiment.state.code.equals("IP")){
			state.code= "IU";
		}else if(experiment.state.code.equals("F")){
			if(experiment.categoryCode.equals("qualitycontrol")){
				state.code="IW-V";
			}else {
				//Mettre à jour l'etat en fonction du volume restant
				state.code= "IS";
			}
		}

		if(state.code!=null){
			setContainerState(experiment.getAllInPutContainer(), experiment, state, contextValidation);
			//Il faut mettre à jour le state du container dans l'experiment.atomicTransfereMethod
		}
	}

	public static void nextOutputContainerState(Experiment experiment,ContextValidation contextValidation) {
		for(ContainerUsed containerUsed:experiment.getAllOutPutContainerWhithInPutContainer()){

			State nextState=new State();
			nextState.user=experiment.traceInformation.modifyUser;

			if(experiment.categoryCode.equals("transformation")){
				if(experiment.state.code.equals("F") && doQC(experiment)){
					nextState.code="A-QC";
				}/*else if(experiment.state.code.equals("F") && doPurif()){
				nextState.code="A-PURIF";
			}else if(experiment.state.code.equals("F") && doTransfert()){
				nextState.code="A-TRANSFERT";
				}*/else if(experiment.state.code.equals("F") && endOfProcess(containerUsed,experiment.typeCode)){
					if(experiment.typeCode.equals("opgen-depot")){
						nextState.code="F";
					}else {
						nextState.code="IW-P";
					}
				}else {
					nextState.code="A";
				}
			}

			if(experiment.categoryCode.equals("purification") || experiment.categoryCode.equals("transfert")){
				if(experiment.state.code.equals("F")){
					nextState.code="IW-V";
				}
			}

			if(nextState.code!=null && containerUsed!=null){
				setContainerState(containerUsed.code, experiment, nextState, contextValidation);
				//Il faut mettre à jour le state du container dans l'experiment.atomicTransfereMethod
			}
		}

	}


	private static boolean endOfProcess(ContainerUsed containerUsed, String experimentTypeCode) {
		Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class,containerUsed.code);
		ProcessType processType;
		try {
			Logger.debug("Container :"+containerUsed.code);
			if(container.getCurrentProcesses()==null) return true;
			processType = ProcessType.find.findByCode(container.getCurrentProcesses().get(0).typeCode);
			if(processType.lastExperimentType.code.equals(experimentTypeCode)){
				return true;
			}else {
				return false;
			}

		} catch (DAOException e) {
			throw new RuntimeException();
		}
	}


	private static boolean endOfProcess(String processCode, String experimentTypeCode) {
		Process process=MongoDBDAO.findByCode(InstanceConstants.PROCESS_COLL_NAME,Process.class,processCode);
		ProcessType processType;
		try {
			Logger.debug("Process "+processCode);
			processType = ProcessType.find.findByCode(process.typeCode);
			if(processType.lastExperimentType.code.equals(experimentTypeCode)){
				return true;
			}else {
				return false;
			}

		} catch (DAOException e) {
			throw new RuntimeException();
		}
	}

	private static boolean nextExperiment(String typeCode) {
		List<ExperimentType> experimentTypes;
		try {
			experimentTypes = ExperimentType.find.findNextExperimentTypeForAnExperimentTypeCode(typeCode);
			if(experimentTypes!=null && experimentTypes.size() >0){
				return true;
			}else {
				return false;
			}

		} catch (DAOException e) {
			throw new RuntimeException();
		}
	}


	private static boolean doQC(Experiment experiment) {
		try{
			ExperimentTypeNode experimentTypeNode=ExperimentTypeNode.find.findByCode(experiment.typeCode);
			return experimentTypeNode.doQualityControl;
		}catch (DAOException e){
			throw new RuntimeException();
		}

	}

	public static void nextProcessState(Container container, Experiment exp,ContextValidation contextValidation){
		if(container.inputProcessCodes!=null ){
			for(String processCode: container.inputProcessCodes){

				State processState=new State();
				processState.date=new Date();
				processState.user=contextValidation.getUser();

				if(container.state.code.equals("IU") && checkProcessState("N",processCode)){
					processState.code="IP";
				}else if((container.state.code.equals("UA") || container.state.code.equals("IS") ) && checkProcessState("IP",processCode) && endOfProcess(processCode, exp.typeCode)){
					processState.code="F";
				}

				if(processState.code != null){
					setProcessState(processCode,processState,contextValidation);					
				}

				/*if(exp.state.code.equals("F")){
					Logger.debug("Update inputProcessCodes");
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code", container.code),DBUpdate.unset("inputProcessCodes"));
				}*/
			}
		}
	}

	private static boolean checkProcessState(String stateCode,
			String processCode) {
		return MongoDBDAO.checkObjectExist(InstanceConstants.PROCESS_COLL_NAME,Process.class, DBQuery.is("code", processCode).is("state.code", stateCode));
	}

	private static void setProcessState(String  processCode, State nextState,
			ContextValidation contextValidation) {
		Process process =MongoDBDAO.findOne(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("code", processCode));

		if(process!=null){ 
			ProcessValidationHelper.validateStateCode(nextState.code,contextValidation);
			if(!contextValidation.hasErrors() && !nextState.code.equals(process.state)){


				process.state=StateHelper.updateHistoricalNextState(process.state,nextState);
				process.traceInformation=StateHelper.updateTraceInformation(process.traceInformation, nextState);

				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,  Process.class, 
						DBQuery.is("code", process.code),
						DBUpdate.set("state", process.state).set("traceInformation",process.traceInformation));

				//Process F, reset fromExperimentTypeCodes if Collab's container 
				if(process.state.code.equals("F")){
					ProcessType processType;
					try {
						processType = ProcessType.find.findByCode(process.typeCode);
						MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,Container.class
								,DBQuery.is("code", process.containerInputCode).in("fromExperimentTypeCodes", processType.voidExperimentType.code)
								,DBUpdate.unset("fromExperimentTypeCodes"));
					} catch (DAOException e) {
					}
				}
			}	

		}

	}

	public static void setContainerState(String containerCode,Experiment exp, State nextState,ContextValidation contextValidation){
		Container container =MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", containerCode));

		if(container==null){
			Logger.error("Container "+containerCode+" not exists");
		} 
		String lastStateCode=container.state.code;
		container.state=StateHelper.updateHistoricalNextState(container.state,nextState);
		container.traceInformation=StateHelper.updateTraceInformation(container.traceInformation, nextState);
		//Validate state for Container
		contextValidation.addKeyToRootKeyName("container");
		ContainerValidationHelper.validateStateCode(container, contextValidation);
		contextValidation.removeKeyFromRootKeyName("container");
		if(!contextValidation.hasErrors() && !nextState.code.equals(lastStateCode)){
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,  Container.class, 
					DBQuery.is("code", container.code),
					DBUpdate.set("state", container.state).set("traceInformation",container.traceInformation));
		}	
		container.state=nextState;
		nextContainerSupportState(container,contextValidation);
		nextProcessState(container,exp,contextValidation);
	}

	private static void nextContainerSupportState(Container container,
			ContextValidation contextValidation) {
		State nextState=new State(container.state.code,container.state.user);
		//Pour le moment des qu'une container change d'etat sont support à la meme etat
		setContainerSupportState(container.support.code, nextState,contextValidation);
	}

	public static void setContainerSupportState(String code,State nextState,ContextValidation contextValidation) {

		ContainerSupport containerSupport = MongoDBDAO.findOne(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", code));

		if(containerSupport==null){
			Logger.error("ContainerSupport "+containerSupport+" not exists");
		}

		if(!contextValidation.hasErrors() && !nextState.code.equals(containerSupport.state.code)){

			containerSupport.state=StateHelper.updateHistoricalNextState(containerSupport.state,nextState);
			containerSupport.traceInformation=StateHelper.updateTraceInformation(containerSupport.traceInformation, nextState);

			MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME,  Container.class, 
					DBQuery.is("code", containerSupport.code),
					DBUpdate.set("state", containerSupport.state).set("traceInformation",containerSupport.traceInformation));
		}	


	}


	public static void setContainerState(List<ContainerUsed> containersUsed, Experiment exp,State state,ContextValidation contextValidation){
		for(ContainerUsed containerUsed:containersUsed){
			Workflows.setContainerState(containerUsed.code, exp, state, contextValidation);
		}
	}

 
	public static void nextContainerState(List<Process> processes, Experiment exp,
			ContextValidation contextValidation) {
		
		for(Process process:processes){
			nextContainerState(process, exp, contextValidation);
		}
	}
 
	

	public static void nextContainerState(Process process, Experiment exp,
			ContextValidation contextValidation) {
		State nextState=new State();
		if(process.state != null && process.state.code.equals("N")){
			nextState.code="A";
		}

		setContainerState(process.containerInputCode, exp,nextState, contextValidation);

	}


}
