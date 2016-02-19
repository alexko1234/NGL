package controllers.instruments.io.hiseq;



import java.io.File;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.hiseq.tpl.txt.sampleSheet_1;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.OutputHelper;
import controllers.instruments.io.utils.TagModel;

public abstract class HiSeqOutput extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment, ContextValidation contextValidation) {
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		TagModel tagModel = OutputHelper.getTagModel(containers);
		String content = OutputHelper.format(sampleSheet_1.render(experiment,containers,tagModel).body());
		File file = new File(OutputHelper.getInstrumentPath(experiment.instrument.code)+containers.get(0).support.code+".csv");
		OutputHelper.writeFile(file, content);
		return file;
	}

}
