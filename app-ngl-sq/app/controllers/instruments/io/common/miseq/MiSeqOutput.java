package controllers.instruments.io.common.miseq;



import java.util.List;


import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.common.miseq.tpl.txt.*;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;
import controllers.instruments.io.utils.TagModel;

public class MiSeqOutput extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment, ContextValidation contextValidation) {
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		System.out.println("testtttt ok");
		
		TagModel tagModel = OutputHelper.getTagModel(containers);
		String content = null;

	/*if ((Boolean)experiment.instrumentProperties.get("customPrimers").value){
			System.out.println("test ok");
			if(!"DUAL-INDEX".equals(tagModel.tagType)){
				content = OutputHelper.format(sampleSheet_1_customPrimers.render(experiment,containers).body());			
			}else{
				content = OutputHelper.format(sampleSheet_2_customPrimers.render(experiment,containers).body());	
			}
		}else{*/
		System.out.println("test ok");
		
			if(!"DUAL-INDEX".equals(tagModel.tagType)){
				content = OutputHelper.format(sampleSheet_1.render(experiment,containers).body());			
			}else{
				content = OutputHelper.format(sampleSheet_2.render(experiment,containers).body());	
			}
	//	}
		String filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+experiment.instrumentProperties.get("miseqReagentCassette").value+".csv";
		File file = new File(filename, content);

		OutputHelper.writeFile(file);
		return file;
	}
}
