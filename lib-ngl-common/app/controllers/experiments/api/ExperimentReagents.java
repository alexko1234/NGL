package controllers.experiments.api;

// import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;
import fr.cea.ig.util.Streamer;
//import static fr.cea.ig.util.Streamer.IStreamer.write;

// import java.io.IOException;
// import java.io.OutputStream;
// import java.io.PrintWriter;
import java.util.ArrayList;
// import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

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

// import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import workflows.experiment.ExpWorkflows;

import com.google.common.collect.Iterators;
// import com.mongodb.AggregationOutput;
import com.mongodb.DBObject;

// import akka.actor.ActorRef;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.mongo.MongoStreamer;
import fr.cea.ig.play.NGLContext;

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
public class ExperimentReagents extends Experiments {
	
	// private final Form<ExperimentSearchForm> experimentSearchForm; // = form(ExperimentSearchForm.class);
	
	@Inject
	public ExperimentReagents(NGLContext ctx, ExpWorkflows workflows) {
		super(ctx,workflows);	
		// experimentSearchForm = ctx.form(ExperimentSearchForm.class);
	}
	
	@Permission(value={"reading"})
	public Result list() {
		
		ExperimentSearchForm experimentsSearch = filledFormQueryString(ExperimentSearchForm.class);
		DBQuery.Query query = getQuery(experimentsSearch);
		Pipeline<Expression<?>> pipeline = aggregation(query);
		JacksonDBCollection<Experiment, String> collection = MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class);
		AggregationResult<Experiment> ar =collection.aggregate(pipeline, Experiment.class);
		
		if(experimentsSearch.datatable){
			/*return ok(
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
				).as("application/json");*/
			/*return Streamer.okStream(new Streamer.IStreamer() {
				@Override
				public void streamTo(ActorRef out) {
					AggregationOutput output = ar.getAggregationOutput();
					Iterable<DBObject> iterable = output.results();
					Iterator<DBObject> iter = iterable.iterator();
					int count = 0;
					write(out,"{\"data\":[");
					while (iter.hasNext()) {
						count++;
						Experiment exp = collection.convertFromDbObject(iter.next(), Experiment.class);
						write(out,Json.toJson(exp).toString());
						if (iter.hasNext()) write(out,",");
					}					
					write(out,"],\"recordsNumber\":"+count);
					write(out,"}");
				}
			});*/
			// WARNING: Check, this iterates twice over the results
			Iterable<DBObject> results = ar.getAggregationOutput().results();
			int count = Iterators.size(results.iterator());
			return Streamer.okStream(Source.from(results)
					.map(r -> { Experiment exp = collection.convertFromDbObject(r, Experiment.class);
						        return Json.toJson(exp).toString(); })
					.intersperse("{\"data\":[", ",", "],\"recordsNumber\":"+count+"}")
					.map(r -> { return ByteString.fromString(r); }));
			
		} else {
			/*return ok(
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
					).as("application/json");*/
			/*return Streamer.okStream(new Streamer.IStreamer() {
				@Override
				public void streamTo(ActorRef out) {
					AggregationOutput output = ar.getAggregationOutput();
					Iterable<DBObject> iterable = output.results();
					Iterator<DBObject> iter = iterable.iterator();
					int count = 0;
					write(out,"[");
					while (iter.hasNext()) {
						count++;
						Experiment exp = collection.convertFromDbObject(iter.next(), Experiment.class);
						write(out,Json.toJson(exp).toString());
						if (iter.hasNext()) write(out,",");
					}					
					write(out,"]");				
				}
			});*/
			// WARNING: check function
			Iterable<DBObject> results = ar.getAggregationOutput().results();
			/*return Streamer.okStream(Source.from(results)
					.map(r -> { Experiment exp = collection.convertFromDbObject(r, Experiment.class);
						        return Json.toJson(exp).toString(); })
					.intersperse("[", ",", "]")
					.map(r -> { return ByteString.fromString(r); }));*/
			
			return MongoStreamer.okStream(Source.from(results), r -> { return collection.convertFromDbObject(r, Experiment.class); });
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
