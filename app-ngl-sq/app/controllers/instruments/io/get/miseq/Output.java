package controllers.instruments.io.get.miseq;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import models.utils.code.DefaultCodeImpl;
import validation.ContextValidation;
//import controllers.instruments.io.get.cbotinterne.play;
import controllers.instruments.io.get.miseq.tpl.txt.sampleSheet_miseq_IEM;
import controllers.instruments.io.get.miseq.tpl.txt.sampleSheet_miseq_10x;
import controllers.instruments.io.get.miseq.tpl.txt.sampleSheet_miseq_jFlow;
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
		String ftype = null;
		String content = null;
		String filename = "";
		//get var type from url ?
		ftype = (String)contextValidation.getObject("fType");		
		logger.debug("Output- ftype : "+ ftype);
		
		//get container
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		
		logger.debug("Output- containers : "+ containers.size());
		//get tag type
//		TagModel tagModel = OutputHelper.getTagModel(containers);
		TagModel tagModel = CsvHelper.getTagModel(containers);
		logger.debug("Output- tagModel : "+ tagModel.tagType);
		
		//call output template by type
		if ("10x".equals(ftype)){
				content = OutputHelper.format(sampleSheet_miseq_10x.render(experiment,containers,tagModel).body());
		} 
		
		if ("IEM".equals(ftype)){
			List<String> contentDoubleName = CsvHelper.contentDubleName(containers);
			logger.debug("nom echantillon repeté - " + contentDoubleName.size());
				content = OutputHelper.format(sampleSheet_miseq_IEM.render(experiment,containers,contentDoubleName,tagModel).body());	
		}

		if ("IEMnew".equals(ftype)){
			List<String> contentDoubleName = CsvHelper.contentDubleName(containers);
			logger.debug("nom echantillon repeté - " + contentDoubleName.size());
				content = OutputHelper.format(sampleSheet_miseq_IEM.render(experiment,containers,contentDoubleName,tagModel).body());	
		}
		if ("jFlow".equals(ftype)){
			content = OutputHelper.format(sampleSheet_miseq_jFlow.render(experiment,containers,tagModel).body());
		}	
//		Logger.debug("Output- content : "+ content);
//		content = OutputHelper.format(sampleSheet_HS3000.render(experiment,containers,tagModel).body());
		//set destination
		//filename = "C:/Users/Public/Downloads/" + filename;
		//String path=new File("").getAbsolutePath();
		//set file name

		if ("IEMnew".equals(ftype)){
			filename = "/tmp/" + experiment.instrumentProperties.get("miseqReagentCassette").value + ".csv";
		}else{
			filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+ new SimpleDateFormat("yyyyMMdd").format((Date) experiment.experimentProperties.get("runStartDate").value) + "_" + experiment.instrument.code + "_" + ftype + "_" + containers.get(0).support.code + ".csv";			
		}
			//Logger.debug("filename dans Output : "+ OutputHelper.getInstrumentPath(experiment.instrument.code)+", "+ (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + "_" + experiment.instrument.code + "_" + ftype + "_" + containers.get(0).support.code+".csv");
			//String filename = "/tmp/" + (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + "_" + experiment.instrument.code + "_" + ftype + "_" + containers.get(0).support.code+".csv";
			File file = new File(filename, content);
			OutputHelper.writeFile(file);
		return file;
	}
}
