package services.instance;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.LimsCNGDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import play.api.modules.spring.Spring;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ImportDataCNG extends AbsImportData implements Runnable{

	static ContextValidation contextError = new ContextValidation();
	static LimsCNGDAO  limsServices = Spring.getBeanOfType(LimsCNGDAO.class);

	public ImportDataCNG(){
		Akka.system().scheduler().schedule(Duration.create(4,TimeUnit.SECONDS),Duration.create(60,TimeUnit.MINUTES)
                , this, Akka.system().dispatcher()
				); 
	}
	
	
	@Override
	public void run() {
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		
		contextError.setCreationMode();
		
		Logger.info("ImportData execution : ");
		try{
			
			Logger.info(" Import Projects ... ");
			createProjectsFromLims(contextError);
			Logger.info("End Import Projects !");
			
			Logger.info(" Import Samples ... ");
			createSamplesFromLims(contextError, null);
			Logger.info("End Import Samples !");
			
			Logger.info(" Import Containers ... ");
			createContainersFromLims(contextError);
			Logger.info("End Import Containers !");
			
		}catch (Exception e) {
			Logger.debug("",e);
		}
		contextError.removeKeyFromRootKeyName("import");
		
		/* Display error messages  */
		contextError.displayErrors();
		/* Logger send an email */
		Logger.info("ImportData End");
	}


    /***
     * Delete and create in NGL active projects from CNG
     * 
     * @return List of Projects
     * @throws SQLException
     * @throws DAOException
     */
	public static List<Project> createProjectsFromLims(ContextValidation contextError) throws SQLException, DAOException{
		
		Logger.info("Start loading projects ..."); 
		List<Project> projects = limsServices.findProjectsToCreate(contextError) ;

		//save projects
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError, true);
		Logger.info("End of load projects !"); 
		
		limsServices.updateLimsProjects(projs, contextError);
		
		return projs;
	}
	

	/**
	 * 
	 * @param contextError
	 * @param sampleCode
	 * @throws SQLException
	 * @throws DAOException
	 */
	public static void deleteSamplesFromLims(ContextValidation contextError, String sampleCode) throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, null); // 2nd parameter null for mass loading 
		for(Sample sample:samples){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			}
		}
	}
	
	/**
	 * 
	 * @param contextError
	 * @param sampleCode
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public static List<Sample> createSamplesFromLims(ContextValidation contextError, String sampleCode) throws SQLException, DAOException{
		Logger.info("Start loading samples ..."); 
		
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, sampleCode); // 2nd parameter null for mass loading

		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		Logger.info("End of load samples !");
			
		limsServices.updateLimsSamples(samps, contextError);
		
		return samps;
	}
	
	
	
	/**
	 * 
	 * @param contextError
	 * @throws SQLException
	 * @throws DAOException
	 */
	public static void deleteContainersFromLims(ContextValidation contextError) throws SQLException, DAOException{
		List<Container> containers = limsServices.findContainersToCreate(contextError) ;
		for(Container container:containers){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
				//Logger.debug("Sample to create :"+sample.code);
			}
		}
	}
	
	/**
	 * 
	 * @param contextError
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	public static List<Container> createContainersFromLims(ContextValidation contextError) throws SQLException, DAOException{
		Logger.info("Start loading containers ..."); 
		List<Container> containers = limsServices.findContainersToCreate(contextError) ;

		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		Logger.info("End of load containers !"); 
		
		limsServices.updateLimsContainers(ctrs, contextError);
		
		return ctrs;
	}


	
}
