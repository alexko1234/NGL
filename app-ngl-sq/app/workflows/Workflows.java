package workflows;

import java.util.Date;
import java.util.List;

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
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.Logger;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import validation.experiment.instance.ExperimentValidationHelper;
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

			InstanceHelpers.updateTraceInformation(experiment.traceInformation);  
			experiment.state = StateHelper.updateHistoricalNextState(experiment.state, nextState);
			experiment.state=nextState;

			if(!ctxValidation.hasErrors()){
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME,  Experiment.class, 
						DBQuery.is("code", experiment.code),
						//DBUpdate.set("state", experiment.state).set("traceInformation",experiment.traceInformation));
						DBUpdate.set("state", experiment.state).set("traceInformation",experiment.traceInformation));
			}

			if(experiment.state.code.equals("IP")){
				try {
					ExperimentHelper.generateOutputContainerUsed(experiment, ctxValidation);
				} catch (DAOException e) {
					throw new RuntimeException();
				}
			}else if(experiment.state.code.equals("F")){
				ExperimentHelper.saveOutputContainerUsed(experiment, ctxValidation);
				if(!ctxValidation.hasErrors()){
					nextOutputContainerState(experiment, ctxValidation);
				}

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
		state.user=InstanceHelpers.getUser();

		if(experiment.state.code.equals("N")){
			state.code= "IW-E"; 
		}else if(experiment.state.code.equals("IP")){
			state.code= "IU";
		}else if(experiment.state.code.equals("F")){
			if(experiment.categoryCode.equals("qualitycontrol")){
				state.code="IW-V";
			}else {
				//Mettre à jour l'etat en fonction du volume restant
				state.code= "UA";
			}
		}

		if(state.code!=null){
			setContainerState(experiment.getAllInPutContainer(), state, contextValidation);
		}
	}

	public static void nextOutputContainerState(Experiment experiment,ContextValidation contextValidation) {
		for(ContainerUsed containerUsed:experiment.getAllOutPutContainer()){

			State nextState=new State();
			nextState.user=experiment.traceInformation.modifyUser;

			if(experiment.categoryCode.equals("transformation")){
				if(experiment.state.code.equals("F") && doQC(experiment)){
					//nextState.code="A-QC";
					nextState.code="IW-QC";
				}/*else if(experiment.state.code.equals("F") && doPurif()){
				nextState.code="A-PURIF";
			}else if(experiment.state.code.equals("F") && doTransfert()){
				nextState.code="A-TRANSFERT";
				}*/else if(experiment.state.code.equals("F") && endOfProcess(containerUsed.code,experiment.typeCode)){
				nextState.code="IW-P";
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
				setContainerState(containerUsed.code, nextState, contextValidation);
			}
		}

	}


	private static boolean endOfProcess(String code, String typeCode) {
		Container container=MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME,Container.class,code);
		ProcessType processType;
		try {
			processType = ProcessType.find.findByCode(container.getCurrentProcesses().get(0).typeCode);
			if(processType.lastExperimentType.code.equals(typeCode)){
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

	//TODO à finir
	public static void nextProcessState(Container container,ContextValidation contextValidation){
		State state=new State();
		state.date=new Date();
		state.user=InstanceHelpers.getUser();

		for(Process process:container.getCurrentProcesses()){
			if(container.state.code.equals("A") && checkProcessState("N",container.inputProcessCodes)){
				state.code="IP";
			}else if(container.state.code.equals("IW-P") && checkProcessState("IP",container.inputProcessCodes)){
				state.code="F";
			}
		}

		if(container.state.code.equals("IW-E")){
			state.code= "IP";
		} 
		//TODO mettre en fin de process les processus
		if(state.code !=null){
			setProcessState(container.inputProcessCodes,state,contextValidation);
		}
	}


	private static boolean checkProcessState(String stateCode,
			List<String> inputProcessCodes) {
		return MongoDBDAO.checkObjectExist(InstanceConstants.PROCESS_COLL_NAME,Process.class, DBQuery.in("code", inputProcessCodes).is("state.code", stateCode));
	}

	private static void setProcessState(List<String> inputProcessCodes, State state,
			ContextValidation contextValidation) {

		if(inputProcessCodes!=null){
			for(String processCode : inputProcessCodes){
				setProcessState(processCode,state,contextValidation);
			}
		}

	}

	private static void setProcessState(String  processCode, State nextState,
			ContextValidation contextValidation) {
		Process process =MongoDBDAO.findOne(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("code", processCode));

		if(process!=null){ 
			Logger.debug("Process e"+process.code);
			ContainerValidationHelper.validateStateCode(nextState.code, contextValidation);
			if(!contextValidation.hasErrors() && !nextState.code.equals(process.state)){

				TraceInformation traceInformation=new TraceInformation();
				InstanceHelpers.updateTraceInformation(traceInformation);
				StateHelper.updateHistoricalNextState(process.state,nextState);
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,  Container.class, 
						DBQuery.is("code", process.code),
						DBUpdate.set("state", nextState).set("traceInformation",traceInformation));
			}	

		}

	}

	public static void setContainerState(String containerCode,State nextState,ContextValidation contextValidation){
		Container container =MongoDBDAO.findOne(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", containerCode));

		if(container==null){
			Logger.error("Container "+containerCode+" not exists");
		} 
		//Validate state for Container
		ContainerValidationHelper.validateStateCode(nextState.code, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(container.state.code)){

			/*if(nextState.code.equals("IW-P")){
				container.inputProcessCodes=null;
			}*/
			TraceInformation traceInformation=new TraceInformation();
			InstanceHelpers.updateTraceInformation(traceInformation);
			StateHelper.updateHistoricalNextState(container.state,nextState);
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,  Container.class, 
					DBQuery.is("code", container.code),
					DBUpdate.set("state", nextState).set("traceInformation",traceInformation));
		}	
		container.state=nextState;
		nextContainerSupportState(container,contextValidation);
		nextProcessState(container,contextValidation);
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

			TraceInformation traceInformation=new TraceInformation();
			InstanceHelpers.updateTraceInformation(traceInformation);
			StateHelper.updateHistoricalNextState(containerSupport.state,nextState);
			MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME,  Container.class, 
					DBQuery.is("code", containerSupport.code),
					DBUpdate.set("state", nextState).set("traceInformation",traceInformation));
		}	


	}


	public static void setContainerState(List<ContainerUsed> containersUsed,State state,ContextValidation contextValidation){
		for(ContainerUsed containerUsed:containersUsed){
			Workflows.setContainerState(containerUsed.code, state, contextValidation);
		}
	}



	public static void nextContainerState(Process process,
			ContextValidation contextValidation) {
		State nextState=new State();
		if(process.state.code != null && process.state.code.equals("N")){
			nextState.code="A";
		}

		setContainerState(process.containerInputCode, nextState, contextValidation);

	}


}
