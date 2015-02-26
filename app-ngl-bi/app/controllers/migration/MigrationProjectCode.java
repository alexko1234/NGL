package controllers.migration;		

import java.util.List;

import models.laboratory.project.instance.Project;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

/**
 * Update file extensions (Jira NGL-138)
 * @author dnoisett
 * 21-02-2014
 */

public class MigrationProjectCode extends CommonController {
	
	static String oldProjectCode = "LIVER_356";
	static String newProjectCode = "LIVERREC_483";
		
	public static Result migration(){
		//project
		MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
				DBQuery.and(DBQuery.is("code", oldProjectCode)), DBUpdate.set("code", newProjectCode));
		
		//sample
		updateProjectCodes(InstanceConstants.SAMPLE_COLL_NAME);
		updateProjectCodes(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME);
		updateProjectCodes(InstanceConstants.CONTAINER_COLL_NAME);
		updateProjectCodes(InstanceConstants.SAMPLE_COLL_NAME);
		
		//todo run readset warning : update readset code and files
		
		
		return ok();
	}

	private static void updateProjectCodes(String colName) {
		//1er push		
		WriteResult w = MongoDBDAO.update(colName, Project.class, 
				DBQuery.and(DBQuery.in("projectCodes", oldProjectCode)), 
				DBUpdate.push("projectCodes", newProjectCode));	
		if(null != w.getError())
			Logger.error(colName+" "+w.getError());
		
		int nb1 = w.getSavedIds().size();
		//2nd pull
		w = MongoDBDAO.update(colName, Project.class, 
				DBQuery.and(DBQuery.in("projectCodes", oldProjectCode)), 
				DBUpdate.pull("projectCodes", oldProjectCode));		
		if(null != w.getError())
			Logger.error(colName+" "+w.getError());
		int nb2 = w.getSavedIds().size();
		
		Logger.info(colName+" = "+nb1+" / "+nb2);
	}

	

	

}
