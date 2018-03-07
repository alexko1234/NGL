package controllers.experiments.api;

// import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import models.laboratory.experiment.description.ExperimentType;
import models.laboratory.processes.description.ProcessType;
import models.utils.ListObject;
import models.utils.dao.DAOException;
// import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Results;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.authorisation.Permission;

public class ExperimentTypes extends CommonController {
	
	/**
	 * Logger.
	 */
	private final static play.Logger.ALogger logger = play.Logger.of(ExperimentTypes.class);
	
	
	final static Form<ExperimentTypesSearchForm> experimentTypeForm = form(ExperimentTypesSearchForm.class);
	
	@Permission(value={"reading"})
	public static Result get(String code){
		ExperimentType experimentType = null;
		try {
			experimentType = ExperimentType.find.findByCode(code);
		} catch (DAOException e) {
			logger.error("DAO error: "+e.getMessage(),e);
			// throw new RuntimeException("get experiment('" + code + "') failed",e);
		}
		if (experimentType == null)
			return notFound();
		return ok(Json.toJson(experimentType));
	}
	
	@Permission(value={"reading"})
	public static Result list() throws DAOException {
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
			}else if(StringUtils.isNotBlank(experimentTypesSearch.previousExperimentTypeCode) 
					&& StringUtils.isNotBlank(experimentTypesSearch.processTypeCode)){
				//experimentTypes = ExperimentType.find.findByPreviousExperimentTypeCodeInProcessTypeContext(experimentTypesSearch.previousExperimentTypeCode, experimentTypesSearch.processTypeCode);
				experimentTypes = ExperimentType.find.findNextExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(experimentTypesSearch.previousExperimentTypeCode, experimentTypesSearch.processTypeCode);
			}else if(StringUtils.isNotBlank(experimentTypesSearch.previousExperimentTypeCode)){
				experimentTypes = ExperimentType.find.findNextExperimentTypeCode(experimentTypesSearch.previousExperimentTypeCode);
			}else{
				experimentTypes = ExperimentType.find.findAll();
			}
			
			if(experimentTypesSearch.datatable){
				return ok(Json.toJson(new DatatableResponse<ExperimentType>(experimentTypes, experimentTypes.size()))); 
			}else if(experimentTypesSearch.list){
				List<ListObject> lop = new ArrayList<ListObject>();
				for(ExperimentType et:experimentTypes){
					if(null == experimentTypesSearch.isActive){
						lop.add(new ListObject(et.code, et.name));
					}else if(experimentTypesSearch.isActive.equals(et.active)){
						lop.add(new ListObject(et.code, et.name));
					}
					
					
				}				
				return Results.ok(Json.toJson(lop));
			}else{
				return Results.ok(Json.toJson(experimentTypes));
			}
		}catch (DAOException e) {
			logger.error("DAO error: "+e.getMessage(),e);
			return  Results.internalServerError(e.getMessage());
		}	
	}
	
	@Permission(value={"reading"})
	public static Result getDefaultFirstExperiments(String processTypeCode) throws DAOException{		
			ProcessType processType = ProcessType.find.findByCode(processTypeCode);
			List<ExperimentType> expTypes = ExperimentType.find.findPreviousExperimentTypeForAnExperimentTypeCodeAndProcessTypeCode(processType.firstExperimentType.code, processType.code, -1);
			return ok(Json.toJson(expTypes));		
	}
	
}
