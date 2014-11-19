package controllers.migration.cng;

import java.text.SimpleDateFormat;
import java.util.List;

import models.Constants;
import models.LimsCNGDAO;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import org.springframework.stereotype.Repository;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * Update contents in the Container (add missing attribute projectCode)
 * 
 * @author dnoisett
 * 19/11/2014
 */

@Repository
public class AddProjectCodeToContent extends CommonController {
		
	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
	private static String backupName = InstanceConstants.CONTAINER_COLL_NAME+"_BCK_PC_"+sdf.format(new java.util.Date());	

	
	
	public static Result migration() {
		
		int n=0;
		
		JacksonDBCollection<Container, String> containersCollBck = MongoDBDAO.getCollection(backupName, Container.class);
		if (containersCollBck.count() == 0) {
	
			backUpContainer();
			
			Logger.info("Migration contents of containers starts : add projectCode attribute");
		
			//find collection up to date
			ContextValidation contextError = new ContextValidation(Constants.NGL_DATA_USER);
			List<Container> newContainers = null;
			try {
				newContainers = limsServices.findAllContainer(contextError, null, "lane");
			} catch (DAOException e) {
				Logger.debug("ERROR in findAllContainer():" + e.getMessage());
			}
			
			//find current collection
			List<Container> oldContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();

			for (Container oldContainer : oldContainers) {
				
				//delete all contents
				WriteResult r = (WriteResult) MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", oldContainer.code),   
						DBUpdate.unset("contents"));
					
				if(StringUtils.isNotEmpty(r.getError())){
					Logger.error("Unset contents : "+oldContainer.code+" / "+r.getError());
				}	
				
				for (Container newContainer : newContainers) {
					
					if (oldContainer.code.equals(newContainer.code)) {	
						//oldContainer.contents = newContainer.contents;
					 
						//set contents to the new ones
						r = (WriteResult) MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", oldContainer.code),   
								DBUpdate.set("contents", newContainer.contents));
							
						if(StringUtils.isNotEmpty(r.getError())){
							Logger.error("Set contents : "+oldContainer.code+" / "+r.getError());
						}
						
						n++;
						break;
					}
				}
			}

						
		} else {
			Logger.info("Migration contents of containers already executed !");
		}
		
		Logger.info("Migration contents of containers Finish : " + n + " contents of containers updated !");
		return ok("End");
	}

	private static void backUpContainer() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" to "+backupName+" start");		
		MongoDBDAO.save(backupName, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" to "+backupName+" end");	
	}
	

}