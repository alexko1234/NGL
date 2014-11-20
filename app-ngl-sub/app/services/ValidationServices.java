package services;

import validation.ContextValidation;
import models.sra.experiment.instance.Experiment;
import models.sra.submission.instance.Submission;
import models.sra.utils.SraException;

public class ValidationServices {
	
	
	public void ValidateExperiment(Experiment experiment, ContextValidation contexValidation) throws SraException{
		//	SraDbServices dbService = new SraDbServices();
			String mess = "";//dbService.checkExperimentExist(experiment); // dbUtil	
			if (mess!= null){
				contexValidation.addErrors("", "");
			}
		
		// validation metier :
			
	}
	

	public void ValidateSubmission(String submissionCode, ContextValidation cont){
		/*
		 Recuperer objet submission
		 submission.validate();
		 - Faire les liens et repertoires.
		 
		 
		 */
		
		
	}
	
}
