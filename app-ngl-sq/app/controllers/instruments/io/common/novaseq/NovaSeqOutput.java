package controllers.instruments.io.common.novaseq;

import java.text.SimpleDateFormat;

// 02/02/2018 NGL-1770

import java.util.List;

import controllers.instruments.io.common.novaseq.tpl.txt.sampleSheet_1;
import controllers.instruments.io.common.novaseq.tpl.txt.sampleSheet_2;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;
import controllers.instruments.io.utils.TagModel;
import controllers.instruments.io.utils.TextOutput;
import models.laboratory.container.instance.Container;
import models.laboratory.container.instance.Content;
import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;

class AbstractNovaSeqSampleSheet extends TextOutput {
	
	public void header(Experiment experiment, List<Container> containers) {
		// Header
		    println("[Header]")
		   .println("IEMFileVersion,5")
		   .println("Experiment Name,", experiment.code)
		   .println("Date,", new SimpleDateFormat("MM/dd/yyyy").format(experiment.traceInformation.creationDate))
		   .println("Workflow,GenerateFASTQ")
		   .println("Application,NovaSeq FASTQ Only")
		   .println("Instrument Type,NovaSeq")
		   .println("Assay,")
		   .println("Index Adapters,")
		   .println("Description,", containers.get(0).support.code)
		   .println("Chemistry,Default")
		   .println();
	}
	
	public void reads(Experiment experiment) {
		// Reads
		    println("[Reads]")
		   .println(experiment.instrumentProperties.get("nbCyclesRead1").value.toString())
		   .println(experiment.instrumentProperties.get("nbCyclesRead2").value.toString())
		   .println();		
	}
	
	public void settings() {
		// Settings
		    println("[Settings]")
		   .println("Adapter,AGATCGGAAGAGCACACGTCTGAACTCCAGTCA")
		   .println("AdapterRead2,AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT")
		   .println();
	}
	
}

class NovaSeqSampleSheetHelper {
	
	public static void header(TextOutput out, Experiment experiment, List<Container> containers) {
		// Header
		out.println("[Header]")
		   .println("IEMFileVersion,5")
		   .println("Experiment Name,", experiment.code)
		   .println("Date,", new SimpleDateFormat("MM/dd/yyyy").format(experiment.traceInformation.creationDate))
		   .println("Workflow,GenerateFASTQ")
		   .println("Application,NovaSeq FASTQ Only")
		   .println("Instrument Type,NovaSeq")
		   .println("Assay,")
		   .println("Index Adapters,")
		   .println("Description,", containers.get(0).support.code)
		   .println("Chemistry,Default")
		   .println();
	}
	
	public static void reads(TextOutput out, Experiment experiment) {
		// Reads
		out.println("[Reads]")
		   .println(experiment.instrumentProperties.get("nbCyclesRead1").value.toString())
		   .println(experiment.instrumentProperties.get("nbCyclesRead2").value.toString())
		   .println();		
	}
	
	public static void settings(TextOutput out) {
		// Settings
		out.println("[Settings]")
		   .println("Adapter,AGATCGGAAGAGCACACGTCTGAACTCCAGTCA")
		   .println("AdapterRead2,AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT")
		   .println();
	}
	
}
// sampleSheet_1.scala.txt
class SampleSheet1 extends TextOutput {
	
	public SampleSheet1 render(Experiment experiment, List<Container> containers) {
//		// Header
//		println("[Header]");
//		println("IEMFileVersion,5");
//		println("Experiment Name,", experiment.code);
//		println("Date,", new SimpleDateFormat("MM/dd/yyyy").format(experiment.traceInformation.creationDate));
//		println("Workflow,GenerateFASTQ");
//		println("Application,NovaSeq FASTQ Only");
//		println("Instrument Type,NovaSeq");
//		println("Assay,");
//		println("Index Adapters,");
//		println("Description,", containers.get(0).support.code);
//		println("Chemistry,Default");
//		println();
//		// Reads
//		println("[Reads]");
//		println(experiment.instrumentProperties.get("nbCyclesRead1").value.toString());
//		println(experiment.instrumentProperties.get("nbCyclesRead2").value.toString());
//		println();
//		// Settings
//		println("[Settings]");
//		println("Adapter,AGATCGGAAGAGCACACGTCTGAACTCCAGTCA");
//		println("AdapterRead2,AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT");
//		println();
		NovaSeqSampleSheetHelper.header(this, experiment, containers);
		NovaSeqSampleSheetHelper.reads(this,experiment);
		NovaSeqSampleSheetHelper.settings(this);
		// Data
		println("[Data]");
		println("Sample_ID,Sample_Name,Sample_Plate,Sample_Well,I7_Index_ID,index,Sample_Project,Description");
		containers.sort(((a,b) -> a.code.compareTo(b.code)));
		for (Container c : containers) {
			for (Content co :c.contents) {
				String sample_id     = c.support.line + "_" + co.sampleCode+"_"+OutputHelper.getContentProperty(co,"libProcessTypeCode")+"_"+OutputHelper.getContentProperty(co,"tag");				
				String i7_index_name = OutputHelper.getContentProperty(co,"tag");
				String i7_index_seq  = OutputHelper.getSequence(OutputHelper.getIndex("index-illumina-sequencing",OutputHelper.getContentProperty(co,"tag")));
				String description   = OutputHelper.getContentProperty(co,"tag")+"_"+co.percentage;
				// Only one of the two is needed
				//println(sample_id, ",,,,", i7_index_name, ",", i7_index_seq, ",", co.projectCode, ",", description);
				printfln("%s,,,,%s,%s,%s,%s", sample_id, i7_index_name, i7_index_seq, co.projectCode, description);
			}
		} 
		return this;
	}
	
}

//sampleSheet_2.scala.txt
class SampleSheet2 extends TextOutput {
		
	public SampleSheet2 render(Experiment experiment, List<Container> containers) {
//		// Header
//		println("[Header]");
//		println("IEMFileVersion,5");
//		println("Experiment Name,", experiment.code);
//		println("Date,", new SimpleDateFormat("MM/dd/yyyy").format(experiment.traceInformation.creationDate));
//		println("Workflow,GenerateFASTQ");
//		println("Application,NovaSeq FASTQ Only");
//		println("Instrument Type,NovaSeq");
//		println("Assay,");
//		println("Index Adapters,");
//		println("Description,", containers.get(0).support.code);
//		println("Chemistry,Amplicon");
//		println();
//		// Reads
//		println("[Reads]");
//		println(experiment.instrumentProperties.get("nbCyclesRead1").value.toString());
//		println(experiment.instrumentProperties.get("nbCyclesRead2").value.toString());
//		println();
//		// Settings
//		println("[Settings]");
//		println("Adapter,AGATCGGAAGAGCACACGTCTGAACTCCAGTCA");
//		println("AdapterRead2,AGATCGGAAGAGCGTCGTGTAGGGAAAGAGTGT");
//		println();
		NovaSeqSampleSheetHelper.header(this, experiment, containers);
		NovaSeqSampleSheetHelper.reads(this,experiment);
		NovaSeqSampleSheetHelper.settings(this);
		// Data
		println("[Data]");
		println("Sample_ID,Sample_Name,Sample_Plate,Sample_Well,I7_Index_ID,index,I5_Index_ID,index2,Sample_Project,Description");
		containers.sort((a,b) -> a.code.compareTo(b.code));
		for(Container c : containers) { 
			for(Content co : c.contents) {
				String sample_id=c.support.line +"_"+ co.sampleCode+"_"+OutputHelper.getContentProperty(co,"libProcessTypeCode")+"_"+OutputHelper.getContentProperty(co,"tag");
				
//			@*** 13/02/2018ATTENTION tous les noms d'index dual ne suivent pas la nomenclature Illumina <index1>-<index2>=> ne pas splitter, ne pas mettre les noms dans la FDR
//			@{i7_index_name=OutputHelper.getContentProperty(co,"tag").split("-")(0)}
//			@{i5_index_name=OutputHelper.getContentProperty(co,"tag").split("-")(1)}
//			***@

				String i7_index_seq=OutputHelper.getSequence(OutputHelper.getIndex("index-illumina-sequencing",OutputHelper.getContentProperty(co,"tag"))).split("-")[0];
				String i5_index_seq=OutputHelper.getSequence(OutputHelper.getIndex("index-illumina-sequencing",OutputHelper.getContentProperty(co,"tag"))).split("-")[1];
				String description=OutputHelper.getContentProperty(co,"tag")+"_"+co.percentage;
				printfln("%,,,,,%,,%,%s,%s", sample_id, i7_index_seq, i5_index_seq, co.projectCode, description);
			}
		}
		return this;
	}
	
}

public abstract class NovaSeqOutput extends AbstractOutput {

	private static final play.Logger.ALogger logger = play.Logger.of(NovaSeqOutput.class);
	
	@Override
	public File generateFile(Experiment experiment, ContextValidation contextValidation) {
		List<Container> containers = OutputHelper.getInputContainersFromExperiment(experiment);
		TagModel tagModel = OutputHelper.getTagModel(containers);
		
		// TODO: remove default content creation as it is a duplicate of the tag cases. 
//		String content = OutputHelper.format(sampleSheet_1.render(experiment,containers).body()); 
		String content = null;
		
		if (!"DUAL-INDEX".equals(tagModel.tagType)) {
			// content = OutputHelper.format(sampleSheet_1.render(experiment,containers).body());	
			// content = new SampleSheet1().render(experiment, containers).getContent();
			content = impl(() -> OutputHelper.format(sampleSheet_1.render(experiment,containers).body()), 
					       () -> new SampleSheet1().render(experiment, containers).getContent());				
		} else {
//			content = OutputHelper.format(sampleSheet_2.render(experiment,containers).body());	
			content = impl(() -> OutputHelper.format(sampleSheet_2.render(experiment,containers).body()),
					       () -> new SampleSheet2().render(experiment, containers).getContent());	
		}
		
		String filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+containers.get(0).support.code+".csv";
		
//		System.out.println("instrument code= "+ experiment.instrument.code );
//		System.out.println("instrument path= "+  OutputHelper.getInstrumentPath(experiment.instrument.code));
		logger.debug("instrument code = {}", experiment.instrument.code );
		logger.debug("instrument path = {}", OutputHelper.getInstrumentPath(experiment.instrument.code));
		
		File file = new File(filename, content);
		OutputHelper.writeFile(file);
		return file;
	}

}
