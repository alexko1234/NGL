package services.instance;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import models.LimsDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
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
		Logger.info("ImportData execution : ");
		try{
			Logger.info(" Import Containers ... ");
			createContainersFromLims();
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
	    Logger.info("ImportData End !");
	}


    /***
     * Delete and create in NGL active projects from CNG
     * 
     * @return List of Projects
     * @throws SQLException
     * @throws DAOException
     */
	public static List<Project> createProjectsFromLims() throws SQLException, DAOException{
		List<Project> projects = limsServices.findProjectsToCreate(contextError) ;
		for(Project project:projects){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
				//Logger.debug("Project to create :"+project.code);
			}
		}
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError);
		return projs;
	}
	

	public static void deleteSamplesFromLims() throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError) ;
		for(Sample sample:samples){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
				//Logger.debug("Sample to create :"+sample.code);
			}
		}
	}
	
	
	public static List<Sample> createSamplesFromLims() throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError) ;
		for(Sample sample:samples){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
				//Logger.debug("Sample to create :"+sample.code);
			}
		}
		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError);
		return samps;
	}
	
	
	public static void deleteContainersFromLims() throws SQLException, DAOException{
		List<Container> containers = limsServices.findContainersToCreate(contextError) ;
		for(Container container:containers){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Container.class, container.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Container.class, container.code);
				//Logger.debug("Sample to create :"+sample.code);
			}
		}
	}
	
	public static List<Container> createContainersFromLims() throws SQLException, DAOException{
		List<Container> containers = limsServices.findContainersToCreate(contextError) ;
		for(Container container:containers){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
				//Logger.debug("Container to create :"+container.code);
			}
		}
		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError);
		return ctrs;
	}
	

}
