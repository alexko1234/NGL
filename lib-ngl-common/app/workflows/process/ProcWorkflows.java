package workflows.process;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;

public class ProcWorkflows extends Workflows<Process> {

	public static ProcWorkflows instance = new ProcWorkflows();
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			Process exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyCurrentStateRules(ContextValidation validation,
			Process object) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void applySuccessPostStateRules(ContextValidation validation,
			Process container) {
				
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Process container, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, Process process,
			State nextState) {
		ContextValidation currentCtxValidation = new ContextValidation(contextValidation.getUser());
		currentCtxValidation.setUpdateMode();
		
		CommonValidationHelper.validateState(ObjectType.CODE.Process, nextState, currentCtxValidation);
		if(!currentCtxValidation.hasErrors() && !nextState.code.equals(process.state.code)){
			applyPreStateRules(currentCtxValidation, process, nextState);
			currentCtxValidation.putObject(FIELD_STATE_CODE , nextState.code);
			//TODO GA improve performance to validate only field impacted by state
			//process.validate(contextValidation); //in comment because no field are state dependant
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
	public void nextState(ContextValidation contextValidation, Process object) {
		// TODO Auto-generated method stub
		
	}

}
