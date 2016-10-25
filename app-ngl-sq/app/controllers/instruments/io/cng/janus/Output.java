package controllers.instruments.io.cng.janus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;

// 21/09/2016 expression generique pour toutes les templates feuilles de route
import controllers.instruments.io.cng.janus.tpl.txt.*;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	 public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		String content=null;
		String fdrType=null;
		
		if ("normalization-and-pooling".equals(experiment.typeCode)){
			//recuperer la valeur de la key "fdrType"dans contextValidation
			Object ftype =contextValidation.getObject("fdrType");
			if ("samples".equals(ftype) ){	
				fdrType="samples";
				Logger.info("generation feuille de route Janus / exp="+ experiment.typeCode + "/ type="+ fdrType );
				// 21/09/2016 appeler une methode pour generer la liste des lignes a mettre dans la feuille de route
				content = OutputHelper.format(normalizationPooling_samples.render(getSampleSheetPoolLines(experiment)).body());
			} else if ("buffer".equals(ftype)) {
				fdrType="buffer";
				Logger.info("generation feuille de route Janus / exp="+ experiment.typeCode + "/ type="+ fdrType );
				content = OutputHelper.format(normalizationPooling_buffer.render(experiment).body());
			}else {
				throw new RuntimeException("Janus sampleSheet type not managed : "+experiment.typeCode + "/" +ftype);
			}
			
		// 13/09/2016 finalement il y a aussi 2 feuilles de route pour pooling
		} else if ("pool".equals(experiment.typeCode)){
			//recuperer la valeur de la key "fdrType"dans contextValidation
			Object ftype =contextValidation.getObject("fdrType");
			if ("samples".equals(ftype) ){	
				fdrType="samples";
				Logger.info("generation feuille de route Janus / exp="+ experiment.typeCode + "/ type="+ fdrType );
				// 21/09/2016 appeler une methode pour generer la liste des lignes a mettre dans la feuille de route
				content = OutputHelper.format(pool_PlatesToPlate_samples.render(getSampleSheetPoolLines(experiment)).body());	
			} else if ("buffer".equals(ftype)) {
				fdrType="buffer";
				Logger.info("generation feuille de route Janus / exp="+ experiment.typeCode + "/ type="+ fdrType );
				content = OutputHelper.format(pool_PlatesToPlate_buffer.render(experiment).body());	
			}
			
		} else if ("lib-normalization".equals(experiment.typeCode)){
			Logger.info("generation feuille de route Janus / exp="+ experiment.typeCode );
			content = OutputHelper.format(normalization.render(experiment).body());	
			
		}else {
			// a venir ????
			//    rna-prep; 
			//    pcr-purif; 
			throw new RuntimeException("Janus sampleSheet type not managed for experiment : "+experiment.typeCode);
		}
		
		File file = new File(getFileName(experiment,fdrType )+".csv", content);
		return file;
	}
	
	// 27/07/2016 ajouter le type de feuille de route dans le nom du fichier s'il y a un type
	private String getFileName(Experiment experiment,String fdrType) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");
		
		String fileName=experiment.typeCode.toUpperCase()+"_"+experiment.atomicTransfertMethods.get(0).outputContainerUseds.get(0).locationOnContainerSupport.code;
		if ( null != fdrType ){ fileName +="_"+fdrType;}
		fileName +="_"+sdf.format(new Date());
		    		   
		return fileName;      
	}
	
	
	// 21/09/2016 Il faut trier en java les lignes a envoyer au template de la feuille de route ( trop complexe a faire en scala !!)
	//            3 methodes adaptées (simplifiees) depuis tecanevo100/output.java
	private List<SampleSheetPoolLine> getSampleSheetPoolLines(Experiment experiment) {
		Map<String, String> sourceMapping = getSourceMapping(experiment);
		
		List<SampleSheetPoolLine> lines = new ArrayList<SampleSheetPoolLine>();
		
		experiment.atomicTransfertMethods.forEach(atm -> {
			
			OutputContainerUsed output = atm.outputContainerUseds.get(0);
			
			atm.inputContainerUseds.forEach(input -> {
				lines.add(getSampleSheetPoolLine(input, output, sourceMapping));
			});
			
		});
		
		return lines;
	}

	private SampleSheetPoolLine getSampleSheetPoolLine(
			InputContainerUsed input, 
			OutputContainerUsed output,
			Map<String, String> sourceMapping) {
		SampleSheetPoolLine sspl = new SampleSheetPoolLine();
		
		sspl.inputSupportCode = input.locationOnContainerSupport.code;// normallement uniqt pour DEBUG
		
		sspl.inputSupportContainerPosition = OutputHelper.getNumberPositionInPlateByColumn(input.locationOnContainerSupport.line, input.locationOnContainerSupport.column);
		// NON garder le separateur decimal "." 
		// sspl.inputSupportContainerVolume = input.experimentProperties.get("inputVolume").value.toString().replace(".", ","); 
		sspl.inputSupportContainerVolume = input.experimentProperties.get("inputVolume").value.toString(); 
		sspl.inputSupportSource =  sourceMapping.get(input.locationOnContainerSupport.code);
			
		sspl.outputSupportPosition = OutputHelper.getNumberPositionInPlateByColumn(output.locationOnContainerSupport.line, output.locationOnContainerSupport.column);
		
		return sspl;
	}

	private Map<String, String> getSourceMapping(Experiment experiment) {
		Map<String, String> sources = new HashMap<String, String>();
		
		String[] inputContainerSupportCodes = experiment.inputContainerSupportCodes.toArray(new String[0]);
		Arrays.sort(inputContainerSupportCodes);
		for(int i = 0; i < inputContainerSupportCodes.length ; i++){
			sources.put(inputContainerSupportCodes[i], "Source_"+(i+1));
		}
		return sources;
	}
}
