package controllers.readsets.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.mongodb.BasicDBObject;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
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
import validation.run.instance.ReadSetValidationHelper;
import validation.run.instance.RunValidationHelper;
import views.components.datatable.DatatableResponse;
import workflows.Workflows;
import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.MongoDBResult.Sort;



public class ReadSets extends CommonController{

	final static Form<ReadSet> readSetForm = form(ReadSet.class);
	final static Form<ReadSetsSearchForm> searchForm = form(ReadSetsSearchForm.class);
	final static Form<ReadSetValuation> valuationForm = form(ReadSetValuation.class);
	final static Form<State> stateForm = form(State.class);
	
	//@Permission(value={"reading"})
	public static Result list() {
		Form<ReadSetsSearchForm> filledForm = filledFormQueryString(searchForm, ReadSetsSearchForm.class);
		ReadSetsSearchForm form = filledForm.get();
		
		Query q = getQuery(form);
		BasicDBObject keys = getKeys(form);
		if(form.datatable){			
			MongoDBResult<ReadSet> results = mongoDBFinder(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class, q, keys);				
			List<ReadSet> readSets = results.toList();
			return ok(Json.toJson(new DatatableResponse<ReadSet>(readSets, results.count())));
		}else{
			MongoDBResult<ReadSet> results = mongoDBFinder(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class, q, keys);							
			List<ReadSet> readSets = results.toList();
			return ok(Json.toJson(readSets));
		}
	}
	
	private static Query getQuery(ReadSetsSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		if (StringUtils.isNotBlank(form.runCode)) { //all
			queries.add(DBQuery.is("runCode", form.runCode));
		}else if(CollectionUtils.isNotEmpty(form.runCodes)){
			queries.add(DBQuery.in("runCode", form.runCodes));
		}
		
		if (null != form.laneNumber) { //all
			queries.add(DBQuery.is("laneNumber", form.laneNumber));
		}else if(CollectionUtils.isNotEmpty(form.laneNumbers)){
			queries.add(DBQuery.in("laneNumber", form.laneNumbers));
		}
		
		if (StringUtils.isNotBlank(form.stateCode)) { //all
			queries.add(DBQuery.is("state.code", form.stateCode));
		}else if (CollectionUtils.isNotEmpty(form.stateCodes)) { //all
			queries.add(DBQuery.in("state.code", form.stateCodes));
		}
		
		if (StringUtils.isNotBlank(form.productionValidCode)) { //all
			queries.add(DBQuery.is("productionValuation.valid", TBoolean.valueOf(form.productionValidCode)));
		}
		
		if (StringUtils.isNotBlank(form.bioinformaticValidCode)) { //all
			queries.add(DBQuery.is("bioinformaticValuation.valid", TBoolean.valueOf(form.bioinformaticValidCode)));
		}

		if (CollectionUtils.isNotEmpty(form.projectCodes)) { //all
			queries.add(DBQuery.in("projectCode", form.projectCodes));
		}else if (StringUtils.isNotBlank(form.projectCode)) { //all
			queries.add(DBQuery.is("projectCode", form.projectCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.sampleCodes)) { //all
			queries.add(DBQuery.in("sampleCode", form.sampleCodes));
		}else if (StringUtils.isNotBlank(form.sampleCode)) { //all
			queries.add(DBQuery.is("sampleCode", form.sampleCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.runTypeCodes)) { //all
			queries.add(DBQuery.in("runTypeCode", form.runTypeCodes));
		}
		
		if(null != form.fromDate){
			queries.add(DBQuery.greaterThanEquals("runSequencingStartDate", form.fromDate));
		}
		
		if(null != form.toDate){
			queries.add(DBQuery.lessThanEquals("runSequencingStartDate", form.toDate));
		}
		
		if (StringUtils.isNotBlank(form.regexCode)) { //all
			queries.add(DBQuery.regex("code", Pattern.compile(form.regexCode)));
		}
		
		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}
		
		return query;
	}
	
	//@Permission(value={"reading"})
	public static Result get(String readSetCode) {
		ReadSet readSet =  getReadSet(readSetCode);		
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
		ReadSet readSetInput = filledForm.get();
		
		if (null == readSetInput._id) { 
			readSetInput.traceInformation = new TraceInformation();
			readSetInput.traceInformation.setTraceInformation("ngsrg");
			
			if(null == readSetInput.state){
				readSetInput.state = new State();
			}
			readSetInput.state.code = "N";
			readSetInput.state.user = getCurrentUser();
			readSetInput.state.date = new Date();	
						
			//hack to simplify ngsrg
			if(null != readSetInput.runCode && (null == readSetInput.runSequencingStartDate || null == readSetInput.runTypeCode)){
				updateReadSet(readSetInput);
				
			}
			
		} else {
			return badRequest("use PUT method to update the run");
		}
		
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.setCreationMode();
		readSetInput.validate(ctxVal);	
		
		if (!ctxVal.hasErrors()) {
			readSetInput = MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSetInput);
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", readSetInput.runCode), DBQuery.is("lanes.number", readSetInput.laneNumber)), 
					DBUpdate.push("lanes.$.readSetCodes", readSetInput.code));	
			
			//To avoid "double" values
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", readSetInput.runCode), DBQuery.notIn("projectCodes", readSetInput.projectCode)), 
					DBUpdate.push("projectCodes", readSetInput.projectCode));
					
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", readSetInput.runCode), DBQuery.notIn("sampleCodes", readSetInput.sampleCode)), 
					DBUpdate.push("sampleCodes", readSetInput.sampleCode));
			
			return ok(Json.toJson(readSetInput));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}

	private static void updateReadSet(ReadSet readSetInput) {
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 0);//Don't need the _id field
		keys.put("sequencingStartDate", 1);
		keys.put("typeCode", 1);
		Run run = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", readSetInput.runCode), keys).toList().get(0); 
		readSetInput.runSequencingStartDate = run.sequencingStartDate;
		readSetInput.runTypeCode = run.typeCode;
	}
	
	
	
	public static Result update(String readSetCode){
		ReadSet readSet =  getReadSet(readSetCode);
		if(readSet == null) {
			return badRequest("ReadSet with code "+readSetCode+" does not exist");
		}
		
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		ReadSet readSetInput = filledForm.get();
		if (readSetInput.code.equals(readSetCode)) {
			if(null != readSetInput.traceInformation){
				readSetInput.traceInformation.setTraceInformation("ngsrg");
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			if(!readSet.state.code.equals(readSetInput.state.code)){
				return badRequest("you cannot change the state code. Please used the state url ! ");
			}
			
			ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
			ctxVal.setUpdateMode();
			readSetInput.validate(ctxVal);
			
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSetInput);
				
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", readSetInput.runCode), DBQuery.notIn("projectCodes", readSetInput.projectCode)), 
						DBUpdate.push("projectCodes", readSetInput.projectCode));
						
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
						DBQuery.and(DBQuery.is("code", readSetInput.runCode), DBQuery.notIn("sampleCodes", readSetInput.sampleCode)), 
						DBUpdate.push("sampleCodes", readSetInput.sampleCode));
				
				return ok(Json.toJson(readSetInput));
			}else {
				return badRequest(filledForm.errorsAsJson());			
			}
		}else{
			return badRequest("readset code are not the same");
		}		
	}

	private static ReadSet getReadSet(String readSetCode) {
		return MongoDBDAO.findByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode);
	}
		
	
	//@Permission(value={"delete_readset"}) 
	public static Result delete(String readSetCode) { 
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet == null) {
			return badRequest("Readset with code "+readSetCode+" does not exist !");
		}		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
				DBQuery.and(DBQuery.is("code",readSet.runCode),DBQuery.is("lanes.number",readSet.laneNumber)), 
				DBUpdate.pull("lanes.$.readSetCodes", readSet.code));

		MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, readSet.code);
		
		
		if ((readSet.projectCode!= null) && (!MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code",readSet.code), DBQuery.is("projectCode",readSet.projectCode))))) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code",readSet.runCode), 
					DBUpdate.pull("projectCodes", readSet.projectCode));
		}
		if ((readSet.sampleCode!= null) && (!MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code",readSet.code), DBQuery.is("sampleCode",readSet.sampleCode))))) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code",readSet.runCode), 
					DBUpdate.pull("sampleCodes", readSet.sampleCode));
		}
		
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
		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code",runCode), DBUpdate.unset("projectCodes").unset("sampleCodes"));
		
		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode", runCode)));
		
		return ok();
	}
	
	public static Result state(String code){
		ReadSet readSet = getReadSet(code);
		if(readSet == null){
			return badRequest();
		}
		Form<State> filledForm =  getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		state.date = new Date();
		state.user = getCurrentUser();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		Workflows.setReadSetState(ctxVal, readSet, state);
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(getReadSet(code)));
		}else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public static Result valuation(String code){
		ReadSet readSet = getReadSet(code);
		if(readSet == null){
			return badRequest();
		}
		Form<ReadSetValuation> filledForm =  getFilledForm(valuationForm, ReadSetValuation.class);
		ReadSetValuation valuations = filledForm.get();
		ContextValidation ctxVal = new ContextValidation(filledForm.errors());
		ctxVal.setUpdateMode();
		manageValidation(readSet, valuations, ctxVal);
		if(!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", code)),
					DBUpdate.set("productionValuation", valuations.productionValuation).set("bioinformaticValuation", valuations.bioinformaticValuation));								
			readSet = getReadSet(code);
			Workflows.nextReadSetState(ctxVal, readSet);
						
		} 
		if(!ctxVal.hasErrors()) {
			return ok(Json.toJson(readSet));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}

	public static Result properties(String code){
		ReadSet readSet = getReadSet(code);
		if(readSet == null){
			return badRequest();
		}
				
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		Map<String, PropertyValue> properties = filledForm.get().properties;
		ctxVal.setUpdateMode();
		ReadSetValidationHelper.validateReadSetType(readSet.typeCode, properties, ctxVal);
		
		if(!ctxVal.hasErrors()){
		    MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("code", code)),
				DBUpdate.set("properties", properties));								
					
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(getReadSet(code)));		
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}		
	}
	
	private static void manageValidation(ReadSet readSet, ReadSetValuation valuations, ContextValidation ctxVal) {
		Valuation productionVal = valuations.productionValuation;
		productionVal.date = new Date();
		productionVal.user = getCurrentUser();
		Valuation bioinfoVal = valuations.bioinformaticValuation;
		bioinfoVal.date = new Date();
		bioinfoVal.user = getCurrentUser();
		//par defaut si valiadation bioinfo pas rempli alors même que prod
		if(TBoolean.UNSET.equals(bioinfoVal.valid)){
			bioinfoVal.valid = productionVal.valid;
		}
		
		ReadSetValidationHelper.validateValuation(readSet.typeCode, productionVal, ctxVal);
		ReadSetValidationHelper.validateValuation(readSet.typeCode, bioinfoVal, ctxVal);		
	}		
}
