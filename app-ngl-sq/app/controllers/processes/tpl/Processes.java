package controllers.processes.tpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ning.http.client.providers.apache.ApacheAsyncHttpProvider;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.processes.description.ProcessType;
import models.utils.dao.DAOException;

import controllers.processes.tpl.routes.javascript;

import play.Logger;
import play.Routes;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;

import views.html.processes.*;

public class Processes extends Controller{
	
	public static Result home(String code){
		return ok(home.render(code));
	}
	
	public static Result searchHome(String code){
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
	
	public static Result search(String processTypeCode){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		columns.add(DatatableHelpers.getColumn("code", Messages.get("processes.table.code"), true, false, false));
		columns.add(DatatableHelpers.getColumn("typeCode", Messages.get("processes.table.typeCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("projectCode", Messages.get("processes.table.projectCode"), true, false, false));						
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("processes.table.sampleCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("containerInputCode", Messages.get("processes.table.containerInputCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("processes.table.stateCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("resolutionCode", Messages.get("processes.table.resolutionCode"), true, false, false));
		columns.add( DatatableHelpers.getDateColumn("traceInformation.creationDate", Messages.get("processes.table.creationDate"), true, false, false));
		columns.add(DatatableHelpers.getColumn("currentExperimentTypeCode", Messages.get("processes.table.currentExperimentTypeCode"), true, false, false));
		
		columns.addAll(getPropertiesDefinitionsColumns(processTypeCode,false));
		
		DatatableConfig config = new DatatableConfig(columns);
		
		return ok(search.render(config));
	}
	
	public static Result newProcesses(String processTypeCode){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		columns.add(DatatableHelpers.getColumn("code", Messages.get("processes.table.code")));
		columns.add(DatatableHelpers.getColumn("projectCode", Messages.get("processes.table.projectCode")));						
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("processes.table.sampleCode")));
		columns.add(DatatableHelpers.getColumn("containerInputCode", Messages.get("processes.table.containerInputCode")));
		columns.add(DatatableHelpers.getColumn("stateCode", Messages.get("processes.table.stateCode")));
		
		columns.addAll(getPropertiesDefinitionsColumns(processTypeCode, true));
		
		DatatableConfig config = new DatatableConfig(columns);
		config.save = true;
		config.edit = true;
		config.remove = true;
		config.button = true;
		return ok(newProcesses.render(config));
	}
	
	
	private static List<DatatableColumn> getPropertiesDefinitionsColumns(String processTypeCode,Boolean edit){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		//Adding property definition columns
		if(!StringUtils.isEmpty(processTypeCode) && !processTypeCode.equals("home")) {
			try {
				ProcessType processType = ProcessType.find.findByCode(processTypeCode);
				if(processType != null && processType.propertiesDefinitions != null) {
					List<PropertyDefinition> propertyDefinition = processType.propertiesDefinitions;
					for(PropertyDefinition p : propertyDefinition) {
						columns.add(DatatableHelpers.getColumn("properties."+p.name, Messages.get("processes.table.properties."+p.name), true, edit, false));
					}
				}
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
		return columns;
	}
	
	public static Result javascriptRoutes() {
  	    response().setContentType("text/javascript");
  	    return ok(  	    		
  	      Routes.javascriptRouter("jsRoutes",
  	        // Routes
  	    		controllers.processes.tpl.routes.javascript.Processes.newProcesses(),  
  	    		controllers.processes.tpl.routes.javascript.Processes.home(),  
  	    		controllers.processes.api.routes.javascript.Processes.save(),
  	    		controllers.lists.api.routes.javascript.Lists.projects(),
  	    		controllers.lists.api.routes.javascript.Lists.samples(),
  	    		controllers.lists.api.routes.javascript.Lists.processTypes(),
  	    		controllers.containers.api.routes.javascript.Containers.list(),
  	    		controllers.processes.api.routes.javascript.Processes.list()
  	      )	  	      
  	    );
  	  }
}
