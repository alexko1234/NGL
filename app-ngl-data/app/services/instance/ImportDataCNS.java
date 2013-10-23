package services.instance;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import controllers.description.samples.Samples;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;

import models.LimsCNSDAO;
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

public class ImportDataCNS extends AbstractImportData{

	static ContextValidation contextError = new ContextValidation();
	static LimsCNSDAO  limsServices = Spring.getBeanOfType(LimsCNSDAO.class);
	static TaraDAO taraServices = Spring.getBeanOfType(TaraDAO.class);


	@Override
	public void run() {
		contextError.clear();
		contextError.addKeyToRootKeyName("import");
		Logger.info("ImportData execution");
		try{
			Logger.info(" Import Projects ");
			createProjectFromLims();
			Logger.info(" Import Containers and Samples ");
			createContainers(contextError,"pl_TubeToNGL ","tube","IW-P",null,null); 
			createContainers(contextError,"pl_PrepaflowcellToNGL","lane","F",null,"pl_BanquesolexaUneLane @nom_lane=?");
			updateSampleFromTara();
			contextError.removeKeyFromRootKeyName("import");
		}catch (Exception e) {
			Logger.debug("",e);
		}

		/* Display error messages  */
		contextError.displayErrors();
		/* Logger send an email */
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


	/**
	 * 
	 * Create containers, contents and samples from 2 sql queries 
	 * @param contextError
	 * @param sqlContainer
	 * @param containerCategoryCode
	 * @param containerStateCode
	 * @param experimentTypeCode
	 * @param sqlContent
	 * @throws SQLException
	 * @throws DAOException
	 */
	public	static void createContainers(ContextValidation contextError, String sqlContainer,String containerCategoryCode,  String containerStateCode, String experimentTypeCode, String sqlContent) throws SQLException, DAOException{
		String rootKeyName=null;

		List<Container> containers=	limsServices.findContainersToCreate(sqlContainer,contextError, containerCategoryCode,containerStateCode,experimentTypeCode);
		
		if(sqlContent!=null){
			createContentsFromContainers(containers,sqlContent);
		}
		
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

		limsServices.updateMaterielmanipLims(newContainers,contextError);

	}


	private static void createContentsFromContainers(List<Container> containers,
			String sqlContent) {

		for(Container container:containers){
			if(container.contents==null){

				if(sqlContent==null){
						//add error
				}else {
					container.contents=new ArrayList<Content>(limsServices.findContentsFromContainer(sqlContent,container.code));
					//Logger.debug("Container.contents :"+container.contents.size());
				}
			}
		}

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
					sample = limsServices.findSampleToCreate(contextError,content.sampleUsed.sampleCode);

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

	//TODO
	//Maj volume, conc, quantity
	public static void updateContainerFromLims() throws SQLException, DAOException{
		//
	}

	//TODO
	//Maj referenceCollab and resolution ???	
	public static void updateSampleFromLims() throws SQLException, DAOException{

	}

	public static void updateSampleFromTara() throws SQLException, DAOException{

		List<Map<String, PropertyValue>> taraPropertyList = taraServices.findTaraSampleUpdated();

		for(Map<String,PropertyValue> taraProperties : taraPropertyList){

			Integer limsCode=Integer.valueOf(taraProperties.get(LimsCNSDAO.LIMS_CODE).value.toString());
			Logger.debug("Tara lims Code :"+limsCode);
			if(!taraProperties.containsKey(LimsCNSDAO.LIMS_CODE)){
				contextError.addErrors(LimsCNSDAO.LIMS_CODE,"error.codeNotExist","");
			}else {

				List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("properties.limsCode.value",limsCode)).toList();

				if(samples.size()!=1 ) {
					contextError.addErrors("sample."+LimsCNSDAO.LIMS_CODE,"error.noObject",limsCode);
				}else {
					Sample sample =samples.get(0);

					for(Entry taraEntry :taraProperties.entrySet()){

						if(!taraEntry.getKey().equals(LimsCNSDAO.LIMS_CODE)){
							String importTypeCode=LimsCNSDAO.getImportTypeCode(true,Boolean.valueOf(sample.properties.get("isAdapters").value.toString()));

							if(!importTypeCode.equals(sample.importTypeCode)){
								MongoDBDAO.updateSet(InstanceConstants.SAMPLE_COLL_NAME, sample, "importTypeCode", importTypeCode);
							}

							MongoDBDAO.updateSet(InstanceConstants.SAMPLE_COLL_NAME, sample, "properties."+taraEntry.getKey()+".value",taraEntry.getValue());
							InstanceHelpers.updateTraceInformation(sample.traceInformation);
							MongoDBDAO.updateSet(InstanceConstants.SAMPLE_COLL_NAME, sample, "traceInformation", sample.traceInformation);
							MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("samples",sample.code), DBUpdate.set("properties"+taraEntry.getKey()+".value",taraEntry.getValue()));
						}
					}

				}
			}


		}

	}
}
