package instruments.io.common.hiseq.api;

import instruments.io.common.hiseq.tpl.txt.sampleSheet_1;
import instruments.io.utils.AbstractSampleSheetsfactory;

import java.io.File;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;

public class SampleSheetsFactory extends AbstractSampleSheetsfactory{

	public SampleSheetsFactory(Experiment varExperiment) {
		super(varExperiment);
	}

	@Override
	public File generate() {
		List<Container> containers = getContainersFromExperiment();
		
		String content = format(sampleSheet_1.render(containers).body());
		File file = new File(containers.get(0).support.code+".csv");
		//play.api.libs.Files.writeFile(file, content);
		//AbstractSampleSheetsfactory.writeFile(file, content);
		
		return file;
	}
}