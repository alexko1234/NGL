package models.sra.study.instance;

import java.util.Date;

import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.utils.VariableSRA;
import models.utils.InstanceConstants;
public class Study extends DBObject implements IValidation {

	// StudyType
	//public String alias;         // required mais remplacé par code herité de DBObject, et valeur = study_projectCode_num
	public String title = "";	       // required next soon      
 	public String projectCode;         // required pour nos stats   
	public String studyAbstract = "";  // required next soon 
	public String description = "";    
	public int bioProjectId;           // required mais peut-etre mis à 0
	public String existingStudyType;   // required et constraint 
 	public String accession = "";      // numeros d'accession attribué par ebi */
  	public Date releaseDate;           // ?? required, date de mise à disposition en public par l'EBI
  	public String centerName = VariableSRA.centerName;        // required pour nos stats valeur fixee à GSC */
    public String centerProjectName; // required pour nos stats valeur fixée à projectCode

	public State state = new State("New", null); // Reference sur "models.laboratory.common.instance.state"
	 	// pour gerer les differents etats de l'objet.
	 	// Les etapes utilisateurs = (new, inWaitingConfiguration,) inProgressConfiguration, finishConfiguration, 
	 	// Les etapes automatisables via birds : inWaitingSubmission, inProgressSubmission, finishSubmission, submit

	public TraceInformation traceInformation; // new TraceInformation .Reference sur "models.laboratory.common.instance.TraceInformation" 
		// pour loguer les dernieres modifications utilisateurs	

	@Override
	public void validate(ContextValidation contextValidation) {
		
		// verifier que projectCode est bien renseigné et existe dans lims:
		SraValidationHelper.validateProjectCode(this.projectCode, contextValidation);
		// Attention on peut vouloir regrouper dans un project_code virtuel ?? 
		ValidationHelper.required(contextValidation, this.centerProjectName , "centerProjectName");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.existingStudyType, VariableSRA.mapExistingStudyType, "existingStudyType");
		SraValidationHelper.requiredAndConstraint(contextValidation, this.centerName, VariableSRA.mapCenterName, "centerName");
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_STUDY_COLL_NAME, contextValidation);
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
	}

}
