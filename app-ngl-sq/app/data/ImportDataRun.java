package data;

import static validation.utils.ConstraintsHelper.addErrors;

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
import models.laboratory.project.description.ProjectType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.HelperObjects;
import models.utils.IValidation;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.validation.ValidationError;
import validation.utils.ConstraintsHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class ImportDataRun implements Runnable {

	static Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
	static LimsDAO  limsServices = Spring.getBeanOfType(LimsDAO.class);
	final static String projectTypeCode="projectType";
	final static String[] keysToCopy= new String[] {"station","iteration","filtre","materiel","profondeur","index","tailleTaxon"};


	@Override
	public void run() {
		errors.clear();
		Logger.debug("ImportDataRun execution");
		try{
			createProjectFromLims();
			createContainersSamples();

		}catch (Exception e) {
			Logger.debug("",e);
		}
		/* Display error messages */
		Iterator entries = errors.entrySet().iterator();
		while (entries.hasNext()) {
			Entry thisEntry = (Entry) entries.next();
			String key = (String) thisEntry.getKey();
			List<ValidationError> value = (List<ValidationError>) thisEntry.getValue();	  

			for(ValidationError validationError:value){
				Logger.debug(key+ " : "+validationError.message() + " "+validationError.arguments());
			}

		}
	}



	public static List<Project> createProjectFromLims(){

		Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();

		ProjectType projectType= new HelperObjects<ProjectType>().getObject(ProjectType.class,projectTypeCode);

		List<Project> projects=new ArrayList<Project>();


		if(projectType==null){
			ConstraintsHelper.addErrors(errors, ConstraintsHelper.getKey(null, "ProjectTypeCode"), "PROJECT TYPE NOT EXISTS",projectTypeCode);
			return null;
		}

		List<ListObject> listObjects =limsServices.getListObjectFromProcedureLims("pl_ProjetEnCoursListe");

		for(ListObject lobject :listObjects){

			Project project = MongoDBDAO.findByCode("Project", Project.class, lobject.code.trim());
			if(project==null){
				Logger.debug("Project "+lobject.code);
				project=new Project(lobject.code.trim(),lobject.name);
				project.traceInformation.setTraceInformation("ngl");
				projects.add(project);
			}
		}

		return InstanceHelpers.save(projects,errors);

	}



	public	static void createContainersSamples() throws SQLException, DAOException{
		
		List<Container> containers = limsServices.findContainersToCreate(errors); 
		List<Container> listContainers = new ArrayList<Container>(containers);
		
		Sample sample =null;
		Sample newSample =null;
		for(Container container :listContainers){

			Content content= container.contents.get(0);
			sample = new HelperObjects<Sample>().getObject(Sample.class, content.sampleUsed.sampleCode);
		
			/* Sample content not in MongoDB */
			if(sample==null){
				/* Find sample in Mongodb */
				sample = limsServices.findSampleToCreate(errors,container.contents.get(0).sampleUsed.sampleCode);
				newSample =(Sample) InstanceHelpers.save(sample,errors);
				
			}else {	newSample = sample;}
			
			if(newSample==null){
				/* Error : No sample, remove container from list to create */
				containers.remove(container);
				Logger.debug("Remove container"+container.code );
				addErrors(errors, "container","initialdata.container.samplenotexist", container.support.barCode,content.sampleUsed.sampleCode);
			}
			else{
				Map<String,PropertyValue> properties=container.contents.get(0).properties;
				container.contents.clear();
				ContainerHelper.addContent(container,newSample);
				container.contents.get(0).properties.putAll(properties);
			}

		}

		List<Container> newContainers=InstanceHelpers.save(containers,errors);

		Logger.debug("Nb containers créés :"+newContainers.size());
		
		limsServices.updateTubeLims(newContainers,errors);

		Logger.debug("Maj des tubes du Lims");
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
