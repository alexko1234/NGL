package controllers.runs.api;

import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;

public class Lanes extends Controller{
	
	final static Form<Lane> laneForm = form(Lane.class);
	
	public static Result save(String code){
		Form<Lane> filledForm = getFilledForm();
		
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
	
		private static Form<Lane> getFilledForm() {
			Form<Lane> filledForm;
			JsonNode json = request().body().asJson();			
			Lane laneInput = Json.fromJson(json, Lane.class);
			filledForm = laneForm.fill(laneInput);	//bindJson ne marche pas !						
			return filledForm;
		}
}
