package controllers.runs.api;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.migration.NGLContext;
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
public class RunTreatments extends RunsController {

	private final Form<Treatment> treatmentForm;
	
	@Inject
	public RunTreatments(NGLContext ctx) {
		treatmentForm = ctx.form(Treatment.class);
	}
	
	@Permission(value={"reading"})
	public Result list(String runCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", runCode));
		if (run == null)
			return notFound();
		return ok(Json.toJson(run.treatments));
	}	
	
	@Permission(value={"reading"})
	public Result get(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run == null)
			return notFound();
		return ok(Json.toJson(run.treatments.get(treatmentCode)));
	}
	
	@Permission(value={"reading"})
	public Result head(String runCode, String treatmentCode) {
		if(MongoDBDAO.checkObjectExist(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode))))
			return ok();
		return notFound();
	}

	@Permission(value={"writing"})	
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	public Result save(String runCode){
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
		if (ctxVal.hasErrors())
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.is("code", runCode),
				DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));						
		return ok(Json.toJson(treatment));			
	}

	@Permission(value={"writing"})	
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	public Result update(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run == null)
			return badRequest(); // TODO: add message
		
		Form<Treatment> filledForm = getFilledForm(treatmentForm, Treatment.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
		
		Treatment treatment = filledForm.get();
		if (!treatmentCode.equals(treatment.code))
			return badRequest("treatment code are not the same");
		ctxVal.setUpdateMode();
		ctxVal.putObject("level", Level.CODE.Run);
		ctxVal.putObject("run", run);

		treatment.validate(ctxVal);
		if (ctxVal.hasErrors())
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.is("code", runCode),
				DBUpdate.set("treatments."+treatment.code, treatment).set("traceInformation", getUpdateTraceInformation(run)));			
		return ok(Json.toJson(treatment));			
	}
	
	@Permission(value={"writing"})
	public Result delete(String runCode, String treatmentCode){
		Run run  = MongoDBDAO.findOne(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", runCode), DBQuery.exists("treatments."+treatmentCode)));
		if (run == null)
			return badRequest();

		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.is("code", runCode), DBUpdate.unset("treatments."+treatmentCode).set("traceInformation", getUpdateTraceInformation(run)));			
		return ok();		
	}
	
}
