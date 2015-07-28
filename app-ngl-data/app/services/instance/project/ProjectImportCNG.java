package services.instance.project;

import java.sql.SQLException;
import java.util.List;

import fr.cea.ig.MongoDBDAO;
import models.laboratory.parameter.Index;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;
import play.Logger;

/**
 * @author dnoisett
 * Import Projects from CNG's LIMS to NGL 
 * FDS remplacement de l'appel a Logger par logger
 */

public class ProjectImportCNG extends AbstractImportDataCNG{

	public ProjectImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ProjectImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		// FDS 17/07/2015 création de de 2 méthodes bien séparées
		loadProjects();	 	
		updateProjects();
	}
	
	public void loadProjects() throws SQLException, DAOException {
		logger.info("start loading projects");
		
		//-1- chargement depuis la base source Postgresql
		List<Project> projects = limsServices.findProjectToCreate(contextError);
		logger.info("found "+projects.size() + " items");
		
		//-2- sauvegarde dans la base cible MongoDb
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError, true);
		
		//-3- timestamp-er dans la base source Postgresql ce qui a été traité
		limsServices.updateLimsProjects(projs, contextError, "creation");
		
		logger.info("end loading projects");
	}
	
	public void updateProjects() throws SQLException, DAOException {
		logger.debug("start update projects");	

		//-1- chargement depuis la base source Postgresql
		List<Project> projects = limsServices.findProjectToModify(contextError);
		logger.info("found "+ projects.size() + " items");
		
		//-2a-delete old projects
		for (Project project : projects) {
			if (MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME,Project.class, project.code)) {
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
			}
		}
		
		//-2b- sauvegarde dans la base cible MongoDb
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError, true);
		
		//-3- timestamp-er dans la base source Postgresql ce qui a été traité
		limsServices.updateLimsProjects(projs, contextError, "update");
		
		logger.debug("end update projects");	
	}
}
