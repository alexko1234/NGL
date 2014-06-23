package controllers.runs.api;


import java.util.Date;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import play.Logger;
import play.data.Form;
import static play.data.Form.form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;
import fr.cea.ig.MongoDBDAO;
import controllers.CommonController;
import controllers.authorisation.Permission;



public class Lanes extends RunsController{
	
	final static Form<Lane> laneForm = form(Lane.class);
	final static Form<Treatment> treatmentForm = form(Treatment.class);
	final static Form<Valuation> valuationForm = form(Valuation.class);
	
	
	
	//@Permission(value={"reading"})
	public static Result list(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(run == null){
			return badRequest();
		}
		return ok(Json.toJson(run.lanes));		
	}
	
	//@Permission(value={"reading"})
	public static Result get(String code, Integer laneNumber) {
		Run run = getRun(code, laneNumber);
		if(run == null){
			return badRequest();
		}
		for(Lane lane: run.lanes) {
			if(lane.number.equals(laneNumber)) {
				return ok(Json.toJson(lane));	
			}
		}
		return notFound();
	}

	//@Permission(value={"reading"})
	public static Result head(String code, Integer laneNumber){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", code), DBQuery.is("lanes.number", laneNumber)))){			
			return ok();					
		}else{
			return notFound();
		}
	}
	
	//@Permission(value={"creation_update_run_lane"})
	public static Result save(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(run == null){
			return badRequest();
		}
				
		Form<Lane> filledForm = getFilledForm(laneForm, Lane.class);			
		Lane laneValue = filledForm.get();
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.putObject("run", run);
		ctxVal.setCreationMode();
		laneValue.validate(ctxVal);
		
		if(!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", code),
					DBUpdate.push("lanes", laneValue).set("traceInformation", getUpdateTraceInformation(run)));
			return ok(Json.toJson(laneValue));	
		} else {
			Logger.error(filledForm.errorsAsJson().toString());
			return badRequest(filledForm.errorsAsJson());
		}		
	}

	
	//@Permission(value={"creation_update_run_lane"})
	public static Result update(String code, Integer laneNumber){
		Run run = getRun(code, laneNumber);
		if(run == null){
			return badRequest();
		}
		Form<Lane> filledForm = getFilledForm(laneForm, Lane.class);			
		Lane laneValue = filledForm.get();
		if (laneNumber.equals(laneValue.number)) {				
			ContextValidation ctxVal = new ContextValidation(filledForm.errors());
			ctxVal.putObject("run", run);
			ctxVal.setUpdateMode();
			laneValue.validate(ctxVal);
			
			if(!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", code), DBQuery.is("lanes.number", laneNumber)),
						DBUpdate.set("lanes.$", laneValue).set("traceInformation", getUpdateTraceInformation(run))); 
				return ok(Json.toJson(laneValue));
			} else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			return badRequest("lane number are not the same");
		}
	}
		
	public static Result delete(String code, Integer laneNumber) { 
		Run run = getRun(code, laneNumber);
		if(run == null){
			return badRequest();
		}
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.and(DBQuery.is("code",code),DBQuery.is("lanes.number",laneNumber)), DBUpdate.unset("lanes.$"));
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.is("code",code), DBUpdate.pull("lanes", null).set("traceInformation", getUpdateTraceInformation(run)));
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode", code), DBQuery.is("laneNumber",laneNumber)));
		return ok();
	
	}
	
	public static Result deleteByRunCode(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (run==null) {
			return badRequest();
		}
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.is("code",code), DBUpdate.unset("lanes").set("traceInformation", getUpdateTraceInformation(run)));
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode", code)));
		return ok();
	}
	
	
	//@Permission(value={"valuation_run_lane"})
	public static Result valuation(String code, Integer laneNumber){
		Run run = getRun(code, laneNumber);
		if(run == null){
			return badRequest();
		}
		Form<Valuation> filledForm = getFilledForm(valuationForm, Valuation.class);
		Valuation valuation = filledForm.get();
		valuation.date = new Date();
		valuation.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.putObject("run", run);
		ctxVal.setUpdateMode();
		RunValidationHelper.validateValuation(run.typeCode, valuation, ctxVal);			
		if(!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.is("lanes.number", laneNumber)),
					DBUpdate.set("lanes.$.valuation", valuation).set("traceInformation", getUpdateTraceInformation(run))); 
			return get(code, laneNumber);
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
		
	}	

}
