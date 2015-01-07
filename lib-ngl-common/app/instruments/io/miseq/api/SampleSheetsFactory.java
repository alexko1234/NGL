package instruments.io.miseq.api;

import java.io.File;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import instruments.io.utils.AbstractSampleSheetsfactory;

import instruments.io.miseq.tpl.txt.*;

public class SampleSheetsFactory extends AbstractSampleSheetsfactory{

	public SampleSheetsFactory(Experiment varExperiment) {
		super(varExperiment);
	}

	@Override
	public String generate() {
		instruments.io.common.hiseq_miseq.api.SampleSheetsFactory sampleSheet = new instruments.io.common.hiseq_miseq.api.SampleSheetsFactory(this.experiment);
		
		return sampleSheet.generate();
	}

}
