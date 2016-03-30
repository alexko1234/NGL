package controllers.instruments.io.tecanevo100;

import java.io.File;

import play.Logger;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.tecanevo100.tpl.txt.sampleSheet_output_tube;
import controllers.instruments.io.tecanevo100.tpl.txt.sampleSheet_output_96_well_plate;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment,
			ContextValidation contextValidation) {
		String content="";
		if(experiment.instrument.outContainerSupportCategoryCode.equals("tube")){
			content = OutputHelper.format(sampleSheet_output_tube.render(experiment).body());
		} else if(experiment.instrument.outContainerSupportCategoryCode.equals("96-well-plate")){
			content = OutputHelper.format(sampleSheet_output_96_well_plate.render(experiment).body());
		}
		File file = new File(OutputHelper.getInstrumentPath(experiment.instrument.code)+experiment.code+"_Tecan.csv");
		OutputHelper.writeFile(file, content);
		return file;
	}

}
