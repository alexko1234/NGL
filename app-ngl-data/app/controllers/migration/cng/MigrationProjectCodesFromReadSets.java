package controllers.migration.cng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.LimsCNGDAO;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Content;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import org.mongojack.WriteResult;

import controllers.CommonController;

import play.Logger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import fr.cea.ig.MongoDBDAO;

/**
 * Update projectCodes from readSets
 * @author dnoisett
 * 03-12-2014
 */
public class MigrationProjectCodesFromReadSets  extends CommonController {
	
	protected static LimsCNGDAO limsServices= Spring.getBeanOfType(LimsCNGDAO.class);	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
	private static String backupName = InstanceConstants.CONTAINER_COLL_NAME+"_BCK_PC_"+sdf.format(new java.util.Date());		
	private static String backupName2 = InstanceConstants.SUPPORT_COLL_NAME+"_BCK_PC_"+sdf.format(new java.util.Date());
	
	
	
	public static Result migration() {		
		int[] intResultsArray = new int[2]; 
		
		backUpCollections();
		
		Logger.info("Migration contents of containers starts : add projectCode attribute");
		
		intResultsArray = migrateContainer("lane");		
		
		Logger.info("Migration contents of containers Finish : " + intResultsArray[0] + " contents and projectCodes of containers updated !");
		Logger.info("Migration contents of container supports Finish : " + intResultsArray[1] + " projectCodes of container supports updated !");
	
		
		return ok("End");
	}
	
	
	
	private static void backUpCollections() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" to "+backupName+" start");		
		MongoDBDAO.save(backupName, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" to "+backupName+" end");	
		
		Logger.info("\tCopie "+InstanceConstants.SUPPORT_COLL_NAME+" to "+backupName2+" start");		
		MongoDBDAO.save(backupName2, MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class).toList());
		Logger.info("\tCopie "+InstanceConstants.SUPPORT_COLL_NAME+" to "+backupName2+" end");	
	}
	
	
	
	private static int[] migrateContainer(String type) {
		
		int[] intResultsArray = new int[] {0,0};
		String errorMsg = "", oldErrorMsg = "", errorMsg2 = "", oldErrorMsg2 = "";
		boolean bFindReadSet;

			
		//find container supports
		List<ContainerSupport> oldSupportContainers = MongoDBDAO.find(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, 
				DBQuery.regex("categoryCode", Pattern.compile("flowcell"))).toList();

	
		for (ContainerSupport oldSupportContainer : oldSupportContainers) {
			
			bFindReadSet = false;
			HashMap<String, String> hmSupportContainers = new HashMap<String, String>();
			
			//find run for this container support
			List<Run> runs = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class,  DBQuery.is("containerSupportCode", oldSupportContainer.code)).toList();
			if (runs == null || runs.size() == 0) {
				errorMsg = "ERROR 1 : No run found for container support " + oldSupportContainer.code;
				if (!errorMsg.equals(oldErrorMsg)) {
					//Logger.error(errorMsg);
				}
			}
			else if (runs.size() > 1) {
				errorMsg = "ERROR 2 : Multiple runs found container support " + oldSupportContainer.code;
				if (!errorMsg.equals(oldErrorMsg)) {
					Logger.error(errorMsg);
				}
			}
			else {
				//find readSets associated with this run
				List<ReadSet> rds = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", runs.get(0).code)).toList();
				if (rds == null || rds.size() == 0) {
					errorMsg = "ERROR 3 : No readSet found for run " + runs.get(0).code;
					if (!errorMsg.equals(oldErrorMsg)) {
						Logger.error(errorMsg);
					}
				}
				else {
					bFindReadSet = true;
					//find sampleCodes and projectCodes
					for (ReadSet rd : rds) {
						hmSupportContainers.put(rd.sampleCode, rd.projectCode); 
					}
				}
			}
			
			
			if (bFindReadSet) {
				
				//update container support
				WriteResult r = (WriteResult) MongoDBDAO.update(InstanceConstants.SUPPORT_COLL_NAME, ContainerSupport.class, DBQuery.is("code", oldSupportContainer.code),   
						DBUpdate.set("projectCodes", hmSupportContainers.values()));				
				if(StringUtils.isNotEmpty(r.getError())){
					Logger.error("ERROR 4 : Set container support project codes: "+oldSupportContainer.code+" / "+ r.getError());
				}						
				else {
					intResultsArray[0]++; 
				}
				
				//find containers associated with this container support
				List<Container> oldContainers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("support.code", oldSupportContainer.code)).toList();
				
				ArrayList<String> containersSampleCodes = new ArrayList<String>();
				
				//iterate over the containers
				for (Container oldContainer : oldContainers) {
					HashMap<String, String> hmContainers = new HashMap<String, String>();
					
					for (Content content : oldContainer.contents) {
						if (hmSupportContainers.containsKey(content.sampleCode)) {
							content.projectCode = hmSupportContainers.get(content.sampleCode);
							hmContainers.put(content.sampleCode, content.projectCode); 
						}
						else {
							//error missing sample code in container support
							errorMsg2 = "ERROR 5 : Missing sample code " + content.sampleCode + " in container support " + oldSupportContainer.code + " OR wrong sample code in a content of container " + oldContainer.code; 
							if (!errorMsg2.equals(oldErrorMsg2)) {
								Logger.error(errorMsg2);
							}
							oldErrorMsg2 = errorMsg2; 
						}					
						if (!containersSampleCodes.contains(content.sampleCode)) {
							containersSampleCodes.add(content.sampleCode);
						}
					}
					
					//update each container
					r = (WriteResult) MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("code", oldContainer.code),   
							DBUpdate.set("contents", oldContainer.contents).set("projectCodes", hmContainers.values()));					
					if(StringUtils.isNotEmpty(r.getError())){
						Logger.error("ERROR 6 : Set container: "+oldContainer.code+" with contents and projectCodes / "+ r.getError());
					}						
					else {
						intResultsArray[1]++; 
					}
					
				}
				
				
				//reverse control
				if (hmSupportContainers.size() > containersSampleCodes.size()) {
					//error too many sample code in container support 
					for (Map.Entry<String, String> e : hmSupportContainers.entrySet()) {
						if (!containersSampleCodes.contains(e.getKey())) {
							errorMsg = "ERROR 7 : The sample code " + e.getKey()  + " is not in the list of sample codes for the container support " + oldSupportContainer.code;
							if (!errorMsg.equals(oldErrorMsg)) {
								Logger.error(errorMsg);
							}
							break;
						}
					}
				}
			}
			
			
				


			//just for not repeated the same error msg
			oldErrorMsg = errorMsg;

		} //end of iteration over the collection of container supports
		
		return intResultsArray;	
	}

}
