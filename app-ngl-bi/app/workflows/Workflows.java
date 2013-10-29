package workflows;

import java.util.Date;
import java.util.List;



import net.vz.mongodb.jackson.DBQuery;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import validation.ContextValidation;

import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class Workflows {
	
	/***
	 * Put the state of Run
	 * @param contextValidation 
	 * @param code
	 * @param stateCode
	 */
	public static void setRunState(ContextValidation contextValidation, Run run, String stateCode){
		if(ValidationHelper.required(contextValidation, stateCode, "stateCode")){
			
			if(!isStateCodeExist(stateCode)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, stateCode);
				return;
			}
			
			if("F-RG".equals(stateCode)){
				stateCode = "F";
			}
			
			State state = getState(stateCode);
			
			run.state = state;
			if("F".equals(state.code)){
				run.dispatch = true;
			}
			for(Lane lane:run.lanes){
				lane.state = state;
			}
			run.traceInformation.setTraceInformation("ngsrg");
			contextValidation.setUpdateMode();
			run.validate(contextValidation);
			
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code)).toList();
			for(ReadSet readSet: readSets){
				if("F".equals(state.code)){
					readSet.state = getState("A");
					readSet.dispatch = Boolean.TRUE;
					for(File file: readSet.files){
						file.state = getState("A");
					}
				}else{
					readSet.state = getState(stateCode);
					readSet.dispatch = Boolean.FALSE;
					for(File file: readSet.files){
						file.state = getState("A");
					}
				}
				readSet.traceInformation.setTraceInformation("ngsrg");
				readSet.validate(contextValidation);
			}
			if(!contextValidation.hasErrors()){
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
				for(ReadSet readSet: readSets){
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
				}
			}
			
			
		}		
	}

	private static State getState(String stateCode) {
		State state = new State();
		state.code = stateCode;
		state.date = new Date();
		state.user = "ngsrg"; //TODO change pour prendre le user authentifié
		return state;
	}

	private static Boolean isStateCodeExist(String stateCode) {
		try {
			return models.laboratory.common.description.State.find.isCodeExist(stateCode);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}
	}
	

}
