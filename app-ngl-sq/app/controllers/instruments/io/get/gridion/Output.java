package controllers.instruments.io.get.gridion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.utils.code.DefaultCodeImpl;
import validation.ContextValidation;
//import controllers.instruments.io.get.cbotinterne.play;
import controllers.instruments.io.get.gridion.tpl.txt.sampleSheet_gridion;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.CsvHelper;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;
import controllers.instruments.io.utils.TagModel;
//import play.Logger;

//set output file .csv
public class Output extends AbstractOutput {
	
	private static final play.Logger.ALogger logger = play.Logger.of(Output.class);
	
	@Override
	public File generateFile(Experiment experiment, ContextValidation contextValidation) {
		String codeContainerOut = null;
		String content = null;
		String filename =null;
		
		//get codeContainerOut from url
		codeContainerOut = (String)contextValidation.getObject("outputCode");
		logger.debug("Output- codeContainerOut : "+ codeContainerOut);
		
		//get container
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		logger.debug("Output- containers : "+ containers.size());
		
		content = '\ufeff' + OutputHelper.format(sampleSheet_gridion.render(experiment,containers,codeContainerOut).body());

		//set file name
		filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+(new SimpleDateFormat("yyyyMMdd")).format(new Date()) + "_" + experiment.instrument.code + "_" + containers.get(0).support.code + ".csv";
		//logger.debug(experiment.projectCodes);
		
		File file = new File(filename, content);
		OutputHelper.writeFile(file);
		return file;
	}
}
