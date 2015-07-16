package controllers.migration.cns;

import java.util.List;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.MigrationNGLSEQ;
import fr.cea.ig.MongoDBDAO;

public class MigrationContainerMeasure extends CommonController{

	static String collection=InstanceConstants.CONTAINER_COLL_NAME;


	public static Result migration(){

		MigrationNGLSEQ.backupOneCollection(collection, Container.class);
		updateContainerMeasure();
		return ok("End migration container");
	}


	private static void updateContainerMeasure() {

		List<Container> containers = MongoDBDAO.find(collection, Container.class,DBQuery.is("categoryCode","tube")).toList();
		for(Container container:containers){

			if(container.mesuredVolume !=null){
				
				PropertySingleValue mesuredQuantity=new PropertySingleValue(container.mesuredVolume.value,"ng");
				MongoDBDAO.update(collection, Container.class,DBQuery.is("code", container.code)
						,DBUpdate.set("mesuredQuantity",mesuredQuantity));
				
			}else {
				MongoDBDAO.update(collection, Container.class,DBQuery.is("code", container.code)
						,DBUpdate.unset("mesuredQuantity"));
			}
			
			if(container.mesuredQuantity!=null){
				
				PropertySingleValue mesuredVolume=new PropertySingleValue(container.mesuredQuantity.value,"µl");
				MongoDBDAO.update(collection, Container.class,DBQuery.is("code", container.code)
						,DBUpdate.set("mesuredVolume", mesuredVolume));
				
			} else {
				MongoDBDAO.update(collection, Container.class,DBQuery.is("code", container.code)
						,DBUpdate.unset("mesuredVolume"));
			}
			
			if(container.mesuredConcentration !=null){
				MongoDBDAO.update(collection, Container.class,DBQuery.is("code", container.code)
						,DBUpdate.set("mesuredConcentration.unit","ng/µl"));
			}
			
		}

	}

}
