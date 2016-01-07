package controllers.processes.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import models.laboratory.processes.description.ProcessType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;

public class ProcessTypes extends CommonController{
	
	final static Form<ProcessTypesSearchForm> processTypeForm = form(ProcessTypesSearchForm.class);
	
	public static Result list() throws DAOException{
		Form<ProcessTypesSearchForm> processTypeFilledForm = filledFormQueryString(processTypeForm,ProcessTypesSearchForm.class);
		ProcessTypesSearchForm processTypesSearch = processTypeFilledForm.get();
		
		List<ProcessType> processTypes;
		
		try{	
			if(StringUtils.isNotBlank(processTypesSearch.categoryCode)){
				processTypes = ProcessType.find.findByProcessCategoryCodes(processTypesSearch.categoryCode);
			}else if(CollectionUtils.isNotEmpty(processTypesSearch.categoryCodes)){
				processTypes = ProcessType.find.findByProcessCategoryCodes(processTypesSearch.categoryCodes.toArray(new String[0]));
			}if(CollectionUtils.isNotEmpty(processTypesSearch.codes)){
				processTypes = ProcessType.find.findByCodes(processTypesSearch.codes);
			}else{
				processTypes = ProcessType.find.findAll();
			}
			
			if(processTypesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ProcessType>(processTypes, processTypes.size()))); 
			}else if(processTypesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ProcessType et:processTypes){
					lop.add(new ListObject(et.code, et.name));
				}
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(processTypes));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
	
	public static Result get(String code) throws DAOException{		 
			ProcessType processType = ProcessType.find.findByCode(code);
			if(processType!=null){
				return ok(Json.toJson(processType));
			}			
			return notFound();		
	}
}
