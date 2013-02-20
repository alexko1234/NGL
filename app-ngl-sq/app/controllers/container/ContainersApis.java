package controllers.container;

import java.util.ArrayList;
import java.util.List;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;

import org.codehaus.jackson.node.ObjectNode;

import models.laboratory.common.description.CommonInfoType;
import fr.cea.ig.MongoDBDAO;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import models.laboratory.container.instance.Container;

public class ContainersApis extends Controller {
	final static DynamicForm inputForm = Controller.form();
	
	public static Result list(){
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
	    
	    List<Container> containers = cursor.limit(10).toArray();
		
		//return ok("[{\"code\": \"zefze\",\"type\": \"tube\",\"sample\":\"ABB_B\",\"experiment\":\"#EXPFRAG20130512121212\",\"state\":\"Available\",\"support\":\"la\"},{\"code\": \"acfd\",\"type\": \"tube\",\"sample\":\"ABB_B\",\"experiment\":\"#EXPFRAG20130512121212\",\"state\":\"Available\",\"support\":\"la\"},{\"code\": \"bhzf\",\"type\": \"tube\",\"sample\":\"ABB_B\",\"experiment\":\"#EXPFRAG20130512121213\",\"state\":\"Available\",\"support\":\"la\"}]");
		return ok(Json.toJson(containers));
	}
}