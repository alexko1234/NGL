package controllers.experiments.api;

import static play.data.Form.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.Aggregation;
import org.mongojack.AggregationResult;
import org.mongojack.DBQuery;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import controllers.DocumentController;
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
import views.components.datatable.DatatableForm;
import views.components.datatable.DatatableResponse;
import workflows.experiment.ExpWorkflows;


public class ExperimentReagents extends Experiments{
	
	final static Form<State> stateForm = form(State.class);
	
	final Form<Experiment> experimentForm = form(Experiment.class);
	final Form<ExperimentSearchForm> experimentSearchForm = form(ExperimentSearchForm.class);
	final List<String> defaultKeys =  Arrays.asList("categoryCode","code","inputContainerSupportCodes","instrument","outputContainerSupportCodes","projectCodes","protocolCode","reagents","sampleCodes","state","status","traceInformation","typeCode","atomicTransfertMethods.inputContainerUseds.contents");
	final ExpWorkflows workflows = Spring.getBeanOfType(ExpWorkflows.class);
	
	final List<String> typeCodesList = Arrays.asList("prepa-flowcell","prepa-fc-ordered","illumina-depot");
	final String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date("01/01/2016"));
	
	public static final String calculationsRules ="calculations";
	
	public ExperimentReagents() {
		super();	
	}
	
	@Permission(value={"reading"})
	public Result list(){
		//Form<ExperimentSearchForm> experimentFilledForm = filledFormQueryString(experimentSearchForm,ExperimentSearchForm.class);
		//ExperimentSearchForm experimentsSearch = experimentFilledForm.get();
		ExperimentSearchForm experimentsSearch = filledFormQueryString(ExperimentSearchForm.class);
		BasicDBObject keys = getKeys(updateForm(experimentsSearch));
		DBQuery.Query query = super.getQuery(experimentsSearch);

		if(experimentsSearch.datatable){
			List<Experiment> results = aggregation();
			
			List<Experiment> experiments = results;
			return ok(Json.toJson(new DatatableResponse<Experiment>(experiments, results.size())));
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
	
	private List<Experiment> aggregation(){
		
		DBObject matchStages = new BasicDBObject("typeCode", new BasicDBObject("$in", typeCodesList));
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
		List<Experiment> output = (List<Experiment>) MongoDBDAO.getCollection(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class).aggregate((Aggregation<Experiment>) pipeline);
		Logger.debug("This is my Output: " + output);
	
		return output;
		
	}
}
