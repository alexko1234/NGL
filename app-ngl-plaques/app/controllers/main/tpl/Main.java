package controllers.main.tpl;

import java.util.List;

import controllers.CommonController;

import jsmessages.JsMessages;

import lims.models.Manip;
import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import play.api.modules.spring.Spring;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;

public class Main extends CommonController {
	
	public static Result home() {
		   return ok(home.render());
	        
	}

   public static Result jsMessages() {
       return ok(JsMessages.generate("Messages")).as("application/javascript");
   }

   public static Result jsCodes() {
	   return ok(generateCodeLabel()).as("application/javascript");
   }

    private static String generateCodeLabel() {

	StringBuilder sb = new StringBuilder();
	sb.append("Codes=(function(){var ms={");

	sb.append("\"valuation.TRUE\":\"Oui\",");
	sb.append("\"valuation.FALSE\":\"Non\",");
	sb.append("\"valuation.UNSET\":\"---\"");
	sb.append("};return function(k){if(typeof k == 'object'){for(var i=0;i<k.length&&!ms[k[i]];i++);var m=ms[k[i]]||k[0]}else{m=ms[k]||k}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})();");
	return sb.toString();
    }
}
