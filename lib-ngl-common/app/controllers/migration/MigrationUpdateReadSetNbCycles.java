package controllers.migration;		

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.SampleOnContainer;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;
import play.libs.Akka;
import play.mvc.Result;
import rules.services.RulesActor6;
import rules.services.RulesMessage;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;

/**
 * Update SampleOnContainer on ReadSet
 * @author galbini
 *
 */
public class MigrationUpdateReadSetNbCycles extends CommonController {
	
	private static ActorRef rulesActor = Akka.system().actorOf(Props.create(RulesActor6.class));
	
	public static Result migration(String code){
		
		Logger.info("Start MigrationUpdateReadSetNbCycles");
		
		MongoDBResult<ReadSet> results = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.notExists("treatments.ngsrg.default.nbUsefulCycleRead1"));
		
		Logger.info("Update nb readsets = "+results.count());
		
		DBCursor<ReadSet> cursor = results.cursor;
		
		while(cursor.hasNext()){
			ReadSet rs = cursor.next();
			rulesActor.tell(new RulesMessage(Play.application().configuration().getString("rules.key"),"F_RG_1", rs),null);
		}
		
		return ok("Migration Finish");

	}

	

	
	
	

}
