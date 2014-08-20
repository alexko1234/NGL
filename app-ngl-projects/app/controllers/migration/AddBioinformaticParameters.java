package controllers.migration;		

/**
 * Didier Noisette, add attribute "bioinformaticParameters" to the project collection
 * 19/08/2014
 */

import java.util.List;

import models.laboratory.project.instance.BioinformaticParameters;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.models.ProjectOld5;
import fr.cea.ig.MongoDBDAO;


public class AddBioinformaticParameters extends CommonController {
	
	private static final String PROJECT_COLL_NAME_BCK = InstanceConstants.PROJECT_COLL_NAME+"_BCK_20140819";
	
	
	
	public static Result migration() {
		Logger.info("Start point of Migration");
		
		JacksonDBCollection<ProjectOld5, String> projectsCollBck = MongoDBDAO.getCollection(PROJECT_COLL_NAME_BCK, ProjectOld5.class);
		if (projectsCollBck.count() == 0) {
			Logger.info("Migration project start");
			backupProject();
			List<ProjectOld5> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld5.class).toList();
			Logger.debug("expected to migrate "+projects.size()+" projects");
			for(ProjectOld5 project : projects){
				migreProject(project);
			}
			Logger.info("Migration project end");
		} else {
			Logger.info("Migration project already execute !");
		}	
		Logger.info("Migration finish");
		return ok("Migration Finish");
	}

	
	
	
	private static void migreProject(ProjectOld5 project) {	
		
		BioinformaticParameters bip = new BioinformaticParameters(project.bioinformaticAnalysis); 
		
		String descr = project.comments.get(0).comment;
		
		MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
				DBQuery.is("code", project.code), 
				DBUpdate.unset("comments").unset("umbrellaProjectCodes").unset("bioinformaticAnalysis").set("bioinformaticParameters", bip).set("description", descr));
	}

	private static void backupProject() {
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" start");
		MongoDBDAO.save(PROJECT_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld5.class).toList());
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" end");
		
	}

}
