package controllers.migration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.parameter.Index;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.instance.ExperimentHelper;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import play.Logger;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;

public class MigrationNGLSEQ extends CommonController{
	
	private static final String today =( new SimpleDateFormat("ddMMyy'_'hhmm")).format(new Date());
	
	public static Result migration() {
		
		Logger.info(">>>>>>>>>>> Migration NGL-SQ in Containers and Experiment starts");

		/*backupOneCollection(InstanceConstants.CONTAINER_COLL_NAME,Container.class);
		updateProjectAndTagCategoryAndMeasureValueContainer();
		backupOneCollection(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class);*/
		//updateExperimentInputContainerSupportCodes();
		
		//backupOneCollection(InstanceConstants.PROCESS_COLL_NAME,Process.class);
		udpdateProcessExperimentAndSupportCodes();
		
		Logger.info(" Migration NGL-SQ in Containers and Experiment ends <<<<<<<");
		
		int nbContentWithoutProjectCode=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.notExists("contents.0.projectCode")).count();
		return ok("Migration Finish");
	}
	
	private static void udpdateProcessExperimentAndSupportCodes() {
		List<Experiment> experiments=MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).toList();
		
		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.exists("code"),
				DBUpdate.unset("experimentCodes")
				.unset("newContainerSupportCodes"),true);

		for(Experiment exp:experiments){
			List<String> codes=ExperimentHelper.getAllProcessCodesFromExperiment(exp);

			if(CollectionUtils.isNotEmpty(codes)){
			MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.in("code", codes),
					DBUpdate.push("experimentCodes", exp.code)
					.pushAll("newContainerSupportCodes", exp.outputContainerSupportCodes),true);
			}
		}
	}



	public static void updateContainerSupportState(String supportCode, String stateCode){
		
		MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.is("code", supportCode)
				,DBUpdate.set("state.code",stateCode));
	}
	
	//Update Content.projectCode, Content.property.get("tagCategory").value, mesuredConcentration, mesuredQuantity, mesuredVolume
	public static void updateProjectAndTagCategoryAndMeasureValueContainer(){
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		
		List<Index> indexList=MongoDBDAO.find(InstanceConstants.PARAMETER_COLL_NAME, Index.class).toList();
		HashMap< String, String> tags=new HashMap<String, String>();
		for(Index index:indexList){
			tags.put(index.code, index.categoryCode);
		}
		
		for(Container container:containers){
			DBUpdate.Builder builder = new DBUpdate.Builder();
		 try{
			for(Content content : container.contents){
				if(content.sampleCode.contains("_")){
					content.projectCode=content.sampleCode.substring(content.sampleCode.indexOf("_"));
				}else { Logger.error("Error container :"+container.code + ", content :"+content.sampleCode); }
				
				if(content.properties.containsKey("tag")){
					content.properties.get("tagCategory").value=tags.get(content.properties.get("tag").value);
				}
				
				builder.set("contents",container.contents);
			}
			
			if(container.mesuredConcentration!=null){
				container.mesuredConcentration.value=getDoubleFromString(container.mesuredConcentration.value.toString());
				builder.set("mesuredConcentration", container.mesuredConcentration);
			}
			if(container.mesuredQuantity!=null){
				container.mesuredQuantity.value=getDoubleFromString(container.mesuredQuantity.value.toString());
				builder.set("mesuredQuantity", container.mesuredQuantity);
			}
			if(container.mesuredVolume!=null){
				container.mesuredVolume.value=getDoubleFromString(container.mesuredVolume.value.toString());
				builder.set("mesuredVolume",container.mesuredVolume);
			}
			
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class
					,DBQuery.is("code",container.code)
					,builder);
			
			updateContainerSupportState(container.support.code, container.state.code);
		 }	catch(Exception e){
			 Logger.error(" Error container "+container.code);
			 Logger.error(e.getMessage());
		 }
		}	
	
	}
	
	public static void updateExperimentInputContainerSupportCodes(){
		
		List<Experiment> experiments=MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class).toList();
		
		for(Experiment experiment:experiments){
			DBUpdate.Builder update=new DBUpdate.Builder();
			
			experiment.inputContainerSupportCodes=ExperimentHelper.getInputContainerSupportCodes(experiment);
			update.set("inputContainerSupportCodes", experiment.inputContainerSupportCodes);
			
			experiment.outputContainerSupportCodes=ExperimentHelper.getOutputContainerSupportCodes(experiment);
			if(CollectionUtils.isNotEmpty(experiment.outputContainerSupportCodes))
				update.set("outputContainerSupportCodes",experiment.outputContainerSupportCodes);
			
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,DBQuery.is("code", experiment.code),
					update);
		}
	}

	
	public static Double getDoubleFromString(String value){
		BigDecimal bg = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
		return bg.doubleValue();

	}
	
	private static <T extends DBObject> void backupOneCollection(String collectionName,Class<T> classType) {
		Logger.info("\tCopie "+collectionName+" start");
		MongoDBDAO.save(collectionName+"_BCK_"+today,MongoDBDAO.find(collectionName, classType).toList());
		Logger.info("\tCopie "+collectionName+" end");
		
		
	}
}
