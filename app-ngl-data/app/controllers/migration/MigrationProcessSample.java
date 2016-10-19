package controllers.migration;

import java.util.List;

import models.utils.InstanceConstants;
import models.utils.instance.SampleHelper;

import org.mongojack.JacksonDBCollection;

import play.Logger;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.migration.models.ContainerOld;
import controllers.migration.models.ProcessOld;
import fr.cea.ig.MongoDBDAO;

public class MigrationProcessSample extends CommonController {
	
	private static final String PROCESS_COLL_NAME_BCK = InstanceConstants.PROCESS_COLL_NAME+"_BCK";


	public static Result migration(){

		Logger.info("Start point of Migration ContainerSupport");

		JacksonDBCollection<ProcessOld, String> containersCollBck = MongoDBDAO.getCollection(PROCESS_COLL_NAME_BCK, ProcessOld.class);
		if(containersCollBck.count() == 0){

			Logger.info("Migration ContainerSupport start");

			backupContainerCollection();

			List<ProcessOld> oldProcesses = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, ProcessOld.class).toList();
			Logger.debug("migre "+oldProcesses.size()+" containers");
			for (ProcessOld process : oldProcesses) {
				migrationProcess(process);
			}
			Logger.info("Migration process end");

		}else{
			Logger.info("Migration Process already execute !");
		}
		Logger.info("Migration Process finish");
		return ok("Migration Process Finish");
	}

	private static void migrationProcess(ProcessOld process) {
		
		process.sampleCodes=SampleHelper.getSampleParent(process.sampleCode);
		process.projectCodes=SampleHelper.getProjectParent(process.sampleCodes);
		
		process.sampleCode=null;
		process.projectCode=null;
		
		ContextValidation contextValidation=new ContextValidation("migration");
		process.validate(contextValidation);
		
		if(!contextValidation.hasErrors()){
			MongoDBDAO.save(InstanceConstants.PROCESS_COLL_NAME, process);
		}else {
			Logger.error("ERROR VALIDATION "+process.code);
		}
		
	}

	private static void backupContainerCollection() {
		Logger.info("\tCopie "+InstanceConstants.PROCESS_COLL_NAME+" start");
		MongoDBDAO.save(PROCESS_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, ProcessOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.PROCESS_COLL_NAME+" end");
	}

}
