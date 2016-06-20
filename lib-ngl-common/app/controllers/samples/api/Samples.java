package controllers.samples.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.sample.instance.Sample;
import models.utils.CodeHelper;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import models.utils.instance.ExperimentHelper;
import models.utils.instance.SampleHelper;

import org.apache.commons.collections.CollectionUtils;
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
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBResult;

public class Samples extends DocumentController<Sample>{
	
	final Form<Sample> sampleForm = form(Sample.class);
	
	public Samples() {
		super(InstanceConstants.SAMPLE_COLL_NAME, Sample.class);	
	}
	
	
	@Permission(value={"reading"})
	public Result list(){
		SamplesSearchForm samplesSearch = filledFormQueryString(SamplesSearchForm.class);
		
		DBQuery.Query query = getQuery(samplesSearch);
		if(samplesSearch.datatable){
			MongoDBResult<Sample> results = mongoDBFinder(samplesSearch, query);
			List<Sample> samples = results.toList();
			return ok(Json.toJson(new DatatableResponse<Sample>(samples, results.count())));
		}
		else if(samplesSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			MongoDBResult<Sample> results = mongoDBFinder(samplesSearch,query).sort("code");
			List<Sample> samples = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Sample p: samples){
				los.add(new ListObject(p.code, p.name));
			}
			
			return Results.ok(Json.toJson(los));
		}else{
			MongoDBResult<Sample> results = mongoDBFinder(samplesSearch, query);
			List<Sample> samples = results.toList();
			return Results.ok(Json.toJson(samples));
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
		if(StringUtils.isNotBlank(samplesSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCode));
		}

		if(CollectionUtils.isNotEmpty(samplesSearch.projectCodes)){ 				//samplesSearch.projectCodes != null && samplesSearch.projectCodes.size() > 0
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCodes));
		}

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
