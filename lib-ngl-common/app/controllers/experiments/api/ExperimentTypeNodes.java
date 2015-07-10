package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.processes.description.ExperimentTypeNode;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ExperimentTypeNodes extends CommonController{

	final static Form<ExperimentTypeNodesSearchForm> experimentTypeNodeForm = form(ExperimentTypeNodesSearchForm.class);
	
	public static Result list() throws DAOException{
		Form<ExperimentTypeNodesSearchForm>  experimentTypeNodeFilledForm = filledFormQueryString(experimentTypeNodeForm,ExperimentTypeNodesSearchForm.class);
		ExperimentTypeNodesSearchForm experimentTypeNodesSearch = experimentTypeNodeFilledForm.get();
		try{
			List<ExperimentTypeNode> experimentTypeNodes = new ArrayList<ExperimentTypeNode>();
			
			if(StringUtils.isNotBlank(experimentTypeNodesSearch.code)){
				experimentTypeNodes.add(ExperimentTypeNode.find.findByCode(experimentTypeNodesSearch.code));
			}else{
				experimentTypeNodes = ExperimentTypeNode.find.findAll();
			}
			
			if(experimentTypeNodesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ExperimentTypeNode>(experimentTypeNodes, experimentTypeNodes.size()))); 
			}else if(experimentTypeNodesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ExperimentTypeNode et:experimentTypeNodes){
					lop.add(new ListObject(et.code, et.code));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(experimentTypeNodes));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
