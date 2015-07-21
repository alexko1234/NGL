package instruments.io.tecanevo100.api;

import java.io.File;
import java.util.Date;
import java.util.List;

import instruments.io.tecanevo100.tpl.txt.sampleSheet_1;
import instruments.io.utils.AbstractSampleSheetsfactory;
import instruments.io.utils.SampleSheetsFactoryHelper;
import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;

public class SampleSheetsFactory extends AbstractSampleSheetsfactory {

	public SampleSheetsFactory(Experiment varExperiment) {
		super(varExperiment);
	}

	@Override
	public File generate() {
		String content = format(sampleSheet_1.render(experiment).body());
		File file = new File(SampleSheetsFactoryHelper.getSampleSheetFilePath(experiment.instrument.code)+experiment.code+"_Tecan.csv");
		//play.api.libs.Files.writeFile(file, content);
		AbstractSampleSheetsfactory.writeFile(file, content);
		
		return file;
	}

}
