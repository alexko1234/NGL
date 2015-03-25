package controllers.main.tpl;

import java.util.List;

import jsmessages.JsMessages;
import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.KitCatalog;
import models.laboratory.reagent.description.ReagentCatalog;
import models.utils.InstanceConstants;

import org.mongojack.DBQuery;

import play.Routes;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import views.html.home;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;


public class Main extends CommonController{

	final static JsMessages messages = JsMessages.create(play.Play.application());

	public static Result home() {
		return ok(home.render());
	}

	public static Result jsCodes() {
		return ok(generateCodeLabel()).as("application/javascript");
	}

	private static String generateCodeLabel() {
		CodeLabelDAO dao = Spring.getBeanOfType(CodeLabelDAO.class);
		List<CodeLabel> list = dao.findAll();

		StringBuilder sb = new StringBuilder();
		sb.append("Codes=(function(){var ms={");
		for(CodeLabel cl : list){
			sb.append("\"").append(cl.tableName).append(".").append(cl.code)
			.append("\":\"").append(cl.label).append("\",");
		}
		sb.append("\"valuation.TRUE\":\"Oui\",");
		sb.append("\"valuation.FALSE\":\"Non\",");
		sb.append("\"valuation.UNSET\":\"---\",");
		List<KitCatalog> kitCatalogs = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, DBQuery.is("category", "Kit")).toList();
		for(KitCatalog kc:  kitCatalogs){
			sb.append("\"").append("kitCatalogs").append(".").append(kc.code)
			.append("\":\"").append(kc.name).append("\",");
		}
		List<BoxCatalog> boxCatalogs = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, DBQuery.is("category", "Box")).toList();
		for(BoxCatalog bc:  boxCatalogs){
			sb.append("\"").append("boxCatalogs").append(".").append(bc.code)
			.append("\":\"").append(bc.name).append("\",");
		}
		List<ReagentCatalog> reagentCatalogs = MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, DBQuery.is("category", "Reagent")).toList();
		for(ReagentCatalog rc:  reagentCatalogs){
			sb.append("\"").append("reagentCatalogs").append(".").append(rc.code)
			.append("\":\"").append(rc.name).append("\",");
		}
		
		sb.append("};return function(k){if(typeof k == 'object'){for(var i=0;i<k.length&&!ms[k[i]];i++);var m=ms[k[i]]||k[0]}else{m=ms[k]||k}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})();");
		return sb.toString();
	}

	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(	  	      
				Routes.javascriptRouter("jsRoutes"  	       
						// Routes	  	         	        
						)
				);
	}


	public static Result jsMessages() {


		return ok(messages.generate("Messages")).as("application/javascript");
	}

}
