package models.sra.submit.common.instance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import validation.ContextValidation;
import validation.IValidation;
import validation.sra.SraValidationHelper;
import validation.utils.ValidationHelper;
import fr.cea.ig.DBObject;
import fr.cea.ig.MongoDBDAO;
import models.laboratory.common.description.ObjectType;
import models.laboratory.common.instance.State;
import models.laboratory.common.instance.TraceInformation;
import models.sra.submit.util.VariableSRA;
import models.utils.InstanceConstants;

public class Study extends AbstractStudy {

	// StudyType
	//public String alias;             // required mais remplacé par code herité de DBObject, et valeur = study_projectCode_num
	public String title = "";	       // required next soon      
	public String studyAbstract = "";  // required next soon 
	public String description = "";    
	public int bioProjectId;           // doit etre mis à 0 si absent.
	public String existingStudyType;   // required et constraint 
	public Date firstSubmissionDate;   // date de premiere soumission du study.
 	public String accession = null;      // numeros d'accession attribué par ebi */
 	public String externalId = null;
  	public Date releaseDate;             // required, date de mise à disposition en public par l'EBI
  	public String centerName = VariableSRA.centerName;        // required pour nos stats valeur fixee à GSC */
    public String centerProjectName;      // required pour nos stats valeur fixée à projectCode
 	public List <String> projectCodes = new ArrayList<String>();    // required pour nos stats  
    // et heritage de state et traceInformation.
	public static final String initialStateCode = "NONE";

	@Override
	public void validate(ContextValidation contextValidation) {

		contextValidation.addKeyToRootKeyName("study");
		System.out.println("dans validate");
		
		// verifier que projectCodes est bien renseigné et existe dans lims:
		SraValidationHelper.validateProjectCodes(this.projectCodes, contextValidation);
		// Attention on peut vouloir regrouper dans un project_code virtuel ?? 
		ValidationHelper.required(contextValidation, this.centerProjectName , "centerProjectName");
		SraValidationHelper.requiredAndConstraint(contextValidation, 
												  this.centerName, 
												  VariableSRA.mapCenterName(), 
												  "centerName");
		SraValidationHelper.validateId(this, contextValidation);
		SraValidationHelper.validateCode(this, InstanceConstants.SRA_STUDY_COLL_NAME, contextValidation);
		SraValidationHelper.validateTraceInformation(traceInformation, contextValidation);
		if ( this.state != null && StringUtils.isNotBlank(this.state.code) && !initialStateCode.equals(this.state.code)) {
			SraValidationHelper.validateState(ObjectType.CODE.SRASubmission, this.state, contextValidation);
		}
		SraValidationHelper.validateFreeText(contextValidation, "title", this.title);
		SraValidationHelper.validateFreeText(contextValidation, "studyAbstract", this.studyAbstract);
		SraValidationHelper.validateFreeText(contextValidation,"description", this.description);

		if (!StringUtils.isNotBlank((CharSequence) contextValidation.getContextObjects().get("type"))){
			contextValidation.addErrors(" study non evaluable ", "sans type de contexte de validation");
			contextValidation.removeKeyFromRootKeyName("study");
			return;
		}
		if (contextValidation.getContextObjects().get("type").equals("sra")) {
			System.out.println("contextValidationType  = sra");
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_STUDY_COLL_NAME, contextValidation);
			SraValidationHelper.requiredAndConstraint(contextValidation, 
													  this.existingStudyType, 
													  VariableSRA.mapExistingStudyType(), 
													  "existingStudyType");
		} else if (contextValidation.getContextObjects().get("type").equals("wgs")) {
			System.out.println("contextValidationType  = wgs");
			SraValidationHelper.validateCode(this, InstanceConstants.SRA_STUDY_WGS_COLL_NAME, contextValidation);
			if (!this.existingStudyType.equals("Whole Genome Sequencing")) {
				contextValidation.addErrors("existingStudyType" + " avec valeur '" + this.existingStudyType + 
						"' qui n'appartient pas a la liste des valeurs autorisees :" , "Whole Genome Sequencing");
			}
		} else {
			System.out.println("contextValidationType = "+contextValidation.getContextObjects().get("type"));
			contextValidation.addErrors("study non evaluable ", "avec type de contexte de validation " 
										 + contextValidation.getContextObjects().get("type"));	
		}
		contextValidation.removeKeyFromRootKeyName("study");

	}

}
