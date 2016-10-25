/* 25/10/2016 FDS: creation */

package controllers.instruments.io.cng.epimotion;

import java.text.SimpleDateFormat;
import java.util.Date;

import play.Logger;
import models.laboratory.experiment.instance.Experiment;
import models.laboratory.experiment.instance.InputContainerUsed;
import models.laboratory.experiment.instance.OutputContainerUsed;
import validation.ContextValidation;

import controllers.instruments.io.cng.epimotion.tpl.txt.*;
import controllers.instruments.io.utils.AbstractOutput;
import controllers.instruments.io.utils.File;
import controllers.instruments.io.utils.OutputHelper;

public class Output extends AbstractOutput {

	@Override
	public File generateFile(Experiment experiment,ContextValidation contextValidation) throws Exception {
		String FDStestparam="when specs ready";// test passage parametre a la feuille de route...
		
		Logger.info("generation feuille de route Epimotion / exp="+ experiment.typeCode );
		String content = OutputHelper.format(sampleSheet_1.render(experiment, FDStestparam).body());
		
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