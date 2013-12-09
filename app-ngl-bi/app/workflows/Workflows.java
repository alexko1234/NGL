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
import models.laboratory.common.instance.TransientState;
import models.laboratory.run.instance.File;
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

	public static void setReadSetState(ContextValidation ctxVal, ReadSet readSet, State state) {
		//TODO Must be manage
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
	
	
	

}
