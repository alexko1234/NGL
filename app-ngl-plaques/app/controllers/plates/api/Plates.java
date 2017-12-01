package controllers.plates.api;

// import static play.data.Form.form;
import static fr.cea.ig.play.IGGlobals.form;

import static validation.utils.ValidationHelper.addErrors;
import static validation.utils.ValidationHelper.required;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lims.cns.dao.LimsManipDAO;
import lims.models.Plate;
import models.utils.CodeHelper;
// import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Result;
import views.components.datatable.DatatableResponse;
import controllers.CommonController;
import controllers.MaterielManipSearch;

// TODO: use DI, extends DocumemntController to start with
public class Plates extends CommonController {
	
	/**
	 * Logger.
	 */
	private static final play.Logger.ALogger logger = play.Logger.of(Plates.class);
	
	
	final static Form<Plate> wellsForm = form(Plate.class);
	
	final static Form<MaterielManipSearch> manipForm = form(MaterielManipSearch.class);
	
	public static Result save() {
		Form<Plate> filledForm = getFilledForm(wellsForm, Plate.class);

		Plate plate = filledForm.get();
		boolean isUpdate = true;
		logger.info("SAVE Plate : " + plate);
		if (plate.code == null) {
			plate.code = newCode(plate.wells[0].typeCode);
			if (plate.wells.length > 0) {
				plate.typeName = plate.wells[0].typeName;
				plate.typeCode = plate.wells[0].typeCode;
			}
			isUpdate = false;
		}
		validatePlate(plate, filledForm.errors(), isUpdate);
		if (!filledForm.hasErrors()) {
			logger.debug(plate.toString());
			if (!isUpdate) {
				Spring.getBeanOfType(LimsManipDAO.class).createPlate(plate,getCurrentUser());
			} else {
				Spring.getBeanOfType(LimsManipDAO.class).updatePlate(plate,getCurrentUser());
			}
			plate = Spring.getBeanOfType(LimsManipDAO.class).getPlate(plate.code);  
			return ok(Json.toJson(plate));
		} else {
			return badRequest(filledForm.errorsAsJson()); // not legit, use ContextValidation
		}
	}
	
	public static Result list(){
		Form<MaterielManipSearch> filledForm =  manipForm.bindFromRequest();
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		logger.info("Manip Form :"+filledForm.toString());		
		List<Plate> plates = limsManipDAO.findPlates(filledForm.get().etmanip,filledForm.get().project, filledForm.get().plaqueId, 
				filledForm.get().matmanom, filledForm.get().percodc, filledForm.get().fromDate, filledForm.get().toDate);		
		return ok(Json.toJson(new DatatableResponse(plates, plates.size())));
	}
	
	public static Result get(String code){
		logger.info("GET Plate : "+code);
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		Plate plate = limsManipDAO.getPlate(code);
		if(plate != null){			
			return ok(Json.toJson(plate));					
		}else{
			return notFound();
		}
	}
	
	public static Result remove(String code){
		logger.info("DELETE Plate : "+code);
		LimsManipDAO  limsManipDAO = Spring.getBeanOfType(LimsManipDAO.class);
		limsManipDAO.deletePlate(code);
		return ok();
	}
	
	
	private static void validatePlate(Plate plate, Map<String, List<ValidationError>> errors, boolean isUpdate) {
		if(required(errors, plate, "plate")){
			required(errors, plate.code, "code");
			required(errors, plate.typeCode, "typeCode");
			if(required(errors, plate.wells, "wells")){
				for(int i = 0 ; i < plate.wells.length ; i++){
					required(errors, plate.wells[i].x, "wells["+i+"]"+".x");
					required(errors, plate.wells[i].y, "wells["+i+"]"+".y");
					required(errors, plate.wells[i].code, "wells["+i+"]"+".code");
					if(!plate.wells[i].typeCode.equals(plate.typeCode)){
						addErrors(errors, "wells["+i+"]"+".typeName", "plates.error.typecode.different", plate.typeName, plate.wells[i].typeName);
					}
				}
				if(!isUpdate){
					validatePlateCode(plate, errors); 
				}
				
				for(int i = 0 ; i < plate.wells.length ; i++){
					for(int j = 0 ; j < plate.wells.length ; j++){
						if(i != j){
							if(plate.wells[i].code.equals(plate.wells[j].code)){
								addErrors(errors, "wells["+i+"]"+".name", "plates.error.severalsamewellcode", plate.wells[i].name);
							}
							
							if(plate.wells[i].x != null && plate.wells[i].y != null && plate.wells[i].x.equals(plate.wells[j].x) && plate.wells[i].y.equals(plate.wells[j].y)){
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
		if(Spring.getBeanOfType(LimsManipDAO.class).isPlateExist(plate.code)){
			addErrors(errors, "code", "plates.error.code.exist");
		}
	}

	private static String newCode(Integer typeCode) {
		String code = CodeHelper.getInstance().generateContainerSupportCode();
		if(Integer.valueOf(12).equals(typeCode)){
		    code = "FRG_"+code;
		}else if(Integer.valueOf(13).equals(typeCode)){
		    code = "LIB_"+code;
		}else if(Integer.valueOf(18).equals(typeCode)){
		    code = "PCR_"+code;
		}else if(Integer.valueOf(14).equals(typeCode)){
		    code = "STK_"+code;
		}else{
		    code = "PL_"+code;
		}
		
		return code;
	}

		
}
