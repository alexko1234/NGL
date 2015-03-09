package instruments.io.common.hiseq_miseq.api;

import instruments.io.common.hiseq_miseq.tpl.txt.sampleSheet_1;
import instruments.io.utils.AbstractSampleSheetsfactory;
import instruments.io.utils.SampleSheetsFactoryHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

import scala.io.Codec;

import models.laboratory.container.instance.Container;
import models.laboratory.experiment.instance.Experiment;

public class SampleSheetsFactory extends AbstractSampleSheetsfactory{

	public SampleSheetsFactory(Experiment varExperiment) {
		super(varExperiment);
	}

	@Override
	public File generate() {
		List<Container> containers = getContainersFromExperiment();
		
		String content = format(sampleSheet_1.render(experiment, containers).body());
		File file = new File(SampleSheetsFactoryHelper.getSampleSheetFilePath(experiment.instrument.code)+containers.get(0).support.code+".csv");
		//play.api.libs.Files.writeFile(file, content);
		//AbstractSampleSheetsfactory.writeFile(file, content);
		
		return file;
	}
}