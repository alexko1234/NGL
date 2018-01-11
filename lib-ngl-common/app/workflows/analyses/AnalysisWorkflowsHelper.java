package workflows.analyses;

import java.util.Date;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import workflows.readset.ReadSetWorkflows;

@Service
public class AnalysisWorkflowsHelper {

	@Autowired
	ReadSetWorkflows readSetWorflows;
	
	public void updateStateMasterReadSetCodes(Analysis analysis, ContextValidation validation, String nextStepCode)
	{
		for(String rsCode : analysis.masterReadSetCodes){
			ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, rsCode, getReadSetKeys());
			State nextStep = cloneState(readSet.state, validation.getUser());
			nextStep.code = nextStepCode;
			readSetWorflows.setState(validation, readSet, nextStep);
		}
	}
	
	public void updateBioinformaticValuationMasterReadSetCodes(Analysis analysis, ContextValidation validation, TBoolean valid, String user, Date date)
	{
		for(String rsCode : analysis.masterReadSetCodes){
			ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, rsCode, getReadSetKeys());
			//if different of state IW-VBA
			if(valid.equals(TBoolean.UNSET) && !"IW-VBA".equals(readSet.state.code)){
				readSet.bioinformaticValuation.valid = valid;
				readSet.bioinformaticValuation.date = date;
				readSet.bioinformaticValuation.user = user;

				readSet.traceInformation.modifyDate = new Date();
				readSet.traceInformation.modifyUser = validation.getUser();


				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
						DBQuery.is("code", rsCode), DBUpdate.set("bioinformaticValuation", readSet.bioinformaticValuation).set("traceInformation", readSet.traceInformation));

			}
		}
	}
	
	
	public BasicDBObject getReadSetKeys() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		return keys;
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
}
