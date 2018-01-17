package fr.cea.ig.ngl.support;

import static play.mvc.Results.ok;

import fr.cea.ig.lfw.support.LFWJavascript;
import fr.cea.ig.ngl.NGLApplicationHolder;
import fr.cea.ig.ngl.NGLConfig;
import play.mvc.Result;

public interface NGLJavascript extends LFWJavascript, NGLApplicationHolder {

	default Result jsAppURL() {
		NGLConfig config = nglConfig();
		StringBuilder sb = 
				new StringBuilder()
					.append("function AppURL (app){")
					.append("if(app===\"sq\") return \"")
					.append(config.getSQUrl())
					.append("\"; else if(app===\"bi\") return \"")
					.append(config.getBIUrl())
					.append("\"; else if(app===\"project\") return \"")
					.append(config.getProjectUrl())
					.append("\";}");
		return ok(sb.toString()).as("application/javascript");
	}

	default Result jsPrintTag(){
		boolean tag = nglConfig().isBarCodePrintingEnabled();		
		String js = "PrintTag={}; PrintTag.isActive =(function(){return " + tag + ";});";
		return ok(js).as("application/javascript");
	}

}
