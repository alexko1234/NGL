package controllers.instruments.io.cns.brandlhs;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import play.Logger;
import models.laboratory.experiment.instance.AtomicTransfertMethod;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;

//import controllers.instruments.io.cns.tecanevo100.SampleSheetPoolLine;
import controllers.instruments.io.cns.brandlhs.PlateSampleSheetLine;
import controllers.instruments.io.cns.brandlhs.tpl.txt.*;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;


import java.util.zip.*;

public class Output extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		String type = (String)contextValidation.getObject("type");

		String adnContent=null;
		String bufferContent=null;
		File file;
		Boolean isPlaque = "96-well-plate".equals(experiment.instrument.inContainerSupportCategoryCode);	
		
		//tube / 96-well-plate
		if("96-well-plate".equals(experiment.instrument.outContainerSupportCategoryCode)){
			if ("normalisation".equals(type) ){
				List<PlateSampleSheetLine> pssl = getPlateSampleSheetLines(experiment, experiment.instrument.inContainerSupportCategoryCode);
				pssl = checkSampleSheetLines(pssl, isPlaque);

				adnContent = OutputHelper.format(normalisation_x_to_plate.render(pssl).body());
				file = new File(getFileName(experiment)+"_ADN.csv", adnContent);

			}else if("normalisation-buffer".equals(type)){
				List<PlateSampleSheetLine> pssl = getPlateSampleSheetLines(experiment, experiment.instrument.inContainerSupportCategoryCode);
				pssl = checkSampleSheetLines(pssl, isPlaque);

				bufferContent = OutputHelper.format(normalisation_x_to_plate_buffer.render(pssl).body());
				file = new File(getFileName(experiment)+"_Buffer.csv", bufferContent);

			}if ("normalisation-highVol".equals(type) ){
				List<PlateSampleSheetLine> pssl = getPlateSampleSheetLines(experiment, experiment.instrument.inContainerSupportCategoryCode);
				pssl = checkSampleSheetLines(pssl, isPlaque);

				adnContent = OutputHelper.format(normalisation_x_to_plate_highVol.render(pssl).body());
				file = new File(getFileName(experiment)+"_ADN.csv", adnContent);

			}else if("normalisation-buffer-highVol".equals(type)){
				List<PlateSampleSheetLine> pssl = getPlateSampleSheetLines(experiment, experiment.instrument.inContainerSupportCategoryCode);
				pssl = checkSampleSheetLines(pssl, isPlaque);

				bufferContent = OutputHelper.format(normalisation_x_to_plate_buffer_highVol.render(pssl).body());
				file = new File(getFileName(experiment)+"_Buffer.csv", bufferContent);

			}else{
				throw new RuntimeException("brandlhs sampleSheet io not managed : "+type);
			}
		}else {
			throw new RuntimeException("brandlhs sampleSheet io combination not managed : "+experiment.instrument.inContainerSupportCategoryCode+" / "+experiment.instrument.outContainerSupportCategoryCode+" / "+type);
		}


		return file;
	}

	private String getFileName(Experiment experiment) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");
		return experiment.code.toUpperCase();
		
	}


	private List<PlateSampleSheetLine> getPlateSampleSheetLines(Experiment experiment, String inputContainerCategory) {

		return  experiment.atomicTransfertMethods
				.parallelStream()
				.map(atm -> getPlateSampleSheetLine(atm,inputContainerCategory, experiment))
				.collect(Collectors.toList());	

	}

	private PlateSampleSheetLine getPlateSampleSheetLine(AtomicTransfertMethod atm, String inputContainerCategory,Experiment experiment) {
		Map<String, String> sourceMapping = getSourceMapping(experiment);
		Map<String, String> destPositionMapping = getDestMapping(experiment);
		
		InputContainerUsed icu = atm.inputContainerUseds.get(0);
		OutputContainerUsed ocu = atm.outputContainerUseds.get(0);
		PlateSampleSheetLine pssl = new PlateSampleSheetLine();

		pssl.inputContainerCode = icu.code;
		pssl.outputContainerCode = ocu.code;

		if(icu.experimentProperties!=null && icu.experimentProperties.containsKey("inputVolume")){
			pssl.inputVolume = (Double)icu.experimentProperties.get("inputVolume").value;
		}else if(ocu.volume!=null && ocu.volume.value!=null){
			pssl.inputVolume = (Double)ocu.volume.value;
		}
		if(icu.experimentProperties!=null && icu.experimentProperties.containsKey("bufferVolume")){
			pssl.bufferVolume = (Double)icu.experimentProperties.get("bufferVolume").value;
		}
		pssl.dwell = ocu.locationOnContainerSupport.line.concat(ocu.locationOnContainerSupport.column);

		return pssl;
	}
	private List<PlateSampleSheetLine> checkSampleSheetLines (List<PlateSampleSheetLine> psslList, Boolean isPlate){

		PlateSampleSheetLine psslTemplate = new PlateSampleSheetLine();
		List<PlateSampleSheetLine> psslListNew = new LinkedList<PlateSampleSheetLine>();
		List<String> plateLines ;
		int colNum;	
		if (isPlate){
			plateLines = Arrays.asList("A","B","C","D","E","F","G","H"); 	
			colNum=12;
		}else{
			/* on gere finalement les tubes par 4 racks donc iso tubes
			 * plateLines = Arrays.asList("A","B","C","D");
			*colNum=6;*/
			plateLines = Arrays.asList("A","B","C","D","E","F","G","H"); 	
			colNum=12;
		}
		boolean found =false;	
		int sampleNum=0;
		for(int line = 0; line < plateLines.size(); line++){

			for(int plateCol = 1; plateCol <= colNum ;plateCol++){
				found = false;
				sampleNum ++;
				ListIterator<PlateSampleSheetLine> psslListItr = psslList.listIterator();	
				while(psslListItr.hasNext()) {
					PlateSampleSheetLine pssl =(PlateSampleSheetLine) psslListItr.next();					
					if (pssl.dwell.equals(plateLines.get(line)+plateCol)){
						found=true;	
						pssl.dwellNum=sampleNum;
						pssl.sampleName = "Sample "+sampleNum;
						psslListNew.add(pssl);			
					}
				}

				if (! found){
					PlateSampleSheetLine psslBlank = new PlateSampleSheetLine();
					psslBlank.dwell=plateLines.get(line)+plateCol;
					psslBlank.dwellNum=sampleNum;

					psslBlank.sampleName = "Sample "+sampleNum;
					psslBlank.inputVolume = new Double(0);
					psslBlank.bufferVolume = new Double(0);
					psslListNew.add(psslBlank);
				}
			}
		}
		return psslListNew;	
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
