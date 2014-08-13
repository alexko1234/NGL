package services.instance.container;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.LimsCNSDAO;
import models.laboratory.common.instance.Comment;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.Valuation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.sample.instance.Sample;
import models.util.DataMappingCNS;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.dao.DAOException;
import models.utils.instance.ContainerHelper;

import models.utils.instance.ContainerSupportHelper;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

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

	public static void saveSampleFromContainer(ContextValidation contextError,List<Container> containers,String sqlContent) throws SQLException, DAOException{
	
		Sample sample =null;
		Sample newSample =null;
		String rootKeyName=null;
	
		List<Container> containersList=new ArrayList<Container>(containers);
		for(Container container :containersList){

			List<Content> contents;
			if(sqlContent!=null){	
						contents=new ArrayList<Content>(limsServices.findContentsFromContainer(sqlContent,container.code));
			}else{
						contents=new ArrayList<Content>(container.contents);
			}
	

			for(Content sampleUsed : contents){
	
				/* Sample content not in MongoDB */
				if(!MongoDBDAO.checkObjectExistByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, sampleUsed.sampleCode)){
	
					rootKeyName="sample["+sampleUsed.sampleCode+"]";
					contextError.addKeyToRootKeyName(rootKeyName);
					sample = limsServices.findSampleToCreate(contextError,sampleUsed.sampleCode);
	
					if(sample!=null){
						newSample =(Sample) InstanceHelpers.save(InstanceConstants.SAMPLE_COLL_NAME,sample,contextError,true);
						if(!contextError.hasErrors()){
							limsServices.updateMaterielLims(newSample, contextError);
						}
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
	
		ContainerImportCNS.deleteContainerAndContainerSupport(containers);
		
		ContainerImportCNS.saveSampleFromContainer(contextError,containers,sqlContent);
		
		Map<String,PropertyValue<String>> propertiesContainerSupports=new HashMap<String, PropertyValue<String>>();
		for(Container container : containers){
			if(!propertiesContainerSupports.containsKey(container.support.code)){
				propertiesContainerSupports.put(container.support.code, container.properties.get("sequencingProgramType"));
			}
		}
		
		ContainerHelper.createSupportFromContainers(containers,propertiesContainerSupports, contextError);
	
		List<Container> newContainers=new ArrayList<Container>();
		
		for(Container container:containers){
			//Logger.debug("Container :"+container.code+ "nb sample code"+container.sampleCodes.size());
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

	
	private static void deleteContainerAndContainerSupport(
			List<Container> containers) {
		for(Container container : containers){
			MongoDBDAO.deleteByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, container.code);
			MongoDBDAO.deleteByCode(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, container.support.code);
		}
	}

	/**
	 * 
	 * Create au niveau Container from a ResultSet
	 * 
	 * The resultset must return fields :code, project, sampleCode, comment, codeSupport, limsCode, receptionDate, mesuredConcentration, mesuredVolume, mesuredQuantity, indexBq, nbContainer
	 * 
	 * @param rs ResulSet from Query
	 * @param containerCategoryCode 
	 * @param containerStatecode
	 * @return
	 * @throws SQLException
	 * @throws DAOException 
	 */
	public static Container createContainerFromResultSet(ResultSet rs, String containerCategoryCode, String containerStatecode, String experimentTypeCode) throws SQLException, DAOException{

		Container container = new Container();
		container.traceInformation.setTraceInformation(InstanceHelpers.getUser());
		//Logger.debug("Container :"+rs.getString("code"));
		container.code=rs.getString("code");
		container.categoryCode=containerCategoryCode;

		container.comments=new ArrayList<Comment>();				
		container.comments.add(new Comment(rs.getString("comment")));
		
		container.state = new State(); 
		container.state.code=DataMappingCNS.getState(containerCategoryCode,rs.getInt("etatLims"),experimentTypeCode);
		container.state.user = InstanceHelpers.getUser();
		container.state.date = new Date();

		
		container.valuation = new Valuation();
		container.valuation.valid=TBoolean.UNSET; // instead of valid=null;

		//TODO 
		container.support=ContainerSupportHelper.getContainerSupport(containerCategoryCode, rs.getInt("nbContainer"), rs.getString("codeSupport"), rs.getString("column"), rs.getString("line"));

		container.properties= new HashMap<String, PropertyValue>();
		container.properties.put("limsCode",new PropertySingleValue(rs.getInt("limsCode")));
		if(rs.getString("sequencingProgramType")!=null)
			container.properties.put("sequencingProgramType", new PropertySingleValue(rs.getString("sequencingProgramType")));
		
		if(rs.getString("receptionDate")!=null){
			container.properties.put("receptionDate",new PropertySingleValue(rs.getString("receptionDate")));
		}

		container.mesuredConcentration=new PropertySingleValue(rs.getFloat("mesuredConcentration"), "ng/µl");
		container.mesuredVolume=new PropertySingleValue(rs.getFloat("mesuredVolume"), "µl");
		container.mesuredQuantity=new PropertySingleValue(rs.getFloat("mesuredQuantity"), "ng");

		container.fromExperimentTypeCodes=InstanceHelpers.addCode(experimentTypeCode, container.fromExperimentTypeCodes);

		container.projectCodes=new ArrayList<String>();					

		if(rs.getString("project")!=null)
		{					
			container.projectCodes.add(rs.getString("project"));
		}
		
		if(rs.getString("controlLane")!=null){
			container.properties.put("controlLane",new PropertySingleValue(rs.getBoolean("controlLane")));
		}
			

		container.sampleCodes=new ArrayList<String>();

		if(rs.getString("sampleCode")!=null){
			
			Content sampleUsed=new Content();
			sampleUsed.sampleCode=rs.getString("sampleCode");
			
			//Todo replace by method in containerHelper who update sampleCodes from contents
			container.sampleCodes.add(rs.getString("sampleCode"));

			if(rs.getString("tag")!=null){
				sampleUsed.properties = new HashMap<String, PropertyValue>();
				sampleUsed.properties.put("tag",new PropertySingleValue(rs.getString("tag")));
				sampleUsed.properties.put("tagCategory",new PropertySingleValue(rs.getString("tagCategory")));
			}
			container.contents.add(sampleUsed);

		}
		return container;

	}

}
