package controllers.lists.api;

import java.util.List;

import com.mongodb.BasicDBObject;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import net.vz.mongodb.jackson.DBQuery;

import play.libs.Json;
import models.laboratory.common.description.State;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import fr.cea.ig.MongoDBDAO;

public class Lists extends Controller{
	public static Result projects(){
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 0);//Don't need the _id field
		keys.put("name", 1);
		keys.put("code", 1);
		List<Project> projects = MongoDBDAO.find("Project", Project.class,DBQuery.exists("_id"),keys).sort("code").toList();
		
		return Results.ok(Json.toJson(ListObject.projectToJsonObject(projects)));	
	}
	
	public static Result experimentTypes(){
		try {
			List<ListObject> exp = ExperimentType.findAllForList();
			
			return Results.ok(Json.toJson(exp));
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		return  Results.internalServerError();
	}
	
	public static Result containerStates(){
		try {
			List<ListObject> states = State.findAllForContainerList();
			
			return Results.ok(Json.toJson(states));
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		return  Results.internalServerError();
	}
	
	public static Result samples(String projectCode){
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 0);//Don't need the _id field
		keys.put("name", 1);
		keys.put("code", 1);
		List<Sample> samples = MongoDBDAO.find("Sample", Sample.class,DBQuery.is("projectCodes", projectCode),keys).sort("code").toList();
		
		return Results.ok(Json.toJson(ListObject.sampleToJsonObject(samples)));
	}
	
	public static Result processTypes(){
		try {
			List<ListObject> processusType = ProcessType.findAllForList();
			
			return Results.ok(Json.toJson(processusType));
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		return  Results.internalServerError();
	}
	
	public static Result containerCategoryCodes(){
		try {
			List<ListObject> containerCategory =  ContainerCategory.findAllForList();
			return Results.ok(Json.toJson(containerCategory));
		
		} catch (DAOException e) {
			e.printStackTrace();
		}
	
		return  Results.internalServerError();
	}
}