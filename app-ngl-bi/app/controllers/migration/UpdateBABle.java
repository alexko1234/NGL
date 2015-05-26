package controllers.migration;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;

import akka.actor.ActorRef;
import akka.actor.Props;

import com.mongodb.BasicDBObject;

import play.Logger;
import play.Play;
import play.libs.Akka;
import play.mvc.Result;
import rules.services.RulesActor;
import rules.services.RulesMessage;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class UpdateBABle extends CommonController {
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor.class));
	
	public static Result migration(){
		Logger.info("Migration Analysis start");
		List<Analysis> analyses = MongoDBDAO.find(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, DBQuery.notExists("treatments.contigFilterBA.pairs.lostBasesPercent")).sort("code").toList();
		Logger.debug("migre "+analyses.size()+" Analysis for LostBasePercent");
		for(Analysis analysis : analyses){
			migre(analysis,"BPA_ContigFilter_1");				
		}
		
		
		analyses = MongoDBDAO.find(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, DBQuery.notExists("masterReadSetProperties")).sort("code").toList();
		Logger.debug("migre "+analyses.size()+" Analysis for ReadSetProperties");
		for(Analysis analysis : analyses){
			migre(analysis,"BPA_SetReadSetProperties_1");				
		}
		
		Logger.info("Migration Analysis end");
		
		Logger.info("Migration finish");
		return ok("Migration Finish");

	}

	private static void migre(Analysis analysis, String ruleCode) {
		ArrayList<Object> facts = new ArrayList<Object>();
		facts.add(analysis);
		// Outside of an actor and if no reply is needed the second argument can be null
		rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),ruleCode, facts),null);
	}
	
	
}

