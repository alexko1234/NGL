package controllers.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.Logger;
import controllers.CommonController;
import controllers.migration.models.container.ContainerOld;
import controllers.migration.models.container.ContentOld;
import fr.cea.ig.MongoDBDAO;
import play.mvc.Result;

public class MigrationContainer  extends CommonController {
	
	private static final String CONTAINER_COLL_NAME_BCK = InstanceConstants.CONTAINER_COLL_NAME+"_BCK_Container_refactoring";

	public static Result migration() {
		List<ContainerOld> containersCollBck = MongoDBDAO.find(CONTAINER_COLL_NAME_BCK, ContainerOld.class).toList();
		if(containersCollBck.size() == 0){
			
			Logger.info(">>>>>>>>>>> 1.a Migration Container starts");

			backupContainerCollection();

			//container
			List<ContainerOld> oldContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class).toList();
			Logger.debug("Migre "+oldContainers.size()+" CONTAINERS");
			for (ContainerOld container : oldContainers) {			

				migreSupportCode(container);
				migreContent(container);
			}
			Logger.info(">>>>>>>>>>> 1.b Migration Container end");
		} else {
			Logger.info("Migration CONTAINER already execute !");
		}
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		return ok("Migration Container "+ containers.size()+ "Finish");
	}
	


	private static void migreContent(ContainerOld container) {
		List<Content> samplesUsed = new ArrayList<Content>();
		for(ContentOld content : container.contents){
			
			Content sampleUsed = new Content();
			sampleUsed.sampleCode=content.sampleUsed.sampleCode;
			sampleUsed.sampleTypeCode=content.sampleUsed.typeCode;
			sampleUsed.sampleCategoryCode=content.sampleUsed.categoryCode;
			sampleUsed.properties=content.properties;
			
			samplesUsed.add(sampleUsed);
			
		}
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				DBQuery.is("code", container.code), 
				DBUpdate.set("contents", samplesUsed));
	}



	private static void migreSupportCode(ContainerOld container) {

		MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
				DBQuery.is("code", container.code), 
				DBUpdate.unset("support.supportCode")
				.set("support.code", container.support.supportCode) );
	}


	private static void backupContainerCollection() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" start");
		MongoDBDAO.save(CONTAINER_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
	}
	
	
}
