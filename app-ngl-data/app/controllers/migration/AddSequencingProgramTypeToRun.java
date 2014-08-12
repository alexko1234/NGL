package controllers.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.run.description.RunCategory;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.springframework.stereotype.Repository;

import play.Logger;
import controllers.CommonController;
import controllers.migration.models.container.ContainerOld;
import controllers.migration.models.container.ContentOld;
import fr.cea.ig.MongoDBDAO;
import play.mvc.Result;


public class AddSequencingProgramTypeToRun  extends CommonController {

	private static final String RUN_ILLUMINA_COLL_NAME_BCK = InstanceConstants.RUN_ILLUMINA_COLL_NAME+"_BCK";

	public static Result migration() {
		
		List<Run> runsCollBck = MongoDBDAO.find(RUN_ILLUMINA_COLL_NAME_BCK, Run.class).toList();
		if(runsCollBck.size() == 0){
			
			Logger.info(">>>>>>>>>>> Migration Run starts");

			backupRunCollection();

			migreSequencingProgramType();
			
			Logger.info(">>>>>>>>>>> Migration Run end");
		} else {
			Logger.info(">>>>>>>>>>> Migration Run already execute !");
		}
		
		return ok(">>>>>>>>>>> Migration Run finish");
	}


	private static void migreSequencingProgramType() {

		//set hashMap hm
		HashMap<String, PropertyValue> hm = new HashMap<String, PropertyValue>();
		
		List<ContainerSupport> containerSupports = MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class).toList();
		for (ContainerSupport containerSupport : containerSupports) {
			hm.put(containerSupport.code, containerSupport.properties.get("sequencingProgramType")); 
		}

		//use hm to retrieve sequencingProgramType with the containerSupportCode and affect it to runs
		List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList();
		
		Logger.debug("Migre "+runs.size()+" RUNS");		
		
		String categoryCode = "";

		for (Run run : runs) {
			
			try {
				categoryCode = models.laboratory.run.description.RunType.find.findByCode(run.typeCode).category.code;
			
				if (categoryCode.equals("illumina")) { 
					
					run.properties.put("sequencingProgramType", hm.get(run.containerSupportCode));
					
					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, ContainerSupport.class, DBQuery.is("code", run.code),   
							DBUpdate.set("properties", run.properties));
					//global update of the object to have the _type (json subtype) like in the import 
					//MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);
				}
			}
			catch(DAOException e) {
				Logger.error("DAOException type error !");
			}
		}

		
	}


	private static void backupRunCollection() {
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" start");
		MongoDBDAO.save(RUN_ILLUMINA_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class).toList());
		Logger.info("\tCopie "+InstanceConstants.RUN_ILLUMINA_COLL_NAME+" end");
	}
	
	
}
