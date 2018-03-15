package controllers.runs.api;


import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.description.Level;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import validation.ContextValidation;

// TODO: cleanup

public class LaneTreatments extends RunsController{

	private final Form<Treatment> treatmentForm;

	@Inject
	public LaneTreatments(NGLContext ctx) {
		treatmentForm = ctx.form(Treatment.class);
	}
	
	@Permission(value={"reading"})
	public Result list(String runCode, Integer laneNumber){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), 
						DBQuery.elemMatch("lanes",DBQuery.is("number", laneNumber))));
		if (run != null) {
			return ok(Json.toJson(getLane(run, laneNumber).treatments));
		} else{
			return notFound();
		}		
	}
	
	@Permission(value={"reading"})
	public Result get(String runCode, Integer laneNumber, String treatmentCode){
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
	
	@Permission(value={"reading"})
	public Result head(String runCode, Integer laneNumber, String treatmentCode){
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

	@Permission(value={"writing"})	
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	public Result save(String runCode, Integer laneNumber){
		Run run = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)));
		if(run == null){
			return badRequest();
		}	
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
		Treatment treatment = filledForm.get();
		
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ctxVal.setCreationMode();
		ctxVal.putObject("level", Level.CODE.Lane);
		ctxVal.putObject("run", run);
		ctxVal.putObject("lane", getLane(run, laneNumber));
		treatment.validate(ctxVal);
		if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)),
					DBUpdate.set("lanes.$.treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));	
			
			
			
			return ok(Json.toJson(treatment));
		} else {
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
			
	}
	
	@Permission(value={"writing"})
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	public Result update(String runCode, Integer laneNumber, String treatmentCode){
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
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
			ctxVal.setUpdateMode();
			ctxVal.putObject("level", Level.CODE.Lane);
			ctxVal.putObject("run", run);
			ctxVal.putObject("lane", getLane(run, laneNumber));
			treatment.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", runCode), DBQuery.is("lanes.number", laneNumber)),
						DBUpdate.set("lanes.$.treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));
				return ok(Json.toJson(treatment));
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("treatment code are not the same");
		}		
	}
	
	@Permission(value={"writing"})
	public Result delete(String runCode,  Integer laneNumber, String treatmentCode){
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
				DBUpdate.unset("lanes.$.treatments."+treatmentCode).set("traceInformation", getUpdateTraceInformation(run)));					
		return ok();		
	}	
	
	
	private Lane getLane(Run run, Integer laneNumber) {
		if(null != run.lanes){
			for (Lane lane : run.lanes) {
				if (lane.number.equals(laneNumber)) {
					return lane;
				}		
			}
		}
		throw new RuntimeException("Lane number does not exist "+run.code+" / "+laneNumber);
	}
}
