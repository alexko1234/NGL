package controllers.migration;

import java.util.Arrays;


import java.util.Collections;

import models.laboratory.container.instance.Container;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;



import org.apache.commons.collections.CollectionUtils;
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
		
		//MongoDBResult<Container> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class);
		
		//1 remove calculedVolume
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("calculedVolume"),DBUpdate.unset("calculedVolume"));
		
		//2 rename mesuredQuantity to quantity, mesuredConcentration to concentration
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("code"),
				DBUpdate.rename("mesuredQuantity", "quantity").rename("mesuredVolume", "volume").rename("mesuredConcentration", "concentration").rename("inputProcessCodes", "processCodes"));
		
		
		//3 move processTypeCode to processTypeCodes
		MongoDBResult<Container> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.exists("processTypeCode"));
		
		results.toList().forEach(c -> {
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code",c.code),
					DBUpdate.set("processTypeCodes",Collections.singleton(c.processTypeCode)).unset("processTypeCode")) ;
		});
		
		return ok("Migration Finish");

	}
}
