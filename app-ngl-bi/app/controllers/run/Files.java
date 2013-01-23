package controllers.run;

import org.codehaus.jackson.JsonNode;

import net.vz.mongodb.jackson.DBQuery;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import validation.BusinessValidationHelper;
import controllers.utils.DataTableForm;
import fr.cea.ig.MongoDBDAO;

public class Files extends Controller{
	
	final static Form<DataTableForm> datatableForm = form(DataTableForm.class);
	final static Form<File> fileForm = form(File.class);
	
	public static Result createOrUpdate(String readsetCode, String format){
			
		Form<File> filledForm = getFilledForm(format);
		
		if(!filledForm.hasErrors()) {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", readsetCode));	
			if(null == run){
				return notFound();
			}
			File file = filledForm.get();			
			
			if(!filledForm.hasErrors()) {
				for(int i = 0; i < run.lanes.size(); i++){
					Lane l = run.lanes.get(i);
					int j = 0;
					boolean isFind = false;
					for(; l.readsets != null && j < l.readsets.size() ; j++){
						ReadSet r = l.readsets.get(j);
						if(readsetCode.equals(r.code)){
							if(r.files == null){
								MongoDBDAO.updatePush(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j+".files", file);
							}else{
								for(int k=0;k<r.files.size();k++){
									File f = r.files.get(k);
									if(f.fullname.equals(file.fullname)){
										MongoDBDAO.updateSet(Constants.RUN_ILLUMINA_COLL_NAME, run, "lanes."+i+".readsets."+j+".files."+k, file);
									}
								}
							}
							isFind = true;
							break;
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
				return badRequest(); //TODO must be complete
				
			}
		}
	}
	
	
	public static Result show(String code,String fullname, String format){
		Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", code));	
		//Logger.info("run value "+run.toString());
		if(null == run){
			return notFound();
		}
		
		File file=null;
 		for(Lane l:run.lanes){
 			if(l.readsets!=null){
				for(ReadSet r:l.readsets){
					if(r.files!=null){
						if(r.code.equals(code)){
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
			if("json".equals(format)){
				return ok(Json.toJson(file));
			}else{
				Form<File> filledForm = fileForm.fill(file);			
				//return ok(run.render(filledForm, Boolean.FALSE));
				return ok(); //TODO must be complete
			}			
		}else{
			return notFound();
		}		
	}
	
	//necessite une double gestion avec et sans json pour pouvoir faire fonctionner les 2 ensemble
	//ceci est du à la gestion des Map qui est différente entre json et spring binder			
	private static Form<File> getFilledForm(String format) {
		Form<File> filledForm;
		if("json".equals(format)){
			JsonNode json = request().body().asJson();			
			File fileInput = Json.fromJson(json, File.class);
			filledForm = fileForm.fill(fileInput);	//bindJson ne marche pas !			
		}else{
			filledForm = fileForm.bindFromRequest();
		}
		return filledForm;
	}
}
