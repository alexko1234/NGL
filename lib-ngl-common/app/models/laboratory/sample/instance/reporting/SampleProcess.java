package models.laboratory.sample.instance.reporting;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.laboratory.processes.instance.SampleOnInputContainer;

public class SampleProcess {
	public String code;
	public String typeCode;
	public String categoryCode;
	public State state;
	public TraceInformation traceInformation;
	public SampleOnInputContainer sampleOnInputContainer;
	
	public Map<String,PropertyValue> properties;
	public String currentExperimentTypeCode;
	public List<SampleExperiment> experiments;
	public List<SampleReadSet> readsets;
	
	public Integer progressInPercent;
}
