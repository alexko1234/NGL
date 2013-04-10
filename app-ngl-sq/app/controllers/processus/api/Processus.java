package controllers.processus.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonNode;

import fr.cea.ig.MongoDBDAO;
import static play.data.Form.form;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import models.laboratory.container.instance.Basket;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.processus.description.ProcessType;
import  models.laboratory.processus.instance.Process;

public class Processus extends Controller{
	public static Result save(){
		JsonNode json = request().body().asJson();			
		String codeType = json.get("type").asText();
		String containerCode = json.get("container").asText();
		
		Container container = MongoDBDAO.findByCode("Container",Container.class,containerCode);
		
		if(container==null){
			return badRequest();
		}
		
		Process processus = new Process();
		processus.projectCode = container.projectCodes.get(0);
		processus.sampleCode = container.sampleCodes.get(0);
		processus.codeType = codeType;
		processus.containerInputCode = container.code;
		
		//processus.code = "testCode";//processtypecode/projectcode/samplecode/YYYYMMDDHHMMSS
		Date d = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMDDHHMMSS");
		processus.code = processus.codeType+"/"+processus.projectCode+"/"+processus.sampleCode+"/"+dateFormat.format(d);
		Logger.info("Code du nouveau processus: "+processus.code);
		
		MongoDBDAO.save("Process", processus);
		
		return ok(Json.toJson(processus));
	}
	
	public static Result addContainer() throws models.utils.dao.DAOException{
		JsonNode json = request().body().asJson();	
		String code = json.get("container").asText();
		//getTheProcessusType
		/*
		ProcessType processType = ProcessType.find.findByCode(process.code);
		
		Experiment experiment=null;
		for(String e : process.experimentCodes){
			//Experiment expriment = process.getExperiments().get(0);
			experiment = MongoDBDAO.findByCode("Experiment", Experiment.class, e);
			if(experiment.getExperimentType().code.equals(processType.code)){
				
			}
		}
		
		if(experiment == null){
			return badRequest();
		}
		
		MongoDBDAO.updatePush("Experiment", experiment ,"listInputOutputContainers.0.inputContainers",code);
		*/
		
		Container container = MongoDBDAO.findByCode("Container", Container.class, code);
		
		if(container == null){
			return badRequest();
		}
		
		MongoDBDAO.updateSet("Container", container, "stateCode", "A");
		
		return ok(Json.toJson(MongoDBDAO.findByCode("Container", Container.class, code)));
	}
}