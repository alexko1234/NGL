package services.instance.cns.project;

import java.sql.SQLException;
import java.util.List;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportData;

public class ProjectImportCNS extends AbstractImportData{

	public ProjectImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		
		List<Project> projects = limsServices.findProjectToCreate(contextError) ;
		
		for(Project project:projects){
	
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
				//Logger.debug("Project to create :"+project.code);
			}
		}
	
		InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError);
	}

}
