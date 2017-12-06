package workflows.container;

import static fr.cea.ig.play.IGGlobals.akkaSystem;
import static fr.cea.ig.play.IGGlobals.configuration;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;
import static validation.common.instance.CommonValidationHelper.*;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.experiment.description.ExperimentCategory;
import models.utils.InstanceConstants;
import models.laboratory.processes.instance.Process;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.container.instance.ContainerValidationHelper;
import workflows.Workflows;
import workflows.process.ProcWorkflows;
import akka.actor.ActorRef;
import akka.actor.Props;
import fr.cea.ig.MongoDBDAO;

@Service
public class ContWorkflows extends Workflows<Container> {

	@Autowired
	ContSupportWorkflows contSupportWorkflows;
	@Autowired
	ProcWorkflows procSupportWorkflows;
	
	@Override
	public void applyPreStateRules(ContextValidation validation,
			Container exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Container object) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Container object) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void applySuccessPostStateRules(ContextValidation validation,
			Container container) {
		//purg when pass to IS, UA or IW-P
		if("IS".equals(container.state.code) || "UA".equals(container.state.code) || "IW-P".equals(container.state.code)){
			
			//TODO GA improve the extraction of fromTransformationTypeCodes

			if (null != container.fromTransformationTypeCodes && container.fromTransformationTypeCodes.size() == 1) {
				String code = container.fromTransformationTypeCodes.iterator().next();
				if (code.startsWith("ext")){
					container.fromTransformationTypeCodes = null;
				}
			} else if (null != container.fromTransformationTypeCodes && container.fromTransformationTypeCodes.size() > 1) {
				Logger.error("several fromTransformationTypeCodes not managed");
			}
			//put all process to F when pass container A-* to IS, UA or IW-P
			String previousStateCode = (String)validation.getObject(FIELD_PREVIOUS_STATE_CODE);
			if(previousStateCode.startsWith("A")){
				validation.addKeyToRootKeyName("processes");
				MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code", container.processCodes))
				.cursor.forEach(process -> {
					validation.addKeyToRootKeyName(process.code);
					procSupportWorkflows.setState(validation, process, getNewState("F", validation.getUser()));
					validation.removeKeyFromRootKeyName(process.code);
				});
				validation.removeKeyFromRootKeyName("processes");
			}
			
			container.processCodes = null;
			container.processTypeCodes = null;
			container.contents.parallelStream().forEach(c -> {c.processProperties = null;c.processComments = null;});
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, container);
			
			
			
			/*
			if(unsetFromExperimentTypeCodes){
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
						DBQuery.is("code",container.code), DBUpdate.unset("processCodes")
																	.unset("processTypeCodes")
																	.unset("fromTransformationTypeCodes"));
			}else{
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,
						DBQuery.is("code",container.code), DBUpdate.unset("processCodes")
																.unset("processTypeCodes"));
			}
			*/		
		}
		
		if(Boolean.TRUE.equals(validation.getObject(FIELD_UPDATE_CONTAINER_SUPPORT_STATE))){
			ContainerSupport containerSupport = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,container.support.code);
			contSupportWorkflows.setStateFromContainers(validation, containerSupport);
		}
		callWorkflowRules(validation,container);
		
		if(validation.hasErrors()){
			Logger.error("Problem on ContWorkflow.applySuccessPostStateRules : "+validation.errors.toString());
		}
	}
	// Same actor as in ContSupportWorkflow
	// private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	// Try lazy init
	private static ActorRef _rulesActor;
	static ActorRef rulesActor() {
		if (_rulesActor == null)
			// _rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
			_rulesActor = akkaSystem().actorOf(Props.create(RulesActor6.class));
		return _rulesActor;
	}


	public static void callWorkflowRules(ContextValidation validation, Container container) {
		// rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), "workflow", container, validation),null);
		rulesActor().tell(new RulesMessage(configuration().getString("rules.key"), "workflow", container, validation),null);
	}
	
	@Override
	public void applyErrorPostStateRules(ContextValidation validation,
			Container container, State nextState) {
		// TODO Auto-generated method stub
		if(validation.hasErrors()){
			Logger.error("Problem on ContWorkflow.applyErrorPostStateRules : "+validation.errors.toString());
		}
	}

	@Override
	public void setState(ContextValidation contextValidation, Container container,
			State nextState) {
		
		ContextValidation currentCtxValidation = new ContextValidation(contextValidation.getUser());
		currentCtxValidation.setContextObjects(contextValidation.getContextObjects());
		currentCtxValidation.setUpdateMode();
		
		ContainerValidationHelper.validateNextState(container, nextState, currentCtxValidation);
		if(!currentCtxValidation.hasErrors() && !nextState.code.equals(container.state.code)){
			applyPreStateRules(currentCtxValidation, container, nextState);
			currentCtxValidation.putObject(FIELD_PREVIOUS_STATE_CODE , container.state.code);
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

	/**
	 * Return the available container state for a experiment category code
	 * @param categoryCode
	 * @return
	 */
	public String getContainerStateFromExperimentCategory(String categoryCode) {
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
