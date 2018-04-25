package workflows.container;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import static validation.common.instance.CommonValidationHelper.FIELD_UPDATE_CONTAINER_STATE;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.InstanceConstants;
import rules.services.LazyRules6Actor;
import validation.ContextValidation;
import validation.container.instance.ContainerSupportValidationHelper;
import workflows.Workflows;

@Singleton
public class ContSupportWorkflows extends Workflows<ContainerSupport> {

	private final ContWorkflows containerWorkflows;
	private final LazyRules6Actor      rulesActor;
	 
	@Inject
	public ContSupportWorkflows(NGLContext ctx, ContWorkflows containerWorkflows) {
		rulesActor              = ctx.rules6Actor();
		this.containerWorkflows = containerWorkflows;
	}
	
	private static final play.Logger.ALogger logger = play.Logger.of(ContSupportWorkflows.class);
	
	
	
	@Override
	public void applyPreStateRules(ContextValidation validation, ContainerSupport exp, State nextState) {
	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, ContainerSupport object) {
	}
	
	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, ContainerSupport object) {
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, ContainerSupport containerSupport) {
		if("IS".equals(containerSupport.state.code) || "UA".equals(containerSupport.state.code) || "IW-P".equals(containerSupport.state.code)){
			//TODO GA improve the extraction of fromTransformationTypeCodes after refactoring inputProcessCodes and processTypeCode

			boolean unsetFromExperimentTypeCodes = false;
			if (null != containerSupport.fromTransformationTypeCodes && containerSupport.fromTransformationTypeCodes.size() == 1) {
				String code = containerSupport.fromTransformationTypeCodes.iterator().next();
				if(code.startsWith("ext"))unsetFromExperimentTypeCodes=true;
			} else if(null != containerSupport.fromTransformationTypeCodes && containerSupport.fromTransformationTypeCodes.size() > 1) {
				logger.error("several fromTransformationTypeCodes not managed");
			}

			if (unsetFromExperimentTypeCodes) {
				MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, Container.class,
						DBQuery.is("code",containerSupport.code), DBUpdate.unset("fromTransformationTypeCodes"));
			}	
		}
		if(Boolean.TRUE.equals(validation.getObject(FIELD_UPDATE_CONTAINER_STATE)) && 
				("IW-P".equals(containerSupport.state.code) || "IS".equals(containerSupport.state.code) || "UA".equals(containerSupport.state.code))) {
			State nextState = cloneState(containerSupport.state, validation.getUser());
			MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code", containerSupport.code))
			.cursor.forEach(c -> {				
				containerWorkflows.setState(validation, c, nextState);
			});
		}
		callWorkflowRules(validation,containerSupport);		

		if (validation.hasErrors()) {
			logger.error("Problem on ContSupportWorkflow.applySuccessPostStateRules : "+validation.errors.toString());
		}
	}
	
	public  void callWorkflowRules(ContextValidation validation, ContainerSupport containerSupport) {
		rulesActor.tellMessage("workflow", containerSupport, validation);
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation, ContainerSupport container, State nextState) {
		if(validation.hasErrors()){
			logger.error("Problem on ContSupportWorkflow.applyErrorPostStateRules : "+validation.errors.toString());
		}
	}

	@Override
	public void setState(ContextValidation contextValidation, ContainerSupport containerSupport,
			State nextState) {
		ContextValidation currentCtxValidation = new ContextValidation(contextValidation.getUser());
		currentCtxValidation.setContextObjects(contextValidation.getContextObjects());
		currentCtxValidation.setUpdateMode();
		
		ContainerSupportValidationHelper.validateNextState(containerSupport, nextState, currentCtxValidation);
		if(!currentCtxValidation.hasErrors() && !nextState.code.equals(containerSupport.state.code)){
			applyPreStateRules(currentCtxValidation, containerSupport, nextState);
			currentCtxValidation.putObject(FIELD_STATE_CODE , nextState.code);
			//TODO GA improve performance to validate only field impacted by state
			//containerSupport.validate(contextValidation); //in comment because no field are state dependant
			//TODO GA what is the rules to change the support state, need a support state ??
			if(!currentCtxValidation.hasErrors()){
				boolean goBack = goBack(containerSupport.state, nextState);
				if (goBack)
					logger.debug(containerSupport.code+" : back to the workflow. "+containerSupport.state.code +" -> "+nextState.code);		
				containerSupport.state = updateHistoricalNextState(containerSupport.state, nextState);
				MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME,  Container.class, 
						DBQuery.is("code", containerSupport.code),
						DBUpdate.set("state", containerSupport.state).set("traceInformation", containerSupport.traceInformation));
				
				applySuccessPostStateRules(currentCtxValidation, containerSupport);
				nextState(currentCtxValidation, containerSupport);
			} else {
				applyErrorPostStateRules(currentCtxValidation, containerSupport, nextState);
			}
		}
		if (currentCtxValidation.hasErrors()) {
			contextValidation.addErrors(currentCtxValidation.errors);
		}
	}

	
	public void setStateFromContainers(ContextValidation contextValidation, ContainerSupport containerSupport){
		State nextStep = getNextStateFromContainerStates(contextValidation.getUser(), ContainerSupportValidationHelper.getContainerStates(containerSupport));
		setState(contextValidation, containerSupport, nextStep);
	}

	public State getNextStateFromContainerStates(String username, Set<String> containerStates) {
		State nextStep = null;
		logger.debug("States = " + containerStates);
		if (containerStates.contains("IW-D")) {
			nextStep = getNewState("IW-D", username);			
		} else if(containerStates.contains("IU")) {
			nextStep = getNewState("IU", username);			
		} else if(containerStates.contains("IW-E")) {
			nextStep = getNewState("IW-E", username);			
		} else if(containerStates.contains("A-TM") && !containerStates.contains("A-QC") && !containerStates.contains("A-P") && !containerStates.contains("A-TF")){
			nextStep = getNewState("A-TM", username);			
		} else if(containerStates.contains("A-QC") && !containerStates.contains("A-TM") && !containerStates.contains("A-P") && !containerStates.contains("A-TF")){
			nextStep = getNewState("A-QC", username);			
		} else if(containerStates.contains("A-PF") && !containerStates.contains("A-TM") && !containerStates.contains("A-QC") && !containerStates.contains("A-TF")){
			nextStep = getNewState("A-PF", username);			
		} else if(containerStates.contains("A-TF") && !containerStates.contains("A-TM") && !containerStates.contains("A-QC") && !containerStates.contains("A-PF")){
			nextStep = getNewState("A-TF", username);			
		} else if(containerStates.contains("A-TF") || containerStates.contains("A-TM") || containerStates.contains("A-QC") || containerStates.contains("A-PF")){
			nextStep = getNewState("A", username);			
		} else if(containerStates.contains("IW-P")) {
			nextStep = getNewState("IW-P", username);			
		} else if(containerStates.contains("IS")) {
			nextStep = getNewState("IS", username);			
		} else if(containerStates.contains("UA")) {
			nextStep = getNewState("UA", username);			
		} else {
			throw new RuntimeException("setStateFromContainer : states "+containerStates+" not managed");
		}
		logger.debug("nextStep = "+nextStep.code);
		return nextStep;
	}
	
	@Override
	public void nextState(ContextValidation contextValidation, ContainerSupport object) {
	}

}
