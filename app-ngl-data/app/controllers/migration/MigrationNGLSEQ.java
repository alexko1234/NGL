package controllers.migration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.parameter.Index;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
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

	/*	backupOneCollection(InstanceConstants.CONTAINER_COLL_NAME,Container.class);
		updateProjectAndTagCategoryAndMeasureValueContainer();
		backupOneCollection(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class);
		updateExperimentInputContainerSupportCodes();
		backupOneCollection(InstanceConstants.PROCESS_COLL_NAME,Process.class);
		udpdateProcessExperimentAndSupportCodes();
		*/
	/*	
		backupOneCollection(InstanceConstants.CONTAINER_COLL_NAME,Container.class);
		migrationRemoveTagInContainerTube();
		backupOneCollection(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class);
		migrationInputContainerSupportCodesDepotOpgen();
		Logger.info(" Migration NGL-SQ in Containers and Experiment ends <<<<<<<");
		
		int nbContentWithoutProjectCode=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.notExists("contents.0.projectCode")).count();
	*/
		
		/* NGL-1.6.1*/
	//	backupOneCollection(InstanceConstants.CONTAINER_COLL_NAME,Container.class);
		backupOneCollection(InstanceConstants.PROCESS_COLL_NAME, Process.class);
		migrationProcessTypeCodeInContainer();
		migrationProcessCurrentExperimentTypeCode();
		
		return ok("Migration Finish");
	}
	
	
	
	private static void migrationProcessTypeCodeInContainer() {
		
		List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.is("state.code", "F")).toList();
		for(Process process:processes){
			if(process.newContainerSupportCodes!=null){
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.in("support.code",process.newContainerSupportCodes).notExists("inputProcessCodes"),DBUpdate.unset("processTypeCode"));
			}
			MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code",process.containerInputCode).notExists("inputProcessCodes"),DBUpdate.unset("processTypeCode"));
			
			if(process.typeCode.equals("opgen-run")){
				
				Logger.debug("Processus "+process.code);
				
				List<Container> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("inputProcessCodes", process.code).in("fromExperimentTypeCodes", "opgen-depot")).toList();

				List<String> newContainers=new ArrayList<String>();
				List<String> experimentCodes=new ArrayList<String>();
				
				for(Container container :containers){
					Logger.debug(" => New Container "+container.support.code);
					newContainers.add(container.support.code);
				}

				if(CollectionUtils.isEmpty(newContainers)){
					Logger.debug(" => Processus sans Container");
				}
				else {
					List<Experiment> exps=MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class,DBQuery.in("outputContainerSupportCodes",newContainers)).toList();
					if(CollectionUtils.isEmpty(newContainers)){
						Logger.debug(" => Container sans Experiment");
					}else {
						experimentCodes.add(exps.get(0).code);
					}
				}
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("code",process.code), DBUpdate.set("newContainerSupportCodes",newContainers).set("experimentCodes", experimentCodes).set("currentExperimentTypeCode", "opgen-depot"));
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("support.code", newContainers),DBUpdate.unset("inputProcessCodes").unset("processTypeCode"));
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME,Container.class,DBQuery.in("code", process.containerInputCode),DBUpdate.unset("inputProcessCodes").unset("processTypeCode"));
				
			}
		}		
	}
	
	private static void migrationProcessCurrentExperimentTypeCode(){
		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.is("typeCode","opgen-run").notExists("currentExperimentTypeCode"),DBUpdate.set("currentExperimentTypeCode", "opgen-depot"));
	}


	private static void udpdateProcessExperimentAndSupportCodes() {
		List<Experiment> experiments=MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).toList();
		
		MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.exists("code"),
				DBUpdate.unset("experimentCodes")
				.unset("newContainerSupportCodes"),true);

		for(Experiment exp:experiments){
			DBUpdate.Builder update=new DBUpdate.Builder();
			List<String> codes=ExperimentHelper.getAllProcessCodesFromExperiment(exp);

			update.push("experimentCodes", exp.code);
			if(CollectionUtils.isNotEmpty( exp.outputContainerSupportCodes)){
				update.pushAll("newContainerSupportCodes", exp.outputContainerSupportCodes);
			}
			if(CollectionUtils.isNotEmpty(codes)){
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.in("code", codes),update,true);
			}
		}
	}



	public static void updateContainerSupportState(String supportCode, String stateCode){
		
		MongoDBDAO.update(InstanceConstants.CONTAINER_SUPPORT_COLL_NAME, ContainerSupport.class,DBQuery.is("code", supportCode)
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
			
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,DBQuery.is("code", experiment.code),DBUpdate.unset("outputContainerSupportCodes").unset("inputContainerSupportCodes"));
			
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
	
	static <T extends DBObject> void backupOneCollection(String collectionName,Class<T> classType) {
		Logger.info("\tCopie "+collectionName+" start");
		MongoDBDAO.save(collectionName+"_BCK_"+today,MongoDBDAO.find(collectionName, classType).toList());
		Logger.info("\tCopie "+collectionName+" end");
		
		
	}
	
	
	public static void migrationRemoveTagInContainerTube(){
		//Recupere tous les containes tube sans fromExperimentTypeCodes dont le nom ne se termine pas par b
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME,
				Container.class,
				DBQuery.exists("contents.properties.tag")
						.is("categoryCode", "tube")
						.size("fromExperimentTypeCodes",0).regex("code", Pattern.compile(".*[^b]$"))).toList();
		Logger.debug("Nb containers "+containers.size());
		for(Container container:containers){
				Logger.debug("Container to update "+container.code);
				Iterator<Content> iterator = container.contents.iterator();
				Content cnt = iterator.next();
				cnt.properties.remove("tag");
				cnt.properties.remove("tagCategory");
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class,DBQuery.is("code",container.code),DBUpdate.set("contents.0.properties",cnt.properties));						
		}
		
	}
	
	
	public static void migrationInputContainerSupportCodesDepotOpgen(){
		//Recupere les experiments qui n'ont pas d'inputContainerSupportCodes
		List<Experiment> experiments=MongoDBDAO.find(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class,
				DBQuery.or(DBQuery.notExists("inputContainerSupportCodes"),DBQuery.size("inputContainerSupportCodes", 0))).toList();
		
		for(Experiment experiment:experiments){
			Logger.debug("Experiment to update"+experiment.code);
			List<String> inputContainerSupportCodes=new ArrayList<String>();
			for(ContainerUsed containerUsed:experiment.getAllInPutContainer()){
				if(containerUsed.locationOnContainerSupport==null)
				 {
					InstanceHelpers.addCode(containerUsed.code, inputContainerSupportCodes);
				}else
				{
					InstanceHelpers.addCode(containerUsed.locationOnContainerSupport.code, inputContainerSupportCodes);
				}
			}
			MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class
					,DBQuery.is("code", experiment.code)
					,DBUpdate.set("inputContainerSupportCodes",inputContainerSupportCodes));
		}
	}
}
