package models.laboratory.sample.instance.reporting;

import java.util.List;
import java.util.Map;

import models.laboratory.common.instance.PropertyValue;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;

public class SampleProcess {
	public String code;
	public String typeCode;
	public String categoryCode;
	public State state;
	public TraceInformation traceInformation;
	
	public Map<String,PropertyValue> properties;
	
	public List<SampleExperiment> experiments;
	public List<SampleReadSet> readsets;
}
 