package controllers.processes.tpl;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.laboratory.common.description.PropertyDefinition;
import models.laboratory.common.description.Value;
import models.laboratory.processes.description.ProcessType;
import models.utils.ListObject;
import models.utils.ListObjectInt;
import models.utils.ListObjectValue;
import models.utils.dao.DAOException;

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
	
	public static Result searchColumns(){
		Form<ProcessesSearchForm> processesFilledForm = filledFormQueryString(processesSearchForm,ProcessesSearchForm.class);
		ProcessesSearchForm processesSearch = processesFilledForm.get();
		
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();
		columns.add(DatatableHelpers.getColumn("containerInputCode", Messages.get("processes.table.containerInputCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("typeCode", Messages.get("processes.table.typeCode"), true, false, false));
		columns.add( DatatableHelpers.getDateColumn("traceInformation.creationDate", Messages.get("processes.table.creationDate"), true, false, false));
		columns.add(DatatableHelpers.getColumn("state.code", Messages.get("processes.table.stateCode"), true, false, false, "codes:'state'"));
		columns.add(DatatableHelpers.getColumn("state.resolutionCodes", Messages.get("processes.table.resolutionCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("currentExperimentTypeCode", Messages.get("processes.table.currentExperimentTypeCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("projectCode", Messages.get("processes.table.projectCode"), true, false, false));						
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("processes.table.sampleCode"), true, false, false));
		columns.add(DatatableHelpers.getColumn("code", Messages.get("processes.table.code"), true, false, false));
		
		if(processesSearch.typeCode != null){
			columns.addAll(getPropertiesDefinitionsColumns(processesSearch.typeCode ,true));
		}
		
		return ok(Json.toJson(columns));
	}

	public static Result newProcesses(String processTypeCode){
		return ok(newProcesses.render());
	}
	
	public static Result newProcessesColumns(String processTypeCode){
		List<DatatableColumn> columns = new ArrayList<DatatableColumn>();		
		columns.add(DatatableHelpers.getColumn("support.code", Messages.get("processes.table.supportCode")));
		columns.add(DatatableHelpers.getColumn("support.line", Messages.get("processes.table.line")));
		columns.add(DatatableHelpers.getColumn("support.column", Messages.get("processes.table.columns")));
		columns.add(DatatableHelpers.getColumn("code", Messages.get("processes.table.code")));
		columns.add(DatatableHelpers.getColumn("projectCode", Messages.get("processes.table.projectCode")));						
		columns.add(DatatableHelpers.getColumn("sampleCode", Messages.get("processes.table.sampleCode")));
		columns.add(DatatableHelpers.getColumn("containerInputCode", Messages.get("processes.table.containerInputCode")));
		columns.add(DatatableHelpers.getColumn("state.code", Messages.get("processes.table.stateCode"),"codes:'state'"));

		columns.addAll(getPropertiesDefinitionsColumns(processTypeCode, true));

		return ok(Json.toJson(columns));
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
						DatatableColumn c = null;
						if(!p.valueType.equals("java.util.Date")){
							c = DatatableHelpers.getColumn("properties."+p.code+".value", Messages.get("processes.table.properties."+p.code), true, edit, false);
						}else{
							c = DatatableHelpers.getDateColumn("properties."+p.code+".value", Messages.get("processes.table.properties."+p.code), true, edit, false);
							
						}
						if(p.choiceInList){
							c.choiceInList = p.choiceInList;
							if(p.possibleValues != null){
								c.possibleValues = new ArrayList<Object>();
								for(Value v: p.possibleValues){										
									ListObjectValue l = null;
									if(p.valueType.equals("java.lang.String")){
										l = new ListObjectValue<String>(v.value,v.value);									
									}
									else if(p.valueType.equals("java.lang.Double")){
										l = new ListObjectValue<Double>(Double.parseDouble(v.value),v.value);										
									}
									else if(p.valueType.equals("java.lang.Float")){
										l = new ListObjectValue<Float>(Float.parseFloat(v.value),v.value);										
									}
									else if(p.valueType.equals("java.lang.Integer")){
										l = new ListObjectValue<Integer>(Integer.parseInt(v.value),v.value);										
									}
									else{										
										Logger.debug("Not implemented :"+ p.valueType);									
									}
									c.possibleValues.add(l);
									
								}
							}
						}
						columns.add(c);
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
		 	    		controllers.processes.api.routes.javascript.Processes.update(),
						controllers.processes.tpl.routes.javascript.Processes.searchColumns(),  
						controllers.processes.tpl.routes.javascript.Processes.newProcessesColumns(),  
						controllers.processes.api.routes.javascript.Processes.save(),
						controllers.processes.api.routes.javascript.ProcessTypes.list(),
						controllers.containers.api.routes.javascript.Containers.list(),
						controllers.processes.api.routes.javascript.Processes.list(),
						controllers.processes.api.routes.javascript.ProcessCategories.list(),
						controllers.commons.api.routes.javascript.CommonInfoTypes.list(),
						controllers.projects.api.routes.javascript.Projects.list(),
		  	    		controllers.samples.api.routes.javascript.Samples.list(),
		  	    		controllers.supports.api.routes.javascript.Supports.list(),
		  	    		controllers.commons.api.routes.javascript.States.list(),
		  	    		controllers.commons.api.routes.javascript.Users.list(),
		  	    		controllers.containers.api.routes.javascript.ContainerSupportCategories.list(),
		  	    		controllers.experiments.api.routes.javascript.ExperimentTypes.list()
						)	  	      
				);
	}
}
