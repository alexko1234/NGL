package controllers.migration;

import java.util.List;

import controllers.CommonController;
import controllers.migration.models.FileOld2;
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
		
			List<ReadSetOld2> rds = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld2.class).toList();
			Logger.debug("migre "+rds.size()+" readSets");
			for(ReadSetOld2 rd : rds){
				if (rd.files != null) {
					for (FileOld2 f : rd.files) {
						migreFile(rd, f);		
					}
				}
				else {
					Logger.warn("Pas de fichier pour le Readset avec le code : " + rd.code);
				}
			}	
		
		}else{
			Logger.info("Migration readset already execute !");
		}
			
		Logger.info("Migration finish");
		return ok("Migration Finish");
	}

	

	private static void migreFile(ReadSetOld2 readSet, FileOld2 file) {		
		MongoDBDAO.update(
				InstanceConstants.READSET_ILLUMINA_COLL_NAME,
				ReadSetOld2.class,
				DBQuery.and(DBQuery.is("code", readSet.code), DBQuery.is("files.fullname", file.fullname)),
				DBUpdate.unset("files.$.state"));
	}

	
	
	private static void backupReadSet() {
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");		
		MongoDBDAO.save(READSET_ILLUMINA_BCK, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSetOld2.class).toList());
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" end");	
	}
}


