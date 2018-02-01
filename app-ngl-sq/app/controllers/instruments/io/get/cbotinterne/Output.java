package controllers.instruments.io.get.cbotinterne;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.get.cbotinterne.tpl.txt.sampleSheet_cbot_int;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.CsvHelper;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;
import controllers.instruments.io.utils.TagModel;
import play.Logger;

//set output file .csv
public class Output extends AbstractOutput {
	
	@Override
	public File generateFile(Experiment experiment, ContextValidation contextValidation) {
		String ftype = null;
		String content = null;
		String filename =null;
		
		//get container
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		Logger.debug("Output- containers : "+ containers.size());
		
		content = '\ufeff' + OutputHelper.format(sampleSheet_cbot_int.render(experiment,containers).body());
		//set file name
		filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+(new SimpleDateFormat("yyyyMMdd")).format(new Date()) + "_" + experiment.instrument.code + "_" + CsvHelper.checkName(OutputHelper.getOutputContainerUsedCode(experiment.atomicTransfertMethods.get(0))) + ".csv";
		Logger.debug("Output filename : " + OutputHelper.getOutputContainerUsedCode(experiment.atomicTransfertMethods.get(0)));
		File file = new File(filename, content);
		OutputHelper.writeFile(file);
		return file;
	}
}
