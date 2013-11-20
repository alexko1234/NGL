package controllers.lists.api;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.State;
import models.laboratory.container.description.ContainerCategory;
import models.laboratory.container.description.ContainerSupportCategory;
import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.laboratory.instrument.description.Instrument;
import models.laboratory.instrument.description.InstrumentUsedType;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.project.instance.Project;
import models.laboratory.sample.instance.Sample;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import net.vz.mongodb.jackson.DBQuery;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import com.mongodb.BasicDBObject;

import controllers.authorisation.Permission;

import fr.cea.ig.MongoDBDAO;

public class Lists extends Controller{

	final static DynamicForm form = new DynamicForm();

	public static Result projects(){
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 0);//Don't need the _id field
		keys.put("name", 1);
		keys.put("code", 1);
		List<Project> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class,DBQuery.exists("_id"),keys).sort("code").toList();

		return Results.ok(Json.toJson(ListObject.projectToJsonObject(projects)));	
	}

	public static Result experimentTypes(){
		try {
			List<ListObject> exp = ExperimentType.find.findAllForList();
			return Results.ok(Json.toJson(exp));
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return  Results.internalServerError();

	}

	public static Result experimentTypesByCategory(String categoryCode) {
		try{
			List<ListObject> exp=ExperimentType.find.findByCategoryCode(categoryCode);
			return Results.ok(Json.toJson(exp));
		}catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return  Results.internalServerError();

	}

	public static Result experimentCategories(){
		try {
			List<ExperimentCategory> exp = ExperimentCategory.find.findAll();
			return Results.ok(Json.toJson(exp));

		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return  Results.internalServerError();
	}

	public static Result containerStates(){		
		try {
			List<ListObject> states = State.find.findAllForContainerList();
			return Results.ok(Json.toJson(states));
		}
		catch (DAOException e) {
			e.printStackTrace();
		}

		return  Results.internalServerError();
	}

	public static Result samples(String projectCode){
		BasicDBObject keys = new BasicDBObject();
		keys.put("_id", 0);//Don't need the _id field
		keys.put("name", 1);
		keys.put("code", 1);
		List<Sample> samples = MongoDBDAO.find(InstanceConstants.SAMPLE_COLL_NAME, Sample.class,DBQuery.is("projectCodes", projectCode),keys).sort("code").toList();

		return Results.ok(Json.toJson(ListObject.sampleToJsonObject(samples)));
	}

	public static Result processTypes(){
		try {
			List<ListObject> processusType = ProcessType.find.findAllForList();

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
			ExperimentType e =  ExperimentType.find.findByCode(experimentTypeCode);
			List<InstrumentUsedType> listInstruUsed = e.instrumentUsedTypes;
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


	@Permission(value={"reading"})
	public static Result resolutions(){
		try {
			DynamicForm inputForm = form.bindFromRequest();
			List<Resolution> resolutions = null;
			if(inputForm.get("typeCode") != null){
				resolutions = Resolution.find.findByTypeCode(inputForm.get("typeCode"));
			}else{
				resolutions =  Resolution.find.findAll();
			}
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

	public static Result categoryCodes(String instrumentUsedTypeCode){
		List<ListObject> list = new ArrayList<ListObject>();
		List<ContainerSupportCategory> containerSupportCategorys = null;
		try {
			InstrumentUsedType i = InstrumentUsedType.find.findByCode(instrumentUsedTypeCode);
			containerSupportCategorys = i.outContainerSupportCategories;

			for(ContainerSupportCategory c: containerSupportCategorys){
				list.add(new ListObject(c.code, c.name));
			}

			return Results.ok(Json.toJson(list));
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return  Results.internalServerError();
	}
}
