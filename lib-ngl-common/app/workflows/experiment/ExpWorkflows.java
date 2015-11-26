package workflows.experiment;

import models.laboratory.common.instance.State;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
import fr.cea.ig.MongoDBDAO;

public class ExpWorkflows extends Workflows<Experiment>{
	
	@Override
	public void applyCurrentStateRules(ContextValidation validation, Experiment exp) {
		if("N".equals(exp.state.code)){
			if(validation.isCreationMode()){
				ExpWorkflowsHelper.updateContainersAndProcesses(exp, validation); 
			}else if(validation.isUpdateMode()){
				ExpWorkflowsHelper.updateRemoveContainersFromExperiment(exp, validation); 
				ExpWorkflowsHelper.updateAddContainersToExperiment(exp, validation);				
			}
			ExpWorkflowsHelper.updateXCodes(exp); 						
		}
	}

	private void applyPreStateRules(ContextValidation validation, Experiment exp, State nextState) {
		if("IP".equals(nextState.code)){
			ExpWorkflowsHelper.updateATMs(exp);				
			//update containers & processes state
			ExpWorkflowsHelper.updateContainersAndProcessesState(exp, validation, "IU", "IP");
		}else if("F".equals(nextState.code)){
			
		}
	}
	
	@Override
	public void setState(ContextValidation contextValidation,
			Experiment exp, State nextState) {
		contextValidation.setUpdateMode();
		
		CommonValidationHelper.validateState(exp.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(exp.state.code)){
			applyPreStateRules(contextValidation, exp, nextState);
			exp.validate(contextValidation);
			if(!contextValidation.hasErrors()){
				boolean goBack = goBack(exp.state, nextState);
				if(goBack)Logger.debug(exp.code+" : back to the workflow. "+exp.state.code +" -> "+nextState.code);		
				
				exp.traceInformation = updateTraceInformation(exp.traceInformation, nextState); 
				exp.state = updateHistoricalNextState(exp.state, nextState);
				
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME,  Experiment.class, 
						DBQuery.is("code", exp.code),
						DBUpdate.set("state", exp.state).set("traceInformation", exp.traceInformation));
				
				applyCurrentStateRules(contextValidation, exp);
				nextState(contextValidation, exp);
			}			
		}
		
	}

	@Override
	public void nextState(ContextValidation contextValidation,	Experiment exp) {
		//in case of experiment nothing to do !	
	}

}
