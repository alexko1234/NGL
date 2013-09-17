package controllers.experiments.tpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentType;
import models.utils.dao.DAOException;
import play.Routes;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.experiments.createExperiments;
import views.html.experiments.home;
import views.html.experiments.newExperiments;
import views.html.experiments.searchContainers;

public class Experiments extends Controller{
	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result searchContainers(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code"), true, false, false));
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("containers.table.projectCodes"), true, false, false));				
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("containers.table.sampleCodes"), true, false, false));		
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("containers.table.valid"), true, false, false));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("containers.table.stateCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("containers.table.categoryCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("containers.table.fromExperimentTypeCodes")));
		
		DatatableConfig config = new DatatableConfig(columns);
		config.button = Boolean.TRUE;
		
		return ok(searchContainers.render(config));
	}
	
	public static Result newExperiments(String experimentTypeCode){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		columns.add(DatatableHelpers.getColumn("code", Messages.get("experiments.table.code")));
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("experiments.table.projectCodes")));						
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("experiments.table.sampleCodes")));
			
		DatatableConfig config = new DatatableConfig(columns);
		config.remove = true;
		config.button = true;
		
		return ok(newExperiments.render(config));
	}
	
	public static Result editExperiment(String experimentTypeCode){
		ExperimentType experimentType = null;
		try{
			experimentType = ExperimentType.find.findByCode(experimentTypeCode);
		}catch(models.utils.dao.DAOException e){
			
		}
		
		List<PropertyDefinition> props = experimentProperties(experimentTypeCode);
		
		DatatableConfig config = new DatatableConfig();
		config.button = true;
		config.edit = true;
		
		return ok(createExperiments.render(Json.toJson(experimentType),config,Json.toJson(props)));
	}
	
	public static Result getEditExperimentColumns(){
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		Map<Integer,String> extraHeaders = new HashMap<Integer, String>();
		extraHeaders.put(0, "Inputs");
		
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("containers.table.projectCodes"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("mesuredVolume.value", Messages.get("experiments.table.volume.value"),true, true, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("containers.table.sampleCodes"), true, false, true,false,extraHeaders));		
		columns.add(DatatableHelpers.getColumn("valid", Messages.get("containers.table.valid"), true, false, true,false,extraHeaders));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("containers.table.stateCode"), true, false, true,false,extraHeaders));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("containers.table.categoryCode"), true, false, true,false,extraHeaders));
		columns.add(DatatableHelpers.getColumn("fromExperimentTypeCodes", Messages.get("containers.table.fromExperimentTypeCodes"), true, false, true,false,extraHeaders));
		
		return ok(Json.toJson(columns));
	}
	
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.experiments.tpl.routes.javascript.Experiments.searchContainers(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.lists.api.routes.javascript.Lists.projects(),
  	    		controllers.lists.api.routes.javascript.Lists.samples(),
  	    		controllers.lists.api.routes.javascript.Lists.processTypes(),
  	    		controllers.lists.api.routes.javascript.Lists.experimentTypesByCategory(),
  	    		controllers.lists.api.routes.javascript.Lists.experimentCategories(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.newExperiments(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.home(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.editExperiment(),
  	    		controllers.experiments.api.routes.javascript.Experiments.generateOutput(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.getInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateComments(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateStateCode(),
  	    		controllers.lists.api.routes.javascript.Lists.instruments(),
  	    		controllers.lists.api.routes.javascript.Lists.instrumentUsedTypes(),
  	    		controllers.experiments.api.routes.javascript.Experiments.save(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateContainers(),
  	    		controllers.lists.api.routes.javascript.Lists.protocols(),
  	    		controllers.lists.api.routes.javascript.Lists.resolutions(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.getEditExperimentColumns(),
  	    		controllers.experiments.api.routes.javascript.Experiments.create()
  	    		
  	      )	  	      
  	    );
  	}
	
	private static List<PropertyDefinition> experimentProperties(String experimentTypeCode){
		 ExperimentType expType = null;
		try {
			expType = ExperimentType.find.findByCode(experimentTypeCode);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return expType.propertiesDefinitions;
	}
}
