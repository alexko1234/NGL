package controllers.runs.api;

import static play.data.Form.form;
import models.laboratory.common.description.Level;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;

public class LaneTreatments extends CommonController{

	final static Form<Treatment> treatmentForm = form(Treatment.class);

	//@Permission(value={"reading"})
	public static Result list(String runCode, Integer laneNumber){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), 
						DBQuery.elemMatch("lanes",DBQuery.is("number", laneNumber))));
		if (run != null) {
			return ok(Json.toJson(getLane(run, laneNumber).treatments));
		} else{
			return notFound();
		}		
	}
	
	//@Permission(value={"reading"})
	public static Result get(String runCode, Integer laneNumber, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), 
						DBQuery.elemMatch("lanes", 
								DBQuery.and(
										DBQuery.is("number", laneNumber),
										DBQuery.exists("treatments."+treatmentCode)))));
		if (run != null) {
			return ok(Json.toJson(getLane(run, laneNumber).treatments.get(treatmentCode)));
		} else{
			return notFound();
		}		
	}
	
	//@Permission(value={"reading"})
	public static Result head(String runCode, Integer laneNumber, String treatmentCode){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), 
						DBQuery.elemMatch("lanes", 
								DBQuery.and(
										DBQuery.is("number", laneNumber),
										DBQuery.exists("treatments."+treatmentCode)))))){
			return ok();
		}else{
			return notFound();
		}
	}

	//@Permission(value={"creation_update_treatments"})
	public static Result save(String runCode, Integer laneNumber){
		Run run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)));
		if(run == null){
			return badRequest();
		}	
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
		Treatment treatment = filledForm.get();
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		ctxVal.setCreationMode();
		ctxVal.putObject("level", Level.CODE.Lane);
		ctxVal.putObject("run", run);
		ctxVal.putObject("lane", getLane(run, laneNumber));
		treatment.validate(ctxVal);
		if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)),
					DBUpdate.set("lanes.$.treatments."+treatment.code, treatment));	
			
			
			
			return ok(Json.toJson(treatment));
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}
			
	}
	
	//@Permission(value={"creation_update_treatments"})
	public static Result update(String runCode, Integer laneNumber, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), 
						DBQuery.elemMatch("lanes", 
								DBQuery.and(
										DBQuery.is("number", laneNumber),
										DBQuery.exists("treatments."+treatmentCode)))));
		if(run == null){
			return badRequest();
		}	
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
		Treatment treatment = filledForm.get();
		if (treatmentCode.equals(treatment.code)) {
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
			ctxVal.setUpdateMode();
			ctxVal.putObject("level", Level.CODE.Lane);
			ctxVal.putObject("run", run);
			ctxVal.putObject("lane", getLane(run, laneNumber));
			treatment.validate(ctxVal);
			if(!ctxVal.hasErrors()){
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)),
						DBUpdate.set("lanes.$.treatments."+treatment.code, treatment));
				return ok(Json.toJson(treatment));
			} else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("treatment code are not the same");
		}		
	}
	
	//@Permission(value={"delete_treatments"})
	public static Result delete(String runCode,  Integer laneNumber, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), 
						DBQuery.elemMatch("lanes", 
								DBQuery.and(
										DBQuery.is("number", laneNumber),
										DBQuery.exists("treatments."+treatmentCode)))));
		if(run == null){
			return badRequest();
		}	
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)),
				DBUpdate.unset("lanes.$.treatments."+treatmentCode));					
		return ok();		
	}	
	
	
	private static Lane getLane(Run run, Integer laneNumber) {
		for (Lane lane : run.lanes) {
			if (lane.number.equals(laneNumber)) {
				return lane;
			}		
		}
		throw new RuntimeException("Lane number does not exist "+run.code+" / "+laneNumber);
	}
}
