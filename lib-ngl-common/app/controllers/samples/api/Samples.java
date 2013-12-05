package controllers.samples.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JSpinner.ListEditor;

import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import net.vz.mongodb.jackson.DBQuery;

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
		Sample sample = MongoDBDAO.findByCode(InstanceConstants.SAMPLE_COLL_NAME, Sample.class, code);
		if(sample != null){
			return ok(Json.toJson(sample));
		}

		return badRequest();
	}

	public static Result list(){
		Form<SamplesSearchForm> sampleFilledForm = filledFormQueryString(sampleForm,SamplesSearchForm.class);
		SamplesSearchForm samplesSearch = sampleFilledForm.get();

		DBQuery.Query query = getQuery(samplesSearch);
		if(samplesSearch.datatable){
			MongoDBResult<Sample> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Sample.class, query)
					.sort(DatatableHelpers.getOrderBy(sampleFilledForm), FormUtils.getMongoDBOrderSense(sampleFilledForm))
					.page(DatatableHelpers.getPageNumber(sampleFilledForm), DatatableHelpers.getNumberRecordsPerPage(sampleFilledForm)); 
			List<Sample> samples = results.toList();

			return ok(Json.toJson(new DatatableResponse(samples, results.count())));
		}
		else if(samplesSearch.list){
			BasicDBObject keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("name", 1);
			keys.put("code", 1);
			MongoDBResult<Sample> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Sample.class, query, keys)
					.sort(DatatableHelpers.getOrderBy(sampleFilledForm), FormUtils.getMongoDBOrderSense(sampleFilledForm)).limit(samplesSearch.limit);
			List<Sample> samples = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Sample p: samples){
				los.add(new ListObject(p.code, p.name));
			}
			
			return Results.ok(Json.toJson(los));
		}else{
			MongoDBResult<Sample> results = MongoDBDAO.find(InstanceConstants.CONTAINER_COLL_NAME, Sample.class, query)
					.sort(DatatableHelpers.getOrderBy(sampleFilledForm), FormUtils.getMongoDBOrderSense(sampleFilledForm));
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
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		if(StringUtils.isNotEmpty(samplesSearch.projectCode)){
			queryElts.add(DBQuery.is("projectCodes", samplesSearch.projectCode));
		}

		if(samplesSearch.projectCodes != null && samplesSearch.projectCodes.size() > 0){
			queryElts.add(DBQuery.in("projectCodes", samplesSearch.projectCodes));
		}

		return DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
	}
}
