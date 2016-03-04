package workflows.container;

import static validation.common.instance.CommonValidationHelper.FIELD_STATE_CODE;

import java.util.Date;

import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.Valuation;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.Props;
import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.common.instance.CommonValidationHelper;
import validation.container.instance.ContainerSupportValidationHelper;
import validation.container.instance.ContainerValidationHelper;
import workflows.Workflows;
import fr.cea.ig.MongoDBDAO;

@Service
public class ContSupportWorkflows extends Workflows<ContainerSupport> {

	//public static ContSupportWorkflows instance = new ContSupportWorkflows();
	
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
			ContainerSupport containerSupport) {
		
		if("IS".equals(containerSupport.state.code) || "UA".equals(containerSupport.state.code)){
			//TODO GA improve the extraction of fromTransformationTypeCodes after refactoring inputProcessCodes and processTypeCode
			 
			 boolean unsetFromExperimentTypeCodes = false;
			 if(null != containerSupport.fromTransformationTypeCodes && containerSupport.fromTransformationTypeCodes.size() == 1){
				 String code = containerSupport.fromTransformationTypeCodes.iterator().next();
				 if(code.startsWith("ext"))unsetFromExperimentTypeCodes=true;
			 }else if(null != containerSupport.fromTransformationTypeCodes && containerSupport.fromTransformationTypeCodes.size() > 1){
				 Logger.error("several fromTransformationTypeCodes not managed");
			 }
			
			if(unsetFromExperimentTypeCodes){
				MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, Container.class,
						DBQuery.is("code",containerSupport.code), DBUpdate.unset("fromTransformationTypeCodes"));
			}	
		}
		callWorkflowRules(validation,containerSupport);		
	}
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));

	public static void callWorkflowRules(ContextValidation validation, ContainerSupport containerSupport) {
		rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), "workflow", containerSupport, validation),null);
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
		
		ContainerSupportValidationHelper.validateNextState(containerSupport, nextState, currentCtxValidation);
		if(!currentCtxValidation.hasErrors() && !nextState.code.equals(containerSupport.state.code)){
			applyPreStateRules(currentCtxValidation, containerSupport, nextState);
			currentCtxValidation.putObject(FIELD_STATE_CODE , nextState.code);
			//TODO GA improve performance to validate only field impacted by state
			//containerSupport.validate(contextValidation); //in comment because no field are state dependant
			//TODO GA what is the rules to change the support state, need a support state ??
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
