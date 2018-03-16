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
//vol seuil pour petit vol
	private int treshold= 20;
	private String name1="pipette_P50";
	private String name2="pipette_P200";
	private static int sampleNum;
	
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
				file = new File(getFileName(experiment)+"_ADN_"+name1+".csv", adnContent);

			}else if("normalisation-buffer".equals(type)){
				List<PlateSampleSheetLine> pssl = getPlateSampleSheetLines(experiment, experiment.instrument.inContainerSupportCategoryCode);
				pssl = checkSampleSheetLines(pssl, isPlaque);

				bufferContent = OutputHelper.format(normalisation_x_to_plate_buffer.render(pssl).body());
				file = new File(getFileName(experiment)+"_Buffer_"+name1+".csv", bufferContent);

			}else if ("normalisation-highVol".equals(type) ){
				List<PlateSampleSheetLine> pssl = getPlateSampleSheetLines(experiment, experiment.instrument.inContainerSupportCategoryCode);
				pssl = checkSampleSheetLines(pssl, isPlaque);

				adnContent = OutputHelper.format(normalisation_x_to_plate_highVol.render(pssl).body());
				file = new File(getFileName(experiment)+"_ADN_"+name2+".csv", adnContent);

			}else if("normalisation-buffer-highVol".equals(type)){
				List<PlateSampleSheetLine> pssl = getPlateSampleSheetLines(experiment, experiment.instrument.inContainerSupportCategoryCode);
				pssl = checkSampleSheetLines(pssl, isPlaque);

				bufferContent = OutputHelper.format(normalisation_x_to_plate_buffer_highVol.render(pssl).body());
				file = new File(getFileName(experiment)+"_Buffer_"+name2+".csv", bufferContent);

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

		Double vol = new Double(0);

		if(icu.experimentProperties!=null && icu.experimentProperties.containsKey("inputVolume"))
			vol = (Double)icu.experimentProperties.get("inputVolume").value;
		else if(ocu.volume!=null && ocu.volume.value!=null)
			vol = (Double)ocu.volume.value;
		else
			Logger.error("Aucun volume renseigné dans l'expérience! ");


		if (vol < treshold){
			pssl.inputVolume = vol.toString().replace(".", ",");
			pssl.inputHighVolume = "0,0";
		}else{
			pssl.inputHighVolume = vol.toString().replace(".", ",");
			pssl.inputVolume = "0,0";
		}

		if(icu.experimentProperties!=null && icu.experimentProperties.containsKey("bufferVolume"))
			vol = (Double)icu.experimentProperties.get("bufferVolume").value;

		if (vol < treshold){
			pssl.bufferVolume = vol.toString().replace(".", ",");
			pssl.bufferHighVolume = "0,0";
		}else{
			pssl.bufferHighVolume = vol.toString().replace(".", ",");
			pssl.bufferVolume = "0,0";
		}
		pssl.dwell = ocu.locationOnContainerSupport.line.concat(ocu.locationOnContainerSupport.column);

		return pssl;
	}
	
	private List<PlateSampleSheetLine> checkSampleSheetLines (List<PlateSampleSheetLine> psslList, Boolean isPlate){

		List<PlateSampleSheetLine> psslListNew = new LinkedList<PlateSampleSheetLine>();
		
		if (isPlate){
			List<String> plateLines = Arrays.asList("A","B","C","D","E","F","G","H"); 	
			List<Integer> colNums = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
			sampleNum=0;
			 psslListNew = filledSampleSheetLines(psslList,plateLines,colNums);

		}else{
			/* on gere finalement les tubes par 4 racks 
			*/
			sampleNum=0;
			List<String> plateLines = Arrays.asList("A","B","C","D"); 	
			List<String> plateLines2 = Arrays.asList("E","F","G","H"); 	
			List<Integer> colNums= Arrays.asList(1,2,3,4,5,6);
			List<Integer> colNums2 = Arrays.asList(7,8,9,10,11,12);
			
			 psslListNew = filledSampleSheetLines(psslList,plateLines,colNums);
			 psslListNew.addAll(filledSampleSheetLines(psslList,plateLines,colNums2));
			 psslListNew.addAll(filledSampleSheetLines(psslList,plateLines2,colNums));
			 psslListNew.addAll(filledSampleSheetLines(psslList,plateLines2,colNums2));
		}

		return psslListNew;	
	}


	private  List<PlateSampleSheetLine> filledSampleSheetLines (List<PlateSampleSheetLine> psslList, List<String> plateLines, List<Integer> colNums){
		
		boolean found = false;
		List<PlateSampleSheetLine> psslListNew = new LinkedList<PlateSampleSheetLine>();

		ListIterator<String> LinesItr = plateLines.listIterator();	
		while(LinesItr.hasNext()) {
			String line =(String) LinesItr.next();		


			ListIterator<Integer> colNumsItr = colNums.listIterator();	
			while(colNumsItr.hasNext()) {
				Integer col =(Integer) colNumsItr.next();		
				found = false;
				sampleNum ++;
				ListIterator<PlateSampleSheetLine> psslListItr = psslList.listIterator();	
				while(psslListItr.hasNext()) {
					PlateSampleSheetLine pssl =(PlateSampleSheetLine) psslListItr.next();					
					if (pssl.dwell.equals(line+col)){
						found=true;	
						pssl.dwellNum=sampleNum;
						pssl.sampleName = "Sample "+sampleNum;
						psslListNew.add(pssl);			
					}
				}

				if (! found){
					PlateSampleSheetLine psslBlank = new PlateSampleSheetLine();
					psslBlank.dwell=line+col;
					psslBlank.dwellNum=sampleNum;

					psslBlank.sampleName = "Sample "+sampleNum;
					psslBlank.inputVolume = "0,0";
					psslBlank.bufferVolume = "0,0";
					psslBlank.inputHighVolume = "0,0";
					psslBlank.bufferHighVolume = "0,0";
					
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
