package controllers.runs.api;

import javax.inject.Inject;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;


import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

//import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.authentication.Authenticated;
import fr.cea.ig.authorization.Authorized;
import fr.cea.ig.lfw.Historized;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.NGLContext;
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

// TODO: cleanup

public class RunTreatments extends RunsController{

	private final /*static*/ Form<Treatment> treatmentForm;// = form(Treatment.class);
	
	@Inject
	public RunTreatments(NGLContext ctx) {
		treatmentForm = ctx.form(Treatment.class);
	}
	
//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result list(String runCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", runCode));
		if (run != null) {
			return ok(Json.toJson(run.treatments));
		} else{
			return notFound();
		}		
	}
	
//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result get(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run != null) {
			return ok(Json.toJson(run.treatments.get(treatmentCode)));
		} else{
			return notFound();
		}		
	}
	
//	@Permission(value={"reading"})
	@Authenticated
	@Historized
	@Authorized.Read
	public /*static*/ Result head(String runCode, String treatmentCode){
		if(MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)))){
			return ok();
		}else{
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
	public /*static*/ Result save(String runCode){
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run == null) 
			return badRequest();
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
		
		Treatment treatment = filledForm.get();
		ctxVal.setCreationMode();
		ctxVal.putObject("level", Level.CODE.Run);
		ctxVal.putObject("run", run);
		treatment.validate(ctxVal);
		/*if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", runCode),
					DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));						
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(treatment));			
		} else {
			return badRequest(filledForm.errors-AsJson());			
		}*/
		if(!ctxVal.hasErrors()){
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.is("code", runCode),
					DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));						
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
	public /*static*/ Result update(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run == null)
			return badRequest(); // TODO: add message
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
		
		Treatment treatment = filledForm.get();
		if (treatmentCode.equals(treatment.code)) {
			ctxVal.setUpdateMode();
			ctxVal.putObject("level", Level.CODE.Run);
			ctxVal.putObject("run", run);
			
			treatment.validate(ctxVal);
			/*if(!ctxVal.hasErrors()){
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.is("code", runCode),
						DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));			
			}
			if (!filledForm.hasErrors()) {
				return ok(Json.toJson(treatment));			
			} else {
				return badRequest(filledForm.errors-AsJson());			
			}*/
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.is("code", runCode),
						DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));			
				return ok(Json.toJson(treatment));			
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}
		} else {
			return badRequest("treatment code are not the same");
		}
	}
	
//	@Permission(value={"writing"})
	@Authenticated
	@Historized
	@Authorized.Write
	//@Permission(value={"delete_treatments"})
	public /*static*/ Result delete(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run == null)
			return badRequest();

		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", runCode), DBUpdate.unset("treatments."+treatmentCode).set("traceInformation", getUpdateTraceInformation(run)));			
		return ok();		
	}		
}
