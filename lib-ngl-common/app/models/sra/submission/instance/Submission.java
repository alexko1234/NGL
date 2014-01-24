package models.sra.submission.instance;

import java.util.Date;
import java.util.List;

import models.sra.experiment.instance.Experiment;
import models.sra.experiment.instance.Run;
import models.sra.sample.instance.Sample;
import models.sra.study.instance.Study;
import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class Submission extends DBObject implements IValidation {

	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = CNS_projectCode_num
	public String projectCode;     // required pour nos stats
 	public String accession;         // numeros d'accession attribué par ebi */
	public Date submissionDate;
	
	public List <Study> list_study;
	public List <Sample> list_sample;
	public List <Experiment> list_experiment;
	public List <Run> list_run;
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub	
	}

}
