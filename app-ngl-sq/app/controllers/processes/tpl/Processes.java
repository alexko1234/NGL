package controllers.processes.tpl;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.processes.description.ProcessType;
import models.laboratory.sample.description.SampleType;
import models.utils.ListObjectValue;
import models.utils.dao.DAOException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Routes;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableHelpers;
import views.html.processes.home;
import views.html.processes.newProcesses;
import views.html.processes.search;
import views.html.processes.searchContainers;
import controllers.CommonController;
import controllers.containers.api.Containers;
import controllers.processes.api.ProcessesSearchForm;

public class Processes extends CommonController{

	final static Form<ProcessesSearchForm> processesSearchForm = form(ProcessesSearchForm.class);

	public static Result home(String code){
		return ok(home.render(code));
	}

	public static Result searchContainers(){
		return ok(searchContainers.render());
	}

	public static Result search(String processTypeCode){
		return ok(search.render());
	}

	public static Result newProcesses(String processTypeCode){
		return ok(newProcesses.render());
	}
	

	public static Result getPropertiesDefinitions(String processTypeCode){
		ProcessType processType;
		try {
			processType = ProcessType.find.findByCode(processTypeCode);
			if(processType != null && processType.propertiesDefinitions != null) {
				return ok(Json.toJson(processType.propertiesDefinitions));
			}
		} catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
		}

		return badRequest();
	}	

	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		return ok(  	    		
				Routes.javascriptRouter("jsRoutes",
						// Routes
						controllers.processes.tpl.routes.javascript.Processes.newProcesses(),  
						controllers.processes.tpl.routes.javascript.Processes.search(),
						controllers.processes.tpl.routes.javascript.Processes.searchContainers(),
						controllers.processes.tpl.routes.javascript.Processes.home(),  
						controllers.processes.api.routes.javascript.Processes.update(),
						controllers.processes.tpl.routes.javascript.Processes.getPropertiesDefinitions(),
						controllers.processes.api.routes.javascript.Processes.save(),
						controllers.processes.api.routes.javascript.Processes.saveBatch(),
						controllers.processes.api.routes.javascript.Processes.delete(),
						controllers.processes.api.routes.javascript.Processes.updateStateCode(),
						controllers.processes.api.routes.javascript.ProcessTypes.list(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.processes.api.routes.javascript.ProcessCategories.list(),
						controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
						controllers.commons.api.routes.javascript.Values.list(),
						controllers.projects.api.routes.javascript.Projects.list(),
						controllers.samples.api.routes.javascript.Samples.list(),
						controllers.experiments.api.routes.javascript.Experiments.list(),
						controllers.containers.api.routes.javascript.ContainerSupports.list(),
						controllers.commons.api.routes.javascript.States.list(),
						controllers.commons.api.routes.javascript.Users.list(),
						controllers.reporting.api.routes.javascript.FilteringConfigurations.list(),
						controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.list(),
						controllers.experiments.api.routes.javascript.ExperimentTypes.getDefaultFirstExperiments(),
						controllers.reporting.api.routes.javascript.ReportingConfigurations.list(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.get(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.save(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.update(),
			      		controllers.reporting.api.routes.javascript.ReportingConfigurations.delete(),
			      		controllers.commons.api.routes.javascript.Values.list(),
			      		controllers.containers.api.routes.javascript.Contents.list(),
			      		controllers.commons.api.routes.javascript.Parameters.list()
						)	  	      
				);
	}
}
