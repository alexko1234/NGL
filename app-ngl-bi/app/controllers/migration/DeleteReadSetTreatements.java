package controllers.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.laboratory.run.description.TreatmentType;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.mvc.Result;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class DeleteReadSetTreatements extends CommonController {


		
private static final String READSET_ILLUMINA_BCK = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK_20140827";
	
	
	public static Result migration() throws DAOException{
		
		Logger.info("Migration start");
		
		//JacksonDBCollection<ReadSet, String> readSetsCollBck = MongoDBDAO.getCollection(READSET_ILLUMINA_BCK, ReadSet.class);
		//if(readSetsCollBck.count() == 0){
			Logger.info("Migration readset start");
			//backupReadSet();
			BasicDBObject keys = new BasicDBObject();
			keys.put("code", 1);
			keys.put("treatments", 1);
			
			
			
			
			List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.in("code", getCodeReadSets()), keys)
					.toList();
			
			
			List<TreatmentType> tTypes = TreatmentType.find.findByTreatmentCategoryNames("quality");
			
			List<String> instanceNames = new ArrayList<String>();
			
			for(TreatmentType tt:tTypes){
				instanceNames.addAll(Arrays.asList(tt.names.split(",")));				
			}
			
			DBUpdate.Builder dbUpdate = DBUpdate.set("state.code", "IW-QC");
			for(String in: instanceNames){
				dbUpdate.unset("treatments."+in);				
			}
			
			Logger.debug("migre "+readSets.size()+" readSets");
			for(ReadSet readSet : readSets){
				Logger.debug("migre "+readSet.code);
					migreReadSet(readSet, dbUpdate);				
				//				
			}
			Logger.info("Migration readset end");
						
		//}else{
		//	Logger.info("Migration readset already execute !");
		//}
		
		Logger.info("Migration finish");
		return ok("Migration Finish");

	}

	private static void migreReadSet(ReadSet readSet, DBUpdate.Builder update) {		
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, 
					DBQuery.is("code", readSet.code), update);		
	}
	
	
	private static List<String> getCodeReadSets(){
		return Arrays.asList("ARD_APQAOSW_7_C3NBBACXX.IND6");
	}
}

