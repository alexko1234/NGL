package models.sra.submission.instance;

import java.util.Date;
import java.util.List;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

// objet qui decrit ce qu'on soumet à l'EBI à l'instant t.
public class Submission extends DBObject implements IValidation {

	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = CNS_projectCode_num
	public String projectCode;     // required pour nos stats //Reference code de la collection project NGL
 	public String accession;       // numeros d'accession attribué par ebi */
	public Date submissionDate;

	public List<String> studyCodes;
	public List<String> sampleCodes;
	public List<String> experimentCodes;
	//soit si on veut tracer les runs soumis par le code des runs
	//public List<String> runCodes;
	public String strategyStudy;
	public String strategySample;

	public State state;// = new State(); // Reference sur "models.laboratory.common.instance.state" 
		// pour gerer les differents etats de l'objet.
		// Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
		// Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit
	public TraceInformation traceInformation; // new TraceInformation .Reference sur "models.laboratory.common.instance.TraceInformation" 
		// pour loguer les dernieres modifications utilisateurs


	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub	
	}

}
