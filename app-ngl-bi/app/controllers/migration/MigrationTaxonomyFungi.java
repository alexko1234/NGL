package controllers.migration;

import java.util.ArrayList;
import java.util.List;

import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import play.Logger;
import play.Play;
import play.mvc.Result;
import rules.services.RulesException;
import rules.services.RulesServices;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;


public class MigrationTaxonomyFungi extends CommonController{
	
	public static Result migration(){
		BasicDBObject keys = new BasicDBObject();
		keys.put("code", 1);
		keys.put("treatments.taxonomy", 1);
		
		List<ReadSet> rsl = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("treatments.taxonomy"), keys).toList();
		
		RulesServices rulesServices = new RulesServices();
		Logger.info("Treat "+rsl.size()+" readset");
		for(ReadSet rs : rsl){
			
			try {
				List<Object> facts = new ArrayList<Object>();
				facts.add(rs);
				
				rulesServices.callRules(Play.application().configuration().getString("rules.key"), "F_QC_1", facts);
			} catch (RulesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ok();
	}
	

}
