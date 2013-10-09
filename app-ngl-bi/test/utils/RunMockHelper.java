package utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.common.instance.TBoolean;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;
import models.laboratory.run.instance.Treatment;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;

public class RunMockHelper {
	
	public static JsonNode getJsonRun(Run run) {
		return Json.toJson(run);
	}
	
	public static JsonNode getJsonLane(Lane lane) {
		return Json.toJson(lane);
	}
	
	public static JsonNode getJsonReadSet(ReadSet readSet) {
		return Json.toJson(readSet);
	}
	
	public static JsonNode getJsonFile(File file) {
		return Json.toJson(file);
	}
	
	public static JsonNode getJsonTreatment(Treatment treatment) {
		return Json.toJson(treatment);
	}
	
	
	

	
	
	public static Run newRun(String code){
		Run run = new Run();
		run.code = code;
		run.containerSupportCode = "FC00000";
		run.dispatch = true;
		
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.code = "HS7";
		run.instrumentUsed.categoryCode = "HISEQ2000";
		
		run.typeCode = "RHS2000";
		
		run.stateCode = "F";
		List<String> lResos = new ArrayList<String>();
		lResos.add("reso1");
		lResos.add("reso2");
		run.resolutionCode = lResos;
		
		TraceInformation ti = new TraceInformation(); 
		ti.setTraceInformation("dnoisett");
		run.traceInformation = ti; 
		/*
		Map<String, Treatment> lT = new HashMap<String, Treatment>();
		Treatment ngsrg = new Treatment(); 
		lT.put("ngsrg", ngsrg);
		ngsrg.categoryCode = "quality";
		ngsrg.typeCode = "ngsrg";
		run.treatments = lT;
		*/
		return run;
	}
	
	public static Lane newLane(int number){
		Lane lane = new Lane();
		lane.number = number;
		
		List<String> lResos = new ArrayList<String>();
		lResos.add("reso1");
		lResos.add("reso2");
		lane.resolutionCode = lResos;
		
		lane.valid = TBoolean.FALSE;
		lane.validDate = new Date();
		
		lane.stateCode = "F";
		
		lane.readSetCodes = null;
				
		return lane;
	}
	
	
	public static ReadSet newReadSet(String code){
		ReadSet r = new ReadSet();
		r.code = code;
		r.path = "/";
		r.sampleCode = "samplecode1";
		r.projectCode = "42";
		r.sampleContainerCode = "container1";


		r.dispatch = false;
		r.laneNumber = 1;
		
		
		r.stateCode = "F-QC";
				
		TraceInformation ti = new TraceInformation(); 
		ti.setTraceInformation("dnoisett");
		r.traceInformation = ti; 
		
		List<String> lResos = new ArrayList<String>();
		lResos.add("reso1");
		lResos.add("reso2");
		r.resolutionCode = lResos;
		
		r.typeCode = "default-readset"; 
		
		r.validBioinformatic = TBoolean.TRUE;
		
		r.validBioinformaticDate = new Date();
		
		r.validProduction = TBoolean.TRUE;
		
		r.validProductionDate = new Date();
		
		
		return r;
	}
	
	public static JsonNode getArchiveJson(String archiveId){
		 System.out.println( Json.newObject().textNode("archiveId:"+archiveId));
		 //System.out.println("parsed::"+Json.parse("[{\"archiveId\":\""+archiveId+"\"}]"));
		 return Json.parse("{\"archiveId\":\""+archiveId+"\"}");
		 
	 }
	 
	public static File newFile(){
		File file = new File();
		file.fullname = "testfile";
		file.extension = ".exe";
		file.typeCode = "42";
		file.usable = true;
		
		file.stateCode = "F-QC";
		
		file.properties.put("label", new PropertySingleValue("thelabel"));
		file.properties.put("asciiEncoding", new PropertySingleValue("xxx"));
		
		return file;
		
	}
	
	public static File newFile(String code){
		File file = new File();
		file.fullname = code;
		file.extension = ".exe";
		file.typeCode = "42";
		file.usable = true;
		
		file.stateCode = "F-QC";
		
		file.properties.put("label", new PropertySingleValue("thelabel"));
		file.properties.put("asciiEncoding", new PropertySingleValue("xxx"));
		
		return file;
		
	}
		
}
