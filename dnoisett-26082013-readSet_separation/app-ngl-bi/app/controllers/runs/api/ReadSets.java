package controllers.runs.api;

import static play.data.Form.form;
import java.util.List;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import validation.utils.ContextValidation;
import controllers.CommonController;
import controllers.Constants;
import fr.cea.ig.MongoDBDAO;





public class ReadSets extends CommonController{

	final static Form<ReadSet> readSetForm = form(ReadSet.class);
	
	public static Result save(String code, Integer laneNumber){
		
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		if(!filledForm.hasErrors()) {
			Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("code", code).is("lanes.number", laneNumber));	
			if(null == run){
				return notFound();
			}
			ReadSet readsetValue = filledForm.get();	
			
			//beginning code for validation
			ctxVal.putObject("run", run);
			String rootKeyName1 = "";
			String rootKeyName2 = "";
			for(int i = 0; i < run.lanes.size(); i++){
				Lane l = run.lanes.get(i);
				rootKeyName1 = "lanes["+i+"]";
				if(l.number.equals(laneNumber)){ 
					ctxVal.putObject("lane",l);
					for(int j=0; l.readsets != null && j < l.readsets.size() ; j++){
						rootKeyName2 = rootKeyName1 + ".readsets["+j+"]"; 
						
					}
					break;
				}
			}
			ctxVal.setRootKeyName(rootKeyName2);
			readsetValue.validate(ctxVal);
			//end
			
			
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
			return ok(Json.toJson(filledForm.get()));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}
	}
	
	public static Result update(String readSetCode){
		
		Form<ReadSet> filledForm = getFilledForm(readSetForm, ReadSet.class);
		
		ContextValidation ctxVal = new ContextValidation(filledForm.errors()); 
		
		if(!filledForm.hasErrors()){
			ReadSet readsetValue = filledForm.get();
		
			if(readsetValue.code.equals(readSetCode)){
				Run run = MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, DBQuery.is("lanes.readsets.code", readSetCode));
				
				if(null == run) {
					return notFound();
				} 
				
				//beginning code for validation
				ctxVal.putObject("run", run);
				String rootKeyName = "";
				boolean flagReadSet = false;
				for(int i=0; i<run.lanes.size() && !flagReadSet; i++){
					for(int j=0;j<run.lanes.get(i).readsets.size() && !flagReadSet; j++) {
						if(run.lanes.get(i).readsets.get(j).code.equals(readsetValue.code)){
							//ReadSet find
							flagReadSet = true;		
							rootKeyName = "lanes["+i+"].readsets["+j+"]";
							ctxVal.putObject("lane",run.lanes.get(i));
						}
					}
				}
				ctxVal.setRootKeyName(rootKeyName);
				readsetValue.validate(ctxVal);
				//end
				
				flagReadSet = false;
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
			return ok(Json.toJson(filledForm.get()));			
		} else {
			return badRequest(filledForm.errorsAsJson());			
		}
	}
	
	
	
	public static Result get(String readSetCode){
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
			return ok(Json.toJson(readsetValue));					
		}else{
			return notFound();
		}		
	}
	

	
	public static Result delete(String readSetCode){
		//Logger.info("in delete "+readSetCode );
		Boolean bUpdate = false;
		Query object = DBQuery.is("lanes.readsets.code", readSetCode);
		Run run =  MongoDBDAO.findOne(Constants.RUN_ILLUMINA_COLL_NAME, Run.class, object);	
		if(run==null){
			return badRequest();
		}
		String runCode = run.code;
		for(int i=0;run.lanes!=null && i<run.lanes.size();i++){
			for(int j=0;run.lanes.get(i).readsets != null && j<run.lanes.get(i).readsets.size();j++){
				// vide
				if (run.lanes.get(i).readsets.get(j).code.equals(readSetCode) ) {
					MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",runCode),DBUpdate.unset("lanes."+i+".readsets."+j));
					bUpdate = true;
				}
			}
			//supprime
			MongoDBDAO.updateSetArray(Constants.RUN_ILLUMINA_COLL_NAME,  Run.class,DBQuery.is("code",runCode),DBUpdate.pull("lanes."+i+".readsets",null));	

		}		
		if (bUpdate) {
			return ok();
		}
		else {
			return notFound();
		}
	}
	
	
	
}
