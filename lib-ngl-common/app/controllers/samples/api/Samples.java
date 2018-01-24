package controllers.samples.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

// import static validation.sample.instance.SampleValidationHelper.*;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import models.laboratory.common.description.Level;
// import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
// import models.laboratory.container.instance.Container;
// import models.laboratory.experiment.instance.Experiment;
// import models.laboratory.run.instance.Analysis;
import models.laboratory.sample.instance.Sample;
// import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
// import models.utils.ListObject;
import models.utils.dao.DAOException;
// import models.utils.instance.ExperimentHelper;
import models.utils.instance.SampleHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.With;
// import play.mvc.Results;
import validation.ContextValidation;
// import views.components.datatable.DatatableForm;
// import views.components.datatable.DatatableResponse;
//import controllers.AbstractCRUDAPIController;

// import com.mongodb.BasicDBObject;

import controllers.DocumentController;
//import controllers.ListForm;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import controllers.history.UserHistory;
// import controllers.containers.api.ContainerBatchElement;
// import controllers.containers.api.ContainersSearchForm;
import fr.cea.ig.MongoDBDAO;
//import fr.cea.ig.MongoDBDatatableResponseChunks;
// import fr.cea.ig.MongoDBResult;
import fr.cea.ig.play.IGBodyParsers;
import fr.cea.ig.play.NGLContext;

//import static fr.cea.ig.mongo.DBQueryBuilder.*;

// TODO: cleanup
// indirection so that it's clear what to implement and who does,
// real method export would not use a subclass so all the methods
// are re-exported and their export status explicit. 

public class Samples extends Samples2 {
// public class Samples extends SamplesCRUD {
	@Inject
	public Samples(NGLContext ctx) {
		super(ctx);
	}
	@Permission(value={"reading"})
	public Result list() {
		return super.list();
	}
	@Permission(value={"writing"})
	public Result save() throws DAOException {
		return super.save();
	}
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	@Permission(value={"writing"})
	public Result update(String code) throws DAOException {
		return super.update(code);
	}
}

// Standard NGL implementation
class Samples2 extends DocumentController<Sample> {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Samples.class);
	
	private final Form<QueryFieldsForm> updateForm; // = form(QueryFieldsForm.class);
	//private final Form<Sample> sampleForm; // = form(Sample.class);
	// private final Form<SamplesSearchForm> sampleSearchForm; // = form(SamplesSearchForm.class);
	//private final Form<SampleBatchElement> batchElementForm; // = form(SampleBatchElement.class);

	public static final List<String> defaultKeys =  
			Arrays.asList("code",
					      "typeCode",
					      "categoryCode",
					      "projectCodes",
					      "referenceCollab",
					      "properties",
					      "valuation",
					      "taxonCode",
					      "ncbiScientificName",
					      "comments",
					      "traceInformation");
	
	private static final List<String> authorizedUpdateFields = 
			Arrays.asList("comments");
	
	@Inject
	public Samples2(NGLContext ctx) {
		super(ctx, InstanceConstants.SAMPLE_COLL_NAME, Sample.class, defaultKeys);
		updateForm       = ctx.form(QueryFieldsForm.class);
		//sampleForm       = ctx.form(Sample.class);
		// sampleSearchForm = ctx.form(SamplesSearchForm.class);
		//batchElementForm = ctx.form(SampleBatchElement.class);
	}
	@Permission(value={"reading"})
	public Result list() {
		SamplesSearchForm samplesSearch = filledFormQueryString(SamplesSearchForm.class);
		if (samplesSearch.reporting) {
			return nativeMongoDBQuery(samplesSearch);
		} else {
			DBQuery.Query query = getQuery(samplesSearch);
			return mongoJackQuery(samplesSearch, query);			
		}		
	}

	/*
	 * Construct the sample query
	 * @param samplesSearch
	 * @return
	 */
	public static DBQuery.Query getQuery(SamplesSearchForm samplesSearch) {
		// TODO: simply build return value at method end
		Query query = DBQuery.empty();
		
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		
		if(CollectionUtils.isNotEmpty(samplesSearch.codes)){
			queryElts.add(DBQuery.in("code", samplesSearch.codes));
		}else if(StringUtils.isNotBlank(samplesSearch.code)){
			queryElts.add(DBQuery.is("code", samplesSearch.code));
		}else if(StringUtils.isNotBlank(samplesSearch.codeRegex)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(samplesSearch.codeRegex)));
		}
		
		if(CollectionUtils.isNotEmpty(samplesSearch.typeCodes)){
			queryElts.add(DBQuery.in("typeCode", samplesSearch.typeCodes));
		}
		
		if(StringUtils.isNotBlank(samplesSearch.referenceCollabRegex)){
			queryElts.add(DBQuery.regex("referenceCollab", Pattern.compile(samplesSearch.referenceCollabRegex)));
		}
		
		if(StringUtils.isNotBlank(samplesSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCode));
		}

		if(CollectionUtils.isNotEmpty(samplesSearch.projectCodes)){ 				//samplesSearch.projectCodes != null && samplesSearch.projectCodes.size() > 0
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCodes));
		}

		if(StringUtils.isNotBlank(samplesSearch.treeOfLifePathRegex)){
			queryElts.add(DBQuery.regex("life.path", Pattern.compile(samplesSearch.treeOfLifePathRegex)));
		}
		
		// TODO: redundant code, done at method end 
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}
		
		
		if(null != samplesSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", samplesSearch.fromDate));
		}

		if(null != samplesSearch.toDate){
			queryElts.add(DBQuery.lessThan("traceInformation.creationDate", (DateUtils.addDays(samplesSearch.toDate, 1))));
		}
		
		if(CollectionUtils.isNotEmpty(samplesSearch.createUsers)){
			queryElts.add(DBQuery.in("traceInformation.createUser", samplesSearch.createUsers));
		}else if(StringUtils.isNotBlank(samplesSearch.createUser)){
			queryElts.add(DBQuery.is("traceInformation.createUser", samplesSearch.createUser));
		}
		
		if(StringUtils.isNotBlank(samplesSearch.commentRegex)){
			queryElts.add(DBQuery.elemMatch("comments", DBQuery.regex("comment", Pattern.compile(samplesSearch.commentRegex))));
		}
		
		if(StringUtils.isNotBlank(samplesSearch.taxonCode)){
			queryElts.add(DBQuery.is("taxonCode", samplesSearch.taxonCode));
		}
		
		if(StringUtils.isNotBlank(samplesSearch.ncbiScientificNameRegex)){
			queryElts.add(DBQuery.regex("ncbiScientificName", Pattern.compile(samplesSearch.ncbiScientificNameRegex)));
		}
		
		if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.elemMatch("processes", DBQuery.is("typeCode",samplesSearch.existingProcessTypeCode)
					.and(DBQuery.is("experiments.typeCode",samplesSearch.existingTransformationTypeCode), DBQuery.notEquals("experiments.typeCode",samplesSearch.notExistingTransformationTypeCode))));
		
		}else if(StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.and(DBQuery.is("processes.experiments.typeCode",samplesSearch.existingTransformationTypeCode)
					,DBQuery.notEquals("processes.experiments.typeCode",samplesSearch.notExistingTransformationTypeCode)));		
		
		}else if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)){
			queryElts.add(DBQuery.elemMatch("processes", DBQuery.is("typeCode",samplesSearch.existingProcessTypeCode).is("experiments.typeCode",samplesSearch.existingTransformationTypeCode)));		
					
		}else if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)
				&& StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.elemMatch("processes", DBQuery.is("typeCode",samplesSearch.existingProcessTypeCode).notEquals("experiments.typeCode",samplesSearch.notExistingTransformationTypeCode)));		
		
		}else if(StringUtils.isNotBlank(samplesSearch.existingProcessTypeCode)){
			queryElts.add(DBQuery.is("processes.typeCode",samplesSearch.existingProcessTypeCode));
		
		}else if(StringUtils.isNotBlank(samplesSearch.notExistingProcessTypeCode)){
			queryElts.add(DBQuery.notEquals("processes.typeCode",samplesSearch.notExistingProcessTypeCode));
		
		}else if(StringUtils.isNotBlank(samplesSearch.existingTransformationTypeCode)){
			queryElts.add(DBQuery.is("processes.experiments.typeCode",samplesSearch.existingTransformationTypeCode));
		
		}else if(StringUtils.isNotBlank(samplesSearch.notExistingTransformationTypeCode)){
			queryElts.add(DBQuery.notEquals("processes.experiments.typeCode",samplesSearch.notExistingTransformationTypeCode));
		
		}
		
		if(CollectionUtils.isNotEmpty(samplesSearch.experimentProtocolCodes)){
			queryElts.add(DBQuery.in("processes.experiments.protocolCode",samplesSearch.experimentProtocolCodes));
		}
		
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(samplesSearch.properties,Level.CODE.Sample, "properties"));
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(samplesSearch.experimentProperties,Level.CODE.Experiment, "processes.experiments.properties"));

		queryElts.addAll(NGLControllerHelper.generateExistsQueriesForFields(samplesSearch.existingFields));
		
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}		
		
		return query;
	}
	/*
	// generic arguments
	protected <T> Result save_wrapper(BiConsumer<ContextValidation,T> f) {
		Form<T> filledForm = getMainFilledForm();
		Sample input = filledForm.get();

	}
	
	// Getting the instance to save goes through the getMainFilledForm
	// function that is used for the instance construction and the errors
	// from the form.
	@Permission(value={"writing"})
	public Result save_small() throws DAOException {
		return save_wrapper((ctx,val) -> {
			
		});
	}
	*/
	@Permission(value={"writing"})
	public Result save() throws DAOException {
		Form<Sample> filledForm = getMainFilledForm();
		Sample input = filledForm.get();
		
		if (null == input._id) {
			input.traceInformation = new TraceInformation();
			input.traceInformation.setTraceInformation(getCurrentUser());				
		} else {
			return badRequest("use PUT method to update the sample");
		}
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors());
		ctxVal.setCreationMode();
		SampleHelper.executeRules(input, "sampleCreation");
		input.validate(ctxVal);
		
		if (!ctxVal.hasErrors()) {
			input = saveObject(input);			
			return ok(Json.toJson(input));
		} else {
			// return badRequest(filledForm.errors-AsJson());
			return badRequest(errorsAsJson(ctxVal.getErrors()));
		}				
	}


	private static Sample findSample(String sampleCode){
		return  MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",sampleCode));
	}


	// @BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
	@BodyParser.Of(value = IGBodyParsers.Json5MB.class)
	@Permission(value={"writing"})
	public Result update(String code) throws DAOException {
		Sample sampleInDB = findSample(code);
		Logger.debug("Sample with code "+code);
		if (sampleInDB == null)
			return badRequest("Sample with code " + code + " does not exist");

		Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);

		QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
		// Form<Sample> filledForm = getFilledForm(sampleForm, Sample.class);
		Form<Sample> filledForm = getMainFilledForm();
		Sample sampleInForm = filledForm.get();

		if(queryFieldsForm.fields == null){
			if (code.equals(sampleInForm.code)) {
				if(null != sampleInForm.traceInformation){
					sampleInForm.traceInformation = getUpdateTraceInformation(sampleInForm.traceInformation);				
				}else{
					Logger.error("traceInformation is null !!");
				}

				ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
				ctxVal.setUpdateMode();
				sampleInForm.comments = InstanceHelpers.updateComments(sampleInForm.comments, ctxVal);

				sampleInForm.validate(ctxVal);
				if (!ctxVal.hasErrors()) {
					MongoDBDAO.update(InstanceConstants.SAMPLE_COLL_NAME, sampleInForm);
					return ok(Json.toJson(sampleInForm));
				} else {
					// return badRequest(filledForm.errors-AsJson());
					return badRequest(errorsAsJson(ctxVal.getErrors()));
				}

			} else {
				return badRequest("sample code are not the same");
			}	
		}else{
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
			validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
			// if(!filledForm.hasErrors()){
			if (!ctxVal.hasErrors()) {
				sampleInForm.comments = InstanceHelpers.updateComments(sampleInForm.comments, ctxVal);

				TraceInformation ti = sampleInDB.traceInformation;
				ti.setTraceInformation(getCurrentUser());

				if(queryFieldsForm.fields.contains("valuation")){
					sampleInForm.valuation.user = getCurrentUser();
					sampleInForm.valuation.date = new Date();
				}

				if (!ctxVal.hasErrors()) {
					updateObject(DBQuery.and(DBQuery.is("code", code)), 
							getBuilder(sampleInForm, queryFieldsForm.fields).set("traceInformation", getUpdateTraceInformation(sampleInDB.traceInformation)));
					if(queryFieldsForm.fields.contains("code") && null != sampleInForm.code){
						code = sampleInForm.code;
					}
					return ok(Json.toJson(findSample(code)));
				} else {
					// return badRequest(filledForm.errors-AsJson());
					return badRequest(errorsAsJson(ctxVal.getErrors()));
				}				
			} else {
				// return badRequest(filledForm.errors-AsJson());
				return badRequest(errorsAsJson(ctxVal.getErrors()));
			}
		}	
	}

	/*private static DatatableForm updateForm(SamplesSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		return form;
	}*/
	
}





