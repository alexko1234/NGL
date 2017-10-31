package workflows.run;

import static fr.cea.ig.play.IGGlobals.akkaSystem;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import akka.actor.ActorRef;
import akka.actor.Props;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.Play;
import play.libs.Akka;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import validation.ContextValidation;
import workflows.readset.ReadSetWorkflows;

@Service
public class RunWorkflowsHelper {

	@Autowired
	ReadSetWorkflows readSetWorkflows;

	//private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	private static ActorRef rulesActor = akkaSystem().actorOf(Props.create(RulesActor6.class));

	public void updateDispatchRun(Run run)
	{
		//update dispatch
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
				DBQuery.is("code", run.code), DBUpdate.set("dispatch", Boolean.TRUE));
	}

	public void updateReadSetLane(Run run, ContextValidation contextValidation, String rules, boolean bioinformaticValuation)
	{
		//Get readSet from lane with VALID = FALSE 
		if(null != run.lanes){
			for(Lane lane : run.lanes){
				List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.and(DBQuery.is("runCode", run.code), DBQuery.is("laneNumber", lane.number)),getReadSetKeys()).toList();
				if(readSets.size() != lane.readSetCodes.size())Logger.error("Problem with number of readsets for run = "+run.code+" and lane = "+lane.number+". Nb RS in lane = "+lane.readSetCodes.size()+", nb RS by query = "+readSets.size());
				for(ReadSet readSet : readSets){
					if(lane.valuation.valid.equals(TBoolean.FALSE)){
						invalidateReadSet(readSet, contextValidation, rules, bioinformaticValuation);
					}else{
						State nextReadSetState = cloneState(run.state, contextValidation.getUser());
						readSetWorkflows.setState(contextValidation, readSet, nextReadSetState);
					}
				}
			}
		}else{
			MongoDBResult<ReadSet> readSetResult = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code));
			DBCursor<ReadSet> cursor = readSetResult.cursor;
			while(cursor.hasNext()){
				ReadSet readSet = cursor.next();
				State nextReadSetState = cloneState(run.state, contextValidation.getUser());
				readSetWorkflows.setState(contextValidation, readSet, nextReadSetState);
			}	
		}
	}

	public void invalidateReadSetLane(Run run, ContextValidation contextValidation, String rules, boolean bioinformaticValuation)
	{
		if(null != run.lanes){
			for(Lane lane : run.lanes){
				List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.and(DBQuery.is("runCode", run.code), DBQuery.is("laneNumber", lane.number)), getReadSetKeys()).toList();
				if(readSets.size() != lane.readSetCodes.size())Logger.error("Problem with number of readsets for run = "+run.code+" and lane = "+lane.number+". Nb RS in lane = "+lane.readSetCodes.size()+", nb RS by query = "+readSets.size());
				for(ReadSet readSet : readSets){
					if(lane.valuation.valid.equals(TBoolean.FALSE)){
						invalidateReadSet(readSet, contextValidation, rules, bioinformaticValuation);
					}
				}
			}
		}
	}

	private void invalidateReadSet(ReadSet readSet, ContextValidation contextValidation, String rules, boolean bioinformaticValuation)
	{
		readSet.productionValuation.valid = TBoolean.FALSE;
		readSet.productionValuation.date = new Date();
		readSet.productionValuation.user = contextValidation.getUser();
		if(null == readSet.productionValuation.resolutionCodes)readSet.productionValuation.resolutionCodes = new HashSet<String>(1);
		readSet.productionValuation.resolutionCodes.add("Run-abandonLane");

		if(bioinformaticValuation){
			readSet.bioinformaticValuation.valid = TBoolean.FALSE;
			readSet.bioinformaticValuation.date = new Date();
			readSet.bioinformaticValuation.user = contextValidation.getUser();
			//if(null == readSet.bioinformaticValuation.resolutionCodes)readSet.bioinformaticValuation.resolutionCodes = new HashSet<String>(1);
			//readSet.bioinformaticValuation.resolutionCodes.add("Run-abandonLane");
		}
		readSet.traceInformation.modifyDate = new Date();
		readSet.traceInformation.modifyUser = contextValidation.getUser();

		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
				DBQuery.is("code", readSet.code), DBUpdate.set("productionValuation", readSet.productionValuation).set("traceInformation", readSet.traceInformation));

		if(bioinformaticValuation){
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), DBUpdate.set("bioinformaticValuation", readSet.bioinformaticValuation));
		}
		
		State nextState = cloneState(readSet.state, contextValidation.getUser());
		nextState.code = "F-VQC";
		readSetWorkflows.setState(contextValidation, readSet, nextState);
		rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"), rules, readSet),null);
	}

	/**
	 * Clone State without historical
	 * @param state
	 * @return
	 */
	private static State cloneState(State state, String user) {
		State nextState = new State();
		nextState.code = state.code;
		nextState.date = new Date();
		nextState.user = user;
		return nextState;
	}

	private static BasicDBObject getReadSetKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		return keys;
	}

	public boolean atLeastOneValuation(Run run) {

		if(!run.valuation.valid.equals(TBoolean.UNSET)){
			return true;
		}
		if(null != run.lanes){
			for(Lane lane : run.lanes){
				if(!lane.valuation.valid.equals(TBoolean.UNSET)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isRunValuationComplete(Run run) {

		if(run.valuation.valid.equals(TBoolean.UNSET)){
			return false;
		}
		if(null != run.lanes){
			for(Lane lane : run.lanes){
				if(lane.valuation.valid.equals(TBoolean.UNSET)){
					return false;
				}
			}
		}
		return true;
	}
}
