package workflows.container;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.container.instance.ContainerValidationHelper;
import workflows.Workflows;

public class ContWorkflows extends Workflows<Container> {

	public static ContWorkflows instance = new ContWorkflows();
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			Container exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyCurrentStateRules(ContextValidation validation,
			Container object) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void applySuccessPostStateRules(ContextValidation validation,
			Container container) {
		
		if("IS".equals(container.state.code) || "UA".equals(container.state.code)){
			//TODO GA improve the extraction of fromExperimentTypeCodes after refactoring inputProcessCodes and processTypeCode
			 
			 boolean unsetFromExperimentTypeCodes = false;
			 if(null != container.fromExperimentTypeCodes && container.fromExperimentTypeCodes.size() == 1){
				 String code = container.fromExperimentTypeCodes.iterator().next();
				 if(code.startsWith("ext"))unsetFromExperimentTypeCodes=true;
			 }else if(null != container.fromExperimentTypeCodes && container.fromExperimentTypeCodes.size() > 1){
				 Logger.error("several fromExperimentTypeCodes not managed");
			 }
			
			if(unsetFromExperimentTypeCodes){
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
						DBQuery.is("code",container.code), DBUpdate.unset("inputProcessCodes")
																	.unset("processTypeCode")
																	.unset("processTypeCodes")
																	.unset("fromExperimentTypeCodes"));
			}else{
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
						DBQuery.is("code",container.code), DBUpdate.unset("inputProcessCodes")
																.unset("processTypeCodes")
																.unset("processTypeCode"));
			}		
		} 
		
		//TODO GA update support if possible
		
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Container container, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, Container container,
			State nextState) {
		
		ContextValidation currentCtxValidation = new ContextValidation(contextValidation.getUser());
		currentCtxValidation.setUpdateMode();
		
		ContainerValidationHelper.validateNextState(container, nextState, currentCtxValidation);
		if(!currentCtxValidation.hasErrors() && !nextState.code.equals(container.state.code)){
			applyPreStateRules(currentCtxValidation, container, nextState);
			currentCtxValidation.putObject(FIELD_STATE_CODE , nextState.code);
			//TODO GA improve performance to validate only field impacted by state
			//container.validate(currentCtxValidation); //in comment because no field are state dependant
			if(!currentCtxValidation.hasErrors()){
				boolean goBack = goBack(container.state, nextState);
				if(goBack)Logger.debug(container.code+" : back to the workflow. "+container.state.code +" -> "+nextState.code);		
				
				container.state = updateHistoricalNextState(container.state, nextState);
				
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,  Container.class, 
						DBQuery.is("code", container.code),
						DBUpdate.set("state", container.state).set("traceInformation", container.traceInformation));
				
				applySuccessPostStateRules(currentCtxValidation, container);
				nextState(currentCtxValidation, container);
			}else{
				applyErrorPostStateRules(currentCtxValidation, container, nextState);
			}
		}
		if(currentCtxValidation.hasErrors()){
			contextValidation.addErrors(currentCtxValidation.errors);
		}
		
	}

	@Override
	public void nextState(ContextValidation contextValidation, Container object) {
		// TODO Auto-generated method stub
		
	}

}
