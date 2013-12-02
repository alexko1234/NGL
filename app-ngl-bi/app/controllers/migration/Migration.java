package controllers.migration;		

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.Valuation;
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
	
	private static final String RUN_ILLUMINA_BCK = InstanceConstants.RUN_ILLUMINA_COLL_NAME+"_BCK";
	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK";
	
	
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
		Valuation valuation = new Valuation();
		State state = new State();
		state.code = readSet.stateCode;
		
		state.user = (null == readSet.traceInformation.modifyUser) ? readSet.traceInformation.createUser : readSet.traceInformation.modifyUser;
		state.date = (null == readSet.traceInformation.modifyUser) ? readSet.traceInformation.creationDate : readSet.traceInformation.modifyDate;
		
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class, 
				DBQuery.is("code", readSet.code), 
				DBUpdate.unset("stateCode").unset("validProduction").unset("validProductionDate").unset("validBioinformatic").unset("validBioinformaticDate")
				.unset("sampleContainerCode").set("productionValuation", valuation).set("bioinformaticValuation", valuation).set("state", state).set("sampleCode", readSet.sampleContainerCode));
		
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
		
		Valuation valuation = new Valuation();
		State state = new State();
		state.code = run.stateCode;
		
		state.user = (null == run.traceInformation.modifyUser) ? run.traceInformation.createUser : run.traceInformation.modifyUser;
		state.date = (null == run.traceInformation.modifyUser) ? run.traceInformation.creationDate : run.traceInformation.modifyDate;
		
		Set<String> projectCodes = new TreeSet<String>();
		Set<String> sampleCodes = new TreeSet<String>();
		List<ReadSetOld> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld.class, DBQuery.is("runCode", run.code)).toList();
		for (ReadSetOld readSetOld : readSets) {
			projectCodes.add(readSetOld.projectCode);
			sampleCodes.add(readSetOld.sampleCode);
		}
		
		
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.is("code", run.code), 
				DBUpdate.unset("stateCode").unset("valid").unset("validDate")
				.set("valuation", valuation).set("state", state).set("projectCodes", projectCodes).set("sampleCodes", sampleCodes));
		
		if (run.lanes != null) {
			for (LaneOld laneOld : run.lanes) {

				MongoDBDAO.update(
						InstanceConstants.RUN_ILLUMINA_COLL_NAME,
						Run.class,
						DBQuery.and(DBQuery.is("code", run.code),
								DBQuery.is("lanes.number", laneOld.number)),
						DBUpdate.unset("lanes.$.stateCode")
								.unset("lanes.$.valid")
								.unset("lanes.$.validDate")
								.set("lanes.$.valuation", valuation));
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
