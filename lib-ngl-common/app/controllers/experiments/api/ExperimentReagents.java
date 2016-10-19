package controllers.experiments.api;

import static play.data.Form.form;

import java.text.SimpleDateFormat;
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
import fr.cea.ig.MongoDBDatatableResponseChunks;
import fr.cea.ig.MongoDBResult;
import models.laboratory.common.instance.State;
import models.laboratory.container.instance.Container;
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

		if(experimentsSearch.datatable){
			AggregationResult<Experiment> ar = MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).aggregate(pipeline, Experiment.class);
			return ok(Json.toJson(new DatatableResponse<Experiment>(ar.results(), ar.results().size())));
		}else{
			AggregationResult<Experiment> ar = MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).aggregate(pipeline, Experiment.class);
			List<Experiment> experiments = ar.results();
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
