package controllers.main.tpl;

import java.util.List;

import controllers.CommonController;
import models.administration.authorisation.Permission;
import fr.cea.ig.MongoDBDAO;
import jsmessages.JsMessages;
import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.valuation.instance.ValuationCriteria;
import models.utils.InstanceConstants;
import play.Application;
import play.Logger;
import play.Play;
import play.Routes;
import play.api.modules.spring.Spring;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Result;
import views.html.home;


public class Main extends CommonController{

   final static JsMessages messages = JsMessages.create(play.Play.application());

   public static Result home() {
        return ok(home.render());
   }
   
   public static Result jsCodes() {
	   return ok(generateCodeLabel()).as("application/javascript");
   }

   /*
   * jsPermissions() method
   * These methods generate Permissions.js' Check Method
   */
   public static Result jsPermissions() {
      return ok(listPermissions()).as("application/javascript");
   }
   private static String listPermissions() {
	   List<Permission> permissions = Permission.find.findByUserLogin(Context.current().session().get("NGL_FILTER_USER"));
	   StringBuilder sb = new StringBuilder();
	   sb.append("Permissions={}; Permissions.check=(function(param){var listPermissions=[");
	   for(Permission p:permissions){
		   sb.append("\"").append(p.code).append("\",");
	   }
	   sb.deleteCharAt(sb.lastIndexOf(","));
	   sb.append("];return(listPermissions.indexOf(param) != -1);})");
	   return sb.toString();
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
		
		sb.append("\"status.TRUE\":\"OK\",");
		sb.append("\"status.FALSE\":\"KO\",");
		sb.append("\"status.UNSET\":\"---\",");
		
		
		List<ValuationCriteria> criterias = MongoDBDAO.find(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class).toList();
		for(ValuationCriteria vc:  criterias){
			sb.append("\"").append("valuation_criteria").append(".").append(vc.code)
			.append("\":\"").append(vc.name).append("\",");
		}
		
		List<Protocol> protocols = MongoDBDAO.find(InstanceConstants.PROTOCOL_COLL_NAME,Protocol.class).toList();
		for(Protocol protocol:protocols){
			sb.append("\"").append("protocol").append(".").append(protocol.code).append("\":\"").append(protocol.name).append("\",");
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

   
   public static Result jsPrintTag(){
	   
	   Boolean isPrintTag = Play.application().configuration().getBoolean("ngl.printing.cb", Boolean.FALSE);
	   String js = "PrintTag={}; PrintTag.isActive =(function(){return "+isPrintTag.booleanValue()+";});";
	   return ok(js).as("application/javascript");
   }
}
