package controllers.migration;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;

import play.Logger;
import play.Play;
import play.mvc.Result;
import rules.services.RulesServices6;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;


public class MigrationTaxonomyFungi extends CommonController{
	
	public static Result migration(){
		BasicDBObject keys = new BasicDBObject();
		keys.put("code", 1);
		keys.put("treatments.taxonomy", 1);
		keys.put("treatments.taxonomyKraken", 1);
		
		MongoDBResult<ReadSet> rsl = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.or(DBQuery.exists("treatments.taxonomy"), DBQuery.exists("treatments.taxonomyKraken")), keys);
		
		RulesServices6 rulesServices = RulesServices6.getInstance();
		Logger.info("Treat "+rsl.size()+" readset");
		DBCursor<ReadSet> cursor = rsl.cursor;
		while(cursor.hasNext()){
			ReadSet rs = cursor.next();
			List<Object> facts = new ArrayList<Object>();
			facts.add(rs);				
			rulesServices.callRules(Play.application().configuration().getString("rules.key"), "F_QC_1", facts);
			
		}
		return ok();
	}

}