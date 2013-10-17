package services.instance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import models.LimsDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
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
			createProjectsFromLims();
			//Maud's code
			//createContainers(contextError,"select * from v_sampletongl;","lane","F",null,null); 
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
	public static List<Project> createProjectsFromLims() throws SQLException, DAOException{
		List<Project> projects = limsServices.findProjectsToCreate(contextError) ;
		for(Project project:projects){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
				//Logger.debug("Project to create :"+project.code);
			}
		}
		List<Project> projs=InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError);
		
		//update projects by block (define by the blockSize)
		String lProjectsCodes = "(";
		int i = 0;
		int blockSize = 100;
		while (i < projects.size()) {
			 int maxIndex = Math.min(i+blockSize, projects.size()-1); 
			 List<Project> projectsToUpdate = projects.subList(i, maxIndex); 
			 for (Project project:projectsToUpdate) {
				if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
					//add code to list of codes to update
					lProjectsCodes += project.code + ",";
				    i++;
				}
			 }
			 lProjectsCodes += lProjectsCodes.substring(0, lProjectsCodes.length()-1 ) + ")";
			 
			//for debug		
			Logger.debug("lProjectsCodes :"+lProjectsCodes);
			
			limsServices.updateImportDateForProjects(lProjectsCodes, contextError);
		}		
		return projs;
	}
	

	public static void deleteSamplesFromLims() throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, null); // 2nd parameter null for mass loading 
		for(Sample sample:samples){
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sample.code);
				//Logger.debug("Sample to create :"+sample.code);
			}
		}
	}
	
	
	public static List<Sample> createSamplesFromLims() throws SQLException, DAOException{
		List<Sample> samples = limsServices.findSamplesToCreate(contextError, null); // 2nd parameter null for mass loading
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
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
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

				/* Sample content not in MongoDB */
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
					/* Find sample in Mongodb */
					newSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, content.sampleUsed.sampleCode);	
				}			

				rootKeyName="container["+container.code+"]";
				contextError.addKeyToRootKeyName(rootKeyName);

				/* Error : No sample, remove container from list to create */
				if(newSample==null){
					containers.remove(container);
					contextError.addErrors("sample","error.codeNotExist", content.sampleUsed.sampleCode);
				}
				else{
					/* From sample, add content in container */
					container.contents.remove(content);
					ContainerHelper.addContent(container,newSample,content.properties);
					
				}
				contextError.removeKeyFromRootKeyName(rootKeyName);

			}
		}

	}
	

}
