package controllers.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.CommonInfoType;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;

import org.codehaus.jackson.node.ObjectNode;

import controllers.utils.DataTableForm;

import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import fr.cea.ig.MongoDBDAO;


public class Experiments extends Controller {
	final static Form<DataTableForm> datatableForm = form(DataTableForm.class);
	
    final static Form<Experiment> experimentForm = form(Experiment.class);

	//@With(SecurityAction.class)
	public static Result home() {	
		
		return ok(views.html.experiment.experiments.render(datatableForm));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result list(){
		Form<DataTableForm> filledForm = datatableForm.bindFromRequest();
		ObjectNode result = Json.newObject();
		List<Map<String, String>> results = new ArrayList<Map<String, String>>();
		
		
		if(filledForm.get().sSearch.get(2).trim().length() > 0){
			System.out.println("Collection :"+filledForm.get().sSearch.get(2).trim());
			List<Experiment> listExp=MongoDBDAO.all(ExperimentType.findExpTypeById(Long.valueOf(filledForm.get().sSearch.get(2).trim())).collectionName, Experiment.class);
			
			for (int i = 0; i < listExp.size(); i++) {
				Map<String,String> r= new HashMap<String, String>();
				
				r.put("id", listExp.get(i)._id);
				r.put("name", listExp.get(i).code);
				r.put("type", listExp.get(i).experimentTypeCode);
				results.add(r);
			}
		//}
		/*	DBObject obj=null;	
			DBCollection coll=MongoConnection.getConnection().getCollection("ExperimentDBBuilder");
			DBCursor cursor=coll.find();

			while (cursor.hasNext()){
				obj=cursor.next();
				Map<String,String> r= new HashMap<String, String>();
				r.put("id", obj.get("_id").toString());
				r.put("name",obj.get("name").toString());
				r.put("type","type");
				r.put("P1", "P1");
				r.put("P2", "P2");
				results.add(r);

			}
		
			
		for(int i = 0; i < 20; i++){
			Map<String, String> r = new HashMap<String, String>();
			r.put("id", ""+i);
			r.put("name", "name"+i);
			r.put("type", "type"+i);
			r.put("P1", "P1_"+i);
			r.put("P2", "P2_"+i);
			results.add(r);
		}
			 
		} */
		result.put("iTotalRecords", listExp.size());
		result.put("iTotalDisplayRecords", results.size());
		result.put("sEcho", filledForm.get().sEcho);
		result.put("aaData", Json.toJson(results));
		}
		return ok(result);
		

	}

	
	public static Result add(){
		Form<Experiment> defaultForm = experimentForm.fill(new Experiment()); //put default value
		return ok(views.html.experiment.experiment.render(defaultForm, true));
	}
	

	public static Result createOrUpdate(String format){
		Form<Experiment> filledForm = experimentForm.bindFromRequest();
		
		if(filledForm.hasErrors()) {
			return badRequest(views.html.experiment.experiment.render(filledForm,true));
		} else {
			
			Experiment exp = filledForm.get();
			CommonInfoType comm=null;
			//Get type informations
			
				
				if(exp.experimentTypeCode!=null){
					comm=CommonInfoType.findByCode(exp.experimentTypeCode);
					exp.experimentTypeCode=comm.code;
				}
				
			
			//System.err.println("Class Name exp :"+exp.experimentTypeRef.className);
			if(exp._id == null){
				MongoDBDAO.save(comm.collectionName,exp);
			}else{
				MongoDBDAO.update(comm.collectionName,exp);
			}						
			filledForm = filledForm.fill(exp);
			if("json".equals(format)){
				return ok(Json.toJson(exp));
			}else{
				
				return ok(views.html.experiment.experiment.render(filledForm,true));
			}
			
			
			
		}		
	}
	
	
	public static Result show(String id){
		Form<Experiment> defaultForm = experimentForm.fill(new Experiment());
		return ok(views.html.experiment.experiment.render(defaultForm,true));
	}
	

	//Display properties input
	public static Result property(String value){
		CommonInfoType commonInfoType =CommonInfoType.findCommonById(Long.valueOf(value));
		
		
		Experiment exp=new Experiment();
		
		Form<Experiment> defaultForm = experimentForm.fill(exp);
		return ok(views.html.experiment.propertiesForm.render(commonInfoType,defaultForm));
	}
	
/*	public static Result displayExp(String id,String collectionName){
		Experiment exp=MongoDBDAO.findById(collectionName, Experiment.class, id); 
		Form<Experiment> defaultForm = experimentForm.fill(exp);
		
		CommonInfoType commonInfoType =CommonInfoType.find.where().eq("code",exp.experimentType.code);
		
		return ok(views.html.experiment.test.render(defaultForm,MongoDBDAO.all(collectionName,Experiment.class),commonInfoType));
	}
	
	public static Result deleteExp(String id,String collectionName){
		MongoDBDAO.delete(collectionName,Experiment.class,id);
		Form<Experiment> defaultForm = form(Experiment.class);
		return ok(views.html.experiment.test.render(defaultForm,MongoDBDAO.all(collectionName,Experiment.class),null));
	}
*/

}
