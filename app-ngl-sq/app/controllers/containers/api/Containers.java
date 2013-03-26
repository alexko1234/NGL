package controllers.containers.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.sample.instance.Sample;

import org.codehaus.jackson.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import static play.data.Form.form;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processus.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.utils.ListObject;
import models.utils.dao.DAOException;

public class Containers extends Controller {
	
	final static DynamicForm inputForm = form();
	
	public static Result list(){
		DynamicForm requestData = form().bindFromRequest();
		
		String projectCode = "";
		String experimentCode = "";
		String containerState = "";
		String containerSample = "";
		String containerProcess = "";
		
		DBCursor<Container> cursor = MongoDBDAO.getCollection("Container",Container.class).find();
		
		if(requestData.get("project") != null && requestData.get("project")!=""){
			 projectCode = Json.parse(requestData.get("project")).get("code").asText();
			 cursor.and(DBQuery.is("projectCodes", projectCode));
	    }
		
	    if(requestData.get("experiment") != null && requestData.get("experiment")!=""){
	    	experimentCode = Json.parse(requestData.get("experiment")).get("code").asText();
	    	cursor.and(DBQuery.in("fromExperimentTypeCodes", experimentCode)).or(DBQuery.is("fromExperimentTypeCodes", null));
	    }
	    
	    if(requestData.get("state") != null && requestData.get("state")!=""){
	    	containerState = Json.parse(requestData.get("state")).get("code").asText();
	    	cursor.and(DBQuery.is("stateCode", containerState));
	    }
	    
	    if(requestData.get("sample") != null && requestData.get("sample")!=""){
	    	containerSample = Json.parse(requestData.get("sample")).get("code").asText();
	    	cursor.and(DBQuery.in("sampleCodes", containerSample));
	    }
	    
	    if(requestData.get("process") != null && requestData.get("process")!=""){
	    	containerProcess = Json.parse(requestData.get("process")).get("code").asText();
	    	ProcessType processType = null;
	    	
	    	try{
	    		processType = ProcessType.find.findByCode(containerProcess);
	    	}catch(DAOException e){
	    		return internalServerError();
	    	}
	    	
	    	List<String> listePrevious = new ArrayList<String>();  
	    	for(ExperimentType e:processType.experimentTypes){
	    		for(ExperimentType et:e.previousExperimentTypes){
	    			listePrevious.add(et.code);
	    		}
	    	}
	    	
	    	cursor.and(DBQuery.in("fromExperimentTypeCodes", listePrevious));
	    }
	    
	    Logger.info("Project code: "+projectCode);
	    Logger.info("Experiment code: "+experimentCode);
	    Logger.info("Container state: "+containerState);
	    Logger.info("Container sample: "+containerSample);
	    Logger.info("Container process: "+containerProcess);
	    
	    List<Container> containers = cursor.limit(100).toArray();
		
		return ok(Json.toJson(new DatatableResponse(containers)));
	}
	
	public static Result samplesProjectsList(){
		DynamicForm requestData = form().bindFromRequest();
		String containersJson = requestData.get("containers");
		Map<ListObject,ListObject> projects = new HashMap<ListObject,ListObject>(); 
		
		try{
			JSONObject myjson = new JSONObject(containersJson);
			JSONArray the_json_array = myjson.getJSONArray("containers");
			for(int i=0;i<the_json_array.length();i++){
				String code = (String) the_json_array.get(i);
				Container containers = MongoDBDAO.findByCode("Container", Container.class, code);
				for(String s : containers.sampleCodes){
					Sample sample = MongoDBDAO.findByCode("Sample",Sample.class,s);
					/*for(String p:sample.projectCode){
						Project project = MongoDBDAO.findByCode("Project",Project.class,p);
						projects.put(new ListObject(sample.code,sample.name), new ListObject(project.code,project.name));
					}*/
					Project project = MongoDBDAO.findByCode("Project",Project.class,sample.projectCode);
					projects.put(new ListObject(sample.code,sample.name), new ListObject(project.code,project.name));
					
				}
			}
		}catch(JSONException jse){
			Logger.info("Error json "+jse);
		}
		
		Logger.info(Json.toJson(projects).toString());
		return ok(Json.toJson(projects));
	}
}