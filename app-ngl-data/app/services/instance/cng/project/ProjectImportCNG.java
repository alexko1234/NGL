package services.instance.cng.project;

import java.sql.SQLException;
import java.util.List;

import play.Logger;

import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportData;

public class ProjectImportCNG extends AbstractImportData{

	public ProjectImportCNG(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		
		Logger.info("Start loading projects ..."); 
		
		List<Project> projects = limsCNGServices.findProjectToCreate(contextError);
		
		//save projects
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError, true);

		//update import date 
		limsCNGServices.updateLimsProjects(projs, contextError);
		
		Logger.info("End of load projects !"); 
		
	}

}
