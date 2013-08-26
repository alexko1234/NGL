package controllers.archives.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;

import org.codehaus.jackson.JsonNode;

import controllers.Constants;

import play.Logger;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import fr.cea.ig.MongoDBDAO;

import controllers.CommonController;
/**
 * Controller that manage the readset archive
 * @author galbini
 *
 */
public class ReadSets extends CommonController{

	static DynamicForm form = new DynamicForm();
	/**
	 * 
	 * @param archive : default 2
	 * @return
	 */
	public static Result list(){
		
		Integer archive = getArchiveValue();
		List<Archive> archives = new ArrayList<Archive>();
		
		List<Run> runs = MongoDBDAO.find(Constants.RUN_ILLUMINA_COLL_NAME, Run.class,getQuery(archive)).toList();
		for(Run run:runs){
			if(run.lanes != null){
				for(Lane lane:run.lanes){
					if(lane.readsets != null){
						for(ReadSet readset:lane.readsets){							
							if(readset != null  && (archive.intValue() == 0 || 
									(archive.intValue() == 1 && readset.archiveId != null) ||
									(archive.intValue() == 2 && readset.archiveId == null))){
								archives.add(createArchive(run, readset));
							}
						}
					}
				}
			}
		}
		return ok(Json.toJson(new DatatableResponse(archives)));		
	}
	

	private static Integer getArchiveValue() {
		try{
			return Integer.valueOf(request().queryString().get("archive")[0]);
		}catch(Exception e){
			Logger.error(e.getMessage());
			return 2; // default value;
		}
	}

	private static Query getQuery(Integer archive) {
		Query query = null;
		if(archive.intValue() == 0){ //all
			query = DBQuery.is("dispatch", true); 
		}else if(archive.intValue() == 1){ //archive
			query = DBQuery.and(DBQuery.is("dispatch", true), DBQuery.elemMatch("lanes", DBQuery.elemMatch("readsets", DBQuery.exists("archiveId")))); 
		}else{ //not archive value = 2
			query = DBQuery.and(DBQuery.is("dispatch", true), DBQuery.elemMatch("lanes", DBQuery.elemMatch("readsets", DBQuery.notExists("archiveId"))));
		}
		 
		return query;
	}

	private static Archive createArchive(Run run, ReadSet readset) {
		Archive archive =  new Archive();
		archive.runCode=run.code;
		archive.projectCode=readset.projectCode;		
		archive.readSetCode = readset.code;
		archive.path = readset.path;
		archive.id = readset.archiveId;
		archive.date = readset.archiveDate;
		
		return archive;
	}
	
	public static Result save(String readSetCode){
		JsonNode json = request().body().asJson();	
		String archiveId = json.get("archiveId").asText();
		
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
			return badRequest();							
		}
	}
	
	
}
