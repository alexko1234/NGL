package controllers.migration;

import controllers.CommonController;
import controllers.migration.models.FileOld2;
import controllers.migration.models.ReadSetOld2;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;

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
			
			// joker "$" don't work, so replace it with 1 to 8 !
			 MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("files"), 
						DBUpdate.unset("files.1.state").unset("files.2.state").unset("files.3.state").unset("files.4.state")
						.unset("files.5.state").unset("files.6.state").unset("files.7.state").unset("files.8.state"));
				 			
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