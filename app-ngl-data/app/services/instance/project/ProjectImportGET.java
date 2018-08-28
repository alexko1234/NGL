package services.instance.project;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataGET;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;
import play.Logger;

public class ProjectImportGET extends AbstractImportDataGET{

	public ProjectImportGET(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("Project GET",durationFromStart, durationFromNextIteration, ctx);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createProjet(contextError);
	}

	
	public static void createProjet(ContextValidation contextValidation) throws SQLException, DAOException{
		Logger.debug("ProjectImportGET - createProjet : Récupération des projets");	
		List<Project> projects = limsServices.findProjectToCreate(contextValidation) ;
				
		for(Project project:projects){
//			Logger.debug("ProjectImportGET - createProjet : Traitement du projet " + project.code);	
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
//					Logger.debug("Project to create :"+project.code + " " + project.state.user);				
			}
			
			
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,project,contextValidation);
			}
		}
		Logger.debug(projects.size() + " projets to import");
	
		//InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextValidation);
		
	}
}
