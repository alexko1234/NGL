package controllers.runs.api;

import java.util.List;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;
/**
 * Controller around Run object
 * @author galbini
 *
 */
public class Runs extends Controller {
	
	final static Form<Run> runForm = form(Run.class);
	
	
	public static Result list(){
		List<Run> runs = MongoDBDAO.find(Constants.RUN_ILLUMINA_COLL_NAME, Run.class).toList();
		return ok(Json.toJson(runs));
	}
	
	public static Result get(String code){
		Run runValue = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(runValue != null){			
			return ok(Json.toJson(runValue));					
		}else{
			return notFound();
		}
	}
	
	public static Result save() {
		Form<Run> filledForm = getFilledForm();

		if (!filledForm.hasErrors()) {
			Run runValue = filledForm.get();
			if (null == runValue._id) {
				runValue.traceInformation = new TraceInformation();
				runValue.traceInformation.setTraceInformation("ngsrg");
			} else {
				runValue.traceInformation.setTraceInformation("ngsrg");
			}
			BusinessValidationHelper.validateRun(filledForm.errors(), runValue, Constants.RUN_ILLUMINA_COLL_NAME);
			if (!filledForm.hasErrors()) {
				runValue = MongoDBDAO.save(Constants.RUN_ILLUMINA_COLL_NAME,runValue);
				filledForm = filledForm.fill(runValue);
			}
		}

		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(filledForm.get()));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public static Result remove(String code){
		Run run = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(run == null){
			return badRequest();
		}		
		MongoDBDAO.delete(Constants.RUN_ILLUMINA_COLL_NAME, run);		
		return ok();
	}
	
	public static Result removeReadsets(String code){
		Run run  = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(run==null){
			return badRequest();
		}
		for(int i=0;run.lanes!=null && i<run.lanes.size();i++){
			for(int j=0;run.lanes.get(i).readsets != null && j<run.lanes.get(i).readsets.size();j++){
				MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",code),DBUpdate.unset("lanes."+i+".readsets."+j));
			}
			MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",code),DBUpdate.pull("lanes."+i+".readsets",null));	
		}		
		return ok();
	}
	
	public static Result removeFiles(String code){
		Run run  = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		if(run==null){
			return badRequest();
		}
		for(int i=0;run.lanes!=null && i<run.lanes.size();i++){
			for(int j=0;run.lanes.get(i).readsets != null && j<run.lanes.get(i).readsets.size();j++){
				for(int k=0;run.lanes.get(i).readsets.get(j).files!=null && k<run.lanes.get(i).readsets.get(j).files.size();k++){
					MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",code),DBUpdate.unset("lanes."+i+".readsets."+j+".files."+k));
				}
				MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",code),DBUpdate.pull("lanes."+i+".readsets."+j+".files",null));
				
			}
			
		}		
		return ok();
	}
	
	public static Result dispatch(String code){
		Run run = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);		
		if(run != null){
			JsonNode json = request().body().asJson();
			Logger.info("Dispatch run : "+code);
			boolean dispatch = json.get("dispatch").asBoolean();
			MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "dispatch", dispatch);
		}else{
			return badRequest();
		}		
		return ok();	
	}
	
	private static Form<Run> getFilledForm() {
		JsonNode json = request().body().asJson();
		Run runInput = Json.fromJson(json, Run.class);
		Form<Run> filledForm = runForm.fill(runInput); // bindJson ne marche pas
		return filledForm;
	}
}
