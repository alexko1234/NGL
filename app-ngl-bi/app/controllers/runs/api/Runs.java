package controllers.runs.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.WriteResult;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import controllers.utils.FormUtils;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import controllers.utils.FormUtils;
/**
 * Controller around Run object
 * @authors galbini, dnoisett
 *
 */
public class Runs extends CommonController {
	
	final static Form<Run> runForm = form(Run.class);
	final static DynamicForm listForm = new DynamicForm();
	final static Form<Treatment> treatmentForm = form(Treatment.class);
	
	public static Result list(){
		DynamicForm filledForm =  listForm.bindFromRequest();
		MongoDBResult<Run> results = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, getQuery(filledForm)) 
				.sort(DatatableHelpers.getOrderBy(filledForm), FormUtils.getMongoDBOrderSense(filledForm))
				.page(DatatableHelpers.getPageNumber(filledForm), DatatableHelpers.getNumberRecordsPerPage(filledForm)); 
		List<Run> runs = results.toList();
		return ok(Json.toJson(new DatatableResponse<Run>(runs, results.count())));
	}
	
	private static Query getQuery(DynamicForm filledForm) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(filledForm.get("stateCode"))) { //all
			queries.add(DBQuery.is("state.code", filledForm.get("stateCode")));
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		
		return query;
	}
	
	public static Result get(String code) {
		Run runValue = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (runValue != null) {		
			return ok(Json.toJson(runValue));					
		} else {
			return notFound();
		}
	}
	
	public static Result head(String code){
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code)){			
			return ok();					
		}else{
			return notFound();
		}
	}
	
	
	public static Result save() {
		Form<Run> filledForm = getFilledForm(runForm, Run.class);
		Run runValue = filledForm.get();
			
		if (null == runValue._id) { 
			runValue.traceInformation = new TraceInformation();
			runValue.traceInformation.setTraceInformation("ngsrg");
		} else {
			return badRequest("use PUT method to update the readset");
		}
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		ctxVal.setCreationMode();
		runValue.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			runValue = MongoDBDAO.save(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runValue);
			return ok(Json.toJson(runValue));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	
	public static Result update(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (run == null) {
			return badRequest("Run with code "+code+" not exist");
		}
		
		Form<Run> filledForm = getFilledForm(runForm, Run.class);
		Run runValue = filledForm.get();
		if (code.equals(runValue.code)) {
			if(null != runValue.traceInformation){
				runValue.traceInformation.setTraceInformation("ngsrg");
			}else{
				Logger.error("traceInformation is null !!");
			}
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 			
			ctxVal.setUpdateMode();
			runValue.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, runValue);
				return ok(Json.toJson(run));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
		}else{
			return badRequest("run code are not the same");
		}
		
	}
	
	
	
	public static Result delete(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (run == null) {
			return badRequest();
		}		
		MongoDBDAO.delete(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run);	
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.is("runCode", code));
		
		return ok();
	}
	
	public static Result workflow(String code, String stateCode){
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if (run == null) {
			return badRequest();
		}
		DynamicForm f = Form.form();
		ContextValidation ctxVal = new ContextValidation(f.errors());
		Workflows.setRunState(ctxVal, run, stateCode);
		if (!ctxVal.hasErrors()) {
			return ok();
		}else {
			return badRequest(f.errorsAsJson());
		}
	}
	
	@Deprecated
	public static Result dispatch(String code) {
		Run run = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, code);		
		if (run != null) {
			JsonNode json = request().body().asJson();
			Logger.info("Dispatch run : "+code);
			boolean dispatch = json.get("dispatch").asBoolean();
			MongoDBDAO.updateSet(InstanceConstants.RUN_ILLUMINA_COLL_NAME, run, "dispatch", dispatch);
			//TODO ReadSet dispatch
		} else {
			return badRequest();
		}		
		return ok();	
	}
	
}
