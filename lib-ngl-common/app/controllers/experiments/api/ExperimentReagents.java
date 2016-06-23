package controllers.experiments.api;

import static play.data.Form.form;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.Aggregation;
import org.mongojack.AggregationResult;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import controllers.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableForm;
import views.components.datatable.DatatableResponse;


public class ExperimentReagents extends Experiments{
	
	/*
	final Form<Experiment> experimentForm = form(Experiment.class);
	final Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	final List<String> defaultKeys = Arrays.asList("categoryCode","code","inputContainerSupportCodes","instrument","outputContainerSupportCodes","projectCodes","protocolCode","reagents","sampleCodes","state","status","traceInformation","typeCode","atomicTransfertMethods.inputContainerUseds.contents");
	*/
	final Form<Experiment> reagentForm = form(Experiment.class);
	final Form<ExperimentReagentSearchForm> experimentReagentSearchForm = form(ExperimentReagentSearchForm.class);
	
	final static List<String> typeCodeList = Arrays.asList(new String[]{"prepa-flowcell", "prepa-fc-ordered", "illumina-depot"});
	final static Date date = new Date("01/01/2016");
	
	public ExperimentReagents() {
		super();
	}
	
	@Permission(value={"reading"})
	public Result list() {
		ExperimentReagentSearchForm experimentReagentSearch = filledFormQueryString(ExperimentReagentSearchForm.class);
		BasicDBObject keys = getKeys(updateForm(experimentReagentSearch));
		
		if(experimentReagentSearch.datatable){
		
			AggregationResult<Experiment> results = aggregation();
			List<Experiment> reagents = (List<Experiment>) results;
			
			return ok(Json.toJson(new DatatableResponse<Experiment>(reagents, reagents.size())));
			/*
		}else if(experimentReagentSearch.list){
			keys = new BasicDBObject();
			keys.put("_id", 0);//Don't need the _id field
			keys.put("code", 1);
			MongoDBResult<Experiment> results = mongoDBFinder(experimentReagentSearch, keys);:
			List<Experiment> experiments = results.toList();
			List<ListObject> los = new ArrayList<ListObject>();
			for(Experiment p: experiments){					
				los.add(new ListObject(p.code, p.code));								
			}
			return Results.ok(Json.toJson(los));
			*/
		}else{

		}
		return ok();
	}

	private DatatableForm updateForm(ExperimentReagentSearchForm form) {
		if(form.includes.contains("default")){
			form.includes.remove("default");
			form.includes.addAll(defaultKeys);
		}
		return form;
	}
	
	private AggregationResult<Experiment> aggregation(){
		
		DBObject matchStages = new BasicDBObject("typeCode", new BasicDBObject("$in", typeCodeList));
		matchStages.put("traceInformation.creationDate", new BasicDBObject("$gt", date));
		matchStages.put("reagents.0", new BasicDBObject("$exists", 1));
		
		DBObject projectFields = new BasicDBObject("typeCode", 1);
		projectFields.put("_id", 0);
		projectFields.put("code", 1);
		projectFields.put("creationDate", "$traceInformation.creationDate");
		projectFields.put("reagentKitCatalogCode","$reagents.kitCatalogCode");
		projectFields.put("reagentBoxCatalogCode","$reagents.boxCatalogCode");
		projectFields.put("reagentBoxCode","$reagents.boxCode");
		projectFields.put("reagentReagentCatalogCode","$reagents.reagentCatalogCode");	
		projectFields.put("reagentReagentCode","$reagents.code");	
		projectFields.put("reagentDescription","$reagents.description");
			
		DBObject sortFields = new BasicDBObject("typeCode", 1);
		sortFields.put("creationDate", 1);
		
		DBObject match = new BasicDBObject("$match", matchStages);
		DBObject unwind = new BasicDBObject("$unwind","$reagents");
		DBObject project = new BasicDBObject("$project", projectFields);
		DBObject sort = new BasicDBObject("$sort", sortFields);

		List<DBObject> pipeline = new LinkedList<DBObject>();
		pipeline.add(match);
		pipeline.add(unwind);
		pipeline.add(project);
		pipeline.add(sort);
		
		//AggregationResult<Reagent> result = MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).aggregate((Aggregation<Reagent>) pipeline);
		AggregationResult<Experiment> output = MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).aggregate((Aggregation<Experiment>) pipeline);
		Logger.debug("This is my Output: " + output);
	
		return output;
		
	}
}
