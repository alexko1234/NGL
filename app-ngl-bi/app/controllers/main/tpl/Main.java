package controllers.main.tpl;


import java.util.List;

import org.mongojack.DBQuery;
import org.mongojack.DBUpdate;

import models.laboratory.common.description.CodeLabel;
import models.laboratory.common.description.Resolution;
import models.laboratory.common.description.dao.CodeLabelDAO;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.utils.InstanceConstants;

import models.laboratory.valuation.instance.ValuationCriteria;

import controllers.CommonController;
import fr.cea.ig.MongoDBDAO;
import jsmessages.JsMessages;
import play.Play;
import play.api.modules.spring.Spring;
import play.i18n.Lang;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home ;


public class Main extends CommonController {

   final static JsMessages messages = JsMessages.create(play.Play.application());	
	
   public static Result home() {
	   
	   ReadSet readSetInput= new ReadSet();
	   readSetInput.projectCode = "projetttt";
	   readSetInput.sampleCode = "sampleeee";
	   readSetInput.code = "THECODE";
	   
		//To avoid "double" values
		MongoDBDAO.update(InstanceConstants.RUN_ILLUMINA_COLL_NAME, Run.class, 
				DBQuery.and(DBQuery.is("code", readSetInput.runCode), DBQuery.notIn("projectCodes", readSetInput.projectCode)), 
				DBUpdate.push("projectCodes", readSetInput.projectCode));
	   
	   return ok(home.render());
        
    }
   
   public static Result jsMessages() {
       return ok(messages.generate("Messages")).as("application/javascript");

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
		
		List<ValuationCriteria> criterias = MongoDBDAO.find(InstanceConstants.VALUATION_CRITERIA_COLL_NAME, ValuationCriteria.class).toList();
		for(ValuationCriteria vc:  criterias){
			sb.append("\"").append("valuation_criteria").append(".").append(vc.code)
			.append("\":\"").append(vc.name).append("\",");
		}
		
		if("CNS".equalsIgnoreCase(Play.application().configuration().getString("institute"))){
			patchTara(sb);
		}
		
		
		sb.append("};return function(k){if(typeof k == 'object'){for(var i=0;i<k.length&&!ms[k[i]];i++);var m=ms[k[i]]||k[0]}else{m=ms[k]||k}for(i=1;i<arguments.length;i++){m=m.replace('{'+(i-1)+'}',arguments[i])}return m}})();");
		return sb.toString();
	}

	private static void patchTara(StringBuilder sb) {
		sb.append("\"taraFilterCode.0-0.2\":\"AACC\",");
		sb.append("\"taraFilterCode.0-inf\":\"AAZZ\",");
		sb.append("\"taraFilterCode.0.1-0.2\":\"BBCC\",");
		sb.append("\"taraFilterCode.0.2-0.45\":\"CCEE\",");
		sb.append("\"taraFilterCode.0.2-1.6\":\"CCII\",");
		sb.append("\"taraFilterCode.0.2-3\":\"CCKK\",");
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
		
	}

}
