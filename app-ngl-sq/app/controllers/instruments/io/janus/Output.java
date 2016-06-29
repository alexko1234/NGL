package controllers.instruments.io.janus;



import java.text.SimpleDateFormat;
import java.util.Date;

import models.laboratory.experiment.instance.Experiment;
import validation.ContextValidation;
import controllers.instruments.io.janus.tpl.txt.sampleSheet_1;
import controllers.instruments.io.janus.tpl.txt.pool_plates_to_plate;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	 public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		String content=null;
		
		if ( "pool".equals(experiment.typeCode)){
			// feuille de route specifique pour les pools de plaques -> plaque
			content = OutputHelper.format(pool_plates_to_plate.render(experiment).body());
		}else if ("lib-normalization".equals(experiment.typeCode)){
			//FDS feuille de route originale: trouver meilleur nom...
			content = OutputHelper.format(sampleSheet_1.render(experiment).body());
		}else {
			//rna-prep; pcr-purif; normalization-and-pooling a venir.....
			throw new RuntimeException("Janus sampleSheet type not Managed : "+experiment.typeCode);
		}
		
		File file = new File(getFileName(experiment)+".csv", content);
		return file;
	}
	
	private String getFileName(Experiment experiment) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");
		return experiment.typeCode.toUpperCase()+"_"+experiment.atomicTransfertMethods.get(0).outputContainerUseds.get(0).locationOnContainerSupport.code+"_"+sdf.format(new Date());
	}

}
