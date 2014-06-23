package controllers.main.tpl;

import java.util.List;

import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import jsmessages.JsMessages;
import play.Routes;
import play.api.modules.spring.Spring;
import play.mvc.Result;
import views.html.home;
import controllers.CommonController;
import controllers.authorisation.Authenticate;
import controllers.authorisation.Permission;


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
		sb.append("\"valuation.UNSET\":\"---\"");
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
