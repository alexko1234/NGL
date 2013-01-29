package controllers.run;

import java.util.List;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

import models.laboratory.container.instance.Basket;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import fr.cea.ig.MongoDBDAO;
import play.mvc.Controller;
import play.mvc.Result;

public class Deletions  extends Controller {
	
	public static Result removeReadsets(String code, String format){
		Run run  = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(run==null){
			return badRequest();
		}
		for(int i=0;run.lanes!=null && i<run.lanes.size();i++){
			for(int j=0;run.lanes.get(i).readsets != null && j<run.lanes.get(i).readsets.size();j++){
				System.out.println("lanes."+i+".readsets."+j);
				MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Basket.class,DBQuery.is("code",code),DBUpdate.unset("lanes."+i+".readsets."+j));
			}
			MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Basket.class,DBQuery.is("code",code),DBUpdate.pull("lanes."+i+".readsets",null));
			
		}
		
		return ok();
	}
	
	public static Result removeFiles(String code,String format){
		Run run  = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(run==null){
			return badRequest();
		}
		for(int i=0;run.lanes!=null && i<run.lanes.size();i++){
			for(int j=0;run.lanes.get(i).readsets != null && j<run.lanes.get(i).readsets.size();j++){
				for(int k=0;run.lanes.get(i).readsets.get(j).files!=null && k<run.lanes.get(i).readsets.get(j).files.size();k++){
					MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Basket.class,DBQuery.is("code",code),DBUpdate.unset("lanes."+i+".readsets."+j+".files."+k));
				}
				MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Basket.class,DBQuery.is("code",code),DBUpdate.pull("lanes."+i+".readsets."+j+".files",null));
				
			}
			
		}
		
		return ok();
	}

}
