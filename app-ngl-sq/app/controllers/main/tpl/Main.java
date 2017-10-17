package controllers.main.tpl;

import java.util.List;

import org.mongojack.DBQuery;

import jsmessages.JsMessages;
import models.administration.authorisation.Permission;
import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import models.laboratory.protocol.instance.Protocol;
import models.laboratory.reagent.description.AbstractCatalog;
import models.laboratory.reagent.description.BoxCatalog;
import models.laboratory.reagent.description.KitCatalog;
import models.laboratory.reagent.description.ReagentCatalog;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.laboratory.valuation.instance.ValuationCriteria;
import models.laboratory.project.instance.Project;
import models.utils.InstanceConstants;
import play.Play;
import play.Routes;
import play.api.modules.spring.Spring;
import play.mvc.Http.Context;
import play.mvc.Result;
import views.html.home;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import javax.inject.Inject;


public class Main extends CommonController {

	// final static JsMessages messages = JsMessages.create(play.Play.application());

	private final JsMessages messages;

	@Inject
	public Main(jsmessages.JsMessagesFactory jsMessagesFactory) {
		messages = jsMessagesFactory.all();
	}

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

	public static Result jsAppURL(){
		return ok(getAppURL()).as("application/javascript");
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

		List<Project> projects = MongoDBDAO.find(InstanceConstants.PROJECT_COLL_NAME, Project.class).toList();
		for(Project p:  projects){
			sb.append("\"").append("project").append(".").append(p.code)
			.append("\":\"").append(p.name).append("\",");
		}

		List<ValuationCriteria> criterias = MongoDBDAO.find(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class).toList();
		for(ValuationCriteria vc:  criterias){
			sb.append("\"").append("valuation_criteria").append(".").append(vc.code)
			.append("\":\"").append(vc.name).append("\",");
		}

		List<ResolutionConfiguration> resolutionConfigs = MongoDBDAO.find(InstanceConstants.RESOLUTION_COLL_NAME, ResolutionConfiguration.class).toList();
		resolutionConfigs
		.stream()
		.map(rc -> rc.resolutions)
		.flatMap(List::stream)
		.forEach(r ->{
			sb.append("\"").append("resolution").append(".").append(r.code)
			.append("\":\"").append(r.name).append("\",");
		});





		List<Protocol> protocols = MongoDBDAO.find(InstanceConstants.PROTOCOL_COLL_NAME,Protocol.class).toList();
		for(Protocol protocol:protocols){
			sb.append("\"").append("protocol").append(".").append(protocol.code).append("\":\"").append(protocol.name).append("\",");
		}

		MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, KitCatalog.class, DBQuery.is("category", "Kit"))
		.cursor.forEach(reagent -> {
			sb.append("\"").append("reagentKit").append(".").append(reagent.code)
			.append("\":\"").append(reagent.name).append("\",");
		});

		MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, BoxCatalog.class, DBQuery.is("category", "Box"))
		.cursor.forEach(reagent -> {
			sb.append("\"").append("reagentBox").append(".").append(reagent.code)
			.append("\":\"").append(reagent.name).append("\",");
		});

		MongoDBDAO.find(InstanceConstants.REAGENT_CATALOG_COLL_NAME, ReagentCatalog.class, DBQuery.is("category", "Reagent"))
		.cursor.forEach(reagent -> {
			sb.append("\"").append("reagentReagent").append(".").append(reagent.code)
			.append("\":\"").append(reagent.name).append("\",");
		});

		sb.append("};return function(k){if(typeof k == 'object'){for(var i=0;i<k.length&&!ms[k[i]];i++);var m=ms[k[i]]||k[0]}else{m=ms[k]||k}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})();");
		return sb.toString();
	}

	private static String getAppURL(){
		StringBuilder sb = new StringBuilder();
		sb.append("function AppURL (app){");
		sb.append("if(app===\"sq\") return ");
		sb.append("\""+Play.application().configuration().getString("sq.url")+"\";");
		sb.append("else if(app===\"bi\") return ");
		sb.append("\""+Play.application().configuration().getString("bi.url")+"\";");
		sb.append("else if(app===\"project\") return ");
		sb.append("\""+Play.application().configuration().getString("project.url")+"\";");
		sb.append("}");
		return sb.toString();
	}


	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(	  	      
				Routes.javascriptRouter("jsRoutes",  	       
						controllers.experiments.api.routes.javascript.Experiments.list(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.samples.api.routes.javascript.Samples.list()
						)
				);
	}


	// public static Result jsMessages() {
	public Result jsMessages() {
		// return ok(messages.generate("Messages")).as("application/javascript");
		return ok(messages.apply(scala.Option.apply("window.Messages"),jsmessages.japi.Helper.messagesFromCurrentHttpContext()));
	}


	public static Result jsPrintTag(){
		Boolean isPrintTag = Play.application().configuration().getBoolean("ngl.printing.cb", Boolean.FALSE);
		String js = "PrintTag={}; PrintTag.isActive =(function(){return "+isPrintTag.booleanValue()+";});";
		return ok(js).as("application/javascript");
	}
}
