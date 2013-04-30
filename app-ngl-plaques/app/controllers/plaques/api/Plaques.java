package controllers.plaques.api;

import static play.data.Form.form;
import static validation.utils.ConstraintsHelper.addErrors;
import static validation.utils.ConstraintsHelper.required;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ls.dao.LimsManipDAO;
import ls.models.Plate;
import ls.models.Well;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.MaterielManipSearch;

public class Plaques extends CommonController {
	final static Form<Plate> wellsForm = form(Plate.class);
	final static Form<MaterielManipSearch> manipForm = form(MaterielManipSearch.class);
	
	public static Result save(){
		
		Form<Plate> filledForm = getFilledForm(wellsForm, Plate.class);
		if (!filledForm.hasErrors()) {
			Plate plate = filledForm.get();
			boolean isUpdate = true;
			if(plate.code == null){
				plate.code = newCode();
				isUpdate = false;
			}
			validatePlate(plate, filledForm.errors(), isUpdate);
			if (!filledForm.hasErrors()) {
				Logger.debug(plate.toString());
				Spring.getBeanOfType(LimsManipDAO.class).updatePlateCoordonates(plate);				
				filledForm.fill(plate);
			}
		}
		if (!filledForm.hasErrors()) {
			return ok(Json.toJson(filledForm.get()));
		} else {
			return badRequest(filledForm.errorsAsJson());
		}		
	}
	
	public static Result list(){
		Form<MaterielManipSearch> filledForm =  manipForm.bindFromRequest();
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		Logger.info("Manip Form :"+filledForm.toString());		
		List<Plate> plates = limsManipDAO.findPlaques(filledForm.get().emateriel,filledForm.get().project);		
		return ok(Json.toJson(new DatatableResponse(plates, plates.size())));
	}
	
	public static Result get(String code){
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		Plate plate = limsManipDAO.findPlate(code);
		if(plate != null){			
			return ok(Json.toJson(plate));					
		}else{
			return notFound();
		}
	}
	
	private static void validatePlate(Plate plate, Map<String, List<ValidationError>> errors, boolean isUpdate) {
		if(required(errors, plate, "plate")){
			required(errors, plate.code, "plateCode");
			if(required(errors, plate.wells, "wells")){
				for(Well well : plate.wells){
					required(errors, well.x, well.name+".x");
					required(errors, well.y, well.name+".y");
					required(errors, well.code, well.name+".code");
				}
				if(!isUpdate){
					validatePlateCode(plate, errors); 
				}
				
				for(int i = 0 ; i < plate.wells.length ; i++){
					for(int j = 0 ; j < plate.wells.length ; j++){
						if(i != j){
							if(plate.wells[i].code.equals(plate.wells[j].code)){
								addErrors(errors, "wells["+i+"]", "plates.error.severalsamewellcode", plate.wells[i].name);
							}
							
							if(plate.wells[i].x.equals(plate.wells[j].x) && plate.wells[i].y.equals(plate.wells[j].y)){
								addErrors(errors, "wells["+i+"]", "plates.error.wellwithsamecoord", plate.wells[i].x, plate.wells[i].y);
							}
						}
					}
				}
			}
		}
	}


	private static void validatePlateCode(Plate plate,
			Map<String, List<ValidationError>> errors) {
		if(Spring.getBeanOfType(LimsManipDAO.class).isPlateCodeExist(plate.code)){
			addErrors(errors, "code", "plates.error.code.exist");
		}
	}

	private static String newCode() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String code = "PL"+sdf.format(new Date());
		return code;
	}

		
}
