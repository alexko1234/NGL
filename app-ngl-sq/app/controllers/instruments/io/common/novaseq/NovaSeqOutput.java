package controllers.instruments.io.common.novaseq;

// 02/02/2018 NGL-1770

import java.util.List;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.common.novaseq.tpl.txt.*;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;
import controllers.instruments.io.utils.TagModel;

public abstract class NovaSeqOutput extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment, ContextValidation contextValidation) {
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		TagModel tagModel = OutputHelper.getTagModel(containers);
		String content = OutputHelper.format(sampleSheet_1.render(experiment,containers).body()); 
		
		if(!"DUAL-INDEX".equals(tagModel.tagType)){
			content = OutputHelper.format(sampleSheet_1.render(experiment,containers).body());			
		}else{
			content = OutputHelper.format(sampleSheet_2.render(experiment,containers).body());	
		}
		
		String filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+containers.get(0).support.code+".csv";
		
		System.out.println("instrument code= "+ experiment.instrument.code );
		System.out.println("instrument path= "+  OutputHelper.getInstrumentPath(experiment.instrument.code));
		
		File file = new File(filename, content);
		OutputHelper.writeFile(file);
		return file;
	}

}