package services.instance;

import static validation.utils.ValidationHelper.addErrors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import models.LimsDAO;
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
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public class ImportDataRun implements Runnable {

	static ContextValidation contextError = new ContextValidation();
	static LimsDAO  limsServices = Spring.getBeanOfType(LimsDAO.class);

	@Override
	public void run() {
		contextError.errors.clear();
		Logger.info("ImportDataRun execution");
		try{
		//	createProjectFromLims();
			createContainersSamples();

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
				Logger.debug(key+ " : "+validationError.message() + " "+validationError.arguments());
			}

		}
	}



	public static List<Project> createProjectFromLims() throws SQLException, DAOException{

		List<Project> projects = limsServices.findProjectToCreate(contextError) ;
		
		for(Project project:projects){
			
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code)){
				MongoDBDAO.deleteByCode(InstanceConstants.PROJECT_COLL_NAME, Project.class, project.code);
				Logger.debug("Project to create :"+project.code);
			}
			
		}
		
		return InstanceHelpers.save(InstanceConstants.PROJECT_COLL_NAME,projects,contextError);
		
	}



	public	static void createContainersSamples() throws SQLException, DAOException{
		
		List<Container> containers = limsServices.findContainersToCreate(contextError); 
		List<Container> listContainers = new ArrayList<Container>(containers);
		
		Sample sample =null;
		Sample newSample =null;
		for(Container container :listContainers){

			Content content= container.contents.get(0);
		
			/* Sample content not in MongoDB */
			if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, content.sampleUsed.sampleCode)){
				/* Find sample in Mongodb */
				sample = limsServices.findSampleToCreate(contextError,container.contents.get(0).sampleUsed.sampleCode);
				newSample =(Sample) InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextError);
				
			}else {	newSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, content.sampleUsed.sampleCode);}
			
			if(newSample==null){
				/* Error : No sample, remove container from list to create */
				containers.remove(container);
				addErrors(contextError.errors, "container","initialdata.container.samplenotexist", container.support.barCode,content.sampleUsed.sampleCode);
			}
			else{
				Map<String,PropertyValue> properties=container.contents.get(0).properties;
				container.contents.clear();
				ContainerHelper.addContent(container,newSample);
				container.contents.get(0).properties.putAll(properties);
			}

		}

		List<Container> newContainers=InstanceHelpers.save(InstanceConstants.CONTAINER_COLL_NAME,containers,contextError);

		Logger.debug("Nb containers créés :"+newContainers.size());
		
		limsServices.updateTubeLims(newContainers,contextError);

		Logger.info("Maj des tubes du Lims");
	}

	//TODO
	//Maj volume, conc, quantity
	public static void updateContainerFromLims() throws SQLException, DAOException{
		
	}

	//TODO
	//Maj referenceCollab and resolution ???	
	public static void updateSampleFromLims() throws SQLException, DAOException{
			
	
	}
}
