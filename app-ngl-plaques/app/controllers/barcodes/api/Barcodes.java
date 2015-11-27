package controllers.barcodes.api;

import static play.data.Form.form;
import static validation.utils.ValidationHelper.addErrors;
import static validation.utils.ValidationHelper.required;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import controllers.CommonController;
import lims.cns.dao.LimsManipDAO;
import lims.models.Plate;
import models.utils.CodeHelper;
import play.Logger;
import play.api.modules.spring.Spring;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Result;

public class Barcodes extends CommonController {

	final static Form<BarcodesForm> form = form(BarcodesForm.class);
	
	public static Result save(){
		
		Form<BarcodesForm> filledForm = getFilledForm(form, BarcodesForm.class);
		BarcodesForm form = filledForm.get();
		validate(form, filledForm.errors());
		if (!filledForm.hasErrors()) {
    	    Set<String> set = new TreeSet<String>();
    	    Logger.debug("number = "+form.number);
    	    for(int i = 0 ; i < form.number; i++){
    	    	String newCode = newCode(form.typeCode, form.projectCode);
    	    	Spring.getBeanOfType(LimsManipDAO.class).createBarcode(newCode, form.typeCode,getCurrentUser());
    	    	set.add(newCode);
    	    	
    	    }
    	    
    	    return ok(Json.toJson(set));
    	} else {
    	    return badRequest(filledForm.errorsAsJson());
    	}
	}
	
	
	public static Result list(){
		return ok(Json.toJson(Spring.getBeanOfType(LimsManipDAO.class).findUnusedBarCodes()));
	}
	
	public static Result delete(String code){
		Spring.getBeanOfType(LimsManipDAO.class).deletePlate(code);
		return ok();
	}
	
	
	private static void validate(BarcodesForm form, Map<String, List<ValidationError>> errors) {
		if(required(errors, form, "form")){
			required(errors, form.number, "number");
			required(errors, form.typeCode, "typeCode");
			required(errors, form.projectCode, "projectCode");
		}
	}
	
	private static String newCode(Integer typeCode, String project) {
		
		String code = project.trim()+"_"+CodeHelper.getInstance().generateContainerSupportCode();
		if(Integer.valueOf(12).equals(typeCode)){
		    code = "FRGE_"+code;
		}else if(Integer.valueOf(13).equals(typeCode)){
		    code = "LIBE_"+code;
		}else if(Integer.valueOf(18).equals(typeCode)){
		    code = "PCRE_"+code;
		}else if(Integer.valueOf(14).equals(typeCode)){
		    code = "STKE_"+code;
		}else{
		    code = "PLE_"+code;
		}
		Logger.debug(code);
		return code;
	}
}
