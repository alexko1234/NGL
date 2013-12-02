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
import controllers.migration.models.LaneOld2;
import controllers.migration.models.ReadSetOld;
import controllers.migration.models.RunOld;
import controllers.migration.models.RunOld2;
import fr.cea.ig.MongoDBDAO;

public class Migration2 extends CommonController {
	
	private static final String RUN_ILLUMINA_BCK = InstanceConstants.RUN_ILLUMINA_COLL_NAME+"_BCK";
	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK";
	
	
	public static Result migration(){
		Logger.info("Migration run start");
		List<RunOld2> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, RunOld2.class).toList();
		Logger.debug("migre "+runs.size()+" runs");
		for(RunOld2 run : runs){
			migreRun(run);				
		}	
		
		Logger.info("Migration finish");
		return ok("Migration Finish");
		
	}

	

	private static void migreRun(RunOld2 run) {
		
		
		if (run.lanes != null) {
			for (LaneOld2 laneOld : run.lanes) {

				MongoDBDAO.update(
						InstanceConstants.RUN_ILLUMINA_COLL_NAME,
						Run.class,
						DBQuery.and(DBQuery.is("code", run.code),
								DBQuery.is("lanes.number", laneOld.number)),
						DBUpdate.unset("lanes.$.validation")
								.set("lanes.$.valuation", laneOld.validation));
			}
		}
	}

	

}
