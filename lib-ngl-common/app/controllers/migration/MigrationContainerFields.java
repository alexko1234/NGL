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
				DBUpdate.rename("mesuredQuantity", "quantity")
						.rename("mesuredVolume", "volume")
						.rename("mesuredConcentration", "concentration")
						.rename("inputProcessCodes", "processCodes")
						.rename("fromExperimentTypeCodes", "fromTransformationTypeCodes")
				);
		
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, Container.class, DBQuery.exists("code"),
				DBUpdate.rename("fromExperimentTypeCodes", "fromTransformationTypeCodes")
				);
		
		//3 move processTypeCode to processTypeCodes
		MongoDBResult<ContainerOld> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class, DBQuery.exists("processTypeCode"));
		
		results.toList().forEach(c -> {
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class, DBQuery.is("code",c.code),
					DBUpdate.set("processTypeCodes",Collections.singleton(c.processTypeCode)).unset("processTypeCode")) ;
		});
		
		
		//4 move Experiment inputProcessCodes
		MongoDBResult<ExperimentOld> results2 = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, ExperimentOld.class, DBQuery.exists("atomicTransfertMethods.inputContainerUseds.inputProcessCodes"));
		results2.toList().forEach(e -> {
			e.atomicTransfertMethods.forEach(atm -> {
				atm.inputContainerUseds.forEach(icu -> {
					icu.fromTransformationTypeCodes = icu.fromExperimentTypeCodes;
					icu.processCodes = icu.inputProcessCodes;
					
					icu.fromExperimentTypeCodes = null;
					icu.inputProcessCodes = null;
				});
			});
			
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, e) ;
		});
		//5 move Experiment fromTransformationTypeCodes
		MongoDBResult<ExperimentOld> results3 = MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, ExperimentOld.class, DBQuery.exists("atomicTransfertMethods.inputContainerUseds.fromExperimentTypeCodes"));
		results3.toList().forEach(e -> {
			e.atomicTransfertMethods.forEach(atm -> {
				atm.inputContainerUseds.forEach(icu -> {
					icu.fromTransformationTypeCodes = icu.fromExperimentTypeCodes;
					icu.processCodes = icu.inputProcessCodes;
					
					icu.fromExperimentTypeCodes = null;
					icu.inputProcessCodes = null;
				});
			});
			
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, e) ;
		});
		return ok("Migration Finish");

	}
}
