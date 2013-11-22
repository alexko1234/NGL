package workflows;

import java.util.Date;
import java.util.List;

import play.Logger;



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

import validation.run.instance.LaneValidationHelper;
import validation.run.instance.ReadSetValidationHelper;
import validation.run.instance.RunValidationHelper;
import validation.utils.ValidationConstants;
import validation.utils.ValidationHelper;

public class Workflows {
	
	/***
	 * Put the state of Run
	 * @param contextValidation 
	 * @param code
	 * @param stateCode
	 */
	public static void setRunState(ContextValidation contextValidation, Run run, State state){
		if(ValidationHelper.required(contextValidation, state.code, "stateCode")){
			if(!isStateCodeExist(state.code)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, state.code);
				return;
			}
			/*
			if("F-RG".equals(state.code)){
				state.code = "F";
			}
			*/
			run.traceInformation.setTraceInformation("ngsrg");
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
					readSet.traceInformation.setTraceInformation("ngsrg");
					ReadSetValidationHelper.validateState(readSet.typeCode, readSet.state, contextValidation);
				}
				if(!contextValidation.hasErrors()){
					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
					for(ReadSet readSet: readSets){
						MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
					}
				}
			}else if(!contextValidation.hasErrors()){
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);				
			}
			
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
	

}
