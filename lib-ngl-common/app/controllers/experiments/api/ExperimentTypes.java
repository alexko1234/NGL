package controllers.experiments.api;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import models.laboratory.experiment.description.ExperimentType;
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

public class ExperimentTypes extends CommonController{
	
	final static Form<ExperimentTypesSearchForm> experimentTypeForm = form(ExperimentTypesSearchForm.class);
	
	public static Result get(String code){
		ExperimentType experimentType = null;
		try {
			experimentType = ExperimentType.find.findByCode(code);
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			Logger.error("DAO error: "+e.getMessage(),e);
		}
		if(experimentType == null){
			return notFound();
		}
		return ok(Json.toJson(experimentType));
	}
	
	public static Result list() throws DAOException{
		Form<ExperimentTypesSearchForm> experimentTypeFilledForm = filledFormQueryString(experimentTypeForm,ExperimentTypesSearchForm.class);
		ExperimentTypesSearchForm experimentTypesSearch = experimentTypeFilledForm.get();
		List<ExperimentType> experimentTypes = new ArrayList<ExperimentType>();
		
		try{		
			
			if(StringUtils.isNotBlank(experimentTypesSearch.categoryCode) && experimentTypesSearch.withoutOneToVoid !=null  && experimentTypesSearch.withoutOneToVoid){
				experimentTypes = ExperimentType.find.findByCategoryCodeWithoutOneToVoid(experimentTypesSearch.categoryCode);				
			}else if(experimentTypesSearch.categoryCodes != null && experimentTypesSearch.categoryCodes.size()>0 && experimentTypesSearch.withoutOneToVoid !=null  && experimentTypesSearch.withoutOneToVoid){
				experimentTypes = ExperimentType.find.findByCategoryCodesWithoutOneToVoid(experimentTypesSearch.categoryCodes);		
			}else if(StringUtils.isNotBlank(experimentTypesSearch.categoryCode) && experimentTypesSearch.processTypeCode == null){
				experimentTypes = ExperimentType.find.findByCategoryCode(experimentTypesSearch.categoryCode);
			}else if(experimentTypesSearch.categoryCodes != null && experimentTypesSearch.categoryCodes.size()>0 && experimentTypesSearch.processTypeCode == null){
					experimentTypes = ExperimentType.find.findByCategoryCodes(experimentTypesSearch.categoryCodes);
			}else if(StringUtils.isNotBlank(experimentTypesSearch.categoryCode) && StringUtils.isNotBlank(experimentTypesSearch.processTypeCode)){
				experimentTypes = ExperimentType.find.findByCategoryCodeAndProcessTypeCode(experimentTypesSearch.categoryCode, experimentTypesSearch.processTypeCode);
			}else if(StringUtils.isNotBlank(experimentTypesSearch.previousExperimentTypeCode)){
				experimentTypes = ExperimentType.find.findByPreviousExperimentTypeCode(experimentTypesSearch.previousExperimentTypeCode);
			}else{
				experimentTypes = ExperimentType.find.findAll();
			}
			
			if(experimentTypesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ExperimentType>(experimentTypes, experimentTypes.size()))); 
			}else if(experimentTypesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ExperimentType et:experimentTypes){
					lop.add(new ListObject(et.code, et.name));
				}				
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(experimentTypes));
			}
		}catch (DAOException e) {
			Logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
	
	public static Result getDefaultFirstExperiments(String processTypeCode) throws DAOException{		
			ProcessType processType = ProcessType.find.findByCode(processTypeCode);
			List<ExperimentType> expTypes = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCode(processType.firstExperimentType.code);
			return ok(Json.toJson(expTypes));		
	}
	
}
