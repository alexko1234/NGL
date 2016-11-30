package controllers.migration.cns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processes.instance.*;
import models.laboratory.processes.instance.Process;
import models.utils.InstanceConstants;



import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;



import play.Logger;
import play.Logger.ALogger;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class MigrationSpriSelect extends CommonController{

	protected static ALogger logger=Logger.of("MigrationSpriSelect");

	public static Result migration() {
				migrationSpriSelect();
				return ok("Migration Spri Select Finish");
	}
	
	private static void migrationSpriSelect() {
		
		List<Container> containers=MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Container.class, DBQuery.is("contents.properties.expectedSize.value", "ss0.6/0.53").in("fromTransformationTypeCodes","sizing")).toList();
		
		Set<String> experimentCodes=new HashSet<String>();

		//Update Container
		for(Container c:containers){
			experimentCodes.addAll(c.fromTransformationCodes);
			if(c.fromTransformationTypeCodes.contains("sizing") && c.fromTransformationTypeCodes.size()==1 ){
				String newCode=c.fromTransformationCodes.iterator().next().replace("SIZING", "SPRI-SELECT");
				c.fromTransformationCodes.clear();
				c.fromTransformationCodes.add(newCode);
				c.fromTransformationTypeCodes.clear();
				c.fromTransformationTypeCodes.add("spri-select");
				c.contents.forEach(content->{
					content.properties.get("libProcessTypeCode").value="DC";
				});
				Logger.debug("Container "+c.code+" udpate");
				
				MongoDBDAO.save(InstanceConstants.CONTAINER_COLL_NAME,c);
				
			}else {
				logger.error("Particular container "+c.code+" typeCodes"+c.fromTransformationTypeCodes+ ", codes "+c.fromTransformationCodes);
			}
		}
		Logger.debug("nb experiments "+experimentCodes.size());
		//Update Experiment and Process
		experimentCodes.forEach(exp->{			
			if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.EXPERIMENT_COLL_NAME,Experiment.class,exp)){
				String newCode=exp.replace("SIZING", "SPRI-SELECT");
				Logger.debug("Experiment "+exp+" replace by new code "+newCode);
				
				MongoDBDAO.update(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, DBQuery.is("code",exp),DBUpdate.set("typeCode","spri-select").set("code", newCode).set("protocolCode","spri_select"));
				
				List<Process> processes=MongoDBDAO.find(InstanceConstants.PROCESS_COLL_NAME, Process.class,DBQuery.in("experimentCodes",exp)).toList();
				List<String> processCodes=processes.stream().map(p->p.code).collect(Collectors.toList());
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code",processCodes),DBUpdate.push("experimentCodes", newCode),true);
				MongoDBDAO.update(InstanceConstants.PROCESS_COLL_NAME, Process.class, DBQuery.in("code",processCodes),DBUpdate.pull("experimentCodes", exp),true);
				
			}else {
				logger.error("Experiment "+exp+" not exists");
			}
		});
	};
	
}
