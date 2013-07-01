package controllers.lists.api;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;

import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import net.vz.mongodb.jackson.DBQuery;

import play.api.modules.spring.Spring;
import play.libs.Json;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.common.description.dao.StateDAO;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.experiment.description.dao.ExperimentTypeDAO;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.instrument.description.dao.InstrumentUsedTypeDAO;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import com.mongodb.BasicDBObject;

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
			List<ListObject> exp = Spring.getBeanOfType(ExperimentTypeDAO.class).findAllForList();	
			return Results.ok(Json.toJson(exp));
	}
	
	public static Result containerStates(){		
		List<ListObject> states = Spring.getBeanOfType(StateDAO.class).findAllForContainerList();			
		return Results.ok(Json.toJson(states));		
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
	
	public static Result instrumentUsedTypes(String experimentTypeCode){
		List<ListObject> list = new ArrayList<ListObject>();
		try {
			List<InstrumentUsedType> listInstruUsed = ExperimentType.find.findByCode(experimentTypeCode).instrumentUsedTypes;
			for(InstrumentUsedType instruUsedType: listInstruUsed){
				list.add(new ListObject(instruUsedType.code,instruUsedType.name));
			}
			
			return Results.ok(Json.toJson(list));
		}catch (DAOException e) {
		e.printStackTrace();
		}
	
		
		return  Results.internalServerError();
	}
	
	
	public static Result instruments(String instrumentUsedTypeCode){
		List<ListObject> list = new ArrayList<ListObject>();
		try {
				InstrumentUsedType instrumentUsedType = InstrumentUsedType.find.findByCode(instrumentUsedTypeCode);
				for(Instrument instru:instrumentUsedType.instruments){
					list.add(new ListObject(instru.code,instru.name));
				}

			return Results.ok(Json.toJson(list));
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
	
		return  Results.internalServerError();
	}
	
	
	public static Result protocols(String experimentTypeCode){
		try {
			List<Protocol> protocols = ExperimentType.find.findByCode(experimentTypeCode).protocols;
			List<ListObject> list = new ArrayList<ListObject>();
			
			for(Protocol protocol:protocols){
				list.add(new ListObject(protocol.code, protocol.name));
			}
			
			return Results.ok(Json.toJson(list));
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		return  Results.internalServerError();
	}
	
	
	public static Result resolutions(){
		try {
			List<Resolution> resolutions = Resolution.find.findAll();
			List<ListObject> list = new ArrayList<ListObject>();
			
			for(Resolution resolution:resolutions){
				list.add(new ListObject(resolution.code, resolution.name));
			}
			
			return Results.ok(Json.toJson(list));
			
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		return  Results.internalServerError();
	}
}