package controllers.experiments.tpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controllers.CommonController;

import fr.cea.ig.MongoDBDAO;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.Logger;
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
import views.html.experiments.search;

public class Experiments extends CommonController{
	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result edit(String code){
		return ok(home.render(code));
	}
	
	
	public static Result searchSupports(){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code"), true, false, false));
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("containers.table.categoryCode"), true, false, false));
		
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
	
	public static Result firstEditExperiment(String experimentTypeCode){
		ExperimentType experimentType = null;
		try{
			experimentType = ExperimentType.find.findByCode(experimentTypeCode);
		}catch(models.utils.dao.DAOException e){
			
		}
		
		List<PropertyDefinition> props = experimentProperties(experimentTypeCode);
		
		DatatableConfig config = new DatatableConfig();
		config.button = true;
		config.edit = true;
		
		return ok(createExperiments.render(Json.toJson(experimentType),config,Json.toJson(props),null));
	}
	
	public static Result editExperiment(String experimentCode){
		Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
		ExperimentType experimentType = null;
		try{
			experimentType = ExperimentType.find.findByCode(experiment.typeCode);
		}catch(models.utils.dao.DAOException e){
			
		}
		
		List<PropertyDefinition> props = experimentProperties(experiment.typeCode);
		
		DatatableConfig config = new DatatableConfig();
		config.button = true;
		config.edit = true;
		
		return ok(createExperiments.render(Json.toJson(experimentType),config,Json.toJson(props),Json.toJson(experiment)));
	}
	
	public static Result getEditExperimentColumns(){
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		Map<Integer,String> extraHeaders = new HashMap<Integer, String>();
		extraHeaders.put(0, "Inputs");
		columns.add(DatatableHelpers.getColumn("support.barCode", Messages.get("containers.table.barCode"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("support.categoryCode", Messages.get("containers.table.categoryCode"), true, false, true,false,extraHeaders));	
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
	
	public static Result search(String experimentType){
		
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
	
		DatatableConfig config = new DatatableConfig(columns);
		config.save = true;
		config.edit = true;
	
		
		return ok(search.render(config));
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
  	    		controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.newExperiments(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.home(),
  	    		controllers.experiments.api.routes.javascript.Experiments.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.firstEditExperiment(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.editExperiment(),
  	    		controllers.experiments.api.routes.javascript.Experiments.generateOutput(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.getInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateComments(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateStateCode(),
  	    		controllers.instruments.api.routes.javascript.Instruments.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentUsedTypes.list(),
  	    		controllers.experiments.api.routes.javascript.Experiments.save(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateContainers(),
  	    		controllers.experiments.api.routes.javascript.Protocols.list(),
  	    		controllers.commons.api.routes.javascript.Resolutions.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.getEditExperimentColumns(),
  	    		controllers.experiments.api.routes.javascript.Experiments.create(),
				controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
				controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
				controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.containers.api.routes.javascript.Containers.list_supports()
  	    		
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
