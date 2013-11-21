package controllers.readsets.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.WriteResult;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.ContextValidation;
import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;



public class ReadSets extends CommonController{

	final static Form<ReadSet> readSetForm = form(ReadSet.class);
	final static DynamicForm form = new DynamicForm();
	
	//@Permission(value={"reading"})
	public static Result list() {
		Query q = getQuery();
		if(null != q){
			List<ReadSet> readSetValues = MongoDBDAO.find(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, q).toList();
			if (readSetValues != null) {		
				return ok(Json.toJson(readSetValues));					
			} else {
				return notFound();
			}
		}else{
			return badRequest("missing parameters");
		}
		
	}
	
	private static Query getQuery() {
		DynamicForm inputForm = form.bindFromRequest();
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(inputForm.get("runCode"))) { //all
			queries.add(DBQuery.is("runCode", inputForm.get("runCode")));
		}
		
		if (StringUtils.isNotBlank(inputForm.get("laneNumber"))) { //all
			queries.add(DBQuery.is("laneNumber", Integer.valueOf(inputForm.get("laneNumber"))));
		}
		
		if (StringUtils.isNotBlank(inputForm.get("projectCode"))) { //all
			queries.add(DBQuery.is("projectCode", inputForm.get("projectCode")));
		}
		
		if (StringUtils.isNotBlank(inputForm.get("sampleCode"))) { //all
			queries.add(DBQuery.is("sampleCode", inputForm.get("sampleCode")));
		}
		
		if (StringUtils.isNotBlank(inputForm.get("stateCode"))) { //all
			queries.add(DBQuery.is("state.code", inputForm.get("stateCode")));
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		
		return query;
	}
	
	//@Permission(value={"reading"})
	public static Result get(String readSetCode) {
		ReadSet readSet =  MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);		
		if(readSet != null) {
			return ok(Json.toJson(readSet));	
		} 		
		else {
			return notFound();
		}		
	}
	
	//@Permission(value={"reading"})
	public static Result head(String readSetCode) {
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode)){			
			return ok();					
		}else{
			return notFound();
		}	
	}
	
	
	public static Result save(){
		
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		ReadSet readsetValue = filledForm.get();
		
		if (null == readsetValue._id) { 
			readsetValue.traceInformation = new TraceInformation();
			readsetValue.traceInformation.setTraceInformation("ngsrg");
		} else {
			return badRequest("use PUT method to update the run");
		}
		
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.setCreationMode();
		readsetValue.validate(ctxVal);	
		
		if (!ctxVal.hasErrors()) {
			readsetValue = MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readsetValue);
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", readsetValue.runCode), DBQuery.is("lanes.number", readsetValue.laneNumber)), 
					DBUpdate.push("lanes.$.readSetCodes", readsetValue.code));		
			return ok(Json.toJson(readsetValue));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	
	
	public static Result update(String readSetCode){
		ReadSet readSet =  MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
		if(readSet == null) {
			return badRequest("ReadSet with code "+readSetCode+" does not exist");
		}
		
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		ReadSet readsetValue = filledForm.get();
		if (readsetValue.code.equals(readSetCode)) {
			if(null != readsetValue.traceInformation){
				readsetValue.traceInformation.setTraceInformation("ngsrg");
			}else{
				Logger.error("traceInformation is null !!");
			}
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
			ctxVal.setUpdateMode();
			readsetValue.validate(ctxVal);
			
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readsetValue);
				return ok(Json.toJson(filledForm.get()));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("readset code are not the same");
		}		
	}
		
	//@Permission(value={"delete_readset"})
	public static Result delete(String readSetCode) { 
		ReadSet readSet = MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
		if (readSet == null) {
			return badRequest("Readset with code "+readSetCode+" does not exist !");
		}		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
				DBQuery.and(DBQuery.is("code",readSet.runCode),DBQuery.is("lanes.number",readSet.laneNumber)), 
				DBUpdate.pull("lanes.$.readSetCodes", readSet.code));
		MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, readSet.code);
		return ok();
	}
	
	//@Permission(value={"delete_readset"})
	public static Result deleteByRunCode(String runCode) {
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run==null) {
			return badRequest();
		}
		for(Lane lane: run.lanes){
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.and(DBQuery.is("code",runCode),DBQuery.is("lanes.number",lane.number)), 
					DBUpdate.unset("lanes.$.readSetCodes"));
		
		}
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode", runCode)));
		return ok();
	}
	
	public static Result workflow(String readSetCode, String stateCode){
		return badRequest("Not implemented");
	}
	
	
	@Deprecated
	public static Result saveOld(String code, Integer laneNumber){
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		ReadSet readsetValue = filledForm.get();	
		readsetValue.laneNumber = laneNumber;
		readsetValue.runCode = code;
		
		if (null == readsetValue._id) { 
			readsetValue.traceInformation = new TraceInformation();
			readsetValue.traceInformation.setTraceInformation("ngsrg");
		} else {
			return badRequest("use PUT method to update the run");
		}
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.setCreationMode();
		readsetValue.validate(ctxVal);	
		
		if (!ctxVal.hasErrors()) {
			readsetValue = MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readsetValue);
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", readsetValue.runCode), DBQuery.is("lanes.number", readsetValue.laneNumber)), 
					DBUpdate.push("lanes.$.readSetCodes", readsetValue.code));		
			
			return ok(Json.toJson(readsetValue));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	
	
}
