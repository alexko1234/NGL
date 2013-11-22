package controllers.migration;		

import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.Validation;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.models.FileOld;
import controllers.migration.models.LaneOld;
import controllers.migration.models.ReadSetOld;
import controllers.migration.models.RunOld;
import fr.cea.ig.MongoDBDAO;

public class Migration extends CommonController {
	
	private static final String RUN_ILLUMINA_BCK = InstanceConstants.RUN_ILLUMINA_COLL_NAME+"_initData2"+"_BCK";
	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_initData2"+"_BCK";
	
	
	public static Result migration(){
		JacksonDBCollection<RunOld, String> runsCollBck = MongoDBDAO.getCollection(RUN_ILLUMINA_BCK, RunOld.class);
		if(runsCollBck.count() == 0){
			Logger.info("Migration run start");
			backupRun();
			List<RunOld> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, RunOld.class).toList();
			Logger.debug("migre "+runs.size()+" runs");
			for(RunOld run : runs){
				migreRun(run);				
			}
		
		}else{
			Logger.info("Migration run already execute !");
		}
		
		
		JacksonDBCollection<ReadSetOld, String> readSetsCollBck = MongoDBDAO.getCollection(READSET_ILLUMINA_BCK, ReadSetOld.class);
		if(readSetsCollBck.count() == 0){
			Logger.info("Migration readset start");
			backupReadSet();
			List<ReadSetOld> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class).toList();
			Logger.debug("migre "+readSets.size()+" readSets");
			for(ReadSetOld readSet : readSets){
				migreReadSet(readSet);				
			}
			
						
		}else{
			Logger.info("Migration readset already execute !");
		}
		
		check();
		Logger.info("Migration finish");
		return ok("Migration Finish");
		
	}

	private static void migreReadSet(ReadSetOld readSet) {
		Validation validation = new Validation();
		State state = new State();
		state.code = readSet.stateCode;
		
		state.user = (null == readSet.traceInformation.modifyUser) ? readSet.traceInformation.createUser : readSet.traceInformation.modifyUser;
		state.date = (null == readSet.traceInformation.modifyUser) ? readSet.traceInformation.creationDate : readSet.traceInformation.modifyDate;
		
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class, 
				DBQuery.is("code", readSet.code), 
				DBUpdate.unset("stateCode").unset("validProduction").unset("validProductionDate").unset("validBioinformatic").unset("validBioinformaticDate")
				.unset("sampleContainerCode").set("validationProduction", validation).set("validationBioinformatic", validation).set("state", state).set("sampleCode", readSet.sampleContainerCode));
		
		if(null != readSet.files){
			for(FileOld fileOld : readSet.files){
				
				if (fileOld.stateCode != null) {
					if(!fileOld.stateCode.equals(state.code)){
						throw new RuntimeException("file state are different from run state");
					}
					
					
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class, 
							DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", fileOld.fullname)),
							DBUpdate.unset("files.$.stateCode").set("files.$.state", state));	
				}
			}
		}
		
	}

	

	private static void migreRun(RunOld run) {
		
		Validation validation = new Validation();
		State state = new State();
		state.code = run.stateCode;
		
		state.user = (null == run.traceInformation.modifyUser) ? run.traceInformation.createUser : run.traceInformation.modifyUser;
		state.date = (null == run.traceInformation.modifyUser) ? run.traceInformation.creationDate : run.traceInformation.modifyDate;
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.is("code", run.code), 
				DBUpdate.unset("stateCode").unset("valid").unset("validDate")
				.set("validation", validation).set("state", state));
		
		if (run.lanes != null) {
			for (LaneOld laneOld : run.lanes) {

				MongoDBDAO.update(
						InstanceConstants.RUN_ILLUMINA_COLL_NAME,
						Run.class,
						DBQuery.and(DBQuery.is("code", run.code),
								DBQuery.is("lanes.number", laneOld.number)),
						DBUpdate.unset("lanes.$.state")
								.unset("lanes.$.stateCode")
								.unset("lanes.$.valid")
								.unset("lanes.$.validDate")
								.set("lanes.$.validation", validation));
			}
		}
	}

	private static void check() {
		
	}

	

	private static void backupRun() {
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" start");
		MongoDBDAO.save(RUN_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, RunOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" end");
		
	}
	
	private static void backupReadSet() {
		
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");		
		MongoDBDAO.save(READSET_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");
		
	}

}
