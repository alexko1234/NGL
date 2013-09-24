package controllers.runs.api;

import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.data.Form;
import static play.data.Form.form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;
import controllers.CommonController;



public class Lanes extends CommonController{
	
	final static Form<Lane> laneForm = form(Lane.class);
	
	public static Result save(String code){
		
		Form<Lane> filledForm = getFilledForm(laneForm, Lane.class);		
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		if(!filledForm.hasErrors()) {
			Run run = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
			if(run == null){
				return notFound();
			}
			Lane laneValue = filledForm.get();
			
			ctxVal.putObject("run", run);
			int laneNumber = laneValue.number;
			int index = 0;
			for(int i = 0;run.lanes != null &&  i < run.lanes.size(); i++){
				ctxVal.setRootKeyName("lanes"+"["+index+++"]");
				if(run.lanes.get(i).number.equals(laneNumber)){
					break;
				}
			}
			laneValue.validate(ctxVal);
			
			
			if(!filledForm.hasErrors()) {
				//Logger.debug("Insert lane OK :"+laneValue.number);
				//MongoDBDAO.createOrUpdateInArray(Constants.RUN_ILLUMINA_COLL_NAME,Run.class,"code" , code, "lanes", "number", laneValue.number,laneValue);
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
		
		if (!filledForm.hasErrors() ) {
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
				MongoDBDAO.update(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.and(DBQuery.is("code",code),DBQuery.is("lanes.number",laneNumber)),DBUpdate.unset("lanes.$"));
				break;
			}
		}
		// remove null
		MongoDBDAO.update(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",code),DBUpdate.pull("lanes",null));
		
		
		if(laneValue == null) {
			return notFound();
		}
		else{
			return ok();
		}
	}

}
