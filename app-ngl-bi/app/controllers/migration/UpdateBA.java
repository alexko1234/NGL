package controllers.migration;

import java.util.List;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.mvc.Result;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class UpdateBA extends CommonController {


		
	private static final String ANALYSIS_BCK = InstanceConstants.ANALYSIS_COLL_NAME+"_BCK_140826";
	
	
	public static Result migration(){
		
		Logger.info("Migration start");
		
		//JacksonDBCollection<Analysis, String> results = MongoDBDAO.getCollection(ANALYSIS_BCK, Analysis.class);
		//if(results.count() == 0){
			Logger.info("Migration Analysis start");
			//backup();
			List<Analysis> analyses = MongoDBDAO.find(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, DBQuery.is("state.code", "IP-BA")).sort("code").toList();
			Logger.debug("migre "+analyses.size()+" Analysis");
			for(Analysis analysis : analyses){
				migre(analysis);				
			}
			Logger.info("Migration Analysis end");
						
		//}else{
		//	Logger.info("Migration Analysis already execute !");
		//}
		
		Logger.info("Migration finish");
		return ok("Migration Finish");

	}

	

	private static void migre(Analysis analysis) {
		BasicDBObject keys = new BasicDBObject();
		keys.put("sampleOnContainer.properties.taxonSize", 1);
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", analysis.masterReadSetCodes.get(0)), keys);
		
		PropertySingleValue taxonSize = (PropertySingleValue) readSet.sampleOnContainer.properties.get("taxonSize");
		PropertySingleValue assemblyContigSize = (PropertySingleValue) analysis.treatments.get("assemblyBA").results.get("pairs").get("assemblyContigSize");
		
		Double v = ((Number)assemblyContigSize.value).doubleValue() / ((Number)taxonSize.value).doubleValue()  * 100;
		MongoDBDAO.update(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, 
				DBQuery.is("state.code", "IP-BA").is("code", analysis.code), 
				DBUpdate.set("treatments.assemblyBA.pairs.expectedPoolSizePercent.value", v));
		Logger.info(analysis.code +" = "+v);
	}
	
	private static void backup() {
		Logger.info("\tCopie "+InstanceConstants.ANALYSIS_COLL_NAME+" start");		
		//MongoDBDAO.save(ANALYSIS_BCK, MongoDBDAO.find(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class).toList());
		Logger.info("\tCopie "+InstanceConstants.ANALYSIS_COLL_NAME+" end");
		
	}
	
}

