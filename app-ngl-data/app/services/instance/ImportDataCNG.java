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
		Logger.info("ImportData execution : ");
		try{
			Logger.info(" Import Containers ... ");
			createContainersFromLims(contextError);
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
		List<Project> projects = limsServices.findProjectsToCreate(contextError) ;

		//save projects
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError);
		
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
		
		Logger.debug("start of updateLimsProjects"); 
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
		Logger.debug("end of updateLimsProjects"); 
	}
	

	public static void deleteSamplesFromLims(ContextValidation contextError) throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, null); // 2nd parameter null for mass loading 
		for(Sample sample:samples){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
				//Logger.debug("Sample to create :"+sample.code);
			}
		}
	}
	
	
	public static List<Sample> createSamplesFromLims(ContextValidation contextError) throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, null); // 2nd parameter null for mass loading

		List<Sample> samps=InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME, samples, contextError);
			
		updateLimsSamples(samps, blockSize, contextError);
		
		return samps;
	}
	

	public static void updateLimsSamples(List<Sample> ts, int blockSize, ContextValidation contextError) throws DAOException {
		
		Logger.debug("start of updateLimsSamples"); 
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
		Logger.debug("end of updateLimsSamples"); 
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
		List<Container> containers = limsServices.findContainersToCreate(contextError) ;

		List<Container> ctrs=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME, containers, contextError);
		
		updateLimsContainers(ctrs, blockSize, contextError);
		
		return ctrs;
	}

	
	public static void updateLimsContainers(List<Container> ts, int blockSize, ContextValidation contextError) throws DAOException {
		
		Logger.debug("start of updateLimsContainers"); 
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
		Logger.debug("end of updateLimsContainers"); 
	}
	
	/*
	public	static void createContainers(ContextValidation contextError, String sqlContainer,String containerCategoryCode,  String containerStateCode, String experimentTypeCode, String sqlContent) throws SQLException, DAOException{
		String rootKeyName=null;

		List<Container> containers=	limsServices.findContainersToCreate(sqlContainer,contextError, containerCategoryCode,containerStateCode,experimentTypeCode);
		saveSampleFromContainer(containers);

		List<Container> newContainers=new ArrayList<Container>();

		for(Container container:containers){

			rootKeyName="container["+container.code+"]";
			contextError.addKeyToRootKeyName(rootKeyName);
			Container result=(Container) InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME,container, contextError,true);
			if(result!=null){
				newContainers.add(result);
			}
			contextError.removeKeyFromRootKeyName(rootKeyName);
		}

		//limsServices.updateMaterielmanipLims(newContainers,contextError);

	}
	
	
	
	private static void saveSampleFromContainer(List<Container> containers) throws SQLException, DAOException{
		List<Container> listContainers = new ArrayList<Container>(containers);

		//
		Sample sample =null;
		Sample newSample =null;
		String rootKeyName=null;

		for(Container container :listContainers){

			//Logger.debug("Container :"+container.code);

			List<Content> contents=new ArrayList<Content>(container.contents);
			for(Content content : contents){

				// Sample content not in MongoDB 
				if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, content.sampleUsed.sampleCode)){

					rootKeyName="sample["+content.sampleUsed.sampleCode+"]";
					contextError.addKeyToRootKeyName(rootKeyName);
					List<Sample> samples = limsServices.findSamplesToCreate(contextError,content.sampleUsed.sampleCode);
					if (samples.size() == 1) {
						newSample = samples.get(0); 
					}
					else {
						newSample = null;
					}

					if(sample!=null){
						newSample =(Sample) InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextError,true);
					}
					contextError.removeKeyFromRootKeyName(rootKeyName);

				}else {	
					// Find sample in Mongodb
					newSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, content.sampleUsed.sampleCode);	
				}			

				rootKeyName="container["+container.code+"]";
				contextError.addKeyToRootKeyName(rootKeyName);

				// Error : No sample, remove container from list to create
				if(newSample==null){
					containers.remove(container);
					contextError.addErrors("sample","error.codeNotExist", content.sampleUsed.sampleCode);
				}
				else{
					// From sample, add content in container 
					container.contents.remove(content);
					ContainerHelper.addContent(container,newSample,content.properties);
					
				}
				contextError.removeKeyFromRootKeyName(rootKeyName);

			}
		}

	}
	*/
	

}
