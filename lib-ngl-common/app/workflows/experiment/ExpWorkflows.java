package workflows.experiment;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import models.laboratory.common.instance.State;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
import fr.cea.ig.MongoDBDAO;
@Service
public class ExpWorkflows extends Workflows<Experiment>{
	
	//public static ExpWorkflows instance = new ExpWorkflows();
	@Autowired
	ExpWorkflowsHelper expWorkflowsHelper;
	
	@Override
	public void applyCurrentStateRules(ContextValidation validation, Experiment exp) {
		if("N".equals(exp.state.code)){
			expWorkflowsHelper.updateXCodes(exp);
			if(validation.isUpdateMode()){
				expWorkflowsHelper.updateRemoveContainersFromExperiment(exp, validation, getNewState(getContainerStateFromExperimentCategory(exp.categoryCode), validation.getUser())); 
				expWorkflowsHelper.updateAddContainersToExperiment(exp, validation, getNewState("IW-E", validation.getUser()));				
			}			 						
		}else if("IP".equals(exp.state.code)){
			expWorkflowsHelper.updateXCodes(exp); //TODO GA 22/01/2016 hack for old experiment without contents, remove in 03/2016
		}
		expWorkflowsHelper.updateStatus(exp, validation);
		expWorkflowsHelper.updateComments(exp, validation);		
	}

	public void applyPreStateRules(ContextValidation validation, Experiment exp, State nextState) {		
		exp.traceInformation = updateTraceInformation(exp.traceInformation, nextState); 			
		expWorkflowsHelper.updateStatus(exp, validation);
		if("N".equals(nextState.code)){
			expWorkflowsHelper.updateComments(exp, validation);
			expWorkflowsHelper.updateXCodes(exp); 	
		} else if("IP".equals(nextState.code)){
			expWorkflowsHelper.updateATMs(exp, false);	
			expWorkflowsHelper.updateOutputContainerCodes(exp);
		}else if("F".equals(nextState.code)){
			long t0 = System.currentTimeMillis();
			expWorkflowsHelper.updateATMs(exp, false);
			long t1 = System.currentTimeMillis();
			expWorkflowsHelper.updateOutputContainerCodes(exp);
			long t2 = System.currentTimeMillis();
			expWorkflowsHelper.createOutputContainerSupports(exp, validation);
			long t3 = System.currentTimeMillis();
			/*
			Logger.debug("applyPreStateRules \n "
					+"1 = "+(t1-t0)+" ms\n"
					+"2 = "+(t2-t1)+" ms\n"
					+"3 = "+(t3-t2)+" ms\n"
					
					
					);
					
			*/
		}
	}
	
	public void applySuccessPostStateRules(ContextValidation validation, Experiment exp){
		expWorkflowsHelper.linkExperimentWithProcesses(exp, validation);
		if("N".equals(exp.state.code)){
			expWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IW-E", validation.getUser()), validation);
			expWorkflowsHelper.updateStateOfInputContainerSupports(exp, getNewState("IW-E", validation.getUser()), validation);					
		} else if("IP".equals(exp.state.code)){		
			expWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IU", validation.getUser()), validation);
			expWorkflowsHelper.updateStateOfInputContainerSupports(exp, getNewState("IU", validation.getUser()), validation);
			expWorkflowsHelper.updateStateOfProcesses(exp,  getNewState("IP", validation.getUser()), validation);			
		}else if("F".equals(exp.state.code)){
			expWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IW-D", validation.getUser()), validation);
			expWorkflowsHelper.updateStateOfInputContainerSupports(exp, getNewState("IW-D", validation.getUser()), validation);				
			expWorkflowsHelper.updateStateOfProcesses(exp, getNewState("IP", validation.getUser()), validation);
			
			if(ExperimentCategory.CODE.qualitycontrol.toString().equals(exp.categoryCode)){
				expWorkflowsHelper.updateInputContainers(exp, validation);
			}
		}
		expWorkflowsHelper.callWorkflowRules(validation, exp);
	}
	
	public void applyErrorPostStateRules(ContextValidation validation, Experiment exp, State nextState){
		if("N".equals(nextState.code)){
			
		} else if("IP".equals(nextState.code)){			
			
		}else if("F".equals(nextState.code)){
			expWorkflowsHelper.deleteOutputContainerSupports(exp, validation);
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
