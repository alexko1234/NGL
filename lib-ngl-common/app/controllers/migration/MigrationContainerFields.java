package controllers.migration;

import models.laboratory.container.instance.Container;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.Play;
import play.mvc.Result;
import rules.services.RulesMessage;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class MigrationContainerFields extends CommonController {
public static Result migration(){
		
		Logger.info("Start MigrationContainerFields");
		
		MongoDBResult<Container> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class);
		
		//1 remove calculedVolume
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("calculedVolume"),DBUpdate.unset("calculedVolume"));
		
		return ok("Migration Finish");

	}
}
