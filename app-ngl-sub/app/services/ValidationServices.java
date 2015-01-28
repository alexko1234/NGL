package services;

import validation.ContextValidation;
import models.sra.submit.common.instance.Submission;
import models.sra.submit.sra.instance.Experiment;
import models.sra.submit.util.SraException;

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
