package instruments.io.hiseq2000.api;

import instruments.io.utils.AbstractSampleSheetsfactory;
import models.laboratory.experiment.instance.Experiment;


public class SampleSheetsFactory extends AbstractSampleSheetsfactory{

	public SampleSheetsFactory(Experiment varExperiment) {
		//super(varExperiment);
		super(varExperiment);
	}

	@Override
	public String generate() {
		instruments.io.common.hiseq.api.SampleSheetsFactory sampleSheet = new instruments.io.common.hiseq.api.SampleSheetsFactory(this.experiment);
	
		return sampleSheet.generate();
	}
}
