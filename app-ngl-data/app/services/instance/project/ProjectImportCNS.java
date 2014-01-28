package services.instance.project;

import java.sql.SQLException;
import java.util.List;

import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ProjectImportCNS extends AbstractImportDataCNS{

	public ProjectImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Project CNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
		createProjet(contextError);
	}

	
	public static void createProjet(ContextValidation contextValidation) throws SQLException, DAOException{
		
	List<Project> projects = limsServices.findProjectToCreate(contextValidation) ;
		
		for(Project project:projects){
	
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
				//Logger.debug("Project to create :"+project.code);
			}
		}
	
		InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextValidation);
		
	}
}
