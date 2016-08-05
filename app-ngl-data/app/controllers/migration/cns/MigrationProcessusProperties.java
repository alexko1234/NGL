package controllers.migration.cns;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.processes.instance.Process;
import models.laboratory.run.instance.ReadSet;
import models.utils.InstanceConstants;
import play.Logger;
import play.mvc.Result;

public class MigrationProcessusProperties extends CommonController{

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
	
	public static Result migration(String processTypeCode, String keyProperty){

		//backupContainerCollection();
		//backupReadSetCollection();
		
		List<Process> processes = MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.is("typeCode", processTypeCode).exists("outputContainerSupportCodes")).toList();

		
		//Check same sample Code in list content container child
		Logger.debug("Check Child Container unique sampleCode");
		List<String> errorMessagesSample = processes.stream().map(process->process.outputContainerSupportCodes).flatMap(container->container.stream()).filter(containerCode->{
			//Get container 
			Set<String> containersError = new HashSet<String>();
			Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerCode);
			Set<String> counts = container.contents.stream().collect(Collectors.collectingAndThen(Collectors.groupingBy(c->c.sampleCode, Collectors.counting()), map->{map.values().removeIf(l -> l==1); return map.keySet();}));
			if(counts.size()>0)
				containersError.add(container.code);
			return containersError.size()>0;
		}).collect(Collectors.toList());

		String error=null;
		
		if(errorMessagesSample.size()>0){
			error=" Child Container with multiple same sample ";
			for(String errorMessage : errorMessagesSample){
				error+=errorMessage+" ";
			}
		}

		if(error!=null)
			return badRequest(error);
		//Get all process to migrate

		Logger.debug("Update "+processes.size()+" processes");
		for(Process p : processes){
			//Get property value to add
			Logger.debug("Update container for process "+p.code);
			PropertyValue property = p.properties.get(keyProperty);
			
			for(String containerCode : p.outputContainerSupportCodes){
				//Get container
				Container container = MongoDBDAO.findByCode(InstanceConstants.CONTAINER_COLL_NAME, Container.class, containerCode);
				container.contents.stream().filter(c->c.sampleCode.equals(p.sampleCode) && c.projectCode.equals(p.projectCode)).map(c->c.properties.put(keyProperty, property));

				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, container);

				//get readSet and update
				List<ReadSet> readSets = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("sampleOnContainer.containerCode", containerCode)).toList();
				int sizeAllReadSets = readSets.size();
				readSets.stream().filter(readset->readset.sampleCode.equals(p.sampleCode) && readset.projectCode.equals(p.projectCode)).map(readset->readset.sampleOnContainer.properties.put(keyProperty, property));
				int sizeUpdateReadSets = readSets.size();
				
				if(sizeAllReadSets!=sizeUpdateReadSets){
					Logger.warn("Check ReadSet for container "+containerCode);
				}
				for(ReadSet readSet : readSets){
					//Logger.debug("Update readset "+readSet.code);
					MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSet);
				}
			}
		}
		return ok();
	}
	
	private static void backupContainerCollection() {
		String backupName = InstanceConstants.CONTAINER_COLL_NAME+"_BCK_"+sdf.format(new java.util.Date());
		
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" start");
		MongoDBDAO.save(backupName, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
	}
	
	private static void backupReadSetCollection() {
		String backupName = InstanceConstants.READSET_ILLUMINA_COLL_NAME+"_BCK_"+sdf.format(new java.util.Date());
		BasicDBObject keys = new BasicDBObject();
		keys.put("treatments", 0);
		
		Logger.info("\tCopie "+InstanceConstants.READSET_ILLUMINA_COLL_NAME+" start");
		MongoDBDAO.save(backupName, MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.exists("sampleOnContainer"), keys).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
	}

}
