package controllers.sra.api;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import models.sra.submit.util.VariableSRA;
import models.utils.ListObject;
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
		} else if (name.equalsIgnoreCase("strategyStudy")){
			return ok(Json.toJson(toListObjects(VariableSRA.mapStrategyStudy)));
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
		
		//Sort by code
		Collections.sort(lo, new Comparator<ListObject>(){
		    public int compare(ListObject lo1, ListObject lo2) {
		    	return lo1.code.compareTo(lo2.code);
		    }
		});
		return lo;
	}	
}
