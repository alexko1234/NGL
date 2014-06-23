package controllers.runs.api;

import static play.data.Form.form;
import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import models.laboratory.common.description.Level;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;

public class RunTreatments extends RunsController{

	final static Form<Treatment> treatmentForm = form(Treatment.class);
	
	//@Permission(value={"reading"})
	public static Result list(String runCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", runCode));
		if (run != null) {
			return ok(Json.toJson(run.treatments));
		} else{
			return notFound();
		}		
	}
	
	//@Permission(value={"reading"})
	public static Result get(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run != null) {
			return ok(Json.toJson(run.treatments.get(treatmentCode)));
		} else{
			return notFound();
		}		
	}
	
	//@Permission(value={"reading"})
	public static Result head(String runCode, String treatmentCode){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)))){
			return ok();
		}else{
			return notFound();
		}
	}

	//@Permission(value={"creation_update_treatments"})
	public static Result save(String runCode){
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run==null) {
			return badRequest();
		}	
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		Treatment treatment = filledForm.get();
		ctxVal.setCreationMode();
		ctxVal.putObject("level", Level.CODE.Run);
		ctxVal.putObject("run", run);
		treatment.validate(ctxVal);
		if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", runCode),
					DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));						
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(treatment));			
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}		
	}

	//@Permission(value={"creation_update_treatments"})
	public static Result update(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run==null) {
			return badRequest();
		}	
		
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		Treatment treatment = filledForm.get();
		if (treatmentCode.equals(treatment.code)) {
			ctxVal.setUpdateMode();
			ctxVal.putObject("level", Level.CODE.Run);
			ctxVal.putObject("run", run);
			
			treatment.validate(ctxVal);
			if(!ctxVal.hasErrors()){
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.is("code", runCode),
						DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));			
			}
			if (!filledForm.hasErrors()) {
				return ok(Json.toJson(treatment));			
			} else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("treatment code are not the same");
		}
	}
	
	//@Permission(value={"delete_treatments"})
	public static Result delete(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run==null) {
			return badRequest();
		}	
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", runCode), DBUpdate.unset("treatments."+treatmentCode).set("traceInformation", getUpdateTraceInformation(run)));			
		return ok();		
	}		
}
