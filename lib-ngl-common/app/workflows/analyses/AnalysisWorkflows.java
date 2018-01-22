package workflows.analyses;

import static fr.cea.ig.play.IGGlobals.akkaSystem;

import java.util.Date;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.Props;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Analysis;
import models.utils.InstanceConstants;
import play.Logger;
import play.Play;
// import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import validation.run.instance.AnalysisValidationHelper;
import workflows.Workflows;

@Service
public class AnalysisWorkflows extends Workflows<Analysis>{

	
	@Autowired
	AnalysisWorkflowsHelper analysisWorkflowsHelper;
	
	// private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	private static ActorRef rulesActor = akkaSystem().actorOf(Props.create(RulesActor6.class));
	
	private static final String ruleFBA="F_BA_1";

	@Override
	public void applyPreStateRules(ContextValidation validation, Analysis exp, State nextState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, Analysis object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, Analysis object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, Analysis analysis) {
		if("IP-BA".equals(analysis.state.code)){
			analysisWorkflowsHelper.updateStateMasterReadSetCodes(analysis, validation, "IP-BA");
		}else if("F-BA".equals(analysis.state.code)){
			//Call rules F-BA
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ruleFBA, analysis),null);	
			analysisWorkflowsHelper.updateStateMasterReadSetCodes(analysis, validation, "F-BA");
										
		}else if("IW-V".equals(analysis.state.code)){
			analysisWorkflowsHelper.updateBioinformaticValuationMasterReadSetCodes(analysis, validation, TBoolean.UNSET, null, null);	
			analysisWorkflowsHelper.updateStateMasterReadSetCodes(analysis, validation, "IW-VBA");
		}else if("F-V".equals(analysis.state.code)){
			analysisWorkflowsHelper.updateBioinformaticValuationMasterReadSetCodes(analysis, validation,  TBoolean.TRUE, validation.getUser(), new Date());	
			analysisWorkflowsHelper.updateStateMasterReadSetCodes(analysis, validation, "F-VBA");
		}
	
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation, Analysis exp, State nextState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setState(ContextValidation contextValidation, Analysis analysis, State nextState) {
		//on valide l'
		contextValidation.setUpdateMode();
		AnalysisValidationHelper.validateState(analysis.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(analysis.state.code)){
			boolean goBack = goBack(analysis.state, nextState);
			if(goBack)Logger.debug(analysis.code+" : back to the workflow. "+analysis.state.code +" -> "+nextState.code);		

			analysis.traceInformation = updateTraceInformation(analysis.traceInformation, nextState); 
			analysis.state = updateHistoricalNextState(analysis.state, nextState);

			MongoDBDAO.update(InstanceConstants.ANALYSIS_COLL_NAME,  Analysis.class, 
					DBQuery.is("code", analysis.code),
					DBUpdate.set("state", analysis.state).set("traceInformation",analysis.traceInformation));

			applySuccessPostStateRules(contextValidation, analysis);
			nextState(contextValidation, analysis);
		}		

	}

	@Override
	public void nextState(ContextValidation contextValidation, Analysis analysis) {
		State nextStep = cloneState(analysis.state, contextValidation.getUser());

		if("F-BA".equals(analysis.state.code)){
			nextStep.code = "IW-V";
		}else if("IW-V".equals(analysis.state.code)){
			if(!TBoolean.UNSET.equals(analysis.valuation.valid)){
				nextStep.code = "F-V";
			}
		}else if("F-V".equals(analysis.state.code)){
			if(TBoolean.UNSET.equals(analysis.valuation.valid)){
				nextStep.code = "IW-V";
			}
		}

		setState(contextValidation, analysis, nextStep);

	}

}
