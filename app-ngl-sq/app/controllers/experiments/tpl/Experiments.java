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
import views.html.experiments.searchContainers;
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
		return ok(searchContainers.render());
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
			}
		}else if(atomicType.equals("OneToVoid")){
			if(outputCategory.equals("void")){
				return ok(views.html.experiments.oneToVoid.inputs.voidContainer.render());
			}
		}
		
		return badRequest("Not implemented");
	}
	
	public static Result createOrEditExperiment(){
	
		return ok(createExperiments.render());
	}

	
	public static Result getEditExperimentColumns(){
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		Map<Integer,String> extraHeaders = new HashMap<Integer, String>();
		extraHeaders.put(0, "Inputs");
		columns.add(DatatableHelpers.getColumn("support.code", Messages.get("containers.table.supportCode"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("support.categoryCode", Messages.get("containers.table.categoryCode"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("containers.table.projectCodes"), true, false, true,false,extraHeaders));
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("containers.table.sampleCodes"), true, false, true,false,extraHeaders));		
		DatatableColumn dc = DatatableHelpers.getColumn("state.code", Messages.get("containers.table.stateCode"), true, false, true,false,extraHeaders);
		dc.filter = "codes:'state'";
		columns.add(dc);
		DatatableColumn dd =DatatableHelpers.getColumn("mesuredVolume.value", Messages.get("experiments.table.volume.value"),true, true, true,false,extraHeaders);
		dd.filter = " number:3 ";
		columns.add(dd);
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("containers.table.fromExperimentTypeCodes"), true, false, true,false,extraHeaders));
		
		return ok(Json.toJson(columns));
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
  	    		controllers.supports.api.routes.javascript.Supports.list(),
  	    		controllers.containers.api.routes.javascript.Containers.get(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.search(),
  	    		controllers.processes.api.routes.javascript.ProcessTypes.list(),
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
  	    		controllers.experiments.api.routes.javascript.Experiments.nextState(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.edit(),
  	    		controllers.instruments.api.routes.javascript.Instruments.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentUsedTypes.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentCategories.list(),
  	    		controllers.experiments.api.routes.javascript.Experiments.save(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateContainers(),
  	    		controllers.experiments.api.routes.javascript.Protocols.list(),
  	    		instruments.io.routes.javascript.Outputs.sampleSheets(),
  	    		controllers.resolutions.api.routes.javascript.Resolutions.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.getEditExperimentColumns(),
				controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
				controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
				controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.commons.api.routes.javascript.Users.list()
  	    		
  	      )	  	      
  	    );
  	}
}
