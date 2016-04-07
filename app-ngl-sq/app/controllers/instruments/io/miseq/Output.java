package controllers.instruments.io.miseq;



import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.miseq.tpl.txt.sampleSheet_1;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment, ContextValidation contextValidation) {
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		String content = OutputHelper.format(sampleSheet_1.render(experiment,containers).body());
		String filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+experiment.instrumentProperties.get("miseqReagentCassette").value+".csv";
		File file = new File(filename, content);
		
		OutputHelper.writeFile(file);
		return file;
	}

}
