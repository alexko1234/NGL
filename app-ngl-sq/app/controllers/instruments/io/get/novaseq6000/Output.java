package controllers.instruments.io.get.novaseq6000;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.get.novaseq6000.tpl.txt.sampleSheet_NovaSeq_IEM;
import controllers.instruments.io.get.novaseq6000.tpl.txt.sampleSheet_NovaSeq_10x;
import controllers.instruments.io.get.novaseq6000.tpl.txt.sampleSheet_NovaSeq_jFlow;
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
//		List<Content> contents = new ArrayList<>();
		
		//get var type from url ?
		ftype = (String)contextValidation.getObject("fType");		
		Logger.debug("Output- ftype : "+ ftype);
		
		//get container
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		Logger.debug("Output- containers : "+ containers.size());

		//get tag type
		TagModel tagModel = CsvHelper.getTagModel(containers);
		Logger.debug("Output- tagModel : "+ tagModel.tagType);
		
		//call output template by type
		if ("10x".equals(ftype)){
				content = OutputHelper.format(sampleSheet_NovaSeq_10x.render(experiment,containers,tagModel).body());
		} 
		
		if ("IEM".equals(ftype)){
			List<String> contentDoubleName = CsvHelper.contentDubleName(containers);
			Logger.debug("nom echantillon repeté - " + contentDoubleName.size());
				content = OutputHelper.format(sampleSheet_NovaSeq_IEM.render(experiment,containers,contentDoubleName,tagModel).body());	
		}
		
		if ("jFlow".equals(ftype)){
			content = OutputHelper.format(sampleSheet_NovaSeq_jFlow.render(experiment,containers,tagModel).body());
		}
//		Logger.debug("Output- content : "+ content);
		String filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+ new SimpleDateFormat("yyyyMMdd").format((Date) experiment.experimentProperties.get("runStartDate").value) + "_" + experiment.instrument.code + "_" + ftype + "_" + containers.get(0).support.code + ".csv";
		//Logger.debug("filename dans Output : "+ OutputHelper.getInstrumentPath(experiment.instrument.code)+", "+ (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + "_" + experiment.instrument.code + "_" + ftype + "_" + containers.get(0).support.code+".csv");
		//String filename = "/tmp/" + (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + "_" + experiment.instrument.code + "_" + ftype + "_" + containers.get(0).support.code+".csv";
		File file = new File(filename, content);
		OutputHelper.writeFile(file);
		return file;
	}
}
