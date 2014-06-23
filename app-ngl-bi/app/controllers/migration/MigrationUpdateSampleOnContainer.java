package controllers.migration;		

import java.util.List;

import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
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
	
	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK4";
	
	
	public static Result migration(){
		
		Logger.info("Migration start");
		
		JacksonDBCollection<ReadSet, String> readSetsCollBck = MongoDBDAO.getCollection(READSET_ILLUMINA_BCK, ReadSet.class);
		if(readSetsCollBck.count() == 0){
			Logger.info("Migration readset start");
			backupReadSet();
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class).toList();
			Logger.debug("migre "+readSets.size()+" readSets");
			for(ReadSet readSet : readSets){
				migreReadSet(readSet);
				
			}
			Logger.info("Migration readset end");
						
		}else{
			Logger.info("Migration readset already execute !");
		}
		
		Logger.info("Migration finish");
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
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");		
		MongoDBDAO.save(READSET_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");
		
	}

	

}
