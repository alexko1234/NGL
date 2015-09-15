package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableForm;
import views.components.datatable.DatatableResponse;

import com.mongodb.BasicDBObject;

import models.laboratory.common.description.Level;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import controllers.DocumentController;
import controllers.NGLControllerHelper;
import fr.cea.ig.MongoDBResult;

public class Experiments extends DocumentController<Experiment>{

	final Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	final List<String> defaultKeys =  Arrays.asList("categoryCode","code","inputContainerSupportCodes","instrument","outputContainerSupportCodes","projectCodes","protocolCode","reagents","sampleCodes","state","traceInformation","typeCode","atomicTransfertMethods.inputContainerUseds.contents");

	
	public Experiments() {
		super(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class);	
	}
	
	public Result list(){
		//Form<ExperimentSearchForm> experimentFilledForm = filledFormQueryString(experimentSearchForm,ExperimentSearchForm.class);
		//ExperimentSearchForm experimentsSearch = experimentFilledForm.get();
		ExperimentSearchForm experimentsSearch = filledFormQueryString(ExperimentSearchForm.class);
		BasicDBObject keys = getKeys(updateForm(experimentsSearch));
		DBQuery.Query query = getQuery(experimentsSearch);

		if(experimentsSearch.datatable){
			MongoDBResult<Experiment> results =  mongoDBFinder(experimentsSearch, query, keys);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(new DatatableResponse<Experiment>(experiments, results.count())));
		}else if (experimentsSearch.list){
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			if(null == experimentsSearch.orderBy)experimentsSearch.orderBy = "code";
			if(null == experimentsSearch.orderSense)experimentsSearch.orderSense = 0;				

			MongoDBResult<Experiment> results = mongoDBFinder(experimentsSearch, query, keys);
			List<Experiment> experiments = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Experiment p: experiments){					
				los.add(new ListObject(p.code, p.code));								
			}
			return Results.ok(Json.toJson(los));
		}else{
			if(null == experimentsSearch.orderBy)experimentsSearch.orderBy = "code";
			if(null == experimentsSearch.orderSense)experimentsSearch.orderSense = 0;
			MongoDBResult<Experiment> results = mongoDBFinder(experimentsSearch, query, keys);
			List<Experiment> experiments = results.toList();
			return ok(Json.toJson(experiments));
		}
	}

	private DatatableForm updateForm(ExperimentSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		}
		return form;
	}
	
	/**
	 * Construct the experiment query
	 * @param experimentSearch
	 * @return the query
	 */
	private DBQuery.Query getQuery(ExperimentSearchForm experimentSearch) {
		List<DBQuery.Query> queryElts = new ArrayList<DBQuery.Query>();
		Query query=null;

		Logger.info("Experiment Query : "+experimentSearch);

		
		if(CollectionUtils.isNotEmpty(experimentSearch.codes)){
			queryElts.add(DBQuery.in("code", experimentSearch.codes));
		}else if(StringUtils.isNotBlank(experimentSearch.code)){
			queryElts.add(DBQuery.regex("code", Pattern.compile(experimentSearch.code)));
		}

		if(StringUtils.isNotBlank(experimentSearch.typeCode)){
			queryElts.add(DBQuery.is("typeCode", experimentSearch.typeCode));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.projectCodes)){
			queryElts.add(DBQuery.in("projectCodes", experimentSearch.projectCodes));
		}

		if(StringUtils.isNotBlank(experimentSearch.projectCode)){
			queryElts.add(DBQuery.in("projectCodes", experimentSearch.projectCode));
		}

		if(null != experimentSearch.fromDate){
			queryElts.add(DBQuery.greaterThanEquals("traceInformation.creationDate", experimentSearch.fromDate));
		}

		if(null != experimentSearch.toDate){
			queryElts.add(DBQuery.lessThanEquals("traceInformation.creationDate", (DateUtils.addDays(experimentSearch.toDate, 1))));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.sampleCodes)){
			queryElts.add(DBQuery.in("sampleCodes", experimentSearch.sampleCodes));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.codes)){
			queryElts.add(DBQuery.in("codes", experimentSearch.codes));
		}

		if(StringUtils.isNotBlank(experimentSearch.containerSupportCode)){			
			List<DBQuery.Query> qs = new ArrayList<DBQuery.Query>();

			qs.add(DBQuery.regex("inputContainerSupportCodes",Pattern.compile(experimentSearch.containerSupportCode)));
			qs.add(DBQuery.regex("outputContainerSupportCodes",Pattern.compile(experimentSearch.containerSupportCode)));
			queryElts.add(DBQuery.or(qs.toArray(new DBQuery.Query[qs.size()])));
		}	

		if(StringUtils.isNotBlank(experimentSearch.sampleCode)){
			queryElts.add(DBQuery.in("sampleCodes", experimentSearch.sampleCode));
		}
/*
		if(CollectionUtils.isNotEmpty(experimentSearch.tags)){
			queryElts.add(DBQuery.in("atomicTransfertMethods.inputContainerUseds.contents.properties.tag.value", experimentSearch.tags));
		}
*/		
		if(CollectionUtils.isNotEmpty(experimentSearch.users)){
			queryElts.add(DBQuery.in("traceInformation.createUser", experimentSearch.users));
		}

		if(StringUtils.isNotBlank(experimentSearch.reagentOrBoxCode)){
			queryElts.add(DBQuery.or(DBQuery.regex("reagents.boxCode", Pattern.compile(experimentSearch.reagentOrBoxCode+"_|_"+experimentSearch.reagentOrBoxCode)),DBQuery.regex("reagents.code", Pattern.compile(experimentSearch.reagentOrBoxCode+"_|_"+experimentSearch.reagentOrBoxCode))));
		}

		if(CollectionUtils.isNotEmpty(experimentSearch.stateCodes)){
			queryElts.add(DBQuery.in("state.code", experimentSearch.stateCodes));
		}else if(StringUtils.isNotBlank(experimentSearch.stateCode)){
			queryElts.add(DBQuery.is("state.code", experimentSearch.stateCode));
		}

		if(StringUtils.isNotBlank(experimentSearch.instrument)){
			queryElts.add(DBQuery.is("instrument.code", experimentSearch.instrument));
		}else if(CollectionUtils.isNotEmpty(experimentSearch.instruments)){
			queryElts.add(DBQuery.in("instrument.code", experimentSearch.instruments));
		}
		
		// FDS 21/08/2015 ajout filtrage sur les types d'echantillon
		if(CollectionUtils.isNotEmpty(experimentSearch.sampleTypeCodes)){
			queryElts.add(DBQuery.in("atomicTransfertMethods.inputContainerUseds.contents.sampleTypeCode", experimentSearch.sampleTypeCodes));
		}
		
		queryElts.addAll(NGLControllerHelper.generateQueriesForProperties(experimentSearch.atomicTransfertMethodsInputContainerUsedsContentsProperties, Level.CODE.Content, "atomicTransfertMethods.inputContainerUseds.contents.properties"));

		if(queryElts.size() > 0){
			query = DBQuery.and(queryElts.toArray(new DBQuery.Query[queryElts.size()]));
		}

		return query;

	}
}
