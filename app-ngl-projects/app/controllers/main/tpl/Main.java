package controllers.main.tpl;


import java.util.List;

import javax.inject.Inject;

import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import controllers.CommonController;
import jsmessages.JsMessages;
import play.api.modules.spring.Spring;
import play.libs.Scala;
import play.mvc.Result;
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
		//return ok(messages.all(Scala.Option("Messages"))).as("application/javascript");
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
		sb.append("};return function(k){if(typeof k == 'object'){for(var i=0;i<k.length&&!ms[k[i]];i++);var m=ms[k[i]]||k[0]}else{m=ms[k]||k}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})();");
		return sb.toString();
	}
   
}
