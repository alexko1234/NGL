package controllers.samples.api;

import static play.data.Form.form;

import static validation.sample.instance.SampleValidationHelper.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import models.laboratory.common.description.Level;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.instance.Sample;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.InstanceHelpers;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
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
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableForm;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import controllers.NGLControllerHelper;
import controllers.QueryFieldsForm;
import controllers.authorisation.Permission;
import controllers.containers.api.ContainersSearchForm;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBDatatableResponseChunks;
import fr.cea.ig.MongoDBResult;

public class Samples extends DocumentController<Sample>{
	
	final Form<Sample> sampleForm = form(Sample.class);
	final static List<String> defaultKeys =  Arrays.asList("code","typeCode","categoryCode","projectCodes","referenceCollab","properties","valuation","taxonCode","ncbiScientificName","comments","traceInformation");
	final static List<String> authorizedUpdateFields = Arrays.asList("comments","volume","quantity","size","concentration");
	
	
	public Samples() {
		super(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, defaultKeys);	
	}
	
	
	
	@Permission(value={"reading"})
	public Result list(){
		SamplesSearchForm searchForm = filledFormQueryString(SamplesSearchForm.class);
		if(searchForm.reporting){
			return nativeMongoDBQuery(searchForm);
		}else{
			DBQuery.Query query = getQuery(searchForm);
			return mongoJackQuery(searchForm, query);			
		}		
	}

	/**
	 * Construct the sample query
	 * @param samplesSearch
	 * @return
	 */
	private static DBQuery.Query getQuery(SamplesSearchForm samplesSearch) {
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

		queryElts.addAll(NGLControllerHelper.generateQueriesForExistingProperties(samplesSearch.existingFields));
		
		
		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}		
		
		return query;
	}
	
	
	@Permission(value={"writing"})
	public Result save() throws DAOException{
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
			return badRequest(filledForm.errorsAsJson());
		}				
	}


private static Sample findSample(String sampleCode){
	return  MongoDBDAO.findOne(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, DBQuery.is("code",sampleCode));
}

@Permission(value={"writing"})
@BodyParser.Of(value = BodyParser.Json.class, maxLength = 5000 * 1024)
public static Result update(String code){
	Sample sample = findSample(code);
	if(sample == null){
		return badRequest("Sample with code "+code+" not exist");
	}
	
	Form<QueryFieldsForm> filledQueryFieldsForm = filledFormQueryString(updateForm, QueryFieldsForm.class);
	QueryFieldsForm queryFieldsForm = filledQueryFieldsForm.get();
	Form<Sample> filledForm = getFilledForm(sampleForm, Sample.class);
	Sample input = filledForm.get();

	if(queryFieldsForm.fields == null){
		if (code.equals(input.code)) {
			if(null != input.traceInformation){
				input.traceInformation.setTraceInformation(getCurrentUser());
			}else{
				Logger.error("traceInformation is null !!");
			}
			
			if(!input.state.code.equals(input.state.code)){
				return badRequest("You cannot change the state code. Please used the state url ! ");
			}
			ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
			ctxVal.setUpdateMode();
			input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
			cleanProperty(input);
			input.validate(ctxVal);
			if (!ctxVal.hasErrors()) {
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, input);
				return ok(Json.toJson(input));
			}else {
				return badRequest(filledForm.errorsAsJson());
			}
			
		}else{
			return badRequest("sample code are not the same");
		}	
	}else{
		ContextValidation ctxVal = new ContextValidation(getCurrentUser(), filledForm.errors()); 	
		ctxVal.setUpdateMode();
		validateAuthorizedUpdateFields(ctxVal, queryFieldsForm.fields, authorizedUpdateFields);
		validateIfFieldsArePresentInForm(ctxVal, queryFieldsForm.fields, filledForm);
		if(!filledForm.hasErrors()){
			input.comments = InstanceHelpers.updateComments(input.comments, ctxVal);
			
			TraceInformation ti = sample.traceInformation;
			ti.setTraceInformation(getCurrentUser());
			
			if(queryFieldsForm.fields.contains("valuation")){
				input.valuation.user = getCurrentUser();
				input.valuation.date = new Date();
			}
			
			if(queryFieldsForm.fields.contains("volume")){
				validateVolume(input.volume, ctxVal);					
			}
			if(queryFieldsForm.fields.contains("quantity")){
				validateQuantity(input.quantity, ctxVal);					
			}
			if(queryFieldsForm.fields.contains("size")){
				validateSize(input.size, ctxVal);
			}
			if(queryFieldsForm.fields.contains("concentration")){
				validateConcentration(input.concentration, ctxVal);					
			}
			if(!ctxVal.hasErrors()){
				MongoDBDAO.update(InstanceConstants.CONTAINER_COLL_NAME, Sample.class, 
						DBQuery.and(DBQuery.is("code", code)), getBuilder(input, queryFieldsForm.fields, Sample.class).set("traceInformation", ti));
				return ok(Json.toJson(findSample(code)));
			}else{
				return badRequest(filledForm.errorsAsJson());
			}				
		}else{
			return badRequest(filledForm.errorsAsJson());
		}		
	}		
}
private static DatatableForm updateForm(SamplesSearchForm form) {
	if(form.includes.contains("default")){
		form.includes.remove("default");
		form.includes.addAll(defaultKeys);
	}
	return form;
}
}