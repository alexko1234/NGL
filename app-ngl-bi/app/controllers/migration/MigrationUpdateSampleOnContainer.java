package controllers.migration;		

import java.text.SimpleDateFormat;
import java.util.List;

import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * Update SampleOnContainer on ReadSet
 * @author galbini
 *
 */
public class MigrationUpdateSampleOnContainer extends CommonController {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
	
	public static Result migration(){
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		
		Logger.info("Migration sample on container start");
		backupReadSet();
		List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("code"), keys).toList();
		Logger.debug("migre "+readSets.size()+" readSets");
		for(ReadSet readSet : readSets){
			migreReadSet(readSet);				
		}
		Logger.info("Migration sample on container finish");
		return ok("Migration Finish");

	}

	

	private static void migreReadSet(ReadSet readSet) {
		SampleOnContainer sampleOnContainer = InstanceHelpers.getSampleOnContainer(readSet);
		if(null != sampleOnContainer){
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), DBUpdate.set("sampleOnContainer", sampleOnContainer));
		}else{
			Logger.error("sampleOnContainer null for "+readSet.code);
		}
	}
	
	private static void backupReadSet() {
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		String backupName = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK_SOC_"+sdf.format(new java.util.Date());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" to "+backupName+" start");		
		MongoDBDAO.save(backupName, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("code"), keys).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" to "+backupName+" end");
		
	}

	

}
