package workflows.experiment;

import java.util.HashSet;
import java.util.Set;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.instance.ExperimentHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
import workflows.container.ContainerWorkflows;
import fr.cea.ig.MongoDBDAO;

public class ExpWorkflows extends Workflows<Experiment>{
	
	@Override
	public void applyWorkflowRules(ContextValidation validation, Experiment exp) {
		if("N".equals(exp.state.code)){

			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,Process.class,
					DBQuery.in("code", ExperimentHelper.getAllProcessCodesFromExperiment(exp))
					,DBUpdate.set("currentExperimentTypeCode", exp.typeCode).push("experimentCodes", exp.code),true);

			Set<Container> inputContainers=new HashSet<>(MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code",exp.inputContainerSupportCodes)).toList());
			ContainerWorkflows.setContainerState(inputContainers,"IW-E", validation);
			
		}else if("IP".equals(exp.state.code)){
			
		}else if("F".equals(exp.state.code)){
			
		}
	}

	@Override
	public void setState(ContextValidation contextValidation,
			Experiment exp, State nextState) {
		contextValidation.setUpdateMode();
		
		CommonValidationHelper.validateState(exp.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(exp.state.code)){
			boolean goBack = goBack(exp.state, nextState);
			if(goBack)Logger.debug(exp.code+" : back to the workflow. "+exp.state.code +" -> "+nextState.code);		
			
			exp.traceInformation = updateTraceInformation(exp.traceInformation, nextState); 
			exp.state = updateHistoricalNextState(exp.state, nextState);
			
			exp.validate(contextValidation);
			if(!contextValidation.hasErrors()){
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME,  Experiment.class, 
						DBQuery.is("code", exp.code),
						DBUpdate.set("state", exp.state).set("traceInformation", exp.traceInformation));
				
				applyWorkflowRules(contextValidation, exp);
				nextState(contextValidation, exp);
			}			
		}
		
	}

	@Override
	public void nextState(ContextValidation contextValidation,	Experiment exp) {
		//in case of experiment nothing to do !	
	}

}
