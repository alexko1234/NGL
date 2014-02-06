package controllers.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;

import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.ContainerSupport;
import models.laboratory.container.instance.Support;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import play.Logger;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.migration.models.ContainerOld;
import fr.cea.ig.MongoDBDAO;

public class MigrationSupportV2 extends CommonController{
	
	private static final String CONTAINER_COLL_NAME_BCK = InstanceConstants.CONTAINER_COLL_NAME+"_BCK";
	
	
	public static Result migrationSupport(){
		
		Logger.info("Start point of Migration Support");
		
		JacksonDBCollection<ContainerOld, String> containersCollBck = MongoDBDAO.getCollection(CONTAINER_COLL_NAME_BCK, ContainerOld.class);
		if(containersCollBck.count() == 0){
			Logger.info("Migration run start");
			backupContainerCollection();
			List<ContainerOld> containers = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class).toList();
			Logger.debug("migre "+containers.size()+" containers");
			for(ContainerOld container : containers){
				migreBarCode(container);
				
				createSupportCollection(); 
			}
			Logger.info("Migration run end");
		
		}else{
			Logger.info("Migration run already execute !");
		}
		
		Logger.info("Migration Support finish");
		return ok("Migration Support Finish");
	}
	
	

	
	public static void migreBarCode(ContainerOld container) {
			
			if (container.support != null) {
			
				Container c = new Container();
				c.support = new ContainerSupport();
				
				if (container.support.barCode != null) {
					c.support.supportCode = container.support.barCode;
					
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
							DBQuery.is("code", container.code), 
							DBUpdate.unset("support.barCode") 
							.set("support.supportCode", c.support.supportCode) );

				}
				else {
					MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Container.class, 
							DBQuery.is("code", container.code), 
							DBUpdate.unset("support.barCode")  );

				}
					
			}
	}
	
	public static Result createSupportCollection() {
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();
		
		Map<String,Support> mapSupport = new HashMap<String, Support>();

		for(Container container:containers){

			if(container.support !=null){
				Support support=new Support();
				if(container.support.categoryCode!=null){
					support.categoryCode=container.support.categoryCode; }
				support.code=container.support.supportCode;
				support.state=new State();
				support.state.code="A";
				support.projectCodes=container.projectCodes;
				support.sampleCodes=container.sampleCodes;
				support.traceInformation=container.traceInformation;
				if(!mapSupport.containsKey(support.code)){
					mapSupport.put(support.code,support);
				}
				
			}
		}
	
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setCreationMode();
		InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME, new ArrayList<Support>(mapSupport.values()),contextValidation);

		if(contextValidation.hasErrors()){
			Logger.info("CreateSupportCollection ends with errors");
			return badRequest("CreateSupportCollection ends with errors");
		}else {
			Logger.info("CreateSupportCollection ends without errors");		
			return ok("CreateSupportCollection ends without errors");
		}

	}
	
	
	private static void backupContainerCollection() {
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" start");
		MongoDBDAO.save(CONTAINER_COLL_NAME_BCK, MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class).toList());
		Logger.info("\tCopie "+InstanceConstants.CONTAINER_COLL_NAME+" end");
		
	}

}

