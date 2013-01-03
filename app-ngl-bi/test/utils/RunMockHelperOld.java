package utils;

import java.util.ArrayList;
import java.util.List;

import models.instance.common.PropertyValue;
import models.instance.instrument.InstrumentUsed;
import models.instance.run.Lane;
import models.instance.run.ReadSet;
import models.instance.run.Run;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;
import fr.cea.ig.MongoDBDAO;

public class RunMockHelperOld {
	
	public static JsonNode getJsonRun(String code) {
		return Json.toJson(getRun(code));
	}
	
	
	public static Run getRun(String code){
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
				
		List<Lane> lanes = new ArrayList<Lane>();
		Lane lane =  createLane(1,"tesssset3");		
		Lane lane2 = createLane(2,"eef3");
		
		lanes.add(lane);
		lanes.add(lane2);
		
		run.lanes = lanes;
		return run;
	}
	
	
	public static JsonNode getJsonRunUpdate() {
		Run run = MongoDBDAO.findByCode("cng.run.illumina2Yann",Run.class, "YANN_TEST79");
		run.properties.clear();
		run.properties.put("nbCycle", new PropertyValue("2500"));		
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
		
		return Json.toJson(run);
	}
	
	public static Lane createLane(int number, String rsCode){
		List<ReadSet> readsets = new ArrayList<ReadSet>();
		readsets.add(getReadSet(rsCode));
		Lane lane = new Lane();
		lane.number = number;
		lane.readsets = readsets;
		lane.properties.put("nbCycleRead1",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex1",new PropertyValue("1056"));
		lane.properties.put("nbCycleRead2",new PropertyValue("1056"));
		lane.properties.put("nbCycleReadIndex2",new PropertyValue("1056"));
		lane.properties.put("nbCluster",new PropertyValue("1056"));
		lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("phasing",new PropertyValue("POP"));
		lane.properties.put("prephasing",new PropertyValue("MOP"));
		lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("nbClusterIlluminaFilter",new PropertyValue("1056"));
		lane.properties.put("percentClusterIlluminaFilter",new PropertyValue("99.52"));		
		lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertyValue("12.32"));
		
		
		return lane;
	}
	
	public static ReadSet getReadSet(String code){
		ReadSet r = new ReadSet();
		r.code = code;
		r.path = "/";
		r.sampleCode = "samplecode1";
		r.projectCode = "42";
		r.sampleContainerCode = "container1";
		r.properties.put("score",  new PropertyValue(1,"Integer"));
		r.properties.put("insertLength",  new PropertyValue(1,"Integer"));
		r.properties.put("q30",  new PropertyValue(1,"Integer"));
		r.properties.put("nbUsableBase",  new PropertyValue(1,"Integer"));
		r.properties.put("nbUsableCluster",  new PropertyValue(1,"Integer"));
		r.properties.put("nbRead",  new PropertyValue(1,"Integer"));
		return r;
	}
	
	public static JsonNode getJsonLane() {
		 	List<ReadSet> readsets = new ArrayList<ReadSet>();
			ReadSet r = new ReadSet();
			r.code = "THECODE666";
			r.path = "/";
			r.sampleCode = "samplecode1";
			r.projectCode = "42";
			r.sampleContainerCode = "container1";
			r.properties.put("score",  new PropertyValue(1,"Integer"));
			r.properties.put("insertLength",  new PropertyValue(1,"Integer"));
			r.properties.put("q30",  new PropertyValue(1,"Integer"));
			r.properties.put("nbUsableBase",  new PropertyValue(1,"Integer"));
			r.properties.put("nbUsableCluster",  new PropertyValue(1,"Integer"));
			r.properties.put("nbRead",  new PropertyValue(1,"Integer"));
			readsets.add(r);
		
			Lane lane = new Lane();
			lane.number = 1;
			lane.readsets = readsets;
		 	
			lane.properties.put("nbCycleRead1",new PropertyValue("1056"));
			lane.properties.put("nbCycleReadIndex1",new PropertyValue("1056"));
			lane.properties.put("nbCycleRead2",new PropertyValue("1056"));
			lane.properties.put("nbCycleReadIndex2",new PropertyValue("1056"));
			lane.properties.put("nbCluster",new PropertyValue("1056"));
			lane.properties.put("nbBaseInternalAndIlluminaFilter",new PropertyValue("1056"));
			lane.properties.put("phasing",new PropertyValue("POP"));
			lane.properties.put("prephasing",new PropertyValue("MOP"));
			lane.properties.put("nbClusterInternalAndIlluminaFilter",new PropertyValue("1056"));
			lane.properties.put("nbClusterIlluminaFilter",new PropertyValue("1056"));
			lane.properties.put("percentClusterIlluminaFilter",new PropertyValue("99.52"));		
			lane.properties.put("percentClusterInternalAndIlluminaFilter",new PropertyValue("12.32"));
			
		
			return Json.toJson(lane);
	 }
	
	
	public static JsonNode getArchiveJson(String archiveId){
		 System.out.println( Json.newObject().textNode("arichiveId:"+archiveId));
		 JsonNode newItem =  Json.newObject();
		 //System.out.println("parsed::"+Json.parse("[{\"archiveId\":\""+archiveId+"\"}]"));
		 return Json.parse("{\"archiveId\":\""+archiveId+"\"}");
		 
	 }
	 
	public static JsonNode getJsonReadSet(){
		 	ReadSet r = new ReadSet();
			r.code = "eef398";
			r.path = "/";
			r.sampleCode = "samplecode18888";
			r.projectCode = "testupdatereadset22";
			r.sampleContainerCode = "container1";
			r.properties.put("score",  new PropertyValue(1,"Integer"));
			r.properties.put("insertLength",  new PropertyValue(1,"Integer"));
			r.properties.put("q30",  new PropertyValue(1,"Integer"));
			r.properties.put("nbUsableBase",  new PropertyValue(1,"Integer"));
			r.properties.put("nbUsableCluster",  new PropertyValue(1,"Integer"));
			r.properties.put("nbRead",  new PropertyValue(1,"Integer"));
			
			return Json.toJson(r);
	 }
	

}
