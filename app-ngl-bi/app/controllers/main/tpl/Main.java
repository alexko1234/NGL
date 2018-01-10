package controllers.main.tpl;


import java.util.List;

import javax.inject.Inject;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import models.administration.authorisation.Permission;
import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.dao.CodeLabelDAO;
import models.laboratory.resolutions.instance.Resolution;
import models.laboratory.resolutions.instance.ResolutionConfiguration;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;
import models.laboratory.valuation.instance.ValuationCriteria;
import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import fr.cea.ig.authentication.Authentication;
import jsmessages.JsMessages;
import play.Logger;
import play.Play;
import play.api.modules.spring.Spring;
import play.i18n.Lang;
import play.libs.Scala;
// import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Http.Context;
import play.mvc.Result;
import views.html.home ;


public class Main extends CommonController {

   // final static JsMessages messages = JsMessages.create(play.Play.application());	
	private final JsMessages messages;

	private final home home;
	
	@Inject
	public Main(jsmessages.JsMessagesFactory jsMessagesFactory, home home) {
		messages = jsMessagesFactory.all();
		this.home = home;
	}

   public Result home() {
	   return ok(home.render());
        
    }
   
   public Result jsMessages() {
       // return ok(messages.generate("Messages")).as("application/javascript");
       // return ok(messages.all(Scala.Option("Messages"))).as("application/javascript");
       return ok(messages.apply(Scala.Option("Messages"), jsmessages.japi.Helper.messagesFromCurrentHttpContext()));

   }
   
   public Result jsCodes() {
	   return ok(generateCodeLabel()).as("application/javascript");
   }
   
   /*
    * jsPermissions() method
    */
   public Result jsPermissions(){
	   return ok(listPermissions()).as("application/javascript");
   }
   
   public Result jsAppURL(){
	   return ok(getAppURL()).as("application/javascript");
   }
   
   private static String listPermissions(){
	   List<Permission> permissions = Permission.find.findByUserLogin(Authentication.getUser());
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
		
		if("CNS".equalsIgnoreCase(Play.application().configuration().getString("institute"))){
			patchTara(sb);
		}
		
		
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

   private static void patchTara(StringBuilder sb) {
		sb.append("\"taraFilterCode.0-0.2\":\"AACC\",");
		sb.append("\"taraFilterCode.0-inf\":\"AAZZ\",");
		sb.append("\"taraFilterCode.0.1-0.2\":\"BBCC\",");
		sb.append("\"taraFilterCode.0.2-0.45\":\"CCEE\",");
		sb.append("\"taraFilterCode.0.2-1.6\":\"CCII\",");
		sb.append("\"taraFilterCode.0.22-3\":\"CCKK\",");
		sb.append("\"taraFilterCode.0.45-0.8\":\"EEGG\",");
		sb.append("\"taraFilterCode.0.45-8\":\"EEOO\",");
		sb.append("\"taraFilterCode.0.8-3\":\"GGKK\",");
		sb.append("\"taraFilterCode.0.8-5\":\"GGMM\",");
		sb.append("\"taraFilterCode.0.8-20\":\"GGQQ\",");
		sb.append("\"taraFilterCode.0.8-180\":\"GGSS\",");
		sb.append("\"taraFilterCode.0.8-200\":\"GGRR\",");
		sb.append("\"taraFilterCode.0.8-inf\":\"GGZZ\",");
		sb.append("\"taraFilterCode.1.6-20\":\"IIQQ\",");
		sb.append("\"taraFilterCode.3-20\":\"KKQQ\",");
		sb.append("\"taraFilterCode.3-inf\":\"KKZZ\",");
		sb.append("\"taraFilterCode.5-20\":\"MMQQ\",");
		sb.append("\"taraFilterCode.20-200\":\"QQRR\",");
		sb.append("\"taraFilterCode.20-180\":\"QQSS\",");
		sb.append("\"taraFilterCode.180-2000\":\"SSUU\",");
		sb.append("\"taraFilterCode.180-inf\":\"SSZZ\",");
		sb.append("\"taraFilterCode.300-inf\":\"TTZZ\",");
		sb.append("\"taraFilterCode.pool\":\"YYYY\",");
		sb.append("\"taraFilterCode.inf-inf\":\"ZZZZ\",");
			
		sb.append("\"taraDepthCode.CTL\":\"CTL\",");
		sb.append("\"taraDepthCode.Deep Chlorophyl Maximum\":\"DCM\",");
		sb.append("\"taraDepthCode.DCM and OMZ Pool\":\"DOP\",");
		sb.append("\"taraDepthCode.DCM and Surface Pool\":\"DSP\",");
		sb.append("\"taraDepthCode.Meso\":\"MES\",");
		sb.append("\"taraDepthCode.MixedLayer\":\"MXL\",");
		sb.append("\"taraDepthCode.NightSampling@25mt0\":\"NSI\",");
		sb.append("\"taraDepthCode.NightSampling@25mt24\":\"NSJ\",");
		sb.append("\"taraDepthCode.NightSampling@25mt48\":\"NSK\",");
		sb.append("\"taraDepthCode.OBLIQUE\":\"OBL\",");
		sb.append("\"taraDepthCode.Oxygen Minimum Zone\":\"OMZ\",");
		sb.append("\"taraDepthCode.PF1\":\"PFA\",");
		sb.append("\"taraDepthCode.PF2\":\"PFB\",");
		sb.append("\"taraDepthCode.PF3\":\"PFC\",");
		sb.append("\"taraDepthCode.PF4\":\"PFD\",");
		sb.append("\"taraDepthCode.PF5\":\"PFE\",");
		sb.append("\"taraDepthCode.PF6\":\"PFF\",");
		sb.append("\"taraDepthCode.P1a\":\"PFG\",");
		sb.append("\"taraDepthCode.P1b\":\"PFH\",");
		sb.append("\"taraDepthCode.B2B1\":\"PFI\",");
		sb.append("\"taraDepthCode.B4B3\":\"PFJ\",");
		sb.append("\"taraDepthCode.B6B5\":\"PFK\",");
		sb.append("\"taraDepthCode.B8B7\":\"PFL\",");
		sb.append("\"taraDepthCode.B10B9\":\"PFM\",");
		sb.append("\"taraDepthCode.Surface OMZ and DCM Pool\":\"SOD\",");
		sb.append("\"taraDepthCode.Surface and OMZ Pool\":\"SOP\",");
		sb.append("\"taraDepthCode.Surface\":\"SUR\",");
		sb.append("\"taraDepthCode.Sub-MixedLayer@100m\":\"SXL\",");
		sb.append("\"taraDepthCode.Other\":\"OTH\",");
		sb.append("\"taraDepthCode.DiscreteDepth\":\"ZZZ\",");
		sb.append("\"taraDepthCode.IntegratedDepth\":\"IZZ\",");
		
   }

}
