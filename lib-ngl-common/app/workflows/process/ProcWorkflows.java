package workflows.process;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.processes.instance.Process;
import models.sra.submit.common.instance.Submission;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.processes.instance.ProcessValidationHelper;
import workflows.Workflows;
import fr.cea.ig.MongoDBDAO;
@Service
public class ProcWorkflows extends Workflows<Process> {

	//public static ProcWorkflows instance = new ProcWorkflows();
	
	@Autowired
	ProcWorkflowHelper procWorkflowsHelper;
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			Process process, State nextState) {
		process.traceInformation = updateTraceInformation(process.traceInformation, nextState); 			
		
	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Process object) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Process process) {
		procWorkflowsHelper.updateContentProcessPropertiesAttribute(validation, process);
		procWorkflowsHelper.updateContentPropertiesWithContentProcessProperties(validation, process);
	}
	
	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Process process) {
		if("N".equals(process.state.code)){
			procWorkflowsHelper.updateInputContainerToStartProcess(validation, process);			
		}
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation, Process process, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, Process process,
			State nextState) {
		ContextValidation currentCtxValidation = new ContextValidation(contextValidation.getUser());
		currentCtxValidation.setUpdateMode();
		
		ProcessValidationHelper.validateNextState(process, nextState, currentCtxValidation);
		if(!currentCtxValidation.hasErrors() && !nextState.code.equals(process.state.code)){
			applyPreStateRules(currentCtxValidation, process, nextState);
			currentCtxValidation.putObject(FIELD_STATE_CODE , nextState.code);
			//TODO GA improve performance to validate only field impacted by state
			//process.validate(contextValidation); //in comment because no field are state dependant maybe state of container			
			if(!currentCtxValidation.hasErrors()){
				boolean goBack = goBack(process.state, nextState);
				if(goBack)Logger.debug(process.code+" : back to the workflow. "+process.state.code +" -> "+nextState.code);		
				
				process.state = updateHistoricalNextState(process.state, nextState);
				
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME,  Process.class, 
						DBQuery.is("code", process.code),
						DBUpdate.set("state", process.state).set("traceInformation", process.traceInformation));
				
				applySuccessPostStateRules(currentCtxValidation, process);
				nextState(currentCtxValidation, process);
			}else{
				applyErrorPostStateRules(currentCtxValidation, process, nextState);
			}
		}
		if(currentCtxValidation.hasErrors()){
			contextValidation.addErrors(currentCtxValidation.errors);
		}
	}

	@Override
	public void nextState(ContextValidation contextValidation, Process process) {
		// TODO Auto-generated method stub
		
	}

}
