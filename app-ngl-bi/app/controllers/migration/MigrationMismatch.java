package controllers.migration;

import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import lims.cns.dao.LimsAbandonDAO;
import lims.models.experiment.illumina.RunSolexa;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import play.Logger;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;

public class MigrationMismatch extends CommonController{
	/*
	public static Result migration(){
		List<RunSolexa> runs = Spring.getBeanOfType(LimsAbandonDAO.class).findRunMismatch();
		int t = 0, f = 0;
		for(RunSolexa rs : runs){
			
			if(MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", rs.runslnom).exists("treatments.ngsrg.default.mismatch.value"))){
				//Logger.info("update run : "+rs.runslnom+" = "+((rs.mismatch == 1)?true:false));
				
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.is("code", rs.runslnom).exists("treatments.ngsrg.default.mismatch.value"), 
						DBUpdate.set("treatments.ngsrg.default.mismatch.value", ((rs.mismatch == 1)?true:false)));
				
				
				if(((rs.mismatch == 1)?true:false))t++;
				else f++;
			}else{
				Logger.debug("Not found ="+rs.runslnom);
			}
			
		}
		Logger.debug("true = "+t+" / false = "+f+" total = "+runs.size());
		
		return ok();
	}
	*/

}
