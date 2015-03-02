package controllers.samples.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSpinner.ListEditor;

import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.ListObject;

import org.mongojack.DBQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableHelpers;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import controllers.CommonController;
import controllers.utils.FormUtils;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;

public class Samples extends CommonController{
	final static Form<SamplesSearchForm> sampleForm = form(SamplesSearchForm.class);

	public static Result get(String code){
		Sample sample = null;
		// Pour récupérer une liste de referenceCollab de samples
		if(code.contains(",")){
			List<String> myList = new ArrayList<String>(Arrays.asList(code.split(",")));			
			List<Sample> samples = new ArrayList<Sample>();
			
			for (String c : myList) {				
				sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, c);
				if(null!=sample){
					samples.add(sample);					
				}				
			}			
			if(null!=samples){
				return ok(Json.toJson(samples));
			}			
		}
		
		sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, code);
		
		if(sample != null){
			return ok(Json.toJson(sample));
		}else{
			return notFound();
		}
	}
	

	public static Result list(){
		Form<SamplesSearchForm> sampleFilledForm = filledFormQueryString(sampleForm,SamplesSearchForm.class);
		SamplesSearchForm samplesSearch = sampleFilledForm.get();

		DBQuery.Query query = getQuery(samplesSearch);
		if(query!=null){
		if(samplesSearch.datatable){
			MongoDBResult<Sample> results = mongoDBFinder(InstanceConstants.SAMPLE_COLL_NAME, samplesSearch, Sample.class, query);
			List<Sample> samples = results.toList();

			return ok(Json.toJson(new DatatableResponse<Sample>(samples, results.count())));
		}
		else if(samplesSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			MongoDBResult<Sample> results = mongoDBFinder(InstanceConstants.SAMPLE_COLL_NAME, samplesSearch, Sample.class, query, keys).sort("code");
			List<Sample> samples = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Sample p: samples){
				los.add(new ListObject(p.code, p.name));
			}
			
			return Results.ok(Json.toJson(los));
		}else{
			MongoDBResult<Sample> results = mongoDBFinder(InstanceConstants.SAMPLE_COLL_NAME, samplesSearch, Sample.class, query);
			List<Sample> samples = results.toList();
			return Results.ok(Json.toJson(samples));
		}
		}
		return Results.ok("{}");
	}

	/**
	 * Construct the sample query
	 * @param samplesSearch
	 * @return
	 */
	private static DBQuery.Query getQuery(SamplesSearchForm samplesSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		if(StringUtils.isNotBlank(samplesSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCode));
		}

		if(CollectionUtils.isNotEmpty(samplesSearch.projectCodes)){ 				//samplesSearch.projectCodes != null && samplesSearch.projectCodes.size() > 0
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCodes));
		}

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}
