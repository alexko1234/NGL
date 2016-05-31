package controllers.instruments.io.tecanevo100;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;



import org.assertj.core.internal.Numbers;



import play.Logger;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;
import controllers.instruments.io.tecanevo100.tpl.txt.*;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment,
			ContextValidation contextValidation) {
		String content="";
		
		if("solution-stock".equals(experiment.typeCode)){
			if("tube".equals(experiment.instrument.outContainerSupportCategoryCode)){
				content = OutputHelper.format(solution_stock_output_tube.render(experiment).body());
			} else if("96-well-plate".equals(experiment.instrument.outContainerSupportCategoryCode)){
				content = OutputHelper.format(solution_stock_output_96_well_plate.render(experiment).body());
			}
		}else if("pool".equals(experiment.typeCode)){
			content = OutputHelper.format(pool_x_to_tubes.render(getSampleSheetPoolLines(experiment)).body());
		}else{
			throw new RuntimeException("Not Managed : "+experiment.typeCode);
		}
		
		
		String filename = OutputHelper.getInstrumentPath(experiment.instrument.code)+experiment.code+"_Tecan.csv";
		File file = new File(filename, content);
		OutputHelper.writeFile(file);
		return file;
	}

	private List<SampleSheetPoolLine> getSampleSheetPoolLines(Experiment experiment) {
		Map<String, String> sourceMapping = getSourceMapping(experiment);
		Map<String, String> destPositionMapping = getDestMapping(experiment);
		
		List<SampleSheetPoolLine> lines = new ArrayList<SampleSheetPoolLine>();
		
		experiment.atomicTransfertMethods.forEach(atm -> {
			
			OutputContainerUsed output = atm.outputContainerUseds.get(0);
			
			atm.inputContainerUseds.forEach(input -> {
				lines.add(getSampleSheetPoolLine(input, output, sourceMapping, destPositionMapping));
			});
			
		});
		
		return lines;
	}

	private SampleSheetPoolLine getSampleSheetPoolLine(
			InputContainerUsed input, OutputContainerUsed output,
			Map<String, String> sourceMapping,
			Map<String, String> destPositionMapping) {
		SampleSheetPoolLine sspl = new SampleSheetPoolLine();
		sspl.inputSupportCode = input.locationOnContainerSupport.code;
		sspl.inputSupportContainerCode = input.code;
		sspl.inputSupportContainerColumn = input.locationOnContainerSupport.column;
		sspl.inputSupportContainerLine = input.locationOnContainerSupport.line;
		sspl.inputSupportContainerPosition = OutputHelper.getNumberPositionInPlateByColumn(input.locationOnContainerSupport.line, input.locationOnContainerSupport.column);
		sspl.inputSupportContainerVolume = input.experimentProperties.get("inputVolume").value.toString().replace(".", ","); 
		sspl.inputSupportSource =  sourceMapping.get(input.locationOnContainerSupport.code);
			
		sspl.outputSupportCode = output.locationOnContainerSupport.code;
		sspl.outputSupportPosition = destPositionMapping.get(output.locationOnContainerSupport.code);
		return sspl;
	}

	private Map<String, String> getSourceMapping(Experiment experiment) {
		Map<String, String> sources = new HashMap<String, String>();
		
		String[] inputContainerSupportCodes = experiment.inputContainerSupportCodes.toArray(new String[0]);
		Arrays.sort(inputContainerSupportCodes);
		for(int i = 0; i < inputContainerSupportCodes.length ; i++){
			sources.put(inputContainerSupportCodes[i], "Src"+(i+1));
		}
		return sources;
	}
	
	private Map<String, String> getDestMapping(Experiment experiment) {
		Map<String, String> dest = new HashMap<String, String>();
		
		String[] outputContainerSupportCodes = experiment.outputContainerSupportCodes.toArray(new String[0]);
		Arrays.sort(outputContainerSupportCodes);
		for(int i = 0; i < outputContainerSupportCodes.length ; i++){
			dest.put(outputContainerSupportCodes[i], (i+1)+"");
		}
		return dest;
	}

}
