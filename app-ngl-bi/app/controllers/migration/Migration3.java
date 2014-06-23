package controllers.migration;		

import java.util.List;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * Update file extensions (Jira NGL-138)
 * @author dnoisett
 * 21-02-2014
 */

public class Migration3 extends CommonController {
	
	private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK3";
	
	
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
		
		String fileExtension1 = "fastq";
		String fileExtension2 = "gz";
		String shortName = "";
		String fullName = "";
		
		if (readSet.files != null  && readSet.files.size() > 0) {
			for (File file : readSet.files) {
				
				if ((file.fullname.endsWith(fileExtension1))) {
					//old situation : migration
					shortName = file.fullname.split("\\.")[0];
					fullName = shortName + "." + fileExtension1 + "." + fileExtension2;
	
					MongoDBDAO.update(
							InstanceConstants.READSET_ILLUMINA_COLL_NAME,
							ReadSet.class,
							DBQuery.and(DBQuery.is("code", readSet.code),
									DBQuery.is("files.fullname", file.fullname)),
							DBUpdate.set("files.$.fullname", fullName).set("files.$.extension", fileExtension1 + "." + fileExtension2)
							);
	
				}
				else {
					if (!file.fullname.endsWith(fileExtension1 + "." + fileExtension2)) {
						//unknown case
						Logger.debug("----------------- Wrong fullname = " + file.fullname);
						
					}
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
