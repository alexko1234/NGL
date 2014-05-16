package controllers.migration;

import controllers.CommonController;
import controllers.migration.models.ReadSetOld2;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;

import fr.cea.ig.MongoDBDAO;

import play.Logger;
import play.mvc.Result;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

public class RemoveStateFromFile  extends CommonController {

	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK";

	public static Result migration(){
		
		JacksonDBCollection<ReadSet, String> readSetsCollBck = MongoDBDAO.getCollection(READSET_ILLUMINA_BCK, ReadSet.class);
		if(readSetsCollBck.count() == 0){
			
			Logger.info("Migration readset start");
			
			backupReadSet();			
			
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("files.state"), 
					DBUpdate.unset("files.$.state"), true);
			 	
		
		}else{
			Logger.info("Migration readset already execute !");
		}
			
		Logger.info("Migration finish");
		return ok("Migration Finish");
	}

	
	
	private static void backupReadSet() {
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");		
		MongoDBDAO.save(READSET_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld2.class).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");	
	}
}


