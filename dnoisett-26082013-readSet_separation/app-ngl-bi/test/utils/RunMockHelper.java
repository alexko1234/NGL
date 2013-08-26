package utils;

import models.laboratory.common.instance.property.PropertySingleValue;
import models.laboratory.run.instance.InstrumentUsed;
import models.laboratory.run.instance.File;
import models.laboratory.run.instance.Lane;
import models.laboratory.run.instance.ReadSet;
import models.laboratory.run.instance.Run;

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
	
	
	public static Run newRun(String code){
		Run run = new Run();
		run.code = code;
		run.typeCode = "RHS2000";
		run.containerSupportCode = "FC00000";				
		run.properties.put("nbCycle", new PropertySingleValue("25"));		
		run.properties.put("nbClusterTotal", new PropertySingleValue("25"));
		run.properties.put("nbClusterIlluminaFilter", new PropertySingleValue("25"));
		run.properties.put("nbCycle", new PropertySingleValue("25"));
		run.properties.put("nbBase", new PropertySingleValue("25"));
		run.properties.put("flowcellPosition", new PropertySingleValue("25"));
		run.properties.put("rtaVersion", new PropertySingleValue("25"));
		run.properties.put("flowcellVersion", new PropertySingleValue("25"));
		run.properties.put("controlLane", new PropertySingleValue("25"));
		run.properties.put("mismatch", new PropertySingleValue("25"));
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.categoryCode = "HISEQ2000";
		run.instrumentUsed.code = "HS7";
		return run;
	}
	
	public static Lane newLane(int number){
		Lane lane = new Lane();
		lane.number = number;
		lane.properties.put("nbCycleRead1",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertySingleValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertySingleValue("1056"));
		lane.properties.put("nbCluster",new PropertySingleValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("phasing",new PropertySingleValue("phasing"));
		lane.properties.put("prephasing",new PropertySingleValue("prephasing"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertySingleValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertySingleValue("99.99"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertySingleValue("99.99"));
				
		return lane;
	}
	
	
	public static ReadSet newReadSet(String code){
		ReadSet r = new ReadSet();
		r.code = code;
		r.path = "/";
		r.sampleCode = "samplecode1";
		r.projectCode = "42";
		r.sampleContainerCode = "container1";
		r.properties.put("score",  new PropertySingleValue("150"));
		//r.properties.put("insertLength",  new PropertyValueOne("1500"));
		r.properties.put("q30",  new PropertySingleValue("1.23"));
		r.properties.put("nbUsableBase",  new PropertySingleValue("1500000"));
		r.properties.put("nbUsableCluster",  new PropertySingleValue("150000"));
		r.properties.put("nbRead",  new PropertySingleValue("1500000"));
		r.properties.put("nbBaseInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		r.properties.put("nbClusterInternalAndIlluminaFilter",new PropertySingleValue("1056"));
		r.properties.put("fraction",new PropertySingleValue("0.2"));
		
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
		
		return file;
		
	}
	
	public static File newFile(String code){
		File file = new File();
		file.fullname = code;
		file.extension = ".exe";
		file.typeCode = "42";
		file.usable = true;
		
		file.properties.put("label", new PropertySingleValue("thelabel"));
		file.properties.put("asciiEncoding", new PropertySingleValue("xxx"));
		
		return file;
		
	}
		
}
