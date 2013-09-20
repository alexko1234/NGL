package services.instance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import models.LimsDAO;
import models.TaraDAO;
import models.laboratory.common.instance.PropertyValue;
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
import fr.cea.ig.MongoDBResult;

public class ImportDataRun implements Runnable {

	static ContextValidation contextError = new ContextValidation();
	static LimsDAO  limsServices = Spring.getBeanOfType(LimsDAO.class);
	static TaraDAO taraServices = Spring.getBeanOfType(TaraDAO.class);


	@Override
	public void run() {
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		Logger.info("ImportData execution");
		try{
	//		Logger.info(" Import Projects ");
	//		List<Project> projects=createProjectFromLims();
			Logger.info(" Import Containers and Samples ");
			createContainersSamples();

		contextError.removeKeyFromRootKeyName("import");
		}catch (Exception e) {
			Logger.debug("",e);
		}
		
			
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



	public	static void createContainersSamples() throws SQLException, DAOException{
				
		List<Container> containers = limsServices.findContainersToCreate(contextError); 
		List<Container> listContainers = new ArrayList<Container>(containers);
		
		Sample sample =null;
		Sample newSample =null;
		String rootKeyName=null;
		
		for(Container container :listContainers){

			//Logger.debug("Container :"+container.code);
			
			Content content= container.contents.get(0);
		
			/* Sample content not in MongoDB */
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, content.sampleUsed.sampleCode)){
				
				rootKeyName="sample["+content.sampleUsed.sampleCode+"]";
				contextError.addKeyToRootKeyName(rootKeyName);
				sample = limsServices.findSampleToCreate(contextError,container.contents.get(0).sampleUsed.sampleCode);

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
				Map<String,PropertyValue> properties=container.contents.get(0).properties;
				container.contents.clear();
				ContainerHelper.addContent(container,newSample);
				container.contents.get(0).properties.putAll(properties);
			}
			contextError.removeKeyFromRootKeyName(rootKeyName);

		}
		
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
				
		limsServices.updateTubeLims(newContainers,contextError);
		
	}

	//TODO
	//Maj volume, conc, quantity
	public static void updateContainerFromLims() throws SQLException, DAOException{
		//
	}

	//TODO
	//Maj referenceCollab and resolution ???	
	public static void updateSampleFromLims() throws SQLException, DAOException{
	
	}
	
	//TODO
	/*
	public static void updateSampleFromTara() throws SQLException, DAOException{
		
		List<Map<String, PropertyValue>> taraPropertyList = taraServices.findTaraSampleUpdated();
		for(Map<String,PropertyValue> taraProperties : taraPropertyList){
			if(taraProperties.containsKey("limsCode")){
				contextError.addErrors("","", null);
			}else {
				List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("properties.limsCode.value",taraProperties.get("limsCode").value.toString())).toList();
				if(samples.size()!=1 ) {
					contextError.addErrors("","", null);
				}else {
					Sample sample =samples.get(0);
				}
			}
			
		}
	}*/
}
