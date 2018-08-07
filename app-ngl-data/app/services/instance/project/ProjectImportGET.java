package services.instance.project;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.DBUpdate.Builder;

import models.Constants;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import services.instance.AbstractImportDataGET;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.migration.NGLContext;

public class ProjectImportGET extends AbstractImportDataGET{
	public static final play.Logger.ALogger logger = play.Logger.of(ProjectImportGET.class);
	@Inject
	public ProjectImportGET(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration, NGLContext ctx) {
		super("Project GET",durationFromStart, durationFromNextIteration, ctx);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		logger.debug("ProjectImportGET - createProjet : avant ");	
		createProjet(contextError);
		logger.debug("ProjectImportGET - createProjet : apres ");	
	}

	
	public static void createProjet(ContextValidation contextValidation) throws SQLException, DAOException{
		logger.debug("ProjectImportGET - createProjet : Récupération des projets");	
		List<Project> projects = limsServices.findProjectToCreate(contextValidation) ;
		
		for(Project project:projects){
			logger.debug("ProjectImportGET - createProjet : Traitement du projet " + project.code);	
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
				logger.debug("Project to create :"+project.code);
			}
			
			
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,project,contextValidation);
			}
		}
	
		//InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextValidation);
		
	}
}
