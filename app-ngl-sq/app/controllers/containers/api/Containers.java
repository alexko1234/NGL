package controllers.containers.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.sample.instance.Sample;

import models.utils.*;

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
import org.apache.commons.lang3.StringUtils;

public class Containers extends Controller {
	
	final static Form<ContainersSearch> containerForm = form(ContainersSearch.class);
	
	public static Result list(){
		
		ContainersSearch containersSearch = containerForm.bindFromRequest().get();
		
		DBCursor<Container> cursor = MongoDBDAO.getCollection("Container",Container.class).find();
		
		if(StringUtils.isNotEmpty(containersSearch.projectCode)){
			 cursor.and(DBQuery.is("projectCodes", containersSearch.projectCode));
	    }
		
	    if(StringUtils.isNotEmpty(containersSearch.experimentCode)){
	    	cursor.and(DBQuery.in("fromExperimentTypeCodes", containersSearch.experimentCode)).or(DBQuery.is("fromExperimentTypeCodes", null));
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.stateCode)){
	    	cursor.and(DBQuery.is("stateCode", containersSearch.stateCode));
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.sampleCode)){
	    	cursor.and(DBQuery.in("sampleCodes", containersSearch.sampleCode));
	    }
	    
	    if(StringUtils.isNotEmpty(containersSearch.processTypeCode)){
	    	ProcessType processType = null;
	    	
	    	try{
	    		processType = ProcessType.find.findByCode(containersSearch.processTypeCode);
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
	    
	    Logger.info("Project code: "+containersSearch.projectCode);
	    Logger.info("Experiment code: "+containersSearch.experimentCode);
	    Logger.info("Container state: "+containersSearch.stateCode);
	    Logger.info("Container sample: "+containersSearch.sampleCode);
	    Logger.info("Container process: "+containersSearch.processTypeCode);
	    
	    List<Container> containers = cursor.limit(100).toArray();
		
		return ok(Json.toJson(new DatatableResponse(containers)));
	}
	
	public static Result samplesProjectsList(){
		JsonNode json = request().body().asJson();		
		String myJson = json.get("containers").toString();
		Map<ListObject,ListObject> projects = new HashMap<ListObject,ListObject>(); 
		
		try{
			JSONArray the_json_array = new JSONArray(myJson);
			
			for(int i=0;i<the_json_array.length();i++){
				JSONObject obj = (JSONObject) the_json_array.get(i);
				String code = obj.getString("code");
				Logger.info(code);
				Container containers = MongoDBDAO.findByCode("Container", Container.class, code);
				
				for(String s : containers.sampleCodes){
					Sample sample = MongoDBDAO.findByCode("Sample",Sample.class,s);
					for(String p:sample.projectCodes){
						Project project = MongoDBDAO.findByCode("Project",Project.class,p);
						projects.put(new ListObject(sample.code,sample.name), new ListObject(project.code,project.name));
					}
				}
			}
		}catch(JSONException jse){
			Logger.info("Error json "+jse);
		}
		return ok(Json.toJson(projects));
	}
}