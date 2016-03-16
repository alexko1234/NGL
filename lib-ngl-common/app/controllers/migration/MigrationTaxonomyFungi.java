package controllers.migration;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import play.Logger;
import play.Play;
import play.mvc.Result;
import rules.services.RulesServices6;


public class MigrationTaxonomyFungi extends CommonController{
	
	public static Result migration(String code){
		BasicDBObject keys = new BasicDBObject();
		keys.put("code", 1);
		keys.put("treatments.taxonomy", 1);
		MongoDBResult<ReadSet> rsl = null;
		if(!"all".equals(code)){
			rsl = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("code", code).exists("treatments.taxonomy"), keys);
		}else{
			rsl = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("treatments.taxonomy"), keys);
		}
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