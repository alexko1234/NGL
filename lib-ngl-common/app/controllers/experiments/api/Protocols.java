package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.experiment.description.Protocol;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.ListForm;

public class Protocols extends CommonController{

final static Form<ProtocolsSearchForm> protocolForm = form(ProtocolsSearchForm.class);
	
	public static Result list(){
		Form<ProtocolsSearchForm> protocolTypeFilledForm = filledFormQueryString(protocolForm,ProtocolsSearchForm.class);
		ProtocolsSearchForm protocolSearch = protocolTypeFilledForm.get();
		
		List<Protocol> protocols = new ArrayList<Protocol>();
		
		try{		
			if(StringUtils.isNotBlank(protocolSearch.experimentTypeCode)){
				protocols = Protocol.find.findByExperimentTypeCode(protocolSearch.experimentTypeCode);
			}else{
				protocols = Protocol.find.findAll();
			}
			
			if(protocolSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<Protocol>(protocols, protocols.size()))); 
			}else if(protocolSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(Protocol et:protocols){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(protocols));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
}
