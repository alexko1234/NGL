package models.sra.study.instance;

import java.util.Date;

import validation.ContextValidation;
import validation.IValidation;
import fr.cea.ig.DBObject;

public class Study extends DBObject implements IValidation {

	// StudyType
	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = projectCode_num
	public String title;	       // required next soon      
 	public String projectCode;     // required pour nos stats   
	public String studyAbstract;   // required next soon 
	public String description;   
	public int bioProjectId;         // required mais peut-etre mis à 0
	public String existingStudyType; // required et constraint */
 	public String accession;         // numeros d'accession attribué par ebi */
  	public Date releaseDate;         // required, date de mise à disposition en public par l'EBI
  	public String centerName;        // required pour nos stats valeur fixee à GSC */
    public String centerProjectName; // required pour nos stats valeur fixée à projectCode
	public String stateCode;         // Reference sur "models.laboratory.common.instance.state" 
	 // pour gerer les differents etats de l'objet.
	 // Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
	 // Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit

	public String traceInformationCode; // Reference sur "models.laboratory.common.instance.TraceInformation" 
	// pour loguer les dernieres modifications utilisateurs
	
	@Override
	public void validate(ContextValidation contextValidation) {
		// TODO Auto-generated method stub
		
	}

}
