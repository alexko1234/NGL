package controllers.main.tpl;

import java.util.List;

import javax.inject.Inject;

import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import models.laboratory.protocol.instance.Protocol;
import models.utils.InstanceConstants;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.libs.Json;
import play.libs.Scala;
import play.mvc.Result;
import jsmessages.JsMessages;
import views.html.home ;


public class Main extends CommonController {

   //final static JsMessages messages = JsMessages.create(play.Play.application());	
	private static JsMessages messages;

	@Inject
	public Main(jsmessages.JsMessagesFactory jsMessagesFactory) {
		messages = jsMessagesFactory.all();
	}

   public static Result home() {
	   return ok(home.render());
        
    }
   
   public static Result jsMessages() {
       // return ok(messages.generate("Messages")).as("application/javascript");
	   // return ok(messages.all(Scala.Option("Messages"))).as("application/javascript");
	   return ok(messages.apply(Scala.Option("Messages"), jsmessages.japi.Helper.messagesFromCurrentHttpContext()));

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
		
		sb.append("\"status.TRUE\":\"OK\",");
		sb.append("\"status.FALSE\":\"KO\",");
		sb.append("\"status.UNSET\":\"---\",");
		
		List<Protocol> protocols = MongoDBDAO.find(InstanceConstants.PROTOCOL_COLL_NAME,Protocol.class).toList();
		for(Protocol protocol:protocols){
			sb.append("\"").append("protocol").append(".").append(protocol.code).append("\":\"").append(protocol.name).append("\",");
		}
		
		sb.append("};return function(k){if(typeof k == 'object'){for(var i=0;i<k.length&&!ms[k[i]];i++);var m=ms[k[i]]||k[0]}else{m=ms[k]||k}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})();");
		return sb.toString();
	}
}
