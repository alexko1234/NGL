package workflows;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;

import com.typesafe.config.ConfigFactory;

import play.Logger;
import play.libs.Akka;
import rules.services.RulesActor;
import rules.services.RulesMessage;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.TransientState;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import validation.ContextValidation;

import validation.run.instance.ReadSetValidationHelper;
import validation.run.instance.RunValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class Workflows {
	
	private static ActorRef rulesActor = Akka.system().actorOf(new Props(RulesActor.class));
	private static final String ruleStatRG="rg_1";
	/***
	 * Put the state of Run
	 * @param contextValidation 
	 * @param code
	 * @param stateCode
	 */
	public static void setRunState(ContextValidation contextValidation, Run run, State state, State oldState){
		if(ValidationHelper.required(contextValidation, state.code, "stateCode")){
			if(!isStateCodeExist(state.code)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, state.code);
				return;
			}
			if("F-RG".equals(state.code)){
				state.code = "F";
			}

			run.traceInformation.setTraceInformation(state.user);
			run.state = state;
			
			contextValidation.setUpdateMode();
			RunValidationHelper.validateState(run.typeCode, run.state, contextValidation);
			contextValidation.putObject("run", run);
			if("F".equals(state.code)){
				run.dispatch = true;
			}

			if(!"E".equals(state.code)){
				List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code)).toList();
				for(ReadSet readSet: readSets){
					if("F".equals(state.code)){
						readSet.state = getState("A", state.user);
						readSet.dispatch = Boolean.TRUE;
						for(File file: readSet.files){
							file.state = getState("A", state.user);
						}
					}else{
						readSet.state = state;
						readSet.dispatch = Boolean.FALSE;
						for(File file: readSet.files){
							file.state = getState("A", state.user);
						}
					}
					readSet.traceInformation.setTraceInformation(state.user);
					contextValidation.addKeyToRootKeyName("readSet."+readSet.code);
					ReadSetValidationHelper.validateState(readSet.typeCode, readSet.state, contextValidation);
					contextValidation.removeKeyFromRootKeyName("readSet."+readSet.code);
				}
				if(!contextValidation.hasErrors()){
					
					// for having the historical of states (until the last state, excepted it)
					State state2 = saveHistorical(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run.code, oldState);
					run.state.historical = state2.historical; 
					
					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
					
					//for having the last state in the historical
					saveHistorical(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run.code, state);
					
					
					for(ReadSet readSet: readSets){
						MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
					}
				}
				else {
					Logger.debug(contextValidation.errors.toString()); 
				}
			}else if(!contextValidation.hasErrors()){
				
				State state2 = saveHistorical(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run.code, oldState);
				run.state.historical = state2.historical; 
				
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);	
				
				saveHistorical(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run.code, state);
			}
			
		}	
		
		//Appel des règles avec en parametre state.code
		//Règles sur state.code="F-RG"
		//Verifier qu on recupere bien le run modifie
		//Send run fact
		if(!contextValidation.hasErrors() && "F-RG".equals(state.code)){
			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(run);
			// Outside of an actor and if no reply is needed the second argument can be null
			rulesActor.tell(new RulesMessage(facts,ConfigFactory.load().getString("rules.key"),ruleStatRG),null);
		}
		
	}

	private static State getState(String code, String user) {
		State state = new State();
		state.code = code;
		state.date = new Date();
		state.user = user;
		return state;
	}
	
	private static Boolean isStateCodeExist(String stateCode) {
		try {
			return models.laboratory.common.description.State.find.isCodeExist(stateCode);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static State saveHistorical(String collectionName, String objectCode, State state) {
		//we put in the historical the last state....(in fact, add a TransientState)
		
		boolean toUpdate = false;
		
		TransientState stateToHistorized = new TransientState(); 
		stateToHistorized.code = state.code;
		stateToHistorized.date = state.date;
		stateToHistorized.user = state.user;
		
		if (state.historical != null && state.historical.size() > 0) {
			int maxIndex = state.historical.size() - 1;
			if (! state.historical.get(maxIndex).code.equals(stateToHistorized.code)) {
				stateToHistorized.index =  state.historical.size() +1;
				state.historical.add(stateToHistorized);
				toUpdate = true;
			}
		}
		else {
			ArrayList<TransientState> l = new ArrayList<TransientState>();
			stateToHistorized.index = 1;
			l.add(stateToHistorized); 
			state.historical = l; 
			toUpdate = true;
		}

		if (toUpdate) {
			MongoDBDAO.update(collectionName,  Run.class, 
					DBQuery.is("code", objectCode),
					DBUpdate.push("state.historical", stateToHistorized)); 
		}
		return state;
		
	}

		
	
	public static void setRunState(ContextValidation contextValidation, Run run, State nextState) {
		
		//on valide l'état			
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
			
			applyRunRules(run);
			nextRunState(contextValidation, run);
		}		
	}

	
	public static void nextRunState(ContextValidation contextValidation, Run run) {
		State nextStep = cloneState(run.state);
		if("F-RG".equals(run.state.code)){
			nextStep.code = "IW-V";
		}else if("F-S".equals(run.state.code)){
			nextStep.code = "IW-RG";
		}else if("IW-V".equals(run.state.code) && atLeastOneValuation(run)){
			nextStep.code = "IP-V";
		}else if("IP-V".equals(run.state.code) && isRunValuationComplete(run)){
			nextStep.code = "F-V";
		}
		setRunState(contextValidation, run, nextStep);
	}

	private static boolean isRunValuationComplete(Run run) {

		if(run.valuation.valid.equals(TBoolean.UNSET)){
			return false;
		}
		for(Lane lane : run.lanes){
			if(lane.valuation.valid.equals(TBoolean.UNSET)){
				return false;
			}
		}
		return true;
	}
	
	private static boolean atLeastOneValuation(Run run) {

		if(!run.valuation.valid.equals(TBoolean.UNSET)){
			return true;
		}
		for(Lane lane : run.lanes){
			if(!lane.valuation.valid.equals(TBoolean.UNSET)){
				return true;
			}
		}
		return false;
	}
	
	private static void applyRunRules(Run run) {
		if("F-RG".equals(run.state.code)){
			ArrayList<Object> facts = new ArrayList<Object>();
			facts.add(run);		
			rulesActor.tell(new RulesMessage(facts,ConfigFactory.load().getString("rules.key"),ruleStatRG),null);
		}
	}

	public static void setReadSetState(ContextValidation contextValidation, ReadSet readSet, State nextState) {
		
		//on valide l'état			
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
			nextReadSetState(contextValidation, readSet);
		}	
	}
	
	public static void nextReadSetState(ContextValidation contextValidation, ReadSet readSet) {
		State nextStep = cloneState(readSet.state);
		if("F-RG".equals(readSet.state.code)){
			nextStep.code = "IW-QC";
		}else if("F-QC".equals(readSet.state.code)){
			nextStep.code = "IW-V";
		}else if("IW-V".equals(readSet.state.code) || "IP-V".equals(readSet.state.code)){
			if(!TBoolean.UNSET.equals(readSet.bioinformaticValuation.valid)
				&& !TBoolean.UNSET.equals(readSet.productionValuation.valid)){
				nextStep.code = "F-V";
			}else{
				nextStep.code = "IP-V";
			}		
		}else if("F-V".equals(readSet.state.code)){
			if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "A";
			}else if(TBoolean.FALSE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "UA";
			}			
		}else if("A".equals(readSet.state.code) || "UA".equals(readSet.state.code)){
			if(TBoolean.TRUE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "A";
			}else if(TBoolean.FALSE.equals(readSet.bioinformaticValuation.valid)){
				nextStep.code = "UA";
			}
		}
		setReadSetState(contextValidation, readSet, nextStep);
	}
	
	
	private static State updateHistoricalNextState(State previousState, State nextState) {
		if (null == previousState.historical) {
			nextState.historical = new ArrayList<TransientState>(0);
		} else {
			nextState.historical = previousState.historical;
		}
		nextState.historical.add(new TransientState(previousState, nextState.historical.size()));		
		return nextState;
	}

	private static TraceInformation updateTraceInformation(
			TraceInformation traceInformation, State nextState) {		
		traceInformation.modifyDate = nextState.date;
		traceInformation.modifyUser = nextState.user;		
		return traceInformation;
	}

	private static boolean goBack(State previousState, State nextState) {
		models.laboratory.common.description.State nextStateDesc = getStateDescription(nextState);
		models.laboratory.common.description.State previousStateDesc = getStateDescription(previousState);
		boolean goBack = false;
		if(nextStateDesc.position < previousStateDesc.position){
			goBack=true;
			
		}
		return goBack;
	}

	private static models.laboratory.common.description.State getStateDescription(
			State state) {
		try {
			return models.laboratory.common.description.State.find.findByCode(state.code);

		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Clone State without historical
	 * @param state
	 * @return
	 */
	private static State cloneState(State state) {
		State nextState = new State();
		nextState.code = state.code;
		nextState.date = state.date;
		nextState.user = state.user;
		return nextState;
	}

	
	
	

}
