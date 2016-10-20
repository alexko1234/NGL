package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;

import org.mongojack.Aggregation;
import org.mongojack.Aggregation.Expression;
import org.mongojack.Aggregation.Pipeline;
import org.mongojack.AggregationResult;
import org.mongojack.DBProjection;
import org.mongojack.DBProjection.ProjectionBuilder;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;

import com.mongodb.AggregationOutput;
import com.mongodb.DBObject;

import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;

/**
 * 
 * @author michieli
 * 
 * Ne pourra pas être déployé tant que la base Mongo uat n'est pas upgradé comme celle de dev
 * 
 * [error] play - Cannot invoke the action, eventually got an error: com.mongodb.MongoCommandException: Command failed with 
 * error 15992: 'exception: disallowed field type Array in object expression (at 'reagents')' on server mongouat.genoscope.cns.fr:27018. 
 * The full response is { "errmsg" : "exception: disallowed field type Array in object expression (at 'reagents')", "code" : 15992, "ok" : 0.0 }
 */
public class ExperimentReagents extends Experiments{
	
	
	final Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	
	public ExperimentReagents() {
		super();	
	}
	
	@Permission(value={"reading"})
	public Result list(){
		
		ExperimentSearchForm experimentsSearch = filledFormQueryString(ExperimentSearchForm.class);
		DBQuery.Query query = getQuery(experimentsSearch);
		Pipeline<Expression<?>> pipeline = aggregation(query);
		JacksonDBCollection<Experiment, String> collection = MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class);
		AggregationResult<Experiment> ar =collection.aggregate(pipeline, Experiment.class);
		
		if(experimentsSearch.datatable){
			return ok(
				new StringChunks() {
					@Override
					public void onReady(Out<String> out) {
						AggregationOutput output = ar.getAggregationOutput();
						Iterable<DBObject> iterable = output.results();
			            Iterator<DBObject> iter = iterable.iterator();
			            int count = 0;
			            out.write("{\"data\":[");
					    while (iter.hasNext())
			            {
					    	count++;
			            	Experiment exp = collection.convertFromDbObject(iter.next(), Experiment.class);
			                out.write(Json.toJson(exp).toString());
			                if(iter.hasNext())out.write(",");
			            }					
			            out.write("],\"recordsNumber\":"+count);
					    out.write("}");
					    out.close();					
					}
				}
				).as("application/json");
			
		}else{
			return ok(
					new StringChunks() {
						@Override
						public void onReady(Out<String> out) {
							AggregationOutput output = ar.getAggregationOutput();
							Iterable<DBObject> iterable = output.results();
				            Iterator<DBObject> iter = iterable.iterator();
				            int count = 0;
				            out.write("[");
						    while (iter.hasNext())
				            {
						    	count++;
				            	Experiment exp = collection.convertFromDbObject(iter.next(), Experiment.class);
				                out.write(Json.toJson(exp).toString());
				                if(iter.hasNext())out.write(",");
				            }					
				            out.write("]");
						    out.close();					
						}
					}
					).as("application/json");
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
			
		pb.put("protocolCode", 1);
		pb.put("inputContainerSupportCodes", 1);
		pb.put("outputContainerSupportCodes", 1);
		
		query.and(stages.toArray(new DBQuery.Query[stages.size()]));
	
		// Aggregate // Not working on server mongouat.genoscope.cns.fr
		Pipeline<Expression<?>> pipeline = Aggregation.match(query)
				.unwind("reagents")
				.project(pb);
		
		return pipeline;
	}
}
