package workflows.run;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.Props;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
import workflows.Workflows;

@Service
public class RunWorkflows extends Workflows<Run>{

	@Autowired
	RunWorkflowsHelper runWorkflowsHelper;
	
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	private static final String ruleFRG="F_RG_1";
	private static final String ruleIPS="IP_S_1";
	private static final String ruleFV="F_V_1";

	@Override
	public void applyPreStateRules(ContextValidation validation, Run exp, State nextState) {
		// TODO Auto-generated method stub

	}
	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Run object) {
		// TODO Auto-generated method stub

	}
	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Run object) {
		// TODO Auto-generated method stub

	}
	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Run run) {

		if("IP-S".equals(run.state.code)){
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), ruleIPS, run),null);
		}else if("F-RG".equals(run.state.code)){
			runWorkflowsHelper.updateDispatchRun(run);
			runWorkflowsHelper.updateReadSetLane(run, validation, ruleFRG,true);
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ruleFRG, run),null);
		}else if("F-V".equals(run.state.code)){
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), ruleFV, run),null);
			runWorkflowsHelper.invalidateReadSetLane(run, validation, ruleFV, false);
		}
	
		
	}
	@Override
	public void applyErrorPostStateRules(ContextValidation validation, Run exp, State nextState) {
		// TODO Auto-generated method stub

	}
	@Override
	public void setState(ContextValidation contextValidation, Run run, State nextState) {
		contextValidation.setUpdateMode();
		RunValidationHelper.validateState(run.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(run.state.code)){
			boolean goBack = goBack(run.state, nextState);
			if(goBack)Logger.debug(run.code+" : back to the workflow. "+run.state.code +" -> "+nextState.code);		

			run.traceInformation = updateTraceInformation(run.traceInformation, nextState); 
			run.state = updateHistoricalNextState(run.state, nextState);

			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code", run.code),
					DBUpdate.set("state", run.state).set("traceInformation",run.traceInformation));

			applySuccessPostStateRules(contextValidation, run);
			nextState(contextValidation, run);
		}	
	}
	@Override
	public void nextState(ContextValidation contextValidation, Run run) {
		State nextStep = cloneState(run.state, contextValidation.getUser());
		if("F-RG".equals(run.state.code)){
			nextStep.code = "IW-V";
		}else if("F-S".equals(run.state.code)){
			nextStep.code = "IW-RG";
		}else if("IW-V".equals(run.state.code) && runWorkflowsHelper.atLeastOneValuation(run)){
			nextStep.code = "IP-V";
		}else if("IP-V".equals(run.state.code) && runWorkflowsHelper.isRunValuationComplete(run)){
			nextStep.code = "F-V";
		}else if("F-V".equals(run.state.code) && !runWorkflowsHelper.isRunValuationComplete(run)){
			nextStep.code = "IP-V";
		}
		setState(contextValidation, run, nextStep);
	}
}
