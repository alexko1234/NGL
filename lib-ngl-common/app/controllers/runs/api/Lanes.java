package controllers.runs.api;


import java.util.Date;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
//import play.Logger;
import play.data.Form;


import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import validation.run.instance.RunValidationHelper;

public class Lanes extends RunsController {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Lanes.class);
	
	private final Form<Lane> laneForm ;
	private final Form<Treatment> treatmentForm;
	private final Form<Valuation> valuationForm ;
	
	@Inject
	public Lanes(NGLContext ctx) {
		laneForm = ctx.form(Lane.class);
		treatmentForm = ctx.form(Treatment.class);
		valuationForm = ctx.form(Valuation.class);
	}
	
	@Permission(value={"reading"})
	public Result list(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (run == null || run.lanes == null) {
			return badRequest();
		}
		return ok(Json.toJson(run.lanes));		
	}
	
//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result get(String code, Integer laneNumber) {
		Run run = getRun(code, laneNumber);
		if(run == null){
			return badRequest();
		}
		if(null != run.lanes){
			for(Lane lane: run.lanes) {
				if(lane.number.equals(laneNumber)) {
					return ok(Json.toJson(lane));	
				}
			}
		}		
		return notFound();
	}

//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result head(String code, Integer laneNumber){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", code), DBQuery.is("lanes.number", laneNumber)))){			
			return ok();					
		}else{
			return notFound();
		}
	}
	
//	@Permission(value={"writing"})
	@Authenticated
	@Historized
	@Authorized.Write
	//@Permission(value={"creation_update_run_lane"})
	public /*static*/ Result save(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (run == null)
			return badRequest();
				
		Form<Lane> filledForm = getFilledForm(laneForm, Lane.class);			
		Lane laneValue = filledForm.get();
		
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		ctxVal.putObject("run", run);
		ctxVal.setCreationMode();
		laneValue.validate(ctxVal);
		if (!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", code),
					DBUpdate.push("lanes", laneValue).set("traceInformation", getUpdateTraceInformation(run)));
			return ok(Json.toJson(laneValue));	
		} else {
			// Logger.error(filledForm.errors-AsJson().toString());
			logger.error(NGLContext._errorsAsJson(ctxVal.getErrors()).toString());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}		
	}

//	@Permission(value={"writing"})	
	@Authenticated
	@Historized
	@Authorized.Write
	//@Permission(value={"creation_update_run_lane"})
	public /*static*/ Result update(String code, Integer laneNumber){
		Run run = getRun(code, laneNumber);
		if (run == null)
			return badRequest();

		Form<Lane> filledForm = getFilledForm(laneForm, Lane.class);			
		Lane laneValue = filledForm.get();
		if (laneNumber.equals(laneValue.number)) {				
//			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
			ctxVal.putObject("run", run);
			ctxVal.setUpdateMode();
			laneValue.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", code), DBQuery.is("lanes.number", laneNumber)),
						DBUpdate.set("lanes.$", laneValue).set("traceInformation", getUpdateTraceInformation(run))); 
				return ok(Json.toJson(laneValue));
			} else {
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("lane number are not the same");
		}
	}
	
//	@Permission(value={"writing"})	
	@Authenticated
	@Historized
	@Authorized.Write
	public /*static*/ Result delete(String code, Integer laneNumber) { 
		Run run = getRun(code, laneNumber);
		if (run == null)
			return badRequest();
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.and(DBQuery.is("code",code),DBQuery.is("lanes.number",laneNumber)), DBUpdate.unset("lanes.$"));
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.is("code",code), DBUpdate.pull("lanes", null).set("traceInformation", getUpdateTraceInformation(run)));
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode", code), DBQuery.is("laneNumber",laneNumber)));
		return ok();
	}
	
//	@Permission(value={"writing"})
	@Authenticated
	@Historized
	@Authorized.Write
	public /*static*/ Result deleteByRunCode(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (run == null)
			return badRequest();

		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.is("code",code), DBUpdate.unset("lanes").set("traceInformation", getUpdateTraceInformation(run)));
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode", code)));
		return ok();
	}
	
//	@Permission(value={"writing"})
	@Authenticated
	@Historized
	@Authorized.Write
	//@Permission(value={"valuation_run_lane"})
	public /*static*/ Result valuation(String code, Integer laneNumber){
		Run run = getRun(code, laneNumber);
		if (run == null)
			return badRequest();

		Form<Valuation> filledForm = getFilledForm(valuationForm, Valuation.class);
		Valuation valuation = filledForm.get();
		valuation.date = new Date();
		valuation.user = getCurrentUser();
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		ctxVal.putObject("run", run);
		ctxVal.setUpdateMode();
		RunValidationHelper.validateValuation(run.typeCode, valuation, ctxVal);			
		if(!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", code), DBQuery.is("lanes.number", laneNumber)),
					DBUpdate.set("lanes.$.valuation", valuation).set("traceInformation", getUpdateTraceInformation(run))); 
			return get(code, laneNumber);
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
		
	}	

}
