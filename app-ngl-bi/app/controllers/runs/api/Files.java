package controllers.runs.api;

import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import play.data.Form;
import static play.data.Form.form;
import play.libs.Json;
import play.mvc.Result;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;

import controllers.CommonController;



public class Files extends CommonController{
	
	final static Form<File> fileForm = form(File.class);
	
	public static Result save(String readsetCode){
			
		Form<File> filledForm = getFilledForm(fileForm, File.class);
		
		if(!filledForm.hasErrors()) {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", readsetCode));	
			if(null == run){
				return notFound();
			}
			File file = filledForm.get();			
			
			if(!filledForm.hasErrors()) {
				for(int i = 0; i < run.lanes.size(); i++){
					Lane l = run.lanes.get(i);
					for(int j = 0; l.readsets != null && j < l.readsets.size() ; j++){
						ReadSet r = l.readsets.get(j);
						if(readsetCode.equals(r.code)){
							if(r.files == null){
								MongoDBDAO.updatePush(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j+".files", file);
							}else{
								boolean isFind = false;
								for(int k=0;k<r.files.size();k++){
									File f = r.files.get(k);
									if(f.fullname.equals(file.fullname)){
										isFind = true;
										MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j+".files."+k, file);
										break;
									}
								}
								if(!isFind){MongoDBDAO.updatePush(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j+".files", file);}
							
							}
							break;
						}
					}
				
				}
			}				
		}
		
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(filledForm.get()));			
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}
	}
	
	
	public static Result get(String readsetCode, String fullname){
		Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", readsetCode));	
		if(null == run){
			return notFound();
		}
		
		File file=null;
 		for(Lane l:run.lanes){
 			if(l.readsets!=null){
				for(ReadSet r:l.readsets){
					if(r.files!=null){
						if(r.code.equals(readsetCode)){
							for(File f:r.files){
								if(f.fullname.equals(fullname)){
									file = f;
									break;
								}
							}
						}
					}
				}
			}
 			
		}
 		
		if(file != null){
			return ok(Json.toJson(file));				
		}else{
			return notFound();
		}		
	}
	
	
	public static Result delete(String readsetCode, String fullname) {
		Boolean bUpdate = false;
		
		Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", readsetCode));	
		//Logger.info("run value "+run.toString());
		String runCode = null;
		if (run != null) {
			runCode = run.code;
			for(int i = 0; i < run.lanes.size(); i++){
				Lane l = run.lanes.get(i);
				for(int j = 0; l.readsets != null && j < l.readsets.size(); j++) {
					ReadSet r = l.readsets.get(j);
					for(int k=0; r.files != null && k<r.files.size(); k++) {
						File f = r.files.get(k);
						if(f.fullname.equals(fullname)) {
							 bUpdate = true;		
							// vide
							MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class, DBQuery.is("code",runCode), DBUpdate.unset("lanes."+i+".readsets."+j+".files."+k));		
						}
						
					}
					//supprime
					MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",runCode),DBUpdate.pull("lanes."+i+".readsets."+j+".files",null));	
							
				}
	 			if (bUpdate) break;
			}
		}
		if(bUpdate){
			return ok();				
		}else{
			return notFound();
		}		
	}
	
	
}
