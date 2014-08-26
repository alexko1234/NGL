package controllers.analyzes.api;

import java.util.Collection;

import org.springframework.stereotype.Controller;

import models.laboratory.common.description.Level;
import models.laboratory.run.instance.Analysis;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.SubDocumentController;
@Controller
public class AnalysisTreatments extends SubDocumentController<Analysis, Treatment>{

	public AnalysisTreatments() {
		super(InstanceConstants.ANALYSIS_COLL_NAME, Analysis.class, Treatment.class);
	}
	
	protected Query getSubObjectQuery(String parentCode, String code){
		return DBQuery.and(DBQuery.is("code", parentCode), DBQuery.exists("treatments."+code));
	}
	
	protected Collection<Treatment> getSubObjects(Analysis object){
		return object.treatments.values();
	}
	
	protected Treatment getSubObject(Analysis object, String code){
		return object.treatments.get(code);
	}
	
	
	//@Permission(value={"creation_update_treatments"})
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public Result save(String parentCode){
		Analysis objectInDB = getObject(parentCode);
		if (objectInDB == null) {
			return notFound();
		}else if(request().body().isMaxSizeExceeded()){
			return badRequest("Max size exceeded");
		}
		
		Form<Treatment> filledForm = getSubFilledForm();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		Treatment inputTreatment = filledForm.get();
		ctxVal.setCreationMode();
		ctxVal.putObject("level", Level.CODE.Analysis);
		ctxVal.putObject("analysis", objectInDB);
		inputTreatment.validate(ctxVal);
		if(!ctxVal.hasErrors()){
			updateObject(DBQuery.is("code", parentCode), 
					DBUpdate.set("treatments."+inputTreatment.code, inputTreatment)
					.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
			return get(parentCode, inputTreatment.code);
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}		
	}

	//@Permission(value={"creation_update_treatments"})
	@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	public Result update(String parentCode, String code){
		Analysis objectInDB = getObject(getSubObjectQuery(parentCode, code));
		if (objectInDB == null) {
			return notFound();			
		}	
		
		Form<Treatment> filledForm = getSubFilledForm();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		Treatment inputTreatment = filledForm.get();
		if (code.equals(inputTreatment.code)) {
			ctxVal.setUpdateMode();
			ctxVal.putObject("level", Level.CODE.Analysis);
			ctxVal.putObject("analysis", objectInDB);
			inputTreatment.validate(ctxVal);
			if(!ctxVal.hasErrors()){
				updateObject(DBQuery.is("code", parentCode), 
						DBUpdate.set("treatments."+inputTreatment.code, inputTreatment)
						.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
				return get(parentCode, code);
			} else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("treatment code are not the same");
		}
	}
	
	//@Permission(value={"delete_treatments"})
	public Result delete(String parentCode, String code){
		Analysis objectInDB = getObject(getSubObjectQuery(parentCode, code));
		if (objectInDB == null) {
			return notFound();			
		}	
		updateObject(DBQuery.is("code", parentCode), 
				DBUpdate.unset("treatments."+code)
				.set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
		return ok();		
	}
	
	public  Result deleteAll(String parentCode){
		Analysis objectInDB = getObject(parentCode);
		if (objectInDB == null) {
			return notFound();
		}
		updateObject(DBQuery.is("code", parentCode), 
				DBUpdate.unset("treatments").set("traceInformation", getUpdateTraceInformation(objectInDB.traceInformation)));
		return ok();
	}
	
	

}
