package controllers.run;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.instance.common.PropertyValue;
import models.instance.instrument.InstrumentUsed;
import models.instance.run.Lane;
import models.instance.run.ReadSet;
import models.instance.run.Run;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;
import fr.cea.ig.MongoDBDAO;

public class RunMockHelper {
	
	public static final String RUN_CODE = "YANN_TEST1";
	public static final int LANE_CODE = 1;
	public static final String READSET_CODE = "READSET_TEST1";
	
	public static Map<String,String> fakeConfiguration(){
		Map<String,String> config = new HashMap<String,String>();
		config.put("mongodb.database", "NGL-BI");
		config.put("mongodb.credentials", "ngl-bi:NglBiPassW");
		config.put("mongodb.servers", "gsphere.genoscope.cns.fr:27017");
		return config;
		
	}
	
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
		Lane lane =  createLane(1,	READSET_CODE);		
		Lane lane2 = createLane(2,	READSET_CODE+"2");
		
		lanes.add(lane);
		lanes.add(lane2);
		
		run.lanes = lanes;
		return run;
	}
	
	
	public static JsonNode getJsonRunUpdate() {//Ok
		Run run = null;
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		Run run = MongoDBDAO.findByCode("cng.run.illuminaYann2",Run.class, RUN_CODE);
		run.dispatch = true;
		run.transfertEndDate = new Date();
		run.properties.clear();
		run.properties.put("nbCycle", new PropertyValue("2500"));		
		run.properties.put("nbClusterTotal", new PropertyValue("25"));
		run.properties.put("nbClusterIlluminaFilter", new PropertyValue("25"));
		run.properties.put("nbBase", new PropertyValue("25"));
		run.properties.put("flowcellPosition", new PropertyValue("25"));
		run.properties.put("rtaVersion", new PropertyValue("25"));
		run.properties.put("flowcellVersion", new PropertyValue("25"));
		run.properties.put("controlLane", new PropertyValue("25"));
		run.properties.put("mismatch", new PropertyValue("25"));
		run.instrumentUsed = new InstrumentUsed();
		run.instrumentUsed.categoryCode = "HISEQ2000";
		run.instrumentUsed.code = "HS7";
		       }});
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
	
	public static JsonNode getJsonLane(int number,String readsetCode) {
		 return Json.toJson(createLane(number,readsetCode));
	 }
	
	public static JsonNode getArchiveJson(String archiveId){
		 return Json.parse("{\"archiveId\":\""+archiveId+"\"}");
	 }
	 
	public static JsonNode getJsonReadSet(String code){
			return Json.toJson(getReadSet(code));
	 }
	

}
