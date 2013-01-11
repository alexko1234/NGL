package models.instances.validation;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.instance.common.PropertyValue;
import models.instance.common.TraceInformation;
import models.instance.instrument.InstrumentUsed;
import models.instance.run.File;
import models.instance.run.Lane;
import models.instance.run.ReadSet;
import models.instance.run.Run;
import models.instance.validation.BusinessValidationHelper;

import org.junit.Test;

import play.data.validation.ValidationError;
import utils.AbstractTests;
public class RunValidationTest extends AbstractTests{
	
	
	 @Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Test
	 public void testRunValidationOk() {
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
		 Run run = getRun();
		 BusinessValidationHelper.validateRun(errors, run, "test.run");
		 assertThat(errors).hasSize(0);
		       }});
	 }
	 
	 @Test
	 public void testLaneValidationOk(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
		 Lane lane = getLane();
		 BusinessValidationHelper.validateLane(errors, new Run(),lane, "test.run", null);
		 assertThat(errors).hasSize(0);
		       }});
	 }
	 
	 @Test
	 public void testReadSetValidationOk(){
		 running(fakeApplication(fakeConfiguration()), new Runnable() {
		       public void run() {
		 Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
		 ReadSet readset = getReadSet();
		 BusinessValidationHelper.validateReadSet(errors, new Run(),-1,readset, "test.run", null);
		 assertThat(errors).hasSize(0);
		       }});
	 }
	 
	 
	 
	@Test
	 public void testFileValidationOk(){
		 Map<String, List<ValidationError>> errors = new HashMap<String, List<ValidationError>>();
		 File file = getFile();
		 BusinessValidationHelper.validateFile(errors, file, null);
		 assertThat(errors).hasSize(0);
	 }
	 
	private Lane getLane(){
		Lane lane = new Lane();
		lane.number = 0;
		
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
	
	private ReadSet getReadSet() {
		ReadSet readSet = new ReadSet();
		readSet.code = "CORE_R1";
		readSet.path = "/path/test";
		readSet.projectCode = "PCODE"; 
		readSet.sampleCode = "SPCODE";
		readSet.sampleContainerCode = "SPCONTCODE";
		readSet.properties.put("insertLength", new PropertyValue("1056"));
		readSet.properties.put("nbUsableBase", new PropertyValue("4565456465"));
		readSet.properties.put("nbUsableCluster", new PropertyValue("132132132132"));
		readSet.properties.put("q30",new PropertyValue("10.23"));
		readSet.properties.put("score", new PropertyValue("0.636"));
		readSet.properties.put("nbRead", new PropertyValue("33"));

		return readSet;
	}
	
	 private File getFile() {
		File file = new File();
		file.extension = "fst";
		file.fullname = "fullname.fst";
		file.typeCode = "RAW";
		file.usable = Boolean.FALSE;
		
		file.properties.put("asciiEncoding", new PropertyValue("33"));
		file.properties.put("label", new PropertyValue("READ1"));		
		return file;
	}
	 
	 
	private Run getRun() {
			Run run = new Run();
			run.code = "TEST";
			run.typeCode = "RHS2000";
			run.containerSupportCode = "FC00000";
			
			run.traceInformation = new TraceInformation();
			run.traceInformation.setTraceInformation("test");
			
			run.properties.put("nbCycle", new PropertyValue("5"));		
			run.properties.put("nbClusterTotal", new PropertyValue("25"));
			run.properties.put("nbClusterIlluminaFilter", new PropertyValue("25"));
			run.properties.put("nbBase", new PropertyValue("25"));
			run.properties.put("flowcellPosition", new PropertyValue("25"));
			run.properties.put("rtaVersion", new PropertyValue("25"));
			run.properties.put("flowcellVersion", new PropertyValue("25"));
			run.properties.put("controlLane", new PropertyValue("25"));
			run.properties.put("mismatch", new PropertyValue("25"));
			
			run.instrumentUsed = new InstrumentUsed();
			run.instrumentUsed.code = "test";
			run.instrumentUsed.categoryCode = "HS2000";
			
			return run;
		}

}
