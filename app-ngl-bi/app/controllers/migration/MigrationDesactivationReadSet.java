package controllers.migration;		

import java.util.List;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * Set resolution QC "Run-abandonLane" for readSets of invalid lanes
 * @author dnoisett
 * 06-05-2014, NGL-202
 */

public class MigrationDesactivationReadSet extends CommonController {
	
	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK_DESACTIVE";
	public static int cpt = 0;	
	
	public static Result migration(){
		
		Logger.info("Migration start");

		JacksonDBCollection<ReadSet, String> readSetsCollBck = MongoDBDAO.getCollection(READSET_ILLUMINA_BCK, ReadSet.class);
		if(readSetsCollBck.count() == 0){
			
			Logger.info("Migration readset start");
			
			backupReadSet();

			migreReadSet();				
			
			Logger.info("Migration readset end : " + cpt + " readSets updated!");			
		}else{
			Logger.info("Migration readset already execute !");
		}
		
		Logger.info("Migration finish");
		return ok("Migration Finish");
		
	}

	

	private static void migreReadSet() {
		List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList();
		for (Run run : runs) {
			for (Lane lane : run.lanes) {
				if (lane.valuation.valid == TBoolean.FALSE ) {
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
							DBQuery.and(DBQuery.is("runCode", run.code), DBQuery.is("laneNumber", lane.number)), 
							DBUpdate.push("productionValuation.resolutionCodes", "Run-abandonLane"));
					cpt++;
				}
			}	
		}
	}
	
	
	private static void backupReadSet() {
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");		
		MongoDBDAO.save(READSET_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");
		
	}

}
