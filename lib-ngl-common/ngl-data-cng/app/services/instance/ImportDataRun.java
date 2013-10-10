package services.instance;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import models.LimsDAO;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ImportDataRun implements Runnable {

	static ContextValidation contextError = new ContextValidation();
	static LimsDAO  limsServices = Spring.getBeanOfType(LimsDAO.class);


	@Override
	public void run() {
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		
		Logger.info("ImportData execution");
		
		try{
			Logger.info(" Import Projects ");
			createProjectFromLims();
			//Logger.info(" Import Containers and Samples ");
			//createContainersSamples()
		}catch (Exception e) {
			Logger.debug("",e);
		}
		contextError.removeKeyFromRootKeyName("import");
		
			
		/* Display error messages */
		Iterator entries = contextError.errors.entrySet().iterator();
		while (entries.hasNext()) {
			 Entry thisEntry = (Entry) entries.next();
			 String key = (String) thisEntry.getKey();
			 List<ValidationError> value = (List<ValidationError>) thisEntry.getValue();	  

			for(ValidationError validationError:value){
				Logger.error( key+ " : "+Messages.get(validationError.message(),validationError.arguments()));
			}

		}
		
	    Logger.info("ImportData End");
	}


    /***
     * Delete and create in NGL active projects from Lims
     * 
     * @return List of Projects
     * @throws SQLException
     * @throws DAOException
     */
	public static List<Project> createProjectFromLims() throws SQLException, DAOException{

		List<Project> projects = limsServices.findProjectToCreate(contextError) ;
		
		for(Project project:projects){
			
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
				//Logger.debug("Project to create :"+project.code);
			}
		}
		
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError);
		return projs;
	}

}
