package services.instance.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public class ContainerImportCNS extends AbstractImportDataCNS {

	public ContainerImportCNS(FiniteDuration durationFromStart,
			FiniteDuration durationFromNextIteration) {
		super("Container CNS",durationFromStart, durationFromNextIteration);
	}

	@Override
	public void runImport() throws SQLException, DAOException {
			createContainers(contextError,"pl_TubeToNGL ","tube","IW-P",null,null);
			createContainers(contextError,"pl_PrepaflowcellToNGL","lane","F",null,"pl_BanquesolexaUneLane @nom_lane=?");
			//Ne doit-on pas séparer cette execution ???
			contextError.setUpdateMode();
			updateSampleFromTara();
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
							String importTypeCode=DataMappingCNS.getImportTypeCode(true,Boolean.valueOf(sample.properties.get("isAdapters").value.toString()));
	
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

	//TODO
	//Maj referenceCollab and resolution ???	
	public static void updateSampleFromLims() throws SQLException, DAOException{
	
	}

	//TODO
	//Maj volume, conc, quantity
	public static void updateContainerFromLims() throws SQLException, DAOException{
		//
	}

	public static void saveSampleFromContainer(List<Container> containers) throws SQLException, DAOException{
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

	public static void createContentsFromContainers(List<Container> containers,
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
			ContainerImportCNS.createContentsFromContainers(containers,sqlContent);
		}
	
		ContainerImportCNS.saveSampleFromContainer(containers);
	
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
