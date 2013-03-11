package controllers.container;

import java.util.List;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;

import fr.cea.ig.MongoDBDAO;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import static play.data.Form.form;

import models.laboratory.container.instance.Container;

public class ContainersApis extends Controller {
	final static DynamicForm inputForm = form();
	
	public static Result list(String state){
		DynamicForm requestData = form().bindFromRequest();
		String projectCode= "";
		String experimentCode="";
		String containerState="";
		DBCursor<Container> cursor = MongoDBDAO.getCollection("Container",Container.class).find();//.skip(n)
		
		if(requestData.get("project") != null){
			 projectCode = Json.parse(requestData.get("project")).get("code").asText();
			 cursor.and(DBQuery.is("projetCodes", projectCode));
	    }
		
	    if(requestData.get("experiment") != null){
	    	experimentCode = Json.parse(requestData.get("experiment")).get("code").asText();
	    	cursor.and(DBQuery.is("fromExperimentTypeCodes", experimentCode));
	    }
	    
	    if(requestData.get("state") != null){
	    	containerState = Json.parse(requestData.get("state")).get("code").asText();
	    	cursor.and(DBQuery.is("stateCode", containerState));
	    }
	    
	    if(state!=""){
	    	cursor.and(DBQuery.is("stateCode", state));
	    }
	    
	    List<Container> containers = cursor.limit(100).toArray();
		
		return ok(Json.toJson(containers));
	}
	
}