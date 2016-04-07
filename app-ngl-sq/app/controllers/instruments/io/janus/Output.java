package controllers.instruments.io.janus;



import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.janus.tpl.txt.sampleSheet_1;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment,
			ContextValidation contextValidation) throws Exception {
		String content = OutputHelper.format(sampleSheet_1.render(experiment).body());
		File file = new File(experiment.code+".csv", content);
		return file;
	}

}
