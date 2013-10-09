package workflows;

import java.util.List;

import play.Logger;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.WriteResult;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import validation.ContextValidation;
import validation.utils.RunPropertyDefinitionHelper;
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
			if(!RunPropertyDefinitionHelper.getRunStateCodes().contains(stateCode)){
				contextValidation.addErrors("stateCode",ValidationConstants.ERROR_VALUENOTAUTHORIZED_MSG, stateCode);
				return;
			}
			
			if("F-RG".equals(stateCode)){
				stateCode = "F";
			}
			
			run.stateCode = stateCode;
			if("F".equals(run.stateCode)){
				run.dispatch = true;
			}
			for(Lane lane:run.lanes){
				lane.stateCode = stateCode;
			}
			run.traceInformation.setTraceInformation("ngsrg");
			contextValidation.setUpdateMode();
			run.validate(contextValidation);
			
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", run.code)).toList();
			for(ReadSet readSet: readSets){
				if("F".equals(run.stateCode)){
					readSet.stateCode = "A";
					readSet.dispatch = Boolean.TRUE;
					for(File file: readSet.files){
						file.stateCode = "A";
					}
				}else{
					readSet.stateCode = stateCode;
					readSet.dispatch = Boolean.FALSE;
					for(File file: readSet.files){
						file.stateCode = stateCode;
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
	

}
