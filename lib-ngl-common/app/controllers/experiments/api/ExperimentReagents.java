package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mongojack.Aggregation;
import org.mongojack.Aggregation.Expression;
import org.mongojack.Aggregation.Pipeline;
import org.mongojack.AggregationResult;
import org.mongojack.DBProjection;
import org.mongojack.DBProjection.ProjectionBuilder;
import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;

import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.MongoDBResult;
import models.laboratory.common.instance.State;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import workflows.experiment.ExpWorkflows;

/**
 * 
 * @author michieli
 *
 */
public class ExperimentReagents extends Experiments{
	
	final static Form<State> stateForm = form(State.class);
	
	final Form<Experiment> experimentForm = form(Experiment.class);
	final Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	final List<String> defaultKeys =  Arrays.asList("categoryCode","code","inputContainerSupportCodes","instrument","outputContainerSupportCodes","projectCodes","protocolCode","reagents","sampleCodes","state","status","traceInformation","typeCode","atomicTransfertMethods.inputContainerUseds.contents");
	final ExpWorkflows workflows = Spring.getBeanOfType(ExpWorkflows.class);
	
	final List<String> typeCodesList = Arrays.asList("prepa-flowcell","prepa-fc-ordered","illumina-depot");
	final Date date = new Date("01/01/2016");
	
	public static final String calculationsRules ="calculations";
	
	public ExperimentReagents() {
		super();	
	}
	
	@Permission(value={"reading"})
	public Result list(){
		
		ExperimentSearchForm experimentsSearch = filledFormQueryString(ExperimentSearchForm.class);
		BasicDBObject keys = getKeys(updateForm(experimentsSearch));
		DBQuery.Query query = getQuery(experimentsSearch);
		Pipeline<Expression<?>> pipeline = aggregation(query);

		if(experimentsSearch.datatable){
			AggregationResult<Experiment> ar = MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).aggregate(pipeline, Experiment.class);
			Logger.debug("AggregationResults: " + ar.results().size());
			return ok(Json.toJson(new DatatableResponse<Experiment>(ar.results(), ar.results().size())));
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
	
	/**
	 * Construct the Aggregation
	 * @param query
	 * @return pipeline
	 */
	private Pipeline<Expression<?>> aggregation(DBQuery.Query query){

		List<DBQuery.Query> stages = new ArrayList<DBQuery.Query>();
		stages.add(DBQuery.exists("reagents.0")); // Only reagents	
		
		ProjectionBuilder pb = DBProjection.include("code");
		pb.put("reagents", new String[]{"$reagents"});
		pb.put("typeCode", 1);
		pb.put("instrument", 1);
		pb.put("projectCodes", 1);
		pb.put("sampleCodes", 1);
		pb.put("traceInformation", 1);
		
		// to preserve from NullPointerException
		if(query == null){
			Logger.debug("Empty search: \"Since "+date+"\"" );
			stages.add(DBQuery.greaterThanEquals("traceInformation.creationDate", date));
			query = DBQuery.and(stages.toArray(new DBQuery.Query[stages.size()]));
		}else{
			query.and(stages.toArray(new DBQuery.Query[stages.size()]));
		}
	
		// Aggregate
		Pipeline<Expression<?>> pipeline = Aggregation.match(query)
				.unwind("reagents")
				.project(pb);
		
		return pipeline;
	}
}
