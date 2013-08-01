package controllers.runs.api;

import java.util.List;

import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.data.Form;
import static play.data.Form.form;
import play.libs.Json;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

import controllers.CommonController;



public class Lanes extends CommonController{
	
	final static Form<Lane> laneForm = form(Lane.class);
	
	public static Result save(String code){
		
		Form<Lane> filledForm = getFilledForm(laneForm, Lane.class);		
		
		if(!filledForm.hasErrors()) {
			Run run = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
			if(run == null){
				return notFound();
			}
			Lane laneValue = filledForm.get();
			BusinessValidationHelper.validateLane(filledForm.errors(), run,laneValue, Constants.RUN_ILLUMINA_COLL_NAME, null);
			if(!filledForm.hasErrors()) {
				Logger.debug("Insert lane OK :"+laneValue.number);
				//MongoDBDAO.createOrUpdateInArray(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,"code" , code, "lanes", "number", laneValue.number,laneValue);
				int laneNumber = laneValue.number;
				boolean isFind = false;
				for(int i = 0;run.lanes != null &&  i < run.lanes.size(); i++){
					Lane l = run.lanes.get(i);
					if(l.number.equals(laneNumber)){ 
						isFind = true;
						MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i, laneValue);
						break;
					}
				}
				if(!isFind){MongoDBDAO.updatePush(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes", laneValue);}
				
				filledForm = filledForm.fill(laneValue);
			}
		}
		
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(filledForm.get()));			
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}		
	}

	
	public static Result get(String code,Integer laneNumber){
		Run runValue = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		Lane laneValue = null;
		for(Lane lane:runValue.lanes) {
			if(lane.number.equals(laneNumber)){
				laneValue = lane;
			}
		}
		
		if(laneValue != null){
			return ok(Json.toJson(laneValue));				
		}else{
			return notFound();
		}		
	}
	
	public static Result delete(String code,Integer laneNumber) { 
		Run runValue = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		Lane laneValue = null;
		
		for(Lane lane:runValue.lanes) {
			if(lane.number.equals(laneNumber)){
				laneValue = lane;
				
				// set the lane to null	
				MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.and(DBQuery.is("code",code),DBQuery.is("lanes.number",laneNumber)),DBUpdate.unset("lanes.$"));
				break;
			}
		}
		// remove null
		MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",code),DBUpdate.pull("lanes",null));
		
		
		if(laneValue == null) {
			return notFound();
		}
		else{
			return ok();
		}
	}

}
