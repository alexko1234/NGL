package controllers.samples.api;

import static play.data.Form.form;

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
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.SampleHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import validation.ContextValidation;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.DocumentController;
import controllers.NGLControllerHelper;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDatatableResponseChunks;
import fr.cea.ig.MongoDBResult;

public class Samples extends DocumentController<Sample>{
	
	final Form<Sample> sampleForm = form(Sample.class);
	final static List<String> defaultKeys =  Arrays.asList("code","typeCode","categoryCode","projectCodes","referenceCollab","properties","valuation","taxonCode","ncbiScientificName","comments","traceInformation");
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
		
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(samplesSearch.properties,Level.CODE.Sample, "properties"));

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
			return badRequest("use PUT method to update the experiment");
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
}
