package controllers.sra.api;



import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import models.sra.submit.common.instance.Study;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;
import models.utils.ListObject;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.CommonController;


public class Variables extends CommonController{
	
	public static Result get(String name){
		if(name.equalsIgnoreCase("mapCenterName")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapCenterName)));
		} else if(name.equalsIgnoreCase("laboratoryName")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapLaboratoryName)));
		} else if (name.equalsIgnoreCase("strategySample")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapStrategySample)));
		} else if(name.equalsIgnoreCase("existingStudyType")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapExistingStudyType)));
		} else if (name.equalsIgnoreCase("libraryStrategy")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapLibraryStrategy)));
		} else if (name.equalsIgnoreCase("librarySource")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapLibrarySource)));
		} else if (name.equalsIgnoreCase("librarySelection")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapLibrarySelection)));
		} else if (name.equalsIgnoreCase("libraryLayout")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapLibraryLayout)));
		} else if (name.equalsIgnoreCase("libraryLayoutOrientation")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapLibraryLayoutOrientation)));
		} else if (name.equalsIgnoreCase("typePlatform")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapTypePlatform)));
		} else if (name.equalsIgnoreCase("instrumentModel")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapInstrumentModel)));
		} else if (name.equalsIgnoreCase("analysisFileType")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapAnalysisFileType)));
		} else {
			return badRequest("champs inexistant");
		}
	}
	
	private static List<ListObject> toListObjects(Map<String, String> map){
		List<ListObject> lo = new ArrayList<ListObject>();
		for(String key : map.keySet()){
			lo.add(new ListObject(key, map.get(key)));
		}
		return lo;
	}	
}
