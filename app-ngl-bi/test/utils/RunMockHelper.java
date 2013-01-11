package utils;

import models.instance.common.PropertyValue;
import models.instance.instrument.InstrumentUsed;
import models.instance.run.Lane;
import models.instance.run.ReadSet;
import models.instance.run.Run;

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
	
	public static Run newRun(String code){
		Run run = new Run();
		run.code = code;
		run.typeCode = "RHS2000";
		run.containerSupportCode = "FC00000";				
		run.properties.put("nbCycle", new PropertyValue("25"));		
		run.properties.put("nbClusterTotal", new PropertyValue("25"));
		run.properties.put("nbClusterIlluminaFilter", new PropertyValue("25"));
		run.properties.put("nbCycle", new PropertyValue("25"));
		run.properties.put("nbBase", new PropertyValue("25"));
		run.properties.put("flowcellPosition", new PropertyValue("25"));
		run.properties.put("rtaVersion", new PropertyValue("25"));
		run.properties.put("flowcellVersion", new PropertyValue("25"));
		run.properties.put("controlLane", new PropertyValue("25"));
		run.properties.put("mismatch", new PropertyValue("25"));
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.categoryCode = "HISEQ2000";
		run.instrumentUsed.code = "HS7";
		return run;
	}
	
	public static Lane newLane(int number){
		Lane lane = new Lane();
		lane.number = number;
		lane.properties.put("nbCycleRead1",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertyValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertyValue("1056"));
		lane.properties.put("nbCluster",new PropertyValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("phasing",new PropertyValue("phasing"));
		lane.properties.put("prephasing",new PropertyValue("prephasing"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertyValue("99.99"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertyValue("99.99"));
				
		return lane;
	}
	
	
	public static ReadSet newReadSet(String code){
		ReadSet r = new ReadSet();
		r.code = code;
		r.path = "/";
		r.sampleCode = "samplecode1";
		r.projectCode = "42";
		r.sampleContainerCode = "container1";
		r.properties.put("score",  new PropertyValue("150"));
		r.properties.put("insertLength",  new PropertyValue("1500"));
		r.properties.put("q30",  new PropertyValue("1.23"));
		r.properties.put("nbUsableBase",  new PropertyValue("1500000"));
		r.properties.put("nbUsableCluster",  new PropertyValue("150000"));
		r.properties.put("nbRead",  new PropertyValue("1500000"));
		return r;
	}
	
	public static JsonNode getArchiveJson(String archiveId){
		 System.out.println( Json.newObject().textNode("arichiveId:"+archiveId));
		 //System.out.println("parsed::"+Json.parse("[{\"archiveId\":\""+archiveId+"\"}]"));
		 return Json.parse("{\"archiveId\":\""+archiveId+"\"}");
		 
	 }
	 
		
}
