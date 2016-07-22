package controllers.instruments.io.biomekfx;



import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;
import controllers.instruments.io.biomekfx.tpl.txt.*;
import controllers.instruments.io.tecanevo100.SampleSheetPoolLine;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	 public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		String content=null;
		//tube / 96-well-plate
		if ( "tube".equals(experiment.instrument.inContainerSupportCategoryCode)
				&& "96-well-plate".equals(experiment.instrument.outContainerSupportCategoryCode)){
			// feuille de route specifique pour les pools de plaques -> plaque
			content = OutputHelper.format(tubes_to_plate.render(getPlateSampleSheetLines(experiment)).body());
		}else {
			//rna-prep; pcr-purif; normalization-and-pooling a venir.....
			throw new RuntimeException("Biomek-FX sampleSheet io combination not managed : "+experiment.instrument.inContainerSupportCategoryCode+" / "+experiment.instrument.outContainerSupportCategoryCode);
		}
		
		File file = new File(getFileName(experiment)+".csv", content);
		return file;
	}
	
	private String getFileName(Experiment experiment) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");
		return experiment.typeCode.toUpperCase()+"_"+experiment.atomicTransfertMethods.get(0).outputContainerUseds.get(0).locationOnContainerSupport.code+"_"+sdf.format(new Date());
	}

	
	private List<PlateSampleSheetLine> getPlateSampleSheetLines(Experiment experiment) {
		
		return experiment.atomicTransfertMethods
			.parallelStream()
			.map(atm -> getPlateSampleSheetLine(atm))
			.collect(Collectors.toList());		
	}

	private PlateSampleSheetLine getPlateSampleSheetLine(AtomicTransfertMethod atm) {
		InputContainerUsed icu = atm.inputContainerUseds.get(0);
		OutputContainerUsed ocu = atm.outputContainerUseds.get(0);
		
		PlateSampleSheetLine pssl = new PlateSampleSheetLine();
		
		pssl.inputSupportCode = icu.locationOnContainerSupport.code;
		pssl.outputSupportCode = ocu.locationOnContainerSupport.code;
		
		pssl.inputVolume = (Double)icu.experimentProperties.get("inputVolume").value;
		pssl.bufferVolume = (Double)icu.experimentProperties.get("bufferVolume").value;
		
		pssl.dwell = OutputHelper.getNumberPositionInPlateByLine(ocu.locationOnContainerSupport.line, ocu.locationOnContainerSupport.column);
		
		pssl.sourceADN = getSourceADN(ocu.locationOnContainerSupport.line, ocu.locationOnContainerSupport.column);
		pssl.swellADN = getSwellADN(ocu.locationOnContainerSupport.line, ocu.locationOnContainerSupport.column);
		
		return pssl;
	}

	private List<String> grp1 = Arrays.asList("A","B","C","D"); 
	private List<String> grp2 = Arrays.asList("E","F","G","H"); 
	
	private String getSourceADN(String line, String column) {
		String value = null;
		Integer col = Integer.valueOf(column);
		
		if(grp1.contains(line) && col < 7){
			value = "A1-D6";
		}else if(grp1.contains(line) && col >= 7){
			value = "A7-D12";
		}else if(grp2.contains(line) && col < 7){
			value = "E1-H6";
		}else if(grp2.contains(line) && col >= 7){
			value = "E7-H12";
		}
		
		return value;
	}

	private Integer getSwellADN(String line, String column) {
		
		Integer value = null;
		Integer col = Integer.valueOf(column);
		
		if("A".equals(line) || "E".equals(line)){
			value = (col < 7)?col:col-6;
		} else if("B".equals(line) || "F".equals(line)){
			value = (col < 7)?col+6:col-6+6;
		} else if("C".equals(line) || "G".equals(line)){
			value = (col < 7)?col+12:col-6+12;
		} else if("D".equals(line) || "H".equals(line)){
			value = (col < 7)?col+18:col-6+18;
		}
		
		return value;
	}
}
