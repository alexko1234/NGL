package controllers.main.tpl;

import java.util.List;

import jsmessages.JsMessages;
import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
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
		
		/*
		List<ResolutionConfiguration> resolutionExperimentConfigurations = MongoDBDAO.find(InstanceConstants.RESOLUTION_COLL_NAME,ResolutionConfiguration.class, DBQuery.is("objectTypeCode","Experiment")).toList();
		for(ResolutionConfiguration resoConfig:resolutionExperimentConfigurations){
			for(Resolution resolution: resoConfig.resolutions){
				sb.append("\"").append(resoConfig.code).append(".").append("resolution").append(".").append(resolution.code).append("\":\"").append(resolution.name).append("\",");
			}			
			
		}
		
		List<ResolutionConfiguration> resolutionProcessConfigurations = MongoDBDAO.find(InstanceConstants.RESOLUTION_COLL_NAME,ResolutionConfiguration.class, DBQuery.is("objectTypeCode","Process")).toList();
		for(ResolutionConfiguration resoConfig:resolutionProcessConfigurations){
			for(Resolution resolution: resoConfig.resolutions){
				sb.append("\"").append(resoConfig.code).append(".").append("resolution").append(".").append(resolution.code).append("\":\"").append(resolution.name).append("\",");
			}			
			
		}
		*/
		
		
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

}
