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
import workflows.container.ContWorkflows;
import fr.cea.ig.MongoDBDAO;
@Service
public class ExpWorkflows extends Workflows<Experiment>{
	
	@Autowired
	ExpWorkflowsHelper expWorkflowsHelper;
	
	@Autowired
	ContWorkflows contWorkflows;
	
	
	@Override
	public void applyCurrentStateRules(ContextValidation validation, Experiment exp) {
		if("N".equals(exp.state.code)){
			expWorkflowsHelper.updateXCodes(exp);
			if(validation.isUpdateMode()){
				expWorkflowsHelper.updateRemoveContainersFromExperiment(exp, validation, getNewState(contWorkflows.getContainerStateFromExperimentCategory(exp.categoryCode), validation.getUser())); 
				expWorkflowsHelper.updateAddContainersToExperiment(exp, validation, getNewState("IW-E", validation.getUser()));				
			}			 						
		}else if("IP".equals(exp.state.code)){
			expWorkflowsHelper.updateXCodes(exp); //TODO GA 22/01/2016 hack for old experiment without contents, remove in 03/2016
			expWorkflowsHelper.updateOutputContainerCode(exp);
			expWorkflowsHelper.updateOutputContainerCodes(exp);
			expWorkflowsHelper.updateOutputContainerContents(exp);			
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
			
			expWorkflowsHelper.createNewSampleCodesIfNeeded(exp, validation);
			expWorkflowsHelper.createNewSamplesIfNeeded(exp, validation);
			
			expWorkflowsHelper.updateOutputContainerCodes(exp);
		}else if("F".equals(nextState.code)){
			long t0 = System.currentTimeMillis();
			expWorkflowsHelper.updateATMs(exp, false);
			long t1 = System.currentTimeMillis();
			
			expWorkflowsHelper.createNewSampleCodesIfNeeded(exp, validation);
			expWorkflowsHelper.createNewSamplesIfNeeded(exp, validation);
			
			expWorkflowsHelper.updateOutputContainerCodes(exp);
			long t2 = System.currentTimeMillis();
			expWorkflowsHelper.createOutputContainerSupports(exp, validation);
			long t3 = System.currentTimeMillis();
		
		}
	}
	
	public void applySuccessPostStateRules(ContextValidation validation, Experiment exp){
		expWorkflowsHelper.linkExperimentWithProcesses(exp, validation);
		if("N".equals(exp.state.code)){
			expWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IW-E", validation.getUser()), validation);
			expWorkflowsHelper.updateStateOfInputContainerSupports(exp, validation);					
		} else if("IP".equals(exp.state.code)){		
			expWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IU", validation.getUser()), validation);
			expWorkflowsHelper.updateStateOfInputContainerSupports(exp, validation);
			expWorkflowsHelper.updateStateOfProcesses(exp,  getNewState("IP", validation.getUser()), validation);			
		}else if("F".equals(exp.state.code)){
			expWorkflowsHelper.updateStateOfInputContainers(exp, getNewState("IW-D", validation.getUser()), validation);
			expWorkflowsHelper.updateStateOfInputContainerSupports(exp, validation);				
			expWorkflowsHelper.updateStateOfProcesses(exp, getNewState("IP", validation.getUser()), validation);
			if(ExperimentCategory.CODE.qualitycontrol.toString().equals(exp.categoryCode)){
				expWorkflowsHelper.updateInputContainers(exp, validation);
			}
		}
		expWorkflowsHelper.callWorkflowRules(validation, exp);
		
		if(validation.hasErrors()){
			Logger.error("Problem on ExpWorkflow.applySuccessPostStateRules : "+validation.errors.toString());
		}
	}
	
	public void applyErrorPostStateRules(ContextValidation validation, Experiment exp, State nextState){
		ContextValidation errorValidation = new ContextValidation(validation.getUser());
		errorValidation.setContextObjects(validation.getContextObjects());
		
		if("N".equals(nextState.code)){
			
		} else if("IP".equals(nextState.code)){			
			expWorkflowsHelper.deleteSamplesIfNeeded(exp, errorValidation); //TODO Need to clean the output container and replace new sample by old sample
		}else if("F".equals(nextState.code)){
			expWorkflowsHelper.deleteOutputContainerSupports(exp, errorValidation);
			expWorkflowsHelper.deleteSamplesIfNeeded(exp, errorValidation);
		}
		
		if(errorValidation.hasErrors()){
			Logger.error("Problem on ExpWorkflow.applyErrorPostStateRules : "+errorValidation.errors.toString());
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

	
}
