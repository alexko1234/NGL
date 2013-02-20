package controllers.tpl.helper;

import java.util.List;

import com.mongodb.BasicDBObject;

import play.mvc.Result;
import play.mvc.Results;
import net.vz.mongodb.jackson.DBQuery;

import play.libs.Json;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.project.instance.Project;
import models.utils.dao.DAOException;
import fr.cea.ig.MongoDBDAO;

public class Lists {
	public static Result projects(){
		BasicDBObject keys = new BasicDBObject();
		keys.put("name", 1);
		List<Project> projects = MongoDBDAO.getCollection("project", Project.class).find(DBQuery.exists("_id"),keys).toArray();
		
		return Results.ok(Json.toJson(projects));	
	}
	
	public static Result experimentTypes(){
		try {
			List<ExperimentType> exp = ExperimentType.find.findAll();
			String json = "[";
			int i=0;
			for(ExperimentType e:exp){
				json += "{\"code\":\""+e.code+"\",\"label\":\""+e.name+"\"}";
				if(i!=exp.size()-1){
					json += ",";
				}
				i++;
			}
			json += "]";
			return  Results.ok(json);
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		return  Results.internalServerError();
	}
}
