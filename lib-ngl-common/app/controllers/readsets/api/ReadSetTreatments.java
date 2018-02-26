package controllers.readsets.api;

import javax.inject.Inject;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;
import models.laboratory.common.description.Level;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.authorisation.Permission;
import controllers.history.UserHistory;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.NGLContext;

// TODO: cleanup

public class ReadSetTreatments extends ReadSetsController {

	private final /*static*/ Form<Treatment> treatmentForm; // = form(Treatment.class);
	
	@Inject
	public ReadSetTreatments(NGLContext ctx) {
		treatmentForm = ctx.form(Treatment.class);
	}
	
//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result list(String readSetCode){
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet != null) {
			return ok(Json.toJson(readSet.treatments));
		} else {
			return notFound();
		}		
	}
	
//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result get(String readSetCode, String treatmentCode){
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)));
		if (readSet != null) {
			return ok(Json.toJson(readSet.treatments.get(treatmentCode)));
		} else{
			return notFound();
		}		
	}
	
//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result head(String readSetCode, String treatmentCode){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)))){
			return ok();
		} else {
			return notFound();
		}
	}
	
//	@Permission(value={"writing"})	
	@Authenticated
	@Historized
	@Authorized.Write
	//@Permission(value={"creation_update_treatments"})
	// @BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	public /*static*/ Result save(String readSetCode){
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet == null)
			return badRequest();
		// WARN: this is supposed to be standard parser behavior in play 2.5
		/*else if(request().body().isMaxSizeExceeded()) {
			return badRequest("Max size exceeded");
		}*/
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
		
		Treatment treatment = filledForm.get();
		ctxVal.setCreationMode();
		ctxVal.putObject("level", Level.CODE.ReadSet);
		ctxVal.putObject("readSet", readSet);
		treatment.validate(ctxVal);
		/*if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", readSetCode),
					DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(readSet)));	
			
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(treatment));			
		} else {
			return badRequest(filledForm.errors-AsJson());			
		}*/
		if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", readSetCode),
					DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(readSet)));	
			return ok(Json.toJson(treatment));			
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

//	@Permission(value={"writing"})
	@Authenticated
	@Historized
	@Authorized.Write
	//@Permission(value={"creation_update_treatments"})
	// @BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	public /*static*/ Result update(String readSetCode, String treatmentCode){
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)));
		if (readSet == null)
			return badRequest();
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
		
		Treatment treatment = filledForm.get();
		if (treatmentCode.equals(treatment.code)) {
			ctxVal.setUpdateMode();
			ctxVal.putObject("level", Level.CODE.ReadSet);
			ctxVal.putObject("readSet", readSet);
			treatment.validate(ctxVal);
			/*if(!ctxVal.hasErrors()){
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.is("code", readSetCode),
						DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(readSet)));				
			}
			if (!filledForm.hasErrors()) {
				return ok(Json.toJson(treatment));			
			} else {
				return badRequest(filledForm.errors-AsJson());			
			}*/
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.is("code", readSetCode),
						DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(readSet)));				
				return ok(Json.toJson(treatment));			
			} else {
				//return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("treatment code are not the same");
		}
	}
	
	@Authenticated
	@Historized
	@Authorized.Write
//	@Permission(value={"writing"})	//@Permission(value={"delete_treatments"})
	public /*static*/ Result delete(String readSetCode, String treatmentCode){
		ReadSet readSet = MongoDBDAO.findOne(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", readSetCode), DBQuery.exists("treatments."+treatmentCode)));
		if (readSet == null)
			return badRequest();	
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", readSetCode), DBUpdate.unset("treatments."+treatmentCode).set("traceInformation", getUpdateTraceInformation(readSet)));			
		return ok();		
	}
	
//	@Permission(value={"writing"})
	@Authenticated
	@Historized
	@Authorized.Write
	public /*static*/ Result deleteAll(String readSetCode){
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet == null)
			return badRequest();
		MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", readSetCode), DBUpdate.unset("treatments").set("traceInformation", getUpdateTraceInformation(readSet)));			
		return ok();		
	}
}
