package controllers.experiments.tpl;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.experiment.instance.Experiment;
import play.Routes;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableHelpers;
import views.html.experiments.createExperiments;
import views.html.experiments.home;
import views.html.experiments.newExperiments;
import views.html.experiments.search;
import views.html.experiments.searchContainersSupports;
import controllers.CommonController;

public class Experiments extends CommonController{
	
	final static Form<Experiment> experimentForm = form(Experiment.class);
	
	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result edit(String code){
		return ok(home.render(code));
	}
	
	public static Result searchSupports(){
		return ok(searchContainersSupports.render());
	}
	
	public static Result newExperiments(String experimentTypeCode){
		return ok(newExperiments.render());
	}
	
	
	public static Result getTemplate(String atomicType, String inputCategory, String outputCategory){
		if(atomicType.equals("OneToOne")){
			if(outputCategory.equals("tube")){
				return ok(views.html.experiments.oneToOne.inputs.tube.render());
			}
		}else if(atomicType.equals("ManyToOne")){
			if(outputCategory.equals("mapcard")){
				return ok(views.html.experiments.manyToOne.inputs.mapcard.render());
			}else if(outputCategory.startsWith("flowcell-")){
				String[] flowcellNumber = outputCategory.split("-");
				return ok(views.html.experiments.manyToOne.inputs.flowcell.render(Integer.parseInt(flowcellNumber[1])));
			}else if(outputCategory.equals("tube")){
				return ok(views.html.experiments.manyToOne.inputs.tube.render());
			}
		}else if(atomicType.equals("OneToVoid")){
			if(outputCategory.equals("void")){
				return ok(views.html.experiments.oneToVoid.inputs.voidContainer.render());
			}
		}
		
		return badRequest("Not implemented");
	}
	
	public static Result createOrEditExperiment(){
	
		return ok(createExperiments.render(getCurrentUser()));
	}
	
	public static Result search(String experimentType){
		return ok(search.render());
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.experiments.tpl.routes.javascript.Experiments.searchSupports(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupports.list(),
  	    		controllers.containers.api.routes.javascript.Containers.get(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.search(),
  	    		controllers.processes.api.routes.javascript.Processes.list(),
  	    		controllers.processes.api.routes.javascript.ProcessTypes.list(),
  	    		controllers.processes.api.routes.javascript.ProcessTypes.get(),
  	    		controllers.processes.api.routes.javascript.ProcessCategories.list(),
  	    		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.get(),  	    		
  	    		controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.newExperiments(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.getTemplate(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.home(),
  	    		controllers.experiments.api.routes.javascript.Experiments.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypeNodes.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.createOrEditExperiment(),
  	    		controllers.experiments.api.routes.javascript.Experiments.get(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.getInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.addComment(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateComment(),
  	    		controllers.experiments.api.routes.javascript.Experiments.deleteComment(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateStateCode(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.edit(),
  	    		controllers.instruments.api.routes.javascript.Instruments.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentUsedTypes.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentCategories.list(),
  	    		controllers.experiments.api.routes.javascript.Experiments.save(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateContainers(),
  	    		controllers.protocols.api.routes.javascript.Protocols.list(),
  	    		instruments.io.routes.javascript.Outputs.sampleSheets(),
  	    		controllers.resolutions.api.routes.javascript.Resolutions.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	      		controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
  	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.save(),
	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.update(),
	      		controllers.reporting.api.routes.javascript.ReportingConfigurations.delete(),
  	      		controllers.reagents.api.routes.javascript.KitCatalogs.list(),
  	      		controllers.reagents.api.routes.javascript.Kits.list(),
  	      		controllers.reagents.api.routes.javascript.Boxes.list(),
  	      		controllers.reagents.api.routes.javascript.Reagents.list(),
  	      		controllers.commons.api.routes.javascript.Values.list(),
				controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
				controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
				controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.commons.api.routes.javascript.Users.list(),
  	    		controllers.containers.api.routes.javascript.Containers.updateBatch(),
  	    		controllers.experiments.api.routes.javascript.Experiments.retry(),
  	    		controllers.experiments.api.routes.javascript.Experiments.endOfProcess(),
  	    		controllers.experiments.api.routes.javascript.Experiments.stopProcess(),
  	    		controllers.containers.api.routes.javascript.Containers.updateStateBatch()
  	      )	  	      
  	    );
  	}
}
