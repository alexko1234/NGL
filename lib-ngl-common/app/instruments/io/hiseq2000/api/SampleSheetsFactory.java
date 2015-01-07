package instruments.io.hiseq2000.api;

import fr.cea.ig.MongoDBDAO;
import instruments.io.utils.AbstractSampleSheetsfactory;
import models.laboratory.experiment.instance.Experiment;
import models.utils.InstanceConstants;


public class SampleSheetsFactory extends AbstractSampleSheetsfactory{

	public SampleSheetsFactory(Experiment varExperiment) {
		//super(varExperiment);
		super(varExperiment);
	}

	@Override
	public String generate() {
		instruments.io.common.hiseq_miseq.api.SampleSheetsFactory sampleSheet = new instruments.io.common.hiseq_miseq.api.SampleSheetsFactory(this.experiment);
	
		return sampleSheet.generate();
	}
}
