package controllers.instruments.io.tecanevo100;

import java.io.File;

import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.tecanevo100.tpl.txt.sampleSheet_1;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment,
			ContextValidation contextValidation) {
		String content = OutputHelper.format(sampleSheet_1.render(experiment).body());
		File file = new File(OutputHelper.getInstrumentPath(experiment.instrument.code)+experiment.code+"_Tecan.csv");
		OutputHelper.writeFile(file, content);
		return file;
	}

}
