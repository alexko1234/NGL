package controllers.experiments.api;

//import static play.data.Form.form;
//import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import controllers.APICommonController;
//import controllers.CommonController;
import controllers.authorisation.Permission;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.processes.description.ExperimentTypeNode;
import models.utils.ListObject;
import models.utils.dao.DAOException;
//import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;

public class ExperimentTypeNodes extends APICommonController<ExperimentTypeNodesSearchForm> { //CommonController{

	private static final play.Logger.ALogger logger = play.Logger.of(ExperimentTypeNodes.class);
	
	private final /*static*/ Form<ExperimentTypeNodesSearchForm> experimentTypeNodeForm; // = form(ExperimentTypeNodesSearchForm.class);
	
	@Inject
	public ExperimentTypeNodes(NGLContext ctx) {
		super(ctx, ExperimentTypeNodesSearchForm.class);
		experimentTypeNodeForm = ctx.form(ExperimentTypeNodesSearchForm.class);
	}
	
	@Permission(value={"reading"})
	public Result get(String code){
		try {
			ExperimentTypeNode experimentTypeNode = ExperimentTypeNode.find.findByCode(code);
			if(experimentTypeNode == null){
				return notFound();
			}else{
				return ok(Json.toJson(experimentTypeNode));
			}
			
		} catch (DAOException e) {
			return internalServerError(e.getMessage());
		}		
	}
	
	@Permission(value={"reading"})
	public Result list() throws DAOException{
		Form<ExperimentTypeNodesSearchForm>  experimentTypeNodeFilledForm = filledFormQueryString(experimentTypeNodeForm,ExperimentTypeNodesSearchForm.class);
		ExperimentTypeNodesSearchForm experimentTypeNodesSearch = experimentTypeNodeFilledForm.get();
		try {
			List<ExperimentTypeNode> experimentTypeNodes = new ArrayList<>();
			
			if(StringUtils.isNotBlank(experimentTypeNodesSearch.code)){
				experimentTypeNodes.add(ExperimentTypeNode.find.findByCode(experimentTypeNodesSearch.code));
			}else if(CollectionUtils.isNotEmpty(experimentTypeNodesSearch.codes)){
				experimentTypeNodes.addAll(ExperimentTypeNode.find.findByCodes(experimentTypeNodesSearch.codes));
			}else{
				experimentTypeNodes = ExperimentTypeNode.find.findAll();
			}
			
			if(experimentTypeNodesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<>(experimentTypeNodes, experimentTypeNodes.size()))); 
			}else if(experimentTypeNodesSearch.list){
				List<ListObject> lop = new ArrayList<>();
				for(ExperimentTypeNode et:experimentTypeNodes){
					lop.add(new ListObject(et.code, et.code));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(experimentTypeNodes));
			}
		} catch (DAOException e) {
			logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
