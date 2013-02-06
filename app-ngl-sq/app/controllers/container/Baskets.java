package controllers.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.TraceInformation;
import models.laboratory.container.instance.Basket;
import models.laboratory.experiment.instance.ContainerUsed;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.Run;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBQuery.Query;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.DBUpdate.Builder;
import net.vz.mongodb.jackson.WriteResult;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.utils.DataTableForm;
import fr.cea.ig.MongoDBDAO;

import views.html.experiment.testbasket;

public class Baskets extends Controller{
	public static final String COLLECTION_NAME = "TEST_BASKET_YANN";
	final static Form<DataTableForm> datatableForm = form(DataTableForm.class);
	final static Form<Basket> basketForm = form(Basket.class);
	final static Form<String> containerForm = form(String.class);
	
	public static Result home() {
		return ok(testbasket.render("projects"));
	}

	public static Result createOrUpdate(String format){
		Form<Basket> filledForm = getFilledForm(format);
		
		if(!filledForm.hasErrors()) {
			Basket basket = filledForm.get();
			if(null == basket._id){
				basket.traceInformation = new TraceInformation();
				basket.traceInformation.setTraceInformation("ngsrg");//TODO: replace				
			}else{
				basket.traceInformation.setTraceInformation("ngsrg");//TODO: replace
			}			
			
			if(!filledForm.hasErrors()) {
				if(MongoDBDAO.findByCode(COLLECTION_NAME, Basket.class, basket.code) == null){
					basket = MongoDBDAO.save(COLLECTION_NAME, basket);
				}
				else{
					return badRequest();
				}
				filledForm = filledForm.fill(basket);
			}
		}
		
		if (!filledForm.hasErrors()) {
			if ("json".equals(format)) {
				return ok(Json.toJson(filledForm.get()));
			} else {
				//return ok(run.render(filledForm, true));
			}
		} else {
			if ("json".equals(format)) {
				return badRequest(filledForm.errorsAsJson());
			} else {
				//return badRequest(run.render(filledForm, true));
			}
		}
		return ok(Json.toJson(filledForm.get()));
	}
	
	public static Result delete(String code,String format){
		Basket basket = MongoDBDAO.findByCode(COLLECTION_NAME, Basket.class,code);
		if(basket!=null){
			System.out.println("_ID: "+basket._id);
			MongoDBDAO.delete(COLLECTION_NAME, basket);
			return ok();
		}
		else{
			
			return badRequest();
		}
	}
	
	public static Result add(String code,String format){
		JsonNode json = request().body().asJson();
		String containerCode = json.get("container").asText();
		if(containerCode != null){
			Basket basket = MongoDBDAO.findByCode(COLLECTION_NAME,Basket.class,code);	
			if(basket == null || (basket.inputContainers!=null && basket.inputContainers.contains(containerCode))){
				return badRequest(json);
			}

			MongoDBDAO.updatePush(COLLECTION_NAME, basket,"inputContainers",containerCode);
			return ok(json);
		}
		
		return badRequest(json);
	}
	
	public static Result delete_container(String code,String code_container,String format){
		MongoDBDAO.updateSetArray(COLLECTION_NAME,  Basket.class,DBQuery.is("inputContainers", code_container),DBUpdate.unset("inputContainers.$"));
		MongoDBDAO.updateSetArray(COLLECTION_NAME,  Basket.class,DBQuery.is("code", code),DBUpdate.pull("inputContainers",null));
		return ok();
	}
	
	//list of all the baskets
	public static Result list() {
		Form<DataTableForm> filledForm = datatableForm.bindFromRequest();
		List<Basket> baskets= MongoDBDAO.all(COLLECTION_NAME,Basket.class);
		ObjectNode result = Json.newObject();
		result.put("iTotalRecords", baskets.size());
		result.put("iTotalDisplayRecords", baskets.size());
		result.put("sEcho", filledForm.get().sEcho);
		result.put("aaData", Json.toJson(baskets));
		return ok(result);
	}
	
	public static Result listByExperimentType(String experimentType) {
		Form<DataTableForm> filledForm = datatableForm.bindFromRequest();
		List<Basket> baskets= MongoDBDAO.find(COLLECTION_NAME, Basket.class, DBQuery.is("experimentTypeCode", experimentType));
		ObjectNode result = Json.newObject();
		result.put("iTotalRecords", baskets.size());
		result.put("iTotalDisplayRecords", baskets.size());
		result.put("sEcho", filledForm.get().sEcho);
		result.put("aaData", Json.toJson(baskets));
		return ok(result);
	}
	
	//see what container are in the basket selected
	public static Result show(String code,String format){
		Basket basket = MongoDBDAO.findOne(COLLECTION_NAME, Basket.class,DBQuery.is("code", code));
		if(basket != null){
			if("json".equals(format)){
				return ok(Json.toJson(basket));
			}else{
				Form<Basket> filledForm = basketForm.fill(basket);
				return ok(); //TODO must be complete
			}			
		}else{
			return notFound();
		}
	}
	
	public static Map<String,String> getSelectList(){
		Form<DataTableForm> filledForm = datatableForm.bindFromRequest();
		List<Basket> baskets= MongoDBDAO.all(COLLECTION_NAME,Basket.class);
		Map<String,String> map = new HashMap<String,String>();
		for(Basket b:baskets){
			map.put(b.code, b.code);
		}
		return map;
	}
	
	
	private static Form<Basket> getFilledForm(String format) {
		Form<Basket> filledForm;
		if("json".equals(format)){
			JsonNode json = request().body().asJson();			
			Basket basket = Json.fromJson(json, Basket.class);
			filledForm = basketForm.fill(basket);	//bindJson ne marche pas !			
		}else{
			filledForm = basketForm.bindFromRequest();
		}
		return filledForm;
	}

}