/* 06/09/2017 FDS: copy de l'epimotion .... */

package controllers.instruments.io.cng.bravoworkstation;

import java.text.SimpleDateFormat;
import java.util.Date;

import play.Logger;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;
import controllers.instruments.io.cng.bravoworkstation.tpl.txt.*;

import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class BravoWorkstationOutput extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		
		Logger.info("generation feuille de route BravoWorkstation / exp="+ experiment.typeCode );
		String content = OutputHelper.format(sampleSheet_1.render(experiment).body());
		
		File file = new File(getFileName(experiment)+".csv", content);
		return file;
	}
	
	private String getFileName(Experiment experiment) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");
		// c'est le premier container qui donne son nom a la feuille de route. Ce mecanisme était fait pour un ouput container Support plaque
		// pas tres pertinent pour tubes... mettre plutot la plaque input dans le nom ???
		
		// return experiment.typeCode.toUpperCase()+"_"+experiment.atomicTransfertMethods.get(0).outputContainerUseds.get(0).locationOnContainerSupport.code+"_"+sdf.format(new Date());
		return experiment.typeCode.toUpperCase()+"_"+experiment.atomicTransfertMethods.get(0).inputContainerUseds.get(0).locationOnContainerSupport.code+"_"+sdf.format(new Date());
	}
}