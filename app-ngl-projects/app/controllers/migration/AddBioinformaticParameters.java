package controllers.migration;		

/** 
 * Add attribute "bioinformaticParameters" to the project collection
 * Remove projectCodes from umbrellaProject collection
 * @author dnoisett
 * 21/08/2014
 */

import java.util.List;

import models.laboratory.project.instance.BioinformaticParameters;
import models.laboratory.project.instance.Project;
import models.laboratory.project.instance.UmbrellaProject;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;
import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.models.ProjectOld5;
import controllers.migration.models.UmbrellaProjectOld;
import fr.cea.ig.MongoDBDAO;


public class AddBioinformaticParameters extends CommonController {
	
	private static final String PROJECT_COLL_NAME_BCK = InstanceConstants.PROJECT_COLL_NAME+"_BCK_20140819";
	private static final String UMBRELLA_PROJECT_COLL_NAME_BCK = InstanceConstants.UMBRELLA_PROJECT_COLL_NAME+"_BCK_20140821";
	
	
	public static Result migration() {
		Logger.info("Start point of Migration");
		
		JacksonDBCollection<ProjectOld5, String> projectsCollBck = MongoDBDAO.getCollection(PROJECT_COLL_NAME_BCK, ProjectOld5.class);
		if (projectsCollBck.count() == 0) {
			Logger.info("Migration project start");
			backupProject();
			
			List<ProjectOld5> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld5.class).toList();
			Logger.debug("expected to migrate "+projects.size()+" projects");
			for(ProjectOld5 project : projects){
				migrateProject(project);
			}
			Logger.info("Migration project end");
		} else {
			Logger.info("Migration project already execute !");
		}		
		
		JacksonDBCollection<UmbrellaProjectOld, String> umbrellaProjectsCollBck = MongoDBDAO.getCollection(UMBRELLA_PROJECT_COLL_NAME_BCK, UmbrellaProjectOld.class);
		if (umbrellaProjectsCollBck.count() == 0) {
			
			Logger.info("Migration umbrella project start");
			backupUmbrellaProject();
			
			List<UmbrellaProjectOld> umbrellaProjects = MongoDBDAO.find(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProjectOld.class).toList();
			Logger.debug("expected to migrate "+umbrellaProjects.size()+" umbrella projects");
			for(UmbrellaProjectOld umbrellaProject : umbrellaProjects){
				migrateUmbrellaProject(umbrellaProject);
			}
			
			Logger.info("Migration umbrella project end");
		} else {
			Logger.info("Migration umbrella project already execute !");
		}	
		Logger.info("Migrations finish");
		
		return ok("Migrations Finish");
	}

	
	
	
	private static void migrateProject(ProjectOld5 project) {	
		
		BioinformaticParameters bip = new BioinformaticParameters(project.bioinformaticAnalysis); 
		
		String umbrellaProjectCode = null;
		if (project.umbrellaProjectCodes != null && project.umbrellaProjectCodes.size() > 0) {
			umbrellaProjectCode = project.umbrellaProjectCodes.get(0);
		}	

		MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
				DBQuery.is("code", project.code), 
				DBUpdate.unset("umbrellaProjectCodes").unset("bioinformaticAnalysis").set("bioinformaticParameters", bip));			
		
		if (umbrellaProjectCode != null) {
			MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
					DBQuery.is("code", project.code), 
					DBUpdate.set("umbrellaProjectCode", umbrellaProjectCode));						
		}
	}
	
	
	private static void migrateUmbrellaProject(UmbrellaProjectOld umbrellaProject) {	
		MongoDBDAO.update(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProject.class, 
				DBQuery.is("code", umbrellaProject.code), 
				DBUpdate.unset("projectCodes"));			
	}

	
	private static void backupProject() {
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" start");
		MongoDBDAO.save(PROJECT_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld5.class).toList());
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" end");	
	}
	
	
	private static void backupUmbrellaProject() {
		Logger.info("\tCopie "+InstanceConstants.UMBRELLA_PROJECT_COLL_NAME+" start");
		MongoDBDAO.save(UMBRELLA_PROJECT_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.UMBRELLA_PROJECT_COLL_NAME, UmbrellaProjectOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.UMBRELLA_PROJECT_COLL_NAME+" end");	
	}

}
