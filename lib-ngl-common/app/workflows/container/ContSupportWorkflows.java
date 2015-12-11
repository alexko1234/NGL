package workflows.container;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import workflows.Workflows;
import fr.cea.ig.MongoDBDAO;

public class ContSupportWorkflows extends Workflows<ContainerSupport> {

	public static ContSupportWorkflows instance = new ContSupportWorkflows();
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			ContainerSupport exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyCurrentStateRules(ContextValidation validation,
			ContainerSupport object) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void applySuccessPostStateRules(ContextValidation validation,
			ContainerSupport container) {
				
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			ContainerSupport container, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, ContainerSupport containerSupport,
			State nextState) {
		ContextValidation currentCtxValidation = new ContextValidation(contextValidation.getUser());
		currentCtxValidation.setUpdateMode();
		
		CommonValidationHelper.validateState(ObjectType.CODE.Container, nextState, currentCtxValidation);
		if(!currentCtxValidation.hasErrors() && !nextState.code.equals(containerSupport.state.code)){
			applyPreStateRules(currentCtxValidation, containerSupport, nextState);
			currentCtxValidation.putObject(FIELD_STATE_CODE , nextState.code);
			//TODO GA improve performance to validate only field impacted by state
			//containerSupport.validate(contextValidation); //in comment because no field are state dependant
			if(!currentCtxValidation.hasErrors()){
				boolean goBack = goBack(containerSupport.state, nextState);
				if(goBack)Logger.debug(containerSupport.code+" : back to the workflow. "+containerSupport.state.code +" -> "+nextState.code);		
				
				containerSupport.state = updateHistoricalNextState(containerSupport.state, nextState);
				
				MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,  Container.class, 
						DBQuery.is("code", containerSupport.code),
						DBUpdate.set("state", containerSupport.state).set("traceInformation", containerSupport.traceInformation));
				
				applySuccessPostStateRules(currentCtxValidation, containerSupport);
				nextState(currentCtxValidation, containerSupport);
			}else{
				applyErrorPostStateRules(currentCtxValidation, containerSupport, nextState);
			}
		}
		
		if(currentCtxValidation.hasErrors()){
			contextValidation.addErrors(currentCtxValidation.errors);
		}
		
	}

	@Override
	public void nextState(ContextValidation contextValidation, ContainerSupport object) {
		// TODO Auto-generated method stub
		
	}

}
