package workflows.readset;

import static fr.cea.ig.play.IGGlobals.akkaSystem;

import java.util.ArrayList;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.Props;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.SampleOnContainer;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import rules.services.RulesServices6;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
import workflows.Workflows;

@Service
public class ReadSetWorkflows extends Workflows<ReadSet>{

	@Autowired
	ReadSetWorkflowsHelper readSetWorkflowsHelper;
	
	// private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	private static ActorRef rulesActor = akkaSystem().actorOf(Props.create(RulesActor6.class));
	
	private static final String ruleFQC="F_QC_1";
	private static final String ruleFRG="F_RG_1";
	private static final String ruleIPS="IP_S_1";
	private static final String ruleFV="F_V_1";
	private static final String ruleFVQC="F_VQC_1";
	private static final String ruleIWBA="IW_BA_1";
	private static final String ruleFBA="F_BA_1";
	private static final String ruleAUA="A-UA_1";
	private static final String ruleA="A_1";
	private static final String ruleFTF="F_TF_1";
	private static final String ruleN = "N_1";
	
	@Override
	public void applyPreStateRules(ContextValidation validation, ReadSet readSet, State nextState) {
		if("N".equals(readSet.state.code)){
			readSetWorkflowsHelper.createSampleReadSetExternal(readSet, validation, ruleN);
		}
	}

	@Override
	public void applyPreValidateCurrentStateRules(ContextValidation validation, ReadSet object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyPostValidateCurrentStateRules(ContextValidation validation, ReadSet object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applySuccessPostStateRules(ContextValidation validation, ReadSet readSet) {

		if("IP-RG".equals(readSet.state.code)){
			readSetWorkflowsHelper.updateContainer(readSet);		
		}else if("F-RG".equals(readSet.state.code)){
			readSetWorkflowsHelper.updateDispatch(readSet);
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ruleFRG, readSet),null);
		}else if("F-QC".equals(readSet.state.code)){			
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ruleFQC, readSet),null);			
		}else if("F-VQC".equals(readSet.state.code)){
			if(TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
				readSetWorkflowsHelper.updateBioinformaticValuation(readSet, readSet.productionValuation.valid, readSet.productionValuation.user, readSet.productionValuation.date);
			}
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), ruleFVQC, readSet),null);

		} else if("IW-BA".equals(readSet.state.code)){
			readSetWorkflowsHelper.updateBioinformaticValuation(readSet, TBoolean.UNSET, null, null);
		} else if("A".equals(readSet.state.code) || "UA".equals(readSet.state.code))	{
			readSetWorkflowsHelper.updateFiles(readSet, validation);
			//if change valuation when final step
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), ruleAUA, readSet),null);
		}
	
	}

	@Override
	public void applyErrorPostStateRules(ContextValidation validation, ReadSet exp, State nextState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setState(ContextValidation contextValidation, ReadSet readSet, State nextState) {
		contextValidation.setUpdateMode();
		RunValidationHelper.validateState(readSet.typeCode, nextState, contextValidation);
		if(!contextValidation.hasErrors() && !nextState.code.equals(readSet.state.code)){
			boolean goBack = goBack(readSet.state, nextState);
			if(goBack)Logger.debug(readSet.code+" : back to the workflow. "+readSet.state.code +" -> "+nextState.code);		

			readSet.traceInformation = updateTraceInformation(readSet.traceInformation, nextState); 
			readSet.state = updateHistoricalNextState(readSet.state, nextState);

			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code", readSet.code),
					DBUpdate.set("state", readSet.state).set("traceInformation",readSet.traceInformation));
			applySuccessPostStateRules(contextValidation, readSet);
			nextState(contextValidation, readSet);
		}	
	}

	@Override
	public void nextState(ContextValidation contextValidation, ReadSet readSet) {
		State nextStep = cloneState(readSet.state, contextValidation.getUser());
		if("F-RG".equals(readSet.state.code)){
			nextStep.code = "IW-QC";
		}else if("F-QC".equals(readSet.state.code)){
			nextStep.code = "IW-VQC";
		}else if("IW-VQC".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.productionValuation.valid)){
				nextStep.code = "F-VQC";
			}
		}else if("IP-VQC".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.productionValuation.valid)){
				nextStep.code = "F-VQC";
			}		
		}else if("F-VQC".equals(readSet.state.code)){
			if(readSetWorkflowsHelper.isHasBA(readSet) && TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "IW-BA";
			}else{
				if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
					nextStep.code = "A";
				}else if(TBoolean.FALSE.equals(readSet.bioinformaticValuation.valid)){
					nextStep.code = "UA";
				}
			}
		}else if("IW-BA".equals(readSet.state.code)){
			Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, readSet.projectCode);
			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(nextStep);
			facts.add(project);
			facts.add(readSet);
			RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), ruleIWBA, facts);
		}else if("F-BA".equals(readSet.state.code)){
			nextStep.code = "IW-VBA";
		}else if("IW-VBA".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "F-VBA";
			}		
		}else if("F-VBA".equals(readSet.state.code)){
			if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "A";
			}else if(TBoolean.FALSE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "UA";
			}					
		}else if("A".equals(readSet.state.code) || "UA".equals(readSet.state.code)){			
			if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "A";
				//Call rules for Transfert CCRT
				//Get project to identify sent to CCRT
				Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, readSet.projectCode);
				ArrayList<Object> facts = new ArrayList<Object>();
				facts.add(nextStep);
				facts.add(project);
				facts.add(readSet);
				RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), ruleA, facts);
			}else { //FALSE or UNSET
				nextStep.code = "UA";
			}			
		}else if("F-TF".equals(readSet.state.code)){
			Project project = MongoDBDAO.findByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, readSet.projectCode);
			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(nextStep);
			facts.add(project);
			RulesServices6.getInstance().callRulesWithGettingFacts(Play.application().configuration().getString("rules.key"), ruleFTF, facts);
		}
		setState(contextValidation, readSet, nextStep);
	}

}
