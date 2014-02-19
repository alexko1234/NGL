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
		// TODO Auto-generated constructor stub
	}

	@Override
	public String generate() {
		
		List<Container> containers = getContainersFromExperiment();
		String content = format(sampleSheet_1.render(this.experiment, containers).body());
		play.api.libs.Files.writeFile(new File(containers.get(0).support.supportCode+".csv"), content);
		
		
		return null;
	}

}
