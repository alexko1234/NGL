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
import models.utils.DescriptionHelper;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.JacksonDBCollection;

import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import controllers.migration.models.ProjectOld;
import fr.cea.ig.MongoDBDAO;


public class AddBioinformaticParameters extends CommonController {
	
	private static final String PROJECT_COLL_NAME_BCK = InstanceConstants.PROJECT_COLL_NAME+"_BCK_201409XX";
	
	
	public static Result migration() {
		Logger.info("Start point of Migration");
		
		JacksonDBCollection<ProjectOld, String> projectsCollBck = MongoDBDAO.getCollection(PROJECT_COLL_NAME_BCK, ProjectOld.class);
		if (projectsCollBck.count() == 0) {
			Logger.info("Migration project start");
			backupProject();
			List<String> institutes = DescriptionHelper.getInstitute();
			String institute;
			if(institutes.size() == 1)institute = institutes.get(0).trim();
			else throw new RuntimeException("Institue");
			
			List<ProjectOld> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld.class).toList();
			Logger.debug("expected to migrate "+projects.size()+" projects");
			for(ProjectOld project : projects){
				migrateProject(project, institute);
			}
			Logger.info("Migration project end");
		} else {
			Logger.info("Migration project already execute !");
		}		
		
		
		
		return ok("Migrations Finish");
	}

	
	
	
	private static void migrateProject(ProjectOld project, String ins) {	
		
		BioinformaticParameters bip = new BioinformaticParameters();
		if("CNS".equals(ins)){
			bip.biologicalAnalysis = project.bioinformaticAnalysis;
			if("BFY".equals(project.code) || "BFZ".equals(project.code)){
				bip.regexBiologicalAnalysis = "^.+_.+F_.+_.+$";
			}
		}else if("CNG".equals(ins)){
			bip.biologicalAnalysis = true;
		}else{
			Logger.error(ins);
		}
		
			

		MongoDBDAO.update(InstanceConstants.PROJECT_COLL_NAME, Project.class, 
				DBQuery.is("code", project.code), 
				DBUpdate.unset("bioinformaticAnalysis").set("bioinformaticParameters", bip));			
		
	}
	
	private static void backupProject() {
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" start");
		MongoDBDAO.save(PROJECT_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, ProjectOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.PROJECT_COLL_NAME+" end");	
	}
	
	
	

}
