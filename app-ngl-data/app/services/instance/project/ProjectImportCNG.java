package services.instance.project;

import java.sql.SQLException;
import java.util.List;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNG;
import play.Logger;

public class ProjectImportCNG extends AbstractImportDataCNG{

	public ProjectImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("ProjectImportCNG",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		
		//creation
		List<Project> projects = limsServices.findProjectToCreate(contextError);
		//save new projects
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError, true);
		//update "import date" 
		limsServices.updateLimsProjects(projs, contextError, "creation");
		
		
		//update
		projects = limsServices.findProjectToModify(contextError);
		//delete old projects
		for (Project project : projects) {
			MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
		}
		//save updated projects
		projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError, true);
		//update "update date" 
		limsServices.updateLimsProjects(projs, contextError, "update");
		
	}

}
