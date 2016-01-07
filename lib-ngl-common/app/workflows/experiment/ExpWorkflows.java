package workflows.experiment;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import models.utils.instance.ProcessHelper;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
import fr.cea.ig.MongoDBDAO;

public class ExpWorkflows extends Workflows<Experiment>{
	
	public static ExpWorkflows instance = new ExpWorkflows();
	
	@Override
	public void applyCurrentStateRules(ContextValidation validation, Experiment exp) {
		if("N".equals(exp.state.code)){
			ExpWorkflowsHelper.updateXCodes(exp);
			if(validation.isUpdateMode()){
				ExpWorkflowsHelper.updateRemoveContainersFromExperiment(exp, validation, getNewState(getContainerStateFromExperimentCategory(exp.categoryCode), validation.getUser())); 
				ExpWorkflowsHelper.updateAddContainersToExperiment(exp, validation, getNewState("IW-E", validation.getUser()));				
			}			 						
		}
		ExpWorkflowsHelper.updateStatus(exp, validation);
		ExpWorkflowsHelper.updateComments(exp, validation);
	}

	public void applyPreStateRules(ContextValidation validation, Experiment exp, State nextState) {
		exp.traceInformation = updateTraceInformation(exp.traceInformation, nextState); 			
		ExpWorkflowsHelper.updateStatus(exp, validation);
		
		if("N".equals(nextState.code)){
			ExpWorkflowsHelper.updateXCodes(exp); 	
		} else if("IP".equals(nextState.code)){
			ExpWorkflowsHelper.updateATMs(exp, false);	
			ExpWorkflowsHelper.updateOutputContainerCodes(exp);
		}else if("F".equals(nextState.code)){
			ExpWorkflowsHelper.updateATMs(exp, false);	
			ExpWorkflowsHelper.updateOutputContainerCodes(exp);		
			ExpWorkflowsHelper.createOutputContainerSupports(exp, validation);
		}
	}
	
	public void applySuccessPostStateRules(ContextValidation validation, Experiment exp){
		ExpWorkflowsHelper.linkExperimentWithProcesses(exp, validation);
		if("N".equals(exp.state.code)){
			ExpWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IW-E", validation.getUser()), validation);
			ExpWorkflowsHelper.updateStateOfInputContainerSupports(exp, getNewState("IW-E", validation.getUser()), validation);					
		} else if("IP".equals(exp.state.code)){		
			ExpWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IU", validation.getUser()), validation);
			ExpWorkflowsHelper.updateStateOfInputContainerSupports(exp, getNewState("IU", validation.getUser()), validation);
			ExpWorkflowsHelper.updateStateOfProcesses(exp,  getNewState("IP", validation.getUser()), validation);
			
		}else if("F".equals(exp.state.code)){
			if(TBoolean.TRUE.equals(exp.status.valid)){
				ExpWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IW-D", validation.getUser()), validation);
				ExpWorkflowsHelper.updateStateOfInputContainerSupports(exp, getNewState("IW-D", validation.getUser()), validation);				
			}else{
				ExpWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IW-D", validation.getUser()), validation);
				ExpWorkflowsHelper.updateStateOfInputContainerSupports(exp, getNewState("IW-D", validation.getUser()), validation);				
			}
			ExpWorkflowsHelper.updateStateOfProcesses(exp, getNewState("IP", validation.getUser()), validation);			
		}
	}
	
	public void applyErrorPostStateRules(ContextValidation validation, Experiment exp, State nextState){
		if("N".equals(nextState.code)){
			
		} else if("IP".equals(nextState.code)){			
			
		}else if("F".equals(nextState.code)){
			ExpWorkflowsHelper.deleteOutputContainerSupports(exp, validation);
		}
	}
	
	
	@Override
	public void setState(ContextValidation contextValidation,
			Experiment exp, State nextState) {
		contextValidation.setUpdateMode();
		
		CommonValidationHelper.validateState(exp.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(exp.state.code)){
			applyPreStateRules(contextValidation, exp, nextState);
			contextValidation.putObject(FIELD_STATE_CODE , nextState.code);
			exp.validate(contextValidation);
			if(!contextValidation.hasErrors()){
				boolean goBack = goBack(exp.state, nextState);
				if(goBack)Logger.debug(exp.code+" : back to the workflow. "+exp.state.code +" -> "+nextState.code);		
				
				exp.state = updateHistoricalNextState(exp.state, nextState);
				
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME,  Experiment.class, 
						DBQuery.is("code", exp.code),
						DBUpdate.set("state", exp.state).set("traceInformation", exp.traceInformation));
				
				applySuccessPostStateRules(contextValidation, exp);
				nextState(contextValidation, exp);
			}else{
				applyErrorPostStateRules(contextValidation, exp, nextState);
			}
		}
		
	}

	@Override
	public void nextState(ContextValidation contextValidation,	Experiment exp) {
		//in case of experiment nothing to do !	
	}

	/**
	 * Return the available container state for a experiment category code
	 * @param categoryCode
	 * @return
	 */
	private static String getContainerStateFromExperimentCategory(String categoryCode) {
		String nextContainerState=null;
		if(categoryCode.equals(ExperimentCategory.CODE.transformation.name())){
			nextContainerState="A-TM";
		}else if(categoryCode.equals(ExperimentCategory.CODE.transfert.name())){
			nextContainerState="A-TF";
		}else if(categoryCode.equals(ExperimentCategory.CODE.qualitycontrol.name())){
			nextContainerState="A-QC";
		}else if(categoryCode.equals(ExperimentCategory.CODE.purification.name())){
			nextContainerState="A-PF";
		}
		return nextContainerState;
	}
}
