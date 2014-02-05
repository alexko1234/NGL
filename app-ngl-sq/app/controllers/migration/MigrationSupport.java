package controllers.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

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

public class MigrationSupport extends CommonController{

	public static Result migrationSupport(){
		Logger.info("Start point of Migration Support");

		Map<String,Support> migrationSupport = new HashMap<String, Support>();
		List<ContainerOld> containersOld = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, ContainerOld.class).toList();
		
		//rename property barCode
		for (ContainerOld container:containersOld) {
			
			if (container.support != null) {
			
				Container c = new Container();
				c.support = new ContainerSupport();
				
				//Logger.debug("container.support.barCode" + container.support.barCode);
				
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
		
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class).toList();

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
				if(!migrationSupport.containsKey(support.code)){
					migrationSupport.put(support.code,support);
				}
				
			}
		}

	
		ContextValidation contextValidation=new ContextValidation();
		contextValidation.setCreationMode();
		InstanceHelpers.save(InstanceConstants.SUPPORT_COLL_NAME, new ArrayList<Support>(migrationSupport.values()),contextValidation);

		
		if(contextValidation.hasErrors()){
			Logger.info("Migration finish with errors");
			return badRequest("Migration finish with errors");
		}else {
			Logger.info("Migration finish");		
			return ok("Migration Support Finish");
		}

	}

}
