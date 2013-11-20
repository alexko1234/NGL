package services.instance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.LimsCNGDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import play.Logger;
import play.Play;
import play.api.modules.spring.Spring;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ImportDataCNG extends AbstractImportData {

	static ContextValidation contextError = new ContextValidation();
	static LimsCNGDAO  limsServices = Spring.getBeanOfType(LimsCNGDAO.class);
	
	private static final int blockSize = Integer.parseInt(Play.application().configuration().getString("db.lims.update.blockSize")); 


	@Override
	public void run() {
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		
		contextError.setCreationMode();
		
		Logger.info("ImportData execution : ");
		try{
			/*
			Logger.info(" Import Projects ... ");
			createProjectsFromLims(contextError);
			Logger.info("End Import Projects !");
			*/
			Logger.info(" Import Samples ... ");
			//createSamplesFromLims(contextError, "26136024");
			createSamplesFromLims(contextError, null);
			Logger.info("End Import Samples !");
			/*
			Logger.info(" Import Containers ... ");
			createContainersFromLims(contextError);
			Logger.info("End Import Containers !");
			*/ 
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
		
		//update project's dates by block (define by the blockSize)
		updateLimsProjects(projs, blockSize, contextError);
		
		return projs;
	}
	
	/**
	 * Function to update ngl_importDate for a list of projects (instead of update them one by one)
	 * 
	 * @param projects
	 * @param blockSize
	 * @throws DAOException
	 */
	public static void updateLimsProjects(List<Project> projects, int blockSize, ContextValidation contextError) throws DAOException {
		
		Logger.info("Start of update import date ..."); 
		int i = 0;

		List<String> codesToUpdate = new ArrayList<String>();

		while (i < projects.size()) {
			
			codesToUpdate.clear();
			 for (Project project : projects.subList(i, Math.min(i+blockSize, projects.size()))) {
				 codesToUpdate.add(project.code);
			 }
			limsServices.updateImportDate( "t_project", "name", "text", codesToUpdate.toArray(new String[codesToUpdate.size()]), contextError);
			 i = i + blockSize; 
		}	
		Logger.info("End of update import date !"); 
	}
	

	public static void deleteSamplesFromLims(ContextValidation contextError, String sampleCode) throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, null); // 2nd parameter null for mass loading 
		for(Sample sample:samples){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
			}
		}
	}
	
	
	public static List<Sample> createSamplesFromLims(ContextValidation contextError, String sampleCode) throws SQLException, DAOException{
		Logger.info("Start loading samples ..."); 
		
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, sampleCode); // 2nd parameter null for mass loading

		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError, true);
		
		Logger.info("End of load samples !");
			
		updateLimsSamples(samps, blockSize, contextError);
		
		return samps;
	}
	

	public static void updateLimsSamples(List<Sample> ts, int blockSize, ContextValidation contextError) throws DAOException {
		
		Logger.info("Start of update import date ..."); 
		int i = 0;
		List<String> codesToUpdate = new ArrayList<String>();

		while (i < ts.size()) {
			
			codesToUpdate.clear();
			 for (Sample t : ts.subList(i, Math.min(i+blockSize, ts.size()))) {
				 codesToUpdate.add(t.code);
			 }
			limsServices.updateImportDate( "t_sample", "stock_barcode", "text", codesToUpdate.toArray(new String[codesToUpdate.size()]), contextError);
			i = i + blockSize; 
		}	
		Logger.info("End of update import date !"); 
	}
	
	
	
	public static void deleteContainersFromLims(ContextValidation contextError) throws SQLException, DAOException{
		List<Container> containers = limsServices.findContainersToCreate(contextError) ;
		for(Container container:containers){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
				//Logger.debug("Sample to create :"+sample.code);
			}
		}
	}
	
	public static List<Container> createContainersFromLims(ContextValidation contextError) throws SQLException, DAOException{
		Logger.info("Start loading containers ..."); 
		List<Container> containers = limsServices.findContainersToCreate(contextError) ;

		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError, true);
		Logger.info("End of load containers !"); 
		
		updateLimsContainers(ctrs, blockSize, contextError);
		
		return ctrs;
	}

	
	public static void updateLimsContainers(List<Container> ts, int blockSize, ContextValidation contextError) throws DAOException {
		
		Logger.info("Start of update import date ..."); 
		int i = 0;
		List<String> codesToUpdate = new ArrayList<String>();

		while (i < ts.size()) {
			
			codesToUpdate.clear();
			 for (Container t : ts.subList(i, Math.min(i+blockSize, ts.size()))) {
				 codesToUpdate.add(t.properties.get("limsCode").value.toString());
			 }
 
			limsServices.updateImportDate( "t_lane", "id", "integer", codesToUpdate.toArray(new String[codesToUpdate.size()]), contextError);
			 i = i + blockSize;
		}	
		Logger.info("End of update import date !"); 
	}
	

}
