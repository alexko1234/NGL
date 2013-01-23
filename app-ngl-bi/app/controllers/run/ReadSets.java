package controllers.run;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.run.instance.Archive;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import controllers.utils.DataTableForm;
import fr.cea.ig.MongoDBDAO;

public class ReadSets extends Controller{

	final static Form<DataTableForm> datatableForm = form(DataTableForm.class);
	final static Form<ReadSet> readSetForm = form(ReadSet.class);
	
	public static Result createOrUpdate(String code, Integer laneNumber, String format){
		
		Form<ReadSet> filledForm = getFilledForm(format);
		
		if(!filledForm.hasErrors()) {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", code).is("lanes.number", laneNumber));	
			if(null == run){
				return notFound();
			}
			ReadSet readsetValue = filledForm.get();			
			BusinessValidationHelper.validateReadSet(filledForm.errors(),run, laneNumber, readsetValue, Constants.RUN_ILLUMINA_COLL_NAME, null);
			
			if(!filledForm.hasErrors()) {
				for(int i = 0; i < run.lanes.size(); i++){
					Lane l = run.lanes.get(i);
					if(l.number.equals(laneNumber)){ 
						int j = 0;
						boolean isFind = false;
						for(; l.readsets != null && j < l.readsets.size() ; j++){
							ReadSet r = l.readsets.get(j);
							if(readsetValue.code.equals(r.code)){	
								MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j, readsetValue);
								isFind = true;
								break;
							}
						}
						if(!isFind){MongoDBDAO.updatePush(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets", readsetValue);}
						break;
						
					}
				}				
			}
		}
		
		if (!filledForm.hasErrors()) {
			if ("json".equals(format)) {
				return ok(Json.toJson(filledForm.get()));
			} else {
				//return ok(run.render(filledForm, true));
				return ok(); //TODO must be complete
			}
		} else {
			if ("json".equals(format)) {
				return badRequest(filledForm.errorsAsJson());
				
			} else {
				//return badRequest(run.render(filledForm, true));
				return badRequest(); //TODO must be complete
				
			}
		}	
	}
	
	public static Result update(String readSetCode, String format){
		Form<ReadSet> filledForm = getFilledForm(format);
		if(!filledForm.hasErrors()){
			ReadSet readsetValue = filledForm.get();
		
			if(readsetValue.code.equals(readSetCode)){
				Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", readSetCode));
				
				if(null == run) {
					return notFound();
				} 
				
				BusinessValidationHelper.validateReadSet(filledForm.errors(), run,-1, readsetValue, Constants.RUN_ILLUMINA_COLL_NAME,null);
				
				boolean flagReadSet = false;
				if(!filledForm.hasErrors()) {
					for(int i=0; i<run.lanes.size() && !flagReadSet;i++){
						for(int j=0;j<run.lanes.get(i).readsets.size() && !flagReadSet;j++) {
							if(run.lanes.get(i).readsets.get(j).code.equals(readsetValue.code)){
								//ReadSet find
								flagReadSet = true;								
								MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j, readsetValue);
							}
						}
					}								
				}
			}
		}
		if (!filledForm.hasErrors()) {
			if ("json".equals(format)) {
				return ok(Json.toJson(filledForm.get()));
			} else {
				//return ok(run.render(filledForm, true));
				return ok(); //TODO must be complete
			}
		} else {
			if ("json".equals(format)) {
				return badRequest(filledForm.errorsAsJson());
				
			} else {
				//return badRequest(run.render(filledForm, true));
				return badRequest(filledForm.errorsAsJson());
				
			}
		}
	}

	public static Result list(){
		Form<DataTableForm> filledForm = datatableForm.bindFromRequest();
	
		List<Run> runs = MongoDBDAO.all(Constants.RUN_ILLUMINA_COLL_NAME, Run.class);
		ObjectNode result = Json.newObject();
		result.put("iTotalRecords", runs.size());
		result.put("iTotalDisplayRecords", runs.size());
		result.put("sEcho", filledForm.get().sEcho);
		result.put("aaData", Json.toJson(runs));
		
		return ok(Json.toJson(result));
	}
	
	public static Result show(String code,Integer laneNumber,String readSetCode,String format){
		Run runValue = MongoDBDAO.findByCode(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, code);
		Lane laneValue = null;
		ReadSet readset = null;
		for(Lane lane:runValue.lanes) {
			if(lane.number.equals(laneNumber)) {
				laneValue = lane;
				break;
			}
		}
		
		for(ReadSet r:laneValue.readsets){
			if(r.code.equals(readSetCode)) {
				readset=r;
				break;
			}
		}
		
		if(laneValue != null && readset != null){
			if("json".equals(format)){
				return ok(Json.toJson(readset));
			}else{
				Form<ReadSet> filledForm = readSetForm.fill(readset);				
				//return ok(run.render(filledForm, Boolean.FALSE));
				return ok(Json.toJson(readset));
			}			
		} else {
			return notFound();
		}		
	}
	
	public static Result showWithReadsetCode(String readSetCode,String format){
		Query object = DBQuery.is("lanes.readsets.code", readSetCode);
		Run run =  MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, object);		
		if(run == null) {
			return notFound();
		} 		
		ReadSet readsetValue = null;
		if(run.lanes != null){
			for(int i=0; i<run.lanes.size();i++){
				List<ReadSet> readsets = run.lanes.get(i).readsets;
				for(int j=0;j<readsets.size();j++) {
					if(readsets.get(j).code.equals(readSetCode)){
						readsetValue = run.lanes.get(i).readsets.get(j);
						break;
					}
				}
			}
		}
		
		if(readsetValue != null){
			if("json".equals(format)){
				return ok(Json.toJson(readsetValue));
			}else{
				Form<ReadSet> filledForm = readSetForm.fill(readsetValue);				
				//return ok(run.render(filledForm, Boolean.FALSE));
				return ok(Json.toJson(readsetValue));
			}			
		}else{
			return notFound();
		}		
	}
	
	public static Result needArchive(String format){
		List<Archive> archives = new ArrayList<Archive>();
		Query object = DBQuery.is("dispatch", true).or(DBQuery.elemMatch("lanes.readsets", DBQuery.is("archiveId",null))).or(DBQuery.elemMatch("lanes.readsets", DBQuery.notEquals("archiveDate", null)).and(DBQuery.notEquals("transfertEndDate", null).and(DBQuery.where("lanes.readsets.archiveDate<transfertEndDate"))));
		List<Run> runs = MongoDBDAO.find(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, object);
		for(Run run:runs){
			if(run.lanes != null){
				for(Lane lane:run.lanes){
					if(lane.readsets != null){
						for(ReadSet readset:lane.readsets){
							if((readset.archiveId == null) || (run.transfertEndDate!=null && readset.archiveDate!=null && run.transfertEndDate.after(readset.archiveDate)))
								archives.add(new Archive(readset.code,readset.path));
						}
					}
				}
			}
		}
		if(format.equals("json")){
			return ok(Json.toJson(archives));
		}else{
			return ok("archives");//TODO
		}
	}
	
	public static Result updateArchive(String readSetCode,String format){
		String archiveId = null;
		if("json".equals(format)){
			JsonNode json = request().body().asJson();	
			archiveId = json.get("archiveId").asText();
		} else {
			archiveId = request().getHeader("archiveId");
		}
		
		if(archiveId != null){
			Query object = DBQuery.is("lanes.readsets.code",readSetCode);
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, object);
			
			if(run == null) {
				return notFound();
			} 
			for(int i=0;i<run.lanes.size();i++){
				for(int j=0;j<run.lanes.get(i).readsets.size();j++){
					if(run.lanes.get(i).readsets.get(j).code.equals(readSetCode)) {
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("lanes."+i+".readsets."+j+".archiveId", archiveId); //Update
						map.put("lanes."+i+".readsets."+j+".archiveDate", new Date());
						MongoDBDAO.update(Constants.RUN_ILLUMINA_COLL_NAME, run, map);
						return ok();
					}
				}
			}
			return notFound();
		}
		else{
			if ("json".equals(format)) {
				return badRequest();				
			} else {
				//return badRequest(run.render(filledForm, true));
				return badRequest();				
			}
		}
	}
	
	
	//necessite une double gestion avec et sans json pour pouvoir faire fonctionner les 2 ensemble
	//ceci est du à la gestion des Map qui est différente entre json et spring binder			
	private static Form<ReadSet> getFilledForm(String format) {
		Form<ReadSet> filledForm;
		if("json".equals(format)){
			JsonNode json = request().body().asJson();			
			ReadSet readSetInput = Json.fromJson(json, ReadSet.class);
			filledForm = readSetForm.fill(readSetInput);	//bindJson ne marche pas !			
		}else{
			filledForm = readSetForm.bindFromRequest();
		}
		return filledForm;
	}
	
	
}
