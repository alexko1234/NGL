package controllers.experiments.tpl;

// import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form;
// import play.Routes;
import play.routing.JavaScriptReverseRouter;


import java.lang.reflect.Method;

import models.laboratory.experiment.description.ExperimentCategory;
import models.laboratory.experiment.instance.Experiment;
import models.utils.DescriptionHelper;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.experiments.details;
import views.html.experiments.graph;
import views.html.experiments.home;
import views.html.experiments.listContainers;
import views.html.experiments.search;
import views.html.experiments.searchContainers;
import controllers.CommonController;

public class Experiments extends CommonController{
	
	final static Form<Experiment> experimentForm = form(Experiment.class);
	
	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result get(String code){
		return ok(home.render("search"));
	}
	
	public static Result details(){
		return ok(details.render(getCurrentUser()));
	}
	
	public static Result search(String experimentType){
		return ok(search.render());
	}
	
	public static Result searchContainers(){
		return ok(searchContainers.render());
	}
	
	public static Result listContainers(){
		return ok(listContainers.render());
	}
	
	public static Result graph(){
		return ok(graph.render());
	}
	
	/*This controller return a Result that contain the template, searching for the class that fit the most
	 * First it will search for views.html.experiments.@atomicType.@institute.@experimentType
	 * then if the class doesn't exist it will search for views.html.experiments.@atomicType.common.@experimentType
	 * then if the class doesn't exist it will search for views.html.experiments.@atomicType.default.@experimentType
	 * then if the class doesn't exist it will search for views.html.experiments.@atomicType.@institute.@outputCategoryCode
	 * then if the class doesn't exist it will search for views.html.experiments.@atomicType.common.@outputCategoryCode
	 * then if the class doesn't exist it will search for views.html.experiments.@atomicType.default.@outputCategoryCode
	 * if all classes doesn't exist, a badRequest result is returned with the message "not implemented"
	 * */
	public static Result getTemplate(String atomicType, String outputCategoryCode, String experimentCategoryCode, String experimentType){
		
		
		if(ExperimentCategory.CODE.qualitycontrol.toString().equals(experimentCategoryCode)){
			outputCategoryCode = outputCategoryCode+"_"+experimentCategoryCode;			
		}
		
		String institute = DescriptionHelper.getInstitute().get(0);	
		Logger.info(atomicType+" / "+outputCategoryCode+" / "+experimentType+" / "+institute);
		Html result = getTemplateClass(atomicType, outputCategoryCode, experimentType, institute);
		if(result == null){
			result = getTemplateClass(atomicType, outputCategoryCode, experimentType, "common");
		}
		
		if(result == null){
			result = getTemplateClass(atomicType, outputCategoryCode, experimentType, "defaults");
		}
		
		if(result == null){
			result = getTemplateClass(atomicType, outputCategoryCode, null, institute);
		}
		
		if(result == null){
			result = getTemplateClass(atomicType, outputCategoryCode, null, "common");
		}
		
		if(result == null){
			result = getTemplateClass(atomicType, outputCategoryCode, null, "defaults");
		}
		
		if(result != null){
			return ok(result);
		}
		
		return badRequest("Not implemented");
	}
	
	/*This method return the HTML if the class exist, or NULL if not
	 * First we search for the class, and if it exist, we load the render method
	 * the template need to have an empty signature like @() because we call the method
	 * without args
	 * */
	private static Html getTemplateClass(String atomicType, String outputCategoryCode, String experimentType, String institute){
		
		String keyWord = null;
		//We use the experimentType in priority
		if(experimentType != null && !experimentType.equals("")){
			keyWord = experimentType.replaceAll("-", "");//Scala template can't have a '-' in their name;
		} else {
			keyWord = outputCategoryCode.replaceAll("-", "");//Scala template can't have a '-' in their name
		}
		
		Logger.info("views.html.experiments."+atomicType.toLowerCase()+"."+institute.toLowerCase()+"."+keyWord.toLowerCase());
		
		try{
			Class<?> clazz = Class.forName("views.html.experiments."+atomicType.toLowerCase()+"."+institute.toLowerCase()+"."+keyWord.toLowerCase());//package in java are always in lower case
			Method m = clazz.getDeclaredMethod("render");
			Html html = (Html)m.invoke(null);
			return html;
		}catch(Exception e){
			return null;
		}
	}
	
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      // Routes.javascriptRouter("jsRoutes",
  	    		JavaScriptReverseRouter.create("jsRoutes",
  	        // Routes
  	    		controllers.experiments.tpl.routes.javascript.Experiments.searchContainers(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.search(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.graph(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.listContainers(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.getTemplate(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.home(),  	    		
  	    		controllers.experiments.tpl.routes.javascript.Experiments.details(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.get(),
  	    		controllers.printing.tpl.routes.javascript.Printing.home(),
  	    		
  	    		controllers.instruments.api.routes.javascript.InstrumentUsedTypes.get(),
	      		controllers.experiments.api.routes.javascript.Experiments.list(),
	      		controllers.experiments.api.routes.javascript.Experiments.get(),
	      		controllers.experiments.api.routes.javascript.Experiments.save(),
	      		controllers.experiments.api.routes.javascript.Experiments.delete(),
	      		controllers.experiments.api.routes.javascript.Experiments.update(),
	      		controllers.experiments.api.routes.javascript.Experiments.updateState(),
	      		controllers.experiments.api.routes.javascript.ExperimentComments.save(),
  	    		controllers.experiments.api.routes.javascript.ExperimentComments.update(),
  	    		controllers.experiments.api.routes.javascript.ExperimentComments.delete(),
  	    		
  	    		controllers.experiments.api.routes.javascript.ExperimentTypeNodes.get(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypeNodes.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentReagents.list(),
  	    		
  	    		controllers.containers.api.routes.javascript.Containers.updateState(),
  	    		controllers.containers.api.routes.javascript.Containers.updateStateBatch(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.updateState(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.updateStateBatch(),
  	    		
  	    		controllers.processes.api.routes.javascript.Processes.updateState(),
  	    		controllers.processes.api.routes.javascript.Processes.updateStateBatch(),
  	    		
  	    		controllers.instruments.io.routes.javascript.IO.generateFile(),
  	    		controllers.instruments.io.routes.javascript.IO.importFile(),
  	    		
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.list(),
  	    		controllers.containers.api.routes.javascript.Containers.get(),
  	    		controllers.processes.api.routes.javascript.Processes.list(),
  	    		controllers.processes.api.routes.javascript.ProcessTypes.list(),
  	    		controllers.processes.api.routes.javascript.ProcessTypes.get(),
  	    		controllers.processes.api.routes.javascript.ProcessCategories.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.get(),  	    		
  	    		controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
  	    		controllers.instruments.api.routes.javascript.Instruments.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentUsedTypes.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentCategories.list(),
  	    		controllers.protocols.api.routes.javascript.Protocols.list(),
  	    		//instruments.io.routes.javascript.Outputs.generateFile(),
  	    		//instruments.io.routes.javascript.Inputs.importFile(),
  	    		controllers.resolutions.api.routes.javascript.Resolutions.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	      		controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
	      		controllers.reagents.api.routes.javascript.KitCatalogs.list(),
	      		controllers.reagents.api.routes.javascript.BoxCatalogs.list(),
	      		controllers.reagents.api.routes.javascript.ReagentCatalogs.list(),
  	      		controllers.reagents.api.routes.javascript.Kits.list(),
  	      		controllers.reagents.api.routes.javascript.Boxes.list(),
  	      		controllers.reagents.api.routes.javascript.Reagents.list(),
  	      		controllers.commons.api.routes.javascript.Values.list(),
				controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
				controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
				controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.commons.api.routes.javascript.Users.list(),  	    
	      		controllers.commons.api.routes.javascript.Parameters.list(),
  	    		controllers.valuation.api.routes.javascript.ValuationCriterias.get(),
  	    		controllers.valuation.api.routes.javascript.ValuationCriterias.list(),
  	    		controllers.runs.api.routes.javascript.Runs.list()
  	      )	  	      
  	    );
  	}
}
