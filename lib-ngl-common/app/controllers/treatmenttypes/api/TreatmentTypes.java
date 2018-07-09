 package controllers.treatmenttypes.api;


import java.util.List;

import javax.inject.Inject;

import controllers.APICommonController;
import fr.cea.ig.play.migration.NGLContext;
import models.laboratory.run.description.TreatmentType;
import models.utils.dao.DAOException;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;



public class TreatmentTypes extends APICommonController<TreatmentTypesSearchForm> {
	@Inject
	public TreatmentTypes(NGLContext ctx) {
		super(ctx, TreatmentTypesSearchForm.class);
		treatmentTypesForm = ctx.form(TreatmentTypesSearchForm.class);
	}

	private final /*static*/ Form<TreatmentTypesSearchForm> treatmentTypesForm;

	public Result list() {
		Form<TreatmentTypesSearchForm> treatmentTypesFilledForm = filledFormQueryString(treatmentTypesForm,TreatmentTypesSearchForm.class);
		TreatmentTypesSearchForm searchForm = treatmentTypesFilledForm.get();

		List<TreatmentType> treatments;

		try{		
			if(searchForm.levels != null){
				treatments = TreatmentType.find.findByLevels(searchForm.levels);
			} else{
				treatments = TreatmentType.find.findAll();
			}
			if(searchForm.datatable){
				return ok(Json.toJson(new DatatableResponse<>(treatments, treatments.size()))); 
			}else{
				return ok(Json.toJson(treatments));
			}
		}catch (DAOException e) {
			Logger.error(e.getMessage());
			return  internalServerError(e.getMessage());
		}	
	}
	
	
	public Result get(String code) {
		TreatmentType treatmentType =  getTreatmentType(code);		
		if(treatmentType != null) {
			return ok(Json.toJson(treatmentType));	
		} 		
		else {
			return notFound();
		}	
	}

	private static TreatmentType getTreatmentType(String code) {
		try {
			return TreatmentType.find.findByCode(code);
		} catch (DAOException e) {
			throw new RuntimeException(e);
		}		
	}
}
