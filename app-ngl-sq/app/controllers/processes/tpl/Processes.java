package controllers.processes.tpl;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.processes.description.ProcessType;
import models.utils.ListObject;
import models.utils.dao.DAOException;

import org.apache.commons.lang3.StringUtils;

import play.Routes;
import play.i18n.Messages;
import play.mvc.Result;
import views.components.datatable.DatatableColumn;
import views.components.datatable.DatatableConfig;
import views.components.datatable.DatatableHelpers;
import views.html.processes.home;
import views.html.processes.newProcesses;
import views.html.processes.search;
import views.html.processes.searchContainers;
import controllers.CommonController;

public class Processes extends CommonController{

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

		columns.addAll(getPropertiesDefinitionsColumns(processTypeCode,true));

		DatatableConfig config = new DatatableConfig(columns);
		config.save = true;
		config.edit = true;

		return ok(search.render(config));
	}

	public static Result newProcesses(String processTypeCode){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		columns.add(DatatableHelpers.getColumn("support.supportCode", Messages.get("processes.table.supportCode")));
		columns.add(DatatableHelpers.getColumn("support.line", Messages.get("processes.table.line")));
		columns.add(DatatableHelpers.getColumn("support.column", Messages.get("processes.table.colums")));
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
						if(!p.choiceInList){
							columns.add(DatatableHelpers.getColumn("properties."+p.code+".value", Messages.get("processes.table.properties."+p.code), true, edit, false));
						}else{
							DatatableColumn c = DatatableHelpers.getColumn("properties."+p.code+".value", Messages.get("processes.table.properties."+p.code), true, edit, false,p.choiceInList);
							if(p.possibleValues != null){
								c.possibleValues = new ArrayList<Object>();
								for(Value v: p.possibleValues){
									ListObject l = new ListObject(v.value,v.value);
									c.possibleValues.add(l);
								}
							}
							columns.add(c);
						}
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
						controllers.processes.tpl.routes.javascript.Processes.search(),
						controllers.processes.tpl.routes.javascript.Processes.searchContainers(),
						controllers.processes.tpl.routes.javascript.Processes.home(),  
						controllers.processes.api.routes.javascript.Processes.save(),
						controllers.processes.api.routes.javascript.ProcessTypes.list(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
						controllers.projects.api.routes.javascript.Projects.list(),
		  	    		controllers.samples.api.routes.javascript.Samples.list()
						)	  	      
				);
	}
}
