package controllers.readsets.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.DBUpdate;

import com.google.inject.Provider;
import com.mongodb.BasicDBObject;

import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import fr.cea.ig.mongo.MongoStreamer;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.NGLContext;
import models.laboratory.common.description.Level;
import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.Valuation;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import play.data.Form;
import play.i18n.Lang;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import rules.services.LazyRules6Actor;
import validation.ContextValidation;
import validation.run.instance.ReadSetValidationHelper;
import views.components.datatable.DatatableBatchResponseElement;
import views.components.datatable.DatatableForm;
import workflows.readset.ReadSetWorkflows;

// TODO: cleanup

public class ReadSets extends ReadSetsController {
	
	private static final play.Logger.ALogger logger = play.Logger.of(ReadSets.class);
	
	final static List<String> authorizedUpdateFields = Arrays.asList("code", "path","location","properties");
	final static List<String> defaultKeys            = Arrays.asList("code", "typeCode", "runCode", "runTypeCode", "laneNumber", "projectCode", "sampleCode", "runSequencingStartDate", "state", "productionValuation", "bioinformaticValuation", "properties","location");

	private final Form<ReadSet>              readSetForm;      // = form(ReadSet.class);
	private final Form<ReadSetValuation>     valuationForm;    // = form(ReadSetValuation.class);
	private final Form<State>                stateForm;        // = form(State.class);
	private final Form<ReadSetBatchElement>  batchElementForm; // = form(ReadSetBatchElement.class);
	private final Form<QueryFieldsForm>      updateForm;       // = form(QueryFieldsForm.class);
	private final Provider<ReadSetWorkflows> workflows;
	private final LazyRules6Actor rulesActor;
	
	@Inject
	public ReadSets(NGLContext ctx, Provider<ReadSetWorkflows> workflows) {
		readSetForm      = ctx.form(ReadSet.class);
		valuationForm    = ctx.form(ReadSetValuation.class);
		stateForm        = ctx.form(State.class);
		batchElementForm = ctx.form(ReadSetBatchElement.class);
		updateForm       = ctx.form(QueryFieldsForm.class);
		rulesActor       = ctx.rules6Actor(); 
		this.workflows   = workflows;
	}
	
	@Permission(value={"reading"})
	public Result list() {
		ReadSetsSearchForm form = filledFormQueryString(ReadSetsSearchForm.class);
		Query q = getQuery(form);		
		BasicDBObject keys = getKeys(updateForm(form));

		if (form.reporting && !form.aggregate) {
			//return nativeMongoDBQQuery(form);
			String jsonKeys = getJSONKeys(updateForm(form));
			return nativeMongoDBQQuery(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class,jsonKeys);
		}else if(form.reporting && form.aggregate){
			return nativeMongoDBAggregate(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class);
		}else if (form.datatable) {			
			MongoDBResult<ReadSet> results = mongoDBFinder(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class, q, keys);				
			return MongoStreamer.okStreamUDT(results);
		} else if(form.count) {
			MongoDBResult<ReadSet> results = mongoDBFinder(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class, q, keys);							
			int count = results.count();
			Map<String, Integer> m = new HashMap<>(1);
			m.put("result", count);
			return ok(Json.toJson(m));
		} else if(form.list) {
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			if(null == form.orderBy)form.orderBy = "code";
			if(null == form.orderSense)form.orderSense = 0;
			MongoDBResult<ReadSet> results = mongoDBFinder(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class, q, keys);						
			return ok(Json.toJson(toListObjects(results.toList())));
		}else {
			MongoDBResult<ReadSet> results = mongoDBFinder(InstanceConstants.READSET_ILLUMINA_COLL_NAME, form, ReadSet.class, q, keys);	
			return MongoStreamer.okStream(results);
		}
	}

	private List<ListObject> toListObjects(List<ReadSet> readSets){
		List<ListObject> jo = new ArrayList<>();
		for(ReadSet r: readSets){
			jo.add(new ListObject(r.code, r.code));
		}
		return jo;
	}

	private DatatableForm updateForm(ReadSetsSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		}
		return form;
	}

	private Query getQuery(ReadSetsSearchForm form) {
		List<Query> queries = new ArrayList<>();
		Query query = null;

		if (StringUtils.isNotBlank(form.typeCode)) { //all
			queries.add(DBQuery.is("typeCode", form.typeCode));
		}else if(CollectionUtils.isNotEmpty(form.typeCodes)){
			queries.add(DBQuery.in("typeCode", form.typeCodes));
		}

		if (StringUtils.isNotBlank(form.submissionStateCode)) { 
			queries.add(DBQuery.is("submissionState.code", form.submissionStateCode));
		}else if(CollectionUtils.isNotEmpty(form.submissionStateCodes)){
			queries.add(DBQuery.in("submissionStateCode", form.submissionStateCodes));
		}

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
		
		if(null != form.fromEvalDate && null != form.toEvalDate){
			queries.add(DBQuery.or(
					DBQuery.and(DBQuery.greaterThanEquals("productionValuation.date", form.fromEvalDate),DBQuery.lessThan("productionValuation.date", (DateUtils.addDays(form.toEvalDate,1)))),
					DBQuery.and(DBQuery.greaterThanEquals("bioinformaticValuation.date", form.fromEvalDate),DBQuery.lessThan("bioinformaticValuation.date", (DateUtils.addDays(form.toEvalDate,1))))
			));
		}else if(null != form.fromEvalDate && null == form.toEvalDate){
			queries.add(DBQuery.or(DBQuery.greaterThanEquals("productionValuation.date", form.fromEvalDate),DBQuery.greaterThanEquals("bioinformaticValuation.date", form.fromEvalDate)));
		}else if(null != form.toEvalDate && null == form.fromEvalDate){
			queries.add(DBQuery.or(DBQuery.lessThan("productionValuation.date", (DateUtils.addDays(form.toEvalDate,1))),DBQuery.lessThan("bioinformaticValuation.date", (DateUtils.addDays(form.toEvalDate,1)))));
		}
		
		if(StringUtils.isNotBlank(form.location)){
			queries.add(DBQuery.is("location", form.location));
		}

		if (StringUtils.isNotBlank(form.code)) { //all
			queries.add(DBQuery.is("code", form.code));
		}else if(CollectionUtils.isNotEmpty(form.codes)){
			queries.add(DBQuery.in("code", form.codes));
		}else if (StringUtils.isNotBlank(form.regexCode)) { //all
			queries.add(DBQuery.regex("code", Pattern.compile(form.regexCode)));
		}

		if (StringUtils.isNotBlank(form.regexSampleCode)) { //all
			queries.add(DBQuery.regex("sampleCode", Pattern.compile(form.regexSampleCode)));
		}

		if(CollectionUtils.isNotEmpty(form.supportCodes)){
			queries.add(DBQuery.in("sampleOnContainer.containerSupportCode", form.supportCodes));
		}else if(StringUtils.isNotBlank(form.regexSupportCode)){
			queries.add(DBQuery.regex("sampleOnContainer.containerSupportCode", Pattern.compile(form.regexSupportCode)));
		}
		
		if(StringUtils.isNotBlank(form.ncbiScientificName)){
			queries.add(DBQuery.is("sampleOnContainer.ncbiScientificName", form.ncbiScientificName));
		}else if(StringUtils.isNotBlank(form.ncbiScientificNameRegex)){
			queries.add(DBQuery.regex("sampleOnContainer.ncbiScientificName", Pattern.compile(form.ncbiScientificNameRegex)));
		}
		
		if (CollectionUtils.isNotEmpty(form.instrumentCodes)) { //all
			queries.add(DBQuery.regex("runCode", Pattern.compile(findRegExpFromStringList(form.instrumentCodes))));
		}

		if (CollectionUtils.isNotEmpty(form.productionResolutionCodes)) { //all
			queries.add(DBQuery.in("productionValuation.resolutionCodes", form.productionResolutionCodes));
		}

		if (CollectionUtils.isNotEmpty(form.bioinformaticResolutionCodes)) { //all
			queries.add(DBQuery.in("bioinformaticValuation.resolutionCodes", form.bioinformaticResolutionCodes));
		}

		if(null != form.productionValuationUser){
			queries.add(DBQuery.is("productionValuation.user", form.productionValuationUser));
		}

		if(null != form.productionValuationCriteriaCode){
			queries.add(DBQuery.is("productionValuation.criteriaCode", form.productionValuationCriteriaCode));
		}
		
		if (CollectionUtils.isNotEmpty(form.sampleCategoryCodes)) { //all
			queries.add(DBQuery.in("sampleOnContainer.sampleCategoryCode", form.sampleCategoryCodes));
		}

		if (CollectionUtils.isNotEmpty(form.sampleTypeCodes)) { //all
			queries.add(DBQuery.in("sampleOnContainer.sampleTypeCode", form.sampleTypeCodes));
		}

		if(CollectionUtils.isNotEmpty(form.archiveIds)){
			queries.add(DBQuery.in("archiveId", form.archiveIds));
		}else if(StringUtils.isNotBlank(form.regexArchiveId)){
			queries.add(DBQuery.regex("archiveId", Pattern.compile(form.regexArchiveId)));
		}
		
		//TODO must be change to used a generic system (see below)
		/*
		if (StringUtils.isNotBlank(form.isSentCCRT)) {
			if (Boolean.valueOf(form.isSentCCRT)) { 
				queries.add(DBQuery.is("properties.isSentCCRT.value", Boolean.valueOf(form.isSentCCRT)));
			}
			else {
				queries.add(DBQuery.notEquals("properties.isSentCCRT.value", !Boolean.valueOf(form.isSentCCRT))); 
			}
		}
		if (StringUtils.isNotBlank(form.isSentCollaborator)) {
			if (Boolean.valueOf(form.isSentCollaborator)) { 
				queries.add(DBQuery.is("properties.isSentCollaborator.value", Boolean.valueOf(form.isSentCollaborator)));
			}
			else {
				queries.add(DBQuery.notEquals("properties.isSentCollaborator.value", !Boolean.valueOf(form.isSentCollaborator))); 
			}
		}
		 */
		//END TODO

		queries.addAll(NGLControllerHelper.generateQueriesForProperties(form.properties, Level.CODE.ReadSet, "properties"));
		queries.addAll(NGLControllerHelper.generateQueriesForProperties(form.sampleOnContainerProperties, Level.CODE.Content, "sampleOnContainer.properties"));
		queries.addAll(NGLControllerHelper.generateQueriesForTreatmentProperties(form.treatmentProperties, Level.CODE.ReadSet, "treatments"));

		if (CollectionUtils.isNotEmpty(form.existingFields)) { //all
			for(String field : form.existingFields){
				queries.add(DBQuery.exists(field));
			}		
		}

		if (CollectionUtils.isNotEmpty(form.notExistingFields)) { //all
			for(String field : form.notExistingFields){
				queries.add(DBQuery.notExists(field));
			}
		}

		if(queries.size() > 0){
			query = DBQuery.and(queries.toArray(new Query[queries.size()]));
		}

		return query;
	}

	@Permission(value={"reading"})
	public Result get(String readSetCode) {
		DatatableForm form = filledFormQueryString(DatatableForm.class);
		ReadSet readSet =  getReadSet(readSetCode, form.includes.toArray(new String[0]));		
		if(readSet != null) {
			return ok(Json.toJson(readSet));	
		} 		
		else {
			return notFound();
		}		
	}

	@Permission(value={"reading"})
	public Result head(String readSetCode) {
		if(MongoDBDAO.checkObjectExistByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, readSetCode)){			
			return ok();					
		}else{
			return notFound();
		}	
	}

	@Permission(value={"writing"})
	public Result save() {

		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		ReadSet readSetInput = filledForm.get();

		if (readSetInput._id == null) { 
			readSetInput.traceInformation = new TraceInformation();
			readSetInput.traceInformation.setTraceInformation(getCurrentUser());

			if (readSetInput.state == null) {
				readSetInput.state = new State();
			}
			readSetInput.state.code = "N";
			readSetInput.state.user = getCurrentUser();
			readSetInput.state.date = new Date();	
			readSetInput.submissionState = new State("NONE", getCurrentUser());
			readSetInput.submissionState.date = new Date();	

			//hack to simplify ngsrg => move to workflow but workflow not call here !!!
			if (readSetInput.runCode != null && (readSetInput.runSequencingStartDate == null || readSetInput.runTypeCode == null)) {
				updateReadSet(readSetInput);
			}
		} else {
			return badRequest("use PUT method to update the run");
		}

//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		
		ReadSetsSaveForm readSetsSaveForm = filledFormQueryString(ReadSetsSaveForm.class);
		if (readSetsSaveForm.external != null)
			ctxVal.putObject("external", readSetsSaveForm.external);
		else
			ctxVal.putObject("external", false);

		//Apply rules before validation
		workflows.get().applyPreStateRules(ctxVal, readSetInput, readSetInput.state);
		
		ctxVal.setCreationMode();
		readSetInput.validate(ctxVal);

		if (!ctxVal.hasErrors()) {
			readSetInput = MongoDBDAO.save(InstanceConstants.READSET_ILLUMINA_COLL_NAME, readSetInput);
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
					DBQuery.and(DBQuery.is("code", readSetInput.runCode), 
							DBQuery.elemMatch("lanes", DBQuery.and(DBQuery.is("number", readSetInput.laneNumber), DBQuery.notIn("readSetCodes", readSetInput.code)))), 
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
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

	private void updateReadSet(ReadSet readSetInput) {
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 0);//Don't need the _id field
		keys.put("sequencingStartDate", 1);
		keys.put("typeCode", 1);
		Run run = MongoDBDAO.find(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", readSetInput.runCode), keys).toList().get(0); 
		readSetInput.runSequencingStartDate = run.sequencingStartDate;
		readSetInput.runTypeCode = run.typeCode;
	}


	@Permission(value={"writing"})
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	public Result update(String readSetCode){
		ReadSet readSet =  getReadSet(readSetCode);
		if(readSet == null) {
			return badRequest("ReadSet with code "+readSetCode+" does not exist");
		}
		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();

		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		ReadSet readSetInput = filledForm.get();

		if(queryFieldsForm.fields == null){
			if (readSetInput.code.equals(readSetCode)) {
				if(null != readSetInput.traceInformation){
					readSetInput.traceInformation.setTraceInformation(getCurrentUser());
				}else{
					logger.error("traceInformation is null !!");
				}

				if(!readSet.state.code.equals(readSetInput.state.code)){
					return badRequest("you cannot change the state code. Please used the state url ! ");
				}

//				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 
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
				} else {
					return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
				}
			}else{
				return badRequest("readset code are not the same");
			}
		} else { //update only some authorized properties
//			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);

			if(queryFieldsForm.fields.contains("code")){
				ctxVal.setCreationMode();
				ReadSetValidationHelper.validateCode(readSetInput, InstanceConstants.READSET_ILLUMINA_COLL_NAME, ctxVal);
			}

			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
						DBQuery.and(DBQuery.is("code", readSetCode)), 
						getBuilder(readSetInput, queryFieldsForm.fields, ReadSet.class).set("traceInformation", getUpdateTraceInformation(readSet)));

				if(queryFieldsForm.fields.contains("code") && null != readSetInput.code){
					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
							DBQuery.and(DBQuery.is("code",readSet.runCode),DBQuery.is("lanes.number",readSet.laneNumber)), 
							DBUpdate.pull("lanes.$.readSetCodes", readSetCode));

					MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
							DBQuery.and(DBQuery.is("code", readSet.runCode), 
									DBQuery.elemMatch("lanes", DBQuery.and(DBQuery.is("number", readSet.laneNumber), DBQuery.notIn("readSetCodes", readSet.code)))), 
							DBUpdate.push("lanes.$.readSetCodes", readSetInput.code));
					readSetCode = readSetInput.code;											
				}
				return ok(Json.toJson(getReadSet(readSetCode)));
			} else {
				return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
			}			
		}
	}

	@Permission(value={"writing"}) 
	public Result delete(String readSetCode) { 
		ReadSet readSet = getReadSet(readSetCode);
		if (readSet == null) {
			return badRequest("Readset with code "+readSetCode+" does not exist !");
		}		
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
				DBQuery.and(DBQuery.is("code",readSet.runCode),DBQuery.is("lanes.number",readSet.laneNumber)), 
				DBUpdate.pull("lanes.$.readSetCodes", readSet.code));

		MongoDBDAO.deleteByCode(InstanceConstants.READSET_ILLUMINA_COLL_NAME,  ReadSet.class, readSet.code);


		if ((readSet.projectCode!= null) && (!MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("runCode",readSet.runCode), DBQuery.is("projectCode",readSet.projectCode))))) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code",readSet.runCode), 
					DBUpdate.pull("projectCodes", readSet.projectCode));
		}
		if ((readSet.sampleCode!= null) && (!MongoDBDAO.checkObjectExist(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
				DBQuery.and(DBQuery.is("runCode",readSet.runCode), DBQuery.is("sampleCode",readSet.sampleCode))))) {
			MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
					DBQuery.is("code",readSet.runCode), 
					DBUpdate.pull("sampleCodes", readSet.sampleCode));
		}

		//TODO delete analysis

		return ok();
	}


	@Permission(value={"writing"})
	public Result deleteByRunCode(String runCode) {
		Run run  = MongoDBDAO.findByCode(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, runCode);
		if (run==null) {
			return badRequest();
		}
		if(null != run.lanes){
			for(Lane lane: run.lanes){
				MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME,  Run.class, 
						DBQuery.and(DBQuery.is("code",runCode),DBQuery.is("lanes.number",lane.number)), 
						DBUpdate.unset("lanes.$.readSetCodes"));		
			}
		}


		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code",runCode), DBUpdate.unset("projectCodes").unset("sampleCodes"));

		MongoDBDAO.delete(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, DBQuery.and(DBQuery.is("runCode", runCode)));

		return ok();
	}

	@Permission(value={"writing"})
	public Result state(String code){
		ReadSet readSet = getReadSet(code);
		if(readSet == null){
			return badRequest();
		}
		Form<State> filledForm =  getFilledForm(stateForm, State.class);
		State state = filledForm.get();
		state.date = new Date();
		state.user = getCurrentUser();
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		workflows.get().setState(ctxVal, readSet, state);
		if (!ctxVal.hasErrors()) {
			return ok(Json.toJson(getReadSet(code)));
		} else {
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

	@Permission(value={"writing"})
	public Result stateBatch(){
		List<Form<ReadSetBatchElement>> filledForms =  getFilledFormList(batchElementForm, ReadSetBatchElement.class);

		final String user = getCurrentUser();
		final Lang lang = Http.Context.Implicit.lang();
		
		
		List<DatatableBatchResponseElement> response = filledForms.parallelStream()
				.map(filledForm->{
					ReadSetBatchElement element = filledForm.get();
					ReadSet readSet = getReadSet(element.data.code);
					if(null != readSet){
						State state = element.data.state;
						state.date = new Date();
						state.user = user;
//						ContextValidation ctxVal = new ContextValidation(user, filledForm.errors());
						ContextValidation ctxVal = new ContextValidation(user, filledForm);
						workflows.get().setState(ctxVal, readSet, state);
						if (!ctxVal.hasErrors()) {
							return new DatatableBatchResponseElement(OK, getReadSet(readSet.code), element.index);
						}else {
							return new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errorsAsJson(lang), element.index);
						}
					}else {
						return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
					}
				}).collect(Collectors.toList());
		
		return ok(Json.toJson(response));
	}

	@Permission(value={"writing"})
	public Result valuation(String code){
		ReadSet readSet = getReadSet(code);
		if(readSet == null){
			return badRequest();
		}
		Form<ReadSetValuation> filledForm =  getFilledForm(valuationForm, ReadSetValuation.class);
		ReadSetValuation valuations = filledForm.get();
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm);
		ctxVal.setUpdateMode();
		manageValidation(readSet, valuations.productionValuation, valuations.bioinformaticValuation, ctxVal);
		if (!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", code)),
					DBUpdate.set("productionValuation", valuations.productionValuation)
					.set("bioinformaticValuation", valuations.bioinformaticValuation)
					.set("traceInformation", getUpdateTraceInformation(readSet)));			
			readSet = getReadSet(code);
			workflows.get().nextState(ctxVal, readSet);
			return ok(Json.toJson(readSet));
		} else {
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

	@Permission(value={"writing"})
	public Result valuationBatch(){
		List<Form<ReadSetBatchElement>> filledForms =  getFilledFormList(batchElementForm, ReadSetBatchElement.class);

		final String user = getCurrentUser();
		final Lang lang = Http.Context.Implicit.lang();
		
		List<DatatableBatchResponseElement> response = filledForms.parallelStream()
				.map(filledForm->{
					ReadSetBatchElement element = filledForm.get();
					ReadSet readSet = getReadSet(element.data.code);
					if (readSet != null) {
//						ContextValidation ctxVal = new ContextValidation(user, filledForm.errors());
						ContextValidation ctxVal = new ContextValidation(user, filledForm);
						ctxVal.setUpdateMode();
						manageValidation(readSet, element.data.productionValuation, element.data.bioinformaticValuation, ctxVal);				
						if (!ctxVal.hasErrors()) {
							MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
									DBQuery.and(DBQuery.is("code", readSet.code)),
									DBUpdate.set("productionValuation", element.data.productionValuation)
									.set("bioinformaticValuation", element.data.bioinformaticValuation)
									.set("traceInformation", getUpdateTraceInformation(readSet,user)));							
							readSet = getReadSet(readSet.code);
							workflows.get().nextState(ctxVal, readSet);
							return new DatatableBatchResponseElement(OK, readSet, element.index);
						}else {
							return new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errorsAsJson(lang), element.index);
						}
					}else {
						return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
					}
				}).collect(Collectors.toList());
		
		
		return ok(Json.toJson(response));
	}

	@Permission(value={"writing"})
	public Result properties(String code){
		ReadSet readSet = getReadSet(code);
		if(readSet == null){
			return badRequest();
		}

		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
//		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm); 

		Map<String, PropertyValue> properties = filledForm.get().properties;
		ctxVal.setUpdateMode();
		ReadSetValidationHelper.validateReadSetType(readSet.typeCode, properties, ctxVal);

		
		if (!ctxVal.hasErrors()) {
			MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
					DBQuery.and(DBQuery.is("code", code)),
					DBUpdate.set("properties", properties)
					.set("traceInformation", getUpdateTraceInformation(readSet)));								
			return ok(Json.toJson(getReadSet(code)));		
		} else {
			return badRequest(NGLContext._errorsAsJson(ctxVal.getErrors()));
		}
	}

	@Permission(value={"writing"})
	public Result propertiesBatch() {
		List<Form<ReadSetBatchElement>> filledForms =  getFilledFormList(batchElementForm, ReadSetBatchElement.class);


		final String user = getCurrentUser();
		final Lang lang = Http.Context.Implicit.lang();
		
		List<DatatableBatchResponseElement> response = filledForms.parallelStream()
				.map(filledForm->{
					ReadSetBatchElement element = filledForm.get();
					ReadSet readSet = getReadSet(element.data.code);
					if (readSet != null) {
//						ContextValidation ctxVal = new ContextValidation(user, filledForm.errors()); 
						ContextValidation ctxVal = new ContextValidation(user, filledForm); 
						Map<String, PropertyValue> properties = element.data.properties;
						ctxVal.setUpdateMode();
						ReadSetValidationHelper.validateReadSetType(readSet.typeCode, properties, ctxVal);

						if (!ctxVal.hasErrors()) {
							MongoDBDAO.update(InstanceConstants.READSET_ILLUMINA_COLL_NAME, ReadSet.class, 
									DBQuery.and(DBQuery.is("code", readSet.code)),
									DBUpdate.set("properties", element.data.properties)
									.set("traceInformation", getUpdateTraceInformation(readSet, user)));								
							return new DatatableBatchResponseElement(OK, getReadSet(readSet.code), element.index);
						} else {
							return new DatatableBatchResponseElement(BAD_REQUEST, filledForm.errorsAsJson(lang), element.index);
						}
					} else {
						return new DatatableBatchResponseElement(BAD_REQUEST, element.index);
					}
				}).collect(Collectors.toList());
		
		return ok(Json.toJson(response));
	}

	private void manageValidation(ReadSet readSet, Valuation productionVal, Valuation bioinfoVal, ContextValidation ctxVal) {
		if (productionVal.valid != readSet.productionValuation.valid) {
			productionVal.date = new Date();
			productionVal.user = ctxVal.getUser();
			ReadSetValidationHelper.validateValuation(readSet.typeCode, productionVal, ctxVal);
		}
		if (bioinfoVal.valid != readSet.bioinformaticValuation.valid) {
			bioinfoVal.date = new Date();
			bioinfoVal.user = ctxVal.getUser();
			ReadSetValidationHelper.validateValuation(readSet.typeCode, bioinfoVal, ctxVal);
		}
	}

	private String findRegExpFromStringList(Set<String> searchList) {
		String regex = ".*("; 
		for (String itemList : searchList) {
			regex += itemList + "|"; 
		}
		regex = regex.substring(0,regex.length()-1);
		regex +=  ").*";
		return regex;
	}

	@Permission(value={"writing"})
	public Result applyRules(String code, String rulesCode){
		ReadSet readSet = getReadSet(code);
		if (readSet != null) {
			rulesActor.tellMessage(rulesCode, readSet);
		} else
			return badRequest();
		return ok();
	}

}
