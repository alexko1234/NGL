package services.instance.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.LimsCNSDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.sample.instance.Sample;
import models.util.DataMappingCNS;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.Logger;
import scala.concurrent.duration.FiniteDuration;
import services.instance.AbstractImportDataCNS;
import validation.ContextValidation;
import fr.cea.ig.MongoDBDAO;

public abstract class ContainerImportCNS extends AbstractImportDataCNS {

	public ContainerImportCNS(String name,FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super(name,durationFromStart, durationFromNextIteration);
	}


	public static void updateSampleFromTara(ContextValidation contextError) throws SQLException, DAOException{
	
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
	
					String importTypeCode=DataMappingCNS.getImportTypeCode(true,Boolean.valueOf(sample.properties.get("isAdapters").value.toString()));
					
					if(!importTypeCode.equals(sample.importTypeCode)){
						MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",sample.code),DBUpdate.set("importTypeCode",sample.importTypeCode));
					}
					
					MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, DBQuery.is("code",sample.code),DBUpdate.addToSet("properties",taraProperties));
					
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,Container.class, DBQuery.is("content.sampleUsed.sampleCode",sample.code),DBUpdate.addToSet("content.$.properties",taraProperties));
					
					MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, DBQuery.is("code",sample.code),DBUpdate.addToSet("properties",taraProperties));

					/*for(Entry taraEntry :taraProperties.entrySet()){
	
						if(!taraEntry.getKey().equals(LimsCNSDAO.LIMS_CODE)){
							
	
							InstanceHelpers.updateTraceInformation(sample.traceInformation);
							MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, DBQuery.is("code",sample.code),DBUpdate.addToSet("properties."+taraEntry.getKey(), taraEntry));
							
							//MongoDBDAO.updateSet(InstanceConstants.SAMPLE_COLL_NAME, sample, "properties."+taraEntry.getKey()+".value",taraEntry.getValue());
							//MongoDBDAO.updateSet(InstanceConstants.SAMPLE_COLL_NAME, sample, "traceInformation", sample.traceInformation);
							//
							MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("samples",sample.code), DBUpdate.set("content.properties"+taraEntry.getKey()+".value",taraEntry.getValue()));
						}
					}*/
	
				}
			}
	
	
		}
	
	}

	//TODO
	//Maj referenceCollab and resolution ???	
	public static void updateSampleFromLims() throws SQLException, DAOException{
	
	}

	//TODO
	//Maj volume, conc, quantity
	public static void updateContainerFromLims() throws SQLException, DAOException{
		//
	}

	public static void saveSampleFromContainer(ContextValidation contextError,List<Container> containers,String sqlContent) throws SQLException, DAOException{
	
		Sample sample =null;
		Sample newSample =null;
		String rootKeyName=null;
	
		for(Container container :containers){

			List<Content> contents;
			if(sqlContent!=null){	
						contents=new ArrayList<Content>(limsServices.findContentsFromContainer(sqlContent,container.code));
			}else{
						contents=new ArrayList<Content>(container.contents);
			}
			//Logger.debug("Container :"+container.code);
	

			for(Content sampleUsed : contents){
	
				/* Sample content not in MongoDB */
				if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sampleUsed.sampleCode)){
	
					rootKeyName="sample["+sampleUsed.sampleCode+"]";
					contextError.addKeyToRootKeyName(rootKeyName);
					sample = limsServices.findSampleToCreate(contextError,sampleUsed.sampleCode);
	
					if(sample!=null){
						newSample =(Sample) InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextError,true);
					}
					contextError.removeKeyFromRootKeyName(rootKeyName);
	
				}else {	
					/* Find sample in Mongodb */
					newSample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME,Sample.class, sampleUsed.sampleCode);	
				}			
	
				rootKeyName="container["+container.code+"]";
				contextError.addKeyToRootKeyName(rootKeyName);
	
				/* Error : No sample, remove container from list to create */
				if(newSample==null){
					containers.remove(container);
					contextError.addErrors("sample","error.codeNotExist", sampleUsed.sampleCode);
				}
				else{
					/* From sample, add content in container */
					container.contents.remove(sampleUsed);
					ContainerHelper.addContent(container,newSample,sampleUsed.properties);
				}
				contextError.removeKeyFromRootKeyName(rootKeyName);
	
			}
		}
	
	}

/*	public static void createContentsFromContainers(List<Container> containers,
			String sqlContent) throws SQLException {
	
		for(Container container:containers){	
					container.contents=new ArrayList<SampleUsed>(limsServices.findContentsFromContainer(sqlContent,container.code));

		}
	
	}
*/
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
	
		ContainerImportCNS.saveSampleFromContainer(contextError,containers,sqlContent);
		
		ContainerHelper.createSupportFromContainers(containers, contextError);
	
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

	

}
