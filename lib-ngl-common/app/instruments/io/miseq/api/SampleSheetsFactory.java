package instruments.io.miseq.api;

import instruments.io.miseq.tpl.txt.sampleSheet_1;
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
		String content = format(sampleSheet_1.render(this.experiment, containers).body());
		//play.api.libs.Files.writeFile(new File(containers.get(0).support.code+".csv"), content);
		File file = new File(experiment.instrumentProperties.get("miseqReagentCassette").value+".csv");
		AbstractSampleSheetsfactory.writeFile(file, content);
		
		return file;
	}

}
