package controllers.samples.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.sra.submit.common.instance.Sample;
import models.utils.InstanceConstants;

import org.apache.commons.collections.CollectionUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.DocumentController;
import fr.cea.ig.MongoDBResult;

public class Samples extends DocumentController<Sample>{

	final static Form<SamplesSearchForm> samplesSearchForm = form(SamplesSearchForm.class);

	public Samples() {
		super(InstanceConstants.SRA_SAMPLE_COLL_NAME, Sample.class);
	}

	public Result list()
	{
		SamplesSearchForm form = filledFormQueryString(SamplesSearchForm.class);
		Query query = getQuery(form);
		MongoDBResult<Sample> results = mongoDBFinder(form, query);							
		List<Sample> list = results.toList();
		return ok(Json.toJson(list));
	}
	
	private Query getQuery(SamplesSearchForm form) {
		List<Query> queries = new ArrayList<Query>();
		Query query = null;
		
		if (CollectionUtils.isNotEmpty(form.listSampleCodes)) { //all
			queries.add(DBQuery.in("code", form.listSampleCodes));
		}
		return query;
	}
}
