package controllers.readsets.api;

import static play.data.Form.form;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import models.laboratory.common.description.Level;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;

public class ReadSetTreatments extends ReadSetsController{

	final static Form<Treatment> treatmentForm = form(Treatment.class);
	
	//@Permission(value={"reading"})
	public static Result list(String readSetCode){
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet != null) {
			return ok(Json.toJson(readSet.treatments));
		} else{
			return notFound();
		}		
	}
	
	//@Permission(value={"reading"})
	public static Result get(String readSetCode, String treatmentCode){
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)));
		if (readSet != null) {
			return ok(Json.toJson(readSet.treatments.get(treatmentCode)));
		} else{
			return notFound();
		}		
	}
	
	//@Permission(value={"reading"})
	public static Result head(String readSetCode, String treatmentCode){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)))){
			return ok();
		}else{
			return notFound();
		}
	}
	
	//@Permission(value={"creation_update_treatments"})
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result save(String readSetCode){
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet == null) {
			return badRequest();
		}else if(request().body().isMaxSizeExceeded()){
			return badRequest("Max size exceeded");
		}
		
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		Treatment treatment = filledForm.get();
		ctxVal.setCreationMode();
		ctxVal.putObject("level", Level.CODE.ReadSet);
		ctxVal.putObject("readSet", readSet);
		treatment.validate(ctxVal);
		if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", readSetCode),
					DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(readSet)));	
			
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(treatment));			
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}		
	}

	//@Permission(value={"creation_update_treatments"})
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public static Result update(String readSetCode, String treatmentCode){
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)));
		if (readSet == null) {
			return badRequest();
		}	
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		Treatment treatment = filledForm.get();
		if (treatmentCode.equals(treatment.code)) {
			ctxVal.setUpdateMode();
			ctxVal.putObject("level", Level.CODE.ReadSet);
			ctxVal.putObject("readSet", readSet);
			treatment.validate(ctxVal);
			if(!ctxVal.hasErrors()){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.is("code", readSetCode),
						DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(readSet)));				
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
	public static Result delete(String readSetCode, String treatmentCode){
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)));
		if (readSet == null) {
			return badRequest();
		}	
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", readSetCode), DBUpdate.unset("treatments."+treatmentCode).set("traceInformation", getUpdateTraceInformation(readSet)));			
		return ok();		
	}
	
	public static Result deleteAll(String readSetCode){
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet == null) {
			return badRequest();
		}
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", readSetCode), DBUpdate.unset("treatments").set("traceInformation", getUpdateTraceInformation(readSet)));			
		return ok();		
	}
	
	

}
