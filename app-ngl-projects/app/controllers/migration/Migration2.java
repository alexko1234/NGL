package controllers.migration;		

/**
 * Didier Noisette, add Boolean attribute "bioinformaticAnalysis" to the project collection
 * 30/04/2014
 */

import java.util.List;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.models.ProjectOld2;
import fr.cea.ig.MongoDBDAO;

public class Migration2 extends CommonController {
	
	private static final String PROJECT_COLL_NAME_BCK = InstanceConstants.PROJECT_COLL_NAME+"_BCK";
	
	
	
	public static Result migration() {
		Logger.info("Start point of Migration");
		
		JacksonDBCollection<ProjectOld2, String> projectsCollBck = MongoDBDAO.getCollection(PROJECT_COLL_NAME_BCK, ProjectOld2.class);
		if (projectsCollBck.count() == 0) {
			Logger.info("Migration project start");
			backupProject();
			List<ProjectOld2> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld2.class).toList();
			Logger.debug("migre "+projects.size()+" projects");
			for(ProjectOld2 project : projects){
				migreProject(project);
			}
			Logger.info("Migration project end");
		} else {
			Logger.info("Migration project already execute !");
		}	
		Logger.info("Migration finish");
		return ok("Migration Finish");
	}

	
	
	
	private static void migreProject(ProjectOld2 project) {			
		MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
				DBQuery.is("code", project.code), 
				DBUpdate.unset("bioInfoAnalysis").set("bioinformaticAnalysis", Boolean.FALSE));
	}

	private static void backupProject() {
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" start");
		MongoDBDAO.save(PROJECT_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld2.class).toList());
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" end");
		
	}

}
