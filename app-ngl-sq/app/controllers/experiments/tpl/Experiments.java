package controllers.experiments.tpl;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.OneToOne;

import org.codehaus.jackson.JsonNode;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;
import models.utils.dao.DAOException;
import play.Logger;
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
import fr.cea.ig.MongoDBDAO;

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
	
	
	public static Result getInputTemplate(String atomicType, String inputCategory){
		if(atomicType.equals("OneToOne")){
			if(inputCategory.equals("tube")){
				return ok(views.html.experiments.oneToOne.inputs.tube.render());
			}
		}else if(atomicType.equals("ManyToOne")){
			if(inputCategory.equals("mapcard")){
				return ok(views.html.experiments.oneToOne.inputs.tube.render());
			}
		}
		
		return badRequest("Not implemented");
	}
	
	public static Result firstEditExperiment(String experimentTypeCode){
		ExperimentType experimentType = null;
		try{
			experimentType = ExperimentType.find.findByCode(experimentTypeCode);
		}catch(models.utils.dao.DAOException e){
			
		}
		
		List<PropertyDefinition> props = experimentProperties(experimentTypeCode);
	
		return ok(createExperiments.render(experimentType.category.code, experimentType.atomicTransfertMethod,Json.toJson(props),null));
	}
	
	public static Result editExperiment(String experimentCode){
		Experiment experiment = MongoDBDAO.findByCode(InstanceConstants.EXPERIMENT_COLL_NAME, Experiment.class, experimentCode);
		ExperimentType experimentType = null;
		try{
			experimentType = ExperimentType.find.findByCode(experiment.typeCode);
		}catch(models.utils.dao.DAOException e){
			
		}
		
		List<PropertyDefinition> props = experimentProperties(experiment.typeCode);

		return ok(createExperiments.render(experimentType.category.code, experimentType.atomicTransfertMethod,Json.toJson(props),Json.toJson(experiment)));
	}
	
	public static Result getEditExperimentColumns(){
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		Map<Integer,String> extraHeaders = new HashMap<Integer, String>();
		extraHeaders.put(0, "Inputs");
		columns.add(DatatableHelpers.getColumn("support.code", Messages.get("containers.table.supportCode"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("support.categoryCode", Messages.get("containers.table.categoryCode"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("code", Messages.get("containers.table.code"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("projectCodes", Messages.get("containers.table.projectCodes"), true, false, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("mesuredVolume.value", Messages.get("experiments.table.volume.value"),true, true, true,false,extraHeaders));	
		columns.add(DatatableHelpers.getColumn("sampleCodes", Messages.get("containers.table.sampleCodes"), true, false, true,false,extraHeaders));		
		DatatableColumn dc = DatatableHelpers.getColumn("state.code", Messages.get("containers.table.stateCode"), true, false, true,false,extraHeaders);
		dc.filter = "codes:'state'";
		columns.add(dc);
		columns.add(DatatableHelpers.getColumn("categoryCode", Messages.get("containers.table.categoryCode"), true, false, true,false,extraHeaders));
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
  	    		controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.newExperiments(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.getInputTemplate(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.home(),
  	    		controllers.experiments.api.routes.javascript.Experiments.list(),
  	    		controllers.experiments.api.routes.javascript.ExperimentTypeNodes.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.firstEditExperiment(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.editExperiment(),
  	    		controllers.experiments.api.routes.javascript.Experiments.generateOutput(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateExperimentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentInformations(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.getInstrumentProperties(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateComments(),
  	    		controllers.experiments.api.routes.javascript.Experiments.nextState(),
  	    		controllers.instruments.api.routes.javascript.Instruments.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentUsedTypes.list(),
  	    		controllers.instruments.api.routes.javascript.InstrumentCategories.list(),
  	    		controllers.experiments.api.routes.javascript.Experiments.save(),
  	    		controllers.experiments.api.routes.javascript.Experiments.updateContainers(),
  	    		controllers.experiments.api.routes.javascript.Protocols.list(),
  	    		instruments.io.routes.javascript.Outputs.sampleSheets(),
  	    		controllers.commons.api.routes.javascript.Resolutions.list(),
  	    		controllers.commons.api.routes.javascript.States.list(),
  	    		controllers.experiments.tpl.routes.javascript.Experiments.getEditExperimentColumns(),
				controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
				controllers.experiments.api.routes.javascript.ExperimentCategories.list(),
				controllers.projects.api.routes.javascript.Projects.list(),
  	    		controllers.samples.api.routes.javascript.Samples.list(),
  	    		controllers.commons.api.routes.javascript.Users.list(),
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
