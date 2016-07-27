package controllers.instruments.io.janus;



import java.text.SimpleDateFormat;
import java.util.Date;

import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;

import controllers.instruments.io.janus.tpl.txt.normalization; 
import controllers.instruments.io.janus.tpl.txt.normalizationPooling_samples; 
import controllers.instruments.io.janus.tpl.txt.normalizationPooling_buffer; 
import controllers.instruments.io.janus.tpl.txt.pool_PlatesToPlate;

import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	 public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		String content=null;
		String fdrType=null;
		// a inverser...pool<->normalization-and-pooling !!!!!
		
		if ("normalization-and-pool".equals(experiment.typeCode)){
			content = OutputHelper.format(pool_PlatesToPlate.render(experiment).body());	
		} else if ("pool".equals(experiment.typeCode)){
			//recuperer la valeur de la key "fdrType"dans contextValidation
			Object ftype =contextValidation.getObject("fdrType");
			if ("samples".equals(ftype) ){	
				fdrType="samples";
				content = OutputHelper.format(normalizationPooling_samples.render(experiment).body());
			} else if ("buffer".equals(ftype)) {
				fdrType="buffer";
				content = OutputHelper.format(normalizationPooling_buffer.render(experiment).body());
			}else {
				throw new RuntimeException("Janus sampleSheet type not managed : "+experiment.typeCode + "/" +ftype);
			}
		} else if ("lib-normalization".equals(experiment.typeCode)){
			content = OutputHelper.format(normalization.render(experiment).body());	
		}else {
			// a venir...
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

}
